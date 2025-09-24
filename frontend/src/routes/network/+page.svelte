<script lang="ts">
	import { onMount, onDestroy } from 'svelte';
	import type { ArcData, LabelData, GlobeDataTuple } from '$lib/types';
	import { createLogger, formatDate } from '$lib/logger';

	const log = createLogger('globe.page');

	export let data: { props: { globeDataTuple: GlobeDataTuple } };

	let globeElement: HTMLElement;
	let globeInstance: any = null;
	let resizeObserver: ResizeObserver;

	const globeDataTuple: GlobeDataTuple = data.props.globeDataTuple;
	const globeData: ArcData[] = globeDataTuple.arcData;
	const labelData: LabelData[] = globeDataTuple.labelData;
	const importedAt: string = globeDataTuple.apiImportedAt;
	const connectionsCount: number = globeDataTuple.connectionsCount;
	const flightsCount: number = globeDataTuple.flightsCount;

	const MAP_CENTER = { lat: 53.6304, lng: 9.9882, altitude: 0.6 }; // HAM

	let hoveredLabel: LabelData | null = null;
	let selectedLabel: LabelData | null = null;
	let displayedArcs: ArcData[] = []; // arcs currently shown

	function updateArcs(label: LabelData | null) {
		if (!label) {
			displayedArcs = [];
		} else {
			displayedArcs = globeData.filter(
				(arc) =>
					arc.connection.hamAirport.airportCode === 'HAM' &&
					arc.connection.connectionAirport.airportCode ===
						label.connection.connectionAirport.airportCode
			);
		}

		if (globeInstance) globeInstance.arcsData(displayedArcs);
	}

	onMount(async () => {
		log('Mounting globe...');
		const THREE = await import('https://esm.sh/three');

		const countries: any[] = await fetch('/ne_110m_admin_0_countries.geojson')
			.then((res) => res.json())
			.then((data) => data.features);

		// rings
		const rings = [
			{
				lat: MAP_CENTER.lat,
				lng: MAP_CENTER.lng,
				maxR: 0.9, // max radius of ring
				propagationSpeed: 0.25, // speed of expansion
				repeatPeriod: 1500 // ms for pulsing loop
			}
		];

		// material
		const mat = new THREE.MeshPhongMaterial({
			color: '#5DADE2',
			shininess: 15,
			specular: new THREE.Color('#888888'),
			transparent: false,
			opacity: 1
		});

		const GlobeClass = (await import('globe.gl')).default;

		globeInstance = new GlobeClass(globeElement, {
			waitForGlobeReady: false,
			animateIn: false
		})
			.backgroundColor('black')
			.pointOfView(MAP_CENTER, 0.1)
			.globeMaterial(mat)

			.polygonsData(countries.filter((d) => d.properties.ISO_A2 !== 'AQ'))
			.polygonCapColor(() => '#f5f5f5')
			.polygonSideColor(() => '#c8c8c8')
			.polygonStrokeColor(() => '#aaa')
			.polygonAltitude(0.007)
			.polygonsTransitionDuration(0)

			.arcsData(displayedArcs) // initially empty
			.arcAltitude(0.05)
			.arcStartAltitude(0.008)
			.arcEndAltitude(0.008)
			.arcColor(() => 'red')
			.arcStroke(0.1)
			.arcsTransitionDuration(0)

			.labelsData(labelData)
			.labelLat((d: any) => d.connection.connectionAirport.latitude)
			.labelLng((d: any) => d.connection.connectionAirport.longitude)
			.labelText((d: any) => d.connection.connectionAirport.airportCode)
			.labelSize(0.25)
			.labelIncludeDot(true)
			.labelDotRadius(0.3)
			.labelColor((label: any) => {
				if (selectedLabel === label) return '#f54242';
				if (hoveredLabel === label) return 'darkred';
				return 'darkblue';
			})
			.labelResolution(2)
			.labelAltitude(0.01)

			// rings
			.ringsData(rings)
			.ringAltitude(0.008) // slightly above globe surface
			.ringColor(() => '#f54242')
			.ringResolution(100)
			.ringMaxRadius('maxR')
			.ringPropagationSpeed('propagationSpeed')
			.ringRepeatPeriod('repeatPeriod');

		// Hover and click for labels
		globeInstance.onLabelHover((label: LabelData | null) => {
			hoveredLabel = label ? label : null;
			updateArcs(hoveredLabel || selectedLabel);
			globeInstance.labelsData(labelData);
		});

		globeInstance.onLabelClick((label: LabelData) => {
			selectedLabel = label;
			updateArcs(selectedLabel);
			globeInstance.labelsData(labelData);
		});

		// resize
		resizeObserver = new ResizeObserver(() => {
			globeInstance.width(globeElement.clientWidth);
			globeInstance.height(globeElement.clientHeight);
		});
		resizeObserver.observe(globeElement);

		log('Globe mounted.');
	});

	onDestroy(() => {
		log('Destroying globe...');

		if (resizeObserver) {
			resizeObserver.disconnect();
		}

		if (globeInstance) {
			globeInstance._destructor();
		}

		log('Globe destroyed.');
	});
