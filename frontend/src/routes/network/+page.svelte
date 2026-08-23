<script lang="ts">
	import { onMount, onDestroy, tick } from 'svelte';
	import type { GlobeDataTuple, NetworkNode } from '$lib/types';
	import { GLOBE } from '$lib/globe-theme';
	import { createLogger, formatDate } from '$lib/logger';

	const log = createLogger('globe.page');

	let { data }: { data: { props: { globeDataTuple: GlobeDataTuple } } } = $props();

	let globeElement: HTMLElement;
	let globeInstance: any = null;
	let resizeObserver: ResizeObserver;
	let emitTimer: ReturnType<typeof setTimeout> | null = null;
	/** every pending setTimeout, so onDestroy can cancel them all */
	const pending = new Set<ReturnType<typeof setTimeout>>();

	const tuple: GlobeDataTuple = $derived(data.props.globeDataTuple);
	const importedAt: string = $derived(tuple.apiImportedAt);
	const connectionsCount: number = $derived(tuple.connectionsCount);
	const flightsCount: number = $derived(tuple.flightsCount);

	/** HAM itself, rendered in the same points layer as the destinations. */
	type HubPoint = { code: string; lat: number; lng: number; isHub: true };
	type GlobePoint = NetworkNode | HubPoint;

	const isHub = (d: GlobePoint): d is HubPoint => 'isHub' in d;

	const nodes: NetworkNode[] = $derived(tuple.nodes);
	const hubPoint: HubPoint = $derived({
		code: 'HAM',
		lat: tuple.hamLat,
		lng: tuple.hamLng,
		isHub: true
	});
	const points: GlobePoint[] = $derived([hubPoint, ...nodes]);

	const maxFlights = $derived(Math.max(1, ...nodes.map((n) => n.flightCount)));
	/** 0..1, square-rooted so a 32-flight route isn't 32x the visual weight of a 1-flight one */
	const traffic = (flightCount: number) => Math.sqrt(flightCount / maxFlights);

	// Far enough out that the sphere still curves, close enough that the (entirely European)
	// route network fills the frame.
	const INITIAL_VIEW = { lat: 48, lng: 12, altitude: 1.6 };

	// --- departures ------------------------------------------------------------------
	// Rather than drawing all 69 routes at once, HAM fires one at a time: a dash travels
	// out along the great circle, then the arc is discarded.

	/** delay between departures */
	const EMIT_MIN_MS = 700;
	const EMIT_MAX_MS = 1900;
	/** how long the dash takes to travel — longer routes take proportionally longer */
	const flightMs = (distanceKm: number) => 1500 + distanceKm / 3;
	/** fraction of the arc the moving dash occupies */
	const DASH_LEN = 0.35;

	type Departure = { key: number; node: NetworkNode; flightTime: number; persistent: false };
	type SelectedArc = { key: number; node: NetworkNode; flightTime: number; persistent: true };
	type Arc = Departure | SelectedArc;

	let departures = $state<Departure[]>([]);
	let seq = 0;

	// A single slow pulse marking the hub. maxR / propagationSpeed = ~3.7s per ring, and
	// repeatPeriod is longer than that, so only one circle is ever expanding at a time.
	const hubRing = $derived([
		{ lat: tuple.hamLat, lng: tuple.hamLng, maxR: 2.2, propagationSpeed: 0.6, repeatPeriod: 4000 }
	]);

	// Loading is slow enough (~3s, most of it a single blocking hexing pass) that without
	// an indicator the page looks broken. See the note on `paint()` below.
	type Phase = 'downloading' | 'building' | 'ready';
	let phase = $state<Phase>('downloading');

	let hovered = $state<NetworkNode | null>(null);
	let selected = $state<NetworkNode | null>(null);

	/** Hover wins over selection, so moving the pointer previews another route. */
	const active = $derived<NetworkNode | null>(hovered ?? selected);

	/** The hovered/selected route stays drawn as a solid arc on top of the departures. */
	const arcs = $derived<Arc[]>(
		active
			? [...departures, { key: -1, node: active, flightTime: 0, persistent: true }]
			: [...departures]
	);

	/** setTimeout that cancels cleanly if the component is destroyed mid-flight */
	function later(fn: () => void, ms: number) {
		const id = setTimeout(() => {
			pending.delete(id);
			fn();
		}, ms);
		pending.add(id);
	}

	function depart() {
		if (nodes.length) {
			const node = nodes[Math.floor(Math.random() * nodes.length)];
			const flightTime = flightMs(node.distance);
			const key = ++seq;

			departures = [...departures, { key, node, flightTime, persistent: false }];
			// the dash needs a second pass to fully clear the arc before removal
			later(() => (departures = departures.filter((a) => a.key !== key)), flightTime * 2);
		}
		emitTimer = setTimeout(depart, EMIT_MIN_MS + Math.random() * (EMIT_MAX_MS - EMIT_MIN_MS));
	}

	/**
	 * globe.gl types every accessor as `(obj: object) => T`, so a callback declared with a
	 * narrower parameter is rejected. These adapt our typed callbacks to that signature.
	 */
	const onPoint =
		<T,>(fn: (d: GlobePoint) => T) =>
		(d: object) =>
			fn(d as GlobePoint);
	const onArc =
		<T,>(fn: (d: Arc) => T) =>
		(d: object) =>
			fn(d as Arc);

	function isActive(d: GlobePoint): boolean {
		return !isHub(d) && d.code === active?.code;
	}

	function airlineBreakdown(node: NetworkNode | null): [string, number][] {
		const counts = new Map<string, number>();
		for (const flight of node?.connection.flights ?? []) {
			counts.set(flight.airlineName, (counts.get(flight.airlineName) ?? 0) + 1);
		}
		return [...counts].sort(([, a], [, b]) => b - a);
	}

	// Feed new server data into an already-built globe (the builder chain in onMount only
	// runs once).
	$effect(() => {
		const pts = points;
		const ring = hubRing;
		if (!globeInstance) return;
		globeInstance.pointsData(pts).ringsData(ring);
	});

	// Departures and selection change constantly; push them on every change. Re-setting an
	// accessor is what makes globe.gl re-evaluate the colour closures.
	$effect(() => {
		const a = arcs;
		if (!globeInstance) return;
		globeInstance
			.arcsData(a)
			.pointColor(
				onPoint((d) => (isHub(d) ? GLOBE.hub : isActive(d) ? GLOBE.dotActive : GLOBE.dot))
			);
	});

	/**
	 * Flush a state change all the way to the screen.
	 *
	 * Building the globe blocks the main thread for over a second, so any phase change has
	 * to be painted *before* the blocking work starts or the user never sees it. tick()
	 * only gets the DOM updated; the double rAF waits for the browser to actually paint it.
	 */
	async function paint() {
		await tick();
		await new Promise<void>((resolve) =>
			requestAnimationFrame(() => requestAnimationFrame(() => resolve()))
		);
	}

	onMount(async () => {
		log('Mounting globe...');

		// All three are independent — running them sequentially wasted most of a second.
		const [THREE, globeModule, countries] = await Promise.all([
			import('three'),
			import('globe.gl'),
			// Antarctica is already excluded by scripts/build-countries-geojson.mjs
			fetch('/ne_50m_countries.geojson')
				.then((res) => res.json())
				.then((data) => data.features as any[])
		]);

		// Flat ocean — no specular, or the sphere reads as shiny plastic.
		const mat = new THREE.MeshPhongMaterial({
			color: GLOBE.ocean,
			shininess: 0,
			specular: new THREE.Color('#000000')
		});

		const GlobeClass = globeModule.default;

		phase = 'building';
		await paint();

		globeInstance = new GlobeClass(globeElement, {
			waitForGlobeReady: false,
			animateIn: false
		})
			.backgroundColor(GLOBE.ocean)
			.backgroundImageUrl('/night-sky.png')
			.globeMaterial(mat)
			.showAtmosphere(true)
			.atmosphereColor(GLOBE.atmosphere)
			.atmosphereAltitude(0.16)
			.pointOfView(INITIAL_VIEW, 0)

			// dotted continents — the geojson drawn as H3 cells instead of filled polygons
			.hexPolygonsData(countries)
			.hexPolygonUseDots(true)
			// H3 resolution: 3 gives ~60km cells, far too coarse to shape a coastline.
			// 4 gives ~22km and is what makes countries actually readable — at the cost of
			// ~1.7s of one-time hexing on load.
			.hexPolygonResolution(4)
			.hexPolygonMargin(0.25)
			.hexPolygonAltitude(0.001)
			.hexPolygonCurvatureResolution(3)
			.hexPolygonColor(() => GLOBE.landDot)
			.hexPolygonsTransitionDuration(0)

			// one departure at a time, plus the hovered/selected route held solid
			.arcsData(arcs)
			.arcStartLat(() => tuple.hamLat)
			.arcStartLng(() => tuple.hamLng)
			.arcEndLat(onArc((d) => d.node.lat))
			.arcEndLng(onArc((d) => d.node.lng))
			.arcAltitude(null) // null => derive from arcAltitudeAutoScale
			.arcAltitudeAutoScale(0.35)
			.arcColor(onArc(() => GLOBE.arcActive))
			.arcStroke(onArc((d) => (d.persistent ? 0.5 : 0.35)))
			// A departure is a short dash with a large gap and an initial gap of 1, so it
			// starts off-arc and travels the full length exactly once. The held arc is
			// solid instead (length 1, no gap, no animation).
			.arcDashLength(onArc((d) => (d.persistent ? 1 : DASH_LEN)))
			.arcDashGap(onArc((d) => (d.persistent ? 0 : 2)))
			.arcDashInitialGap(onArc((d) => (d.persistent ? 0 : 1)))
			.arcDashAnimateTime(onArc((d) => d.flightTime))
			.arcsTransitionDuration(0)

			// airports as dots; the code shows in a tooltip on hover rather than on the sphere
			.pointsData(points)
			.pointsMerge(false) // required, or onPointHover/onPointClick never fire
			.pointLat(onPoint((d) => d.lat))
			.pointLng(onPoint((d) => d.lng))
			// Points are extruded cylinders; keep them almost flat so they read as discs
			// rather than 3D columns — but above hexPolygonAltitude, or the land dots
			// occlude them.
			.pointAltitude(0.008)
			.pointRadius(onPoint((d) => (isHub(d) ? 0.34 : 0.16 + 0.24 * traffic(d.flightCount))))
			.pointColor(
				onPoint((d) => (isHub(d) ? GLOBE.hub : isActive(d) ? GLOBE.dotActive : GLOBE.dot))
			)
			.pointResolution(16)
			.pointLabel(
				onPoint(
					(d) =>
						`<span style="font:600 12px/1 ui-sans-serif,system-ui,sans-serif;letter-spacing:.08em;color:#0b1b34;background:#64ffda;padding:4px 7px;border-radius:5px;">${d.code}</span>`
				)
			)
			.pointsTransitionDuration(0)

			// a single slow pulse on the hub — destinations get no rings
			.ringsData(hubRing)
			.ringAltitude(0.012)
			.ringColor(() => GLOBE.arcActive)
			.ringResolution(100)
			.ringMaxRadius('maxR')
			.ringPropagationSpeed('propagationSpeed')
			.ringRepeatPeriod('repeatPeriod');

		const controls = globeInstance.controls();
		// No auto-rotation: every route is European, so spinning just carries the whole
		// network out of view. The departures supply the motion instead.
		controls.autoRotate = false;
		controls.enableDamping = true;
		// globe radius is 100, so distance = 100 * (1 + altitude). The lower clamp keeps the
		// camera from getting close enough to flatten the sphere back into a map.
		controls.minDistance = 160;
		controls.maxDistance = 600;

		globeInstance.onPointHover((point: GlobePoint | null) => {
			hovered = point && !isHub(point) ? point : null;
		});

		globeInstance.onPointClick((point: GlobePoint) => {
			if (isHub(point)) {
				globeInstance.pointOfView(INITIAL_VIEW, 900);
				return;
			}
			selected = point;
			globeInstance.pointOfView({ lat: point.lat, lng: point.lng, altitude: 1.7 }, 900);
		});

		// resize
		resizeObserver = new ResizeObserver(() => {
			globeInstance.width(globeElement.clientWidth);
			globeInstance.height(globeElement.clientHeight);
		});
		resizeObserver.observe(globeElement);

		// the builder chain above has returned, but nothing is on screen until the next
		// frame — wait for it before pulling the overlay away
		await paint();
		phase = 'ready';

		depart();

		log('Globe mounted.');
	});

	onDestroy(() => {
		log('Destroying globe...');

		if (emitTimer) clearTimeout(emitTimer);
		pending.forEach(clearTimeout);
		pending.clear();

		if (resizeObserver) {
			resizeObserver.disconnect();
		}

		if (globeInstance) {
			globeInstance._destructor();
		}

		log('Globe destroyed.');
	});
