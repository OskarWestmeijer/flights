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

	let hoveredLabel: string | null = null;
	let selectedLabel: string | null = null;
	let displayedArcs: ArcData[] = []; // arcs currently shown

	function updateArcs(labelName: string | null) {
		if (!labelName) {
			displayedArcs = [];
		} else {
			displayedArcs = globeData.filter(
				(arc) => arc.startName === 'HAM' && arc.endName === labelName
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
			.labelLat('lat')
			.labelLng('lng')
			.labelText('name')
			.labelSize('size')
			.labelIncludeDot(true)
			.labelDotRadius(0.5)
			.labelColor((d) => {
				if (selectedLabel === d.name) return '#f54242';
				if (hoveredLabel === d.name) return 'darkred';
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
			hoveredLabel = label ? label.name : null;
			updateArcs(hoveredLabel || selectedLabel);
			globeInstance.labelsData(labelData);
		});

		globeInstance.onLabelClick((label: LabelData) => {
			selectedLabel = label.name;
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

		<!-- Upper-right info popup -->
		{#if selectedLabel || hoveredLabel}
			<div
				class="absolute top-6 right-6 p-3 bg-white bg-opacity-90 rounded-lg shadow-lg text-sm z-50 max-w-[30%]"
			>
				<p><strong>{selectedLabel || hoveredLabel}</strong></p>
				<!-- You can add more info per label here, e.g., flight count -->
			</div>
		{/if}
	</div>
</div>