</script>

<div class="flex flex-col items-center text-center py-6 space-y-1">
	<h2 class="text-xl font-bold">
		Hamburg Airport (HAM) network — {formatDate(importedAt)}
	</h2>
	<p class="text-base">Total connections: {connectionsCount}, Total flights: {flightsCount}</p>
	<p class="text-xs text-gray-400">Last updated: {importedAt}</p>
</div>

<div class="flex justify-center relative">
	<div
		class="w-full max-w-7xl h-[90vh] rounded-2xl shadow-xl bg-white p-4 relative overflow-hidden"
	>
		<div bind:this={globeElement} class="w-full h-full" id="helloWorld"></div>

		{#if selectedLabel || hoveredLabel}
			<div
				class="absolute top-6 right-6 bg-white bg-opacity-95 rounded-xl shadow-2xl p-4 text-sm z-50 max-w-[32rem] max-h-[60vh] overflow-y-auto border border-gray-200 flex flex-col"
			>
				<!-- Header row: left and right blocks -->
				<div class="flex items-start justify-between mb-3 gap-4">
					<!-- Left block: airport codes + city/country -->
					<div class="flex flex-col">
						<div class="flex items-center gap-1 font-bold text-gray-800">
							<span
								>{selectedLabel?.connection.hamAirport.airportCode ||
									hoveredLabel?.connection.hamAirport.airportCode}</span
							>
							<span>→</span>
							<span
								>{selectedLabel?.connection.connectionAirport.airportCode ||
									hoveredLabel?.connection.connectionAirport.airportCode}</span
							>
						</div>
						<div class="text-xs text-gray-500 mt-1">
							{selectedLabel?.connection.connectionAirport.airportName ||
								hoveredLabel?.connection.connectionAirport.airportName},
							{selectedLabel?.connection.connectionAirport.countryCode ||
								hoveredLabel?.connection.connectionAirport.countryCode}
						</div>
					</div>

					<!-- Right block: distance + flights -->
					<div class="flex flex-col text-xs text-gray-500 text-right gap-1">
						<div>
							<span class="font-semibold text-gray-800"
								>{selectedLabel?.distance || hoveredLabel?.distance} km</span
							>
							<span class="ml-1">Distance</span>
						</div>
						<div>
							<span class="font-semibold text-gray-800"
								>{selectedLabel?.connection.totalFlightCount ||
									hoveredLabel?.connection.totalFlightCount}</span
							>
							<span class="ml-1">Flights</span>
						</div>
					</div>
				</div>

				<!-- Airlines Table -->
				<div class="overflow-x-auto mb-3">
					<table class="table table-xs table-pin-rows w-full">
						<thead>
							<tr>
								<th>Airline</th>
								<th>Flights</th>
							</tr>
						</thead>
						<tbody>
							{#each Object.entries((selectedLabel?.connection.flights || hoveredLabel?.connection.flights).reduce( (acc, flight) => {
										acc[flight.airlineName] = (acc[flight.airlineName] || 0) + 1;
										return acc;
									}, {} )).sort(([, aCount], [, bCount]) => bCount - aCount) as [airline, count]}
								<tr>
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
						class="btn rounded-lg bg-gray-200 hover:bg-gray-300 text-gray-700"
						on:click={() => {
							selectedLabel = null;
							hoveredLabel = null;
							displayedArcs = [];
							if (globeInstance) globeInstance.arcsData(displayedArcs).labelsData(labelData);
						}}
					>
						Close
					</button>
				</div>
			</div>
		{/if}
	</div>
</div>