</script>

<div class="relative w-full bg-[#0b1b34]" style="height: calc(100dvh - 4rem)">
	<div bind:this={globeElement} class="w-full h-full" id="helloWorld"></div>

	<!--
		Loading overlay. Everything here animates with transform/opacity ONLY, so the
		compositor keeps it running while the main thread is blocked building the globe.
		Anything driven by JS, or animating layout properties, would visibly freeze.
	-->
	{#if phase !== 'ready'}
		<div
			class="loading-overlay absolute inset-0 z-30 flex flex-col items-center justify-center gap-5 bg-[#0b1b34]"
			data-testid="globe-loading"
		>
			<div class="relative h-20 w-20">
				<!-- static dial -->
				<div class="absolute inset-0 rounded-full border border-white/15"></div>
				<!-- the spinning arc -->
				<div class="spinner absolute inset-0 rounded-full border-2 border-transparent"></div>
				<!-- a pulsing core, echoing the hub ring on the globe itself -->
				<div class="core absolute inset-[42%] rounded-full bg-[#64ffda]"></div>
			</div>
			<p class="text-xs tracking-[0.2em] text-white/70 uppercase">
				{phase === 'downloading' ? 'Loading map data' : 'Building globe'}
			</p>
		</div>
	{/if}

	<!-- minimal caption, in place of the old page header -->
	<div class="pointer-events-none absolute bottom-6 left-6 z-40 text-white/70">
		<p class="text-sm font-semibold tracking-wide text-white">Hamburg Airport (HAM) network</p>
		<p class="text-xs">
			Total connections: {connectionsCount}, Total flights: {flightsCount}
		</p>
		<p class="text-[11px] text-white/40">Last updated: {formatDate(importedAt)}</p>
	</div>

	{#if active}
		<div
			class="absolute top-6 right-6 bg-[#112240]/85 backdrop-blur-md rounded-xl shadow-2xl p-4 text-sm z-50 max-w-[32rem] max-h-[60vh] overflow-y-auto border border-white/10 text-white flex flex-col"
		>
			<!-- Header row: left and right blocks -->
			<div class="flex items-start justify-between mb-3 gap-4">
				<!-- Left block: airport codes + city/country -->
				<div class="flex flex-col">
					<div class="flex items-center gap-1 font-bold">
						<span>{active.connection.hamAirport.airportCode}</span>
						<span>→</span>
						<span>{active.connection.connectionAirport.airportCode}</span>
					</div>
					<div class="text-xs text-white/60 mt-1">
						{active.connection.connectionAirport.airportName},
						{active.connection.connectionAirport.countryCode}
					</div>
				</div>

				<!-- Right block: distance + flights -->
				<div class="flex flex-col text-xs text-white/60 text-right gap-1">
					<div>
						<span class="font-semibold text-white">{active.distance} km</span>
						<span class="ml-1">Distance</span>
					</div>
					<div>
						<span class="font-semibold text-white">{active.flightCount}</span>
						<span class="ml-1">Flights</span>
					</div>
				</div>
			</div>

			<!-- Airlines Table -->
			<div class="overflow-x-auto mb-3">
				<!-- no table-pin-rows: it forces a bg-base-100 (white) header row onto the dark card -->
				<table class="table table-xs w-full">
					<thead>
						<tr class="text-white/60 border-white/10">
							<th>Airline</th>
							<th>Flights</th>
						</tr>
					</thead>
					<tbody class="text-white/80">
						{#each airlineBreakdown(active) as [airline, count] (airline)}
							<tr class="border-white/10">
								<td>{airline}</td>
								<td>{count}</td>
							</tr>
						{/each}
					</tbody>
				</table>
			</div>

			<!-- Clear Selection Button -->
			<div class="mt-auto flex justify-end">
				<button
					class="btn btn-sm rounded-lg border-white/20 bg-white/10 text-white hover:bg-white/20 hover:text-white"
					onclick={() => {
						selected = null;
						hovered = null;
					}}
				>
					Close
				</button>
			</div>
		</div>
	{/if}
</div>

<style>
	/* Composited properties only — these keep running while the main thread is blocked. */
	.spinner {
		border-top-color: #64ffda;
		border-right-color: rgba(100, 255, 218, 0.35);
		animation: spin 1.1s linear infinite;
	}

	.core {
		animation: pulse 1.6s ease-in-out infinite;
	}

	.loading-overlay {
		animation: fade-in 0.2s ease-out;
	}

	@keyframes spin {
		to {
			transform: rotate(360deg);
		}
	}

	@keyframes pulse {
		0%,
		100% {
			transform: scale(0.7);
			opacity: 0.5;
		}
		50% {
			transform: scale(1.15);
			opacity: 1;
		}
	}

	@keyframes fade-in {
		from {
			opacity: 0;
		}
	}

	@media (prefers-reduced-motion: reduce) {
		.spinner,
		.core,
		.loading-overlay {
			animation: none;
		}
	}
</style>
