<script lang="ts">
	import { onMount, onDestroy } from 'svelte';
	import type { ArcData, LabelData, GlobeDataTuple } from '$lib/types';
	import { createLogger } from '$lib/logger';

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

	const MAP_CENTER = { lat: 44.787197, lng: 20.457273, altitude: 0.9 };

	// States for hovered items
	let hoveredArc: ArcData | null = null;
	let hoveredLabel: LabelData | null = null;

	onMount(async () => {
		log('Mounting globe...');

		const GlobeClass = (await import('globe.gl')).default;

		// Create globe instance with animateIn disabled
		globeInstance = new GlobeClass(globeElement, {
			waitForGlobeReady: false,
			animateIn: false
		})
			.globeImageUrl('earth-night.jpg')
			.backgroundImageUrl('night-sky.png')
			.pointOfView(MAP_CENTER, 0.1)
			.arcsData(globeData)
			.arcStroke(d => d.stroke)
			.arcDashLength(1)
			.arcDashGap(0.8)
			.arcDashInitialGap(() => Math.random())
			.arcColor(d => d.color)
			.arcsTransitionDuration(0)
			.arcDashAnimateTime(8000)
			.labelsData(labelData)
			.labelLat('lat')
			.labelLng('lng')
			.labelText('name')
			.labelSize('size')
			.labelDotRadius('dotRadius')
			.labelColor('color')
			.labelResolution(2);

		globeInstance(globeElement);

		// Set up hover callbacks
		globeInstance.onArcHover((arc: ArcData | null) => {
			hoveredArc = arc;
			hoveredLabel = null; // reset label hover when hovering an arc
		});
		globeInstance.onLabelHover((label: LabelData | null) => {
			hoveredLabel = label;
			hoveredArc = null; // reset arc hover when hovering a label
		});

		// Resize observer
		resizeObserver = new ResizeObserver(() => {
			globeInstance.width(globeElement.clientWidth);
			globeInstance.height(globeElement.clientHeight);
			log('Resizing globe.');
		});
		resizeObserver.observe(globeElement);

		log('Globe mounted.');
	});

	onDestroy(() => {
		log('Destroying globe...');
		if (resizeObserver) resizeObserver.disconnect();
		if (globeInstance) globeInstance.renderer()?.dispose?.();
		if (globeElement) globeElement.innerHTML = '';
		log('Globe destroyed.');
	});
</script>

<div class="flex flex-col items-center text-center py-4">
	<p class="text-lg font-semibold">Todays Hamburg airport (HAM) connections</p>
	<p>Connections: {connectionsCount}, Flights: {flightsCount}</p>
	<p class="text-sm text-gray-400">Updated at: {importedAt}</p>
</div>

<div class="flex justify-center relative">
	<div class="w-full max-w-7xl h-[90vh] rounded-2xl shadow-xl bg-base-400 p-4 bg-primary relative overflow-hidden">
		<div bind:this={globeElement} class="w-full h-full"></div>

		<!-- Hover overlay -->
		{#if hoveredArc}
			<div class="absolute top-6 right-6 p-3 bg-white bg-opacity-90 rounded-lg shadow-lg text-sm z-50 max-w-[30%]">
				<p><strong>{hoveredArc.startName} → {hoveredArc.endName}</strong></p>
				<p>Flights: {hoveredArc.flightCount}</p>
				<p>Distance: {hoveredArc.distance} km</p>
			</div>
		{:else if hoveredLabel}
			<div class="absolute top-6 right-6 p-3 bg-white bg-opacity-90 rounded-lg shadow-lg text-sm z-50 max-w-[30%]">
				<p><strong>{hoveredLabel.name}</strong></p>
				<p>Flights: {hoveredLabel.flightCount}</p>
				<p>Distance: {hoveredLabel.distance} km</p>
			</div>
		{/if}
	</div>
</div>
