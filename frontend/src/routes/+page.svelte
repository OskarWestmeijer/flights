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

	let hoveredArc: ArcData | null = null;
	let selectedArc: ArcData | null = null;

	function clearSelection() {
		selectedArc = null;
		hoveredArc = null;
		if (globeInstance) globeInstance.arcsData(globeData); // redraw arcs
	}

	onMount(async () => {
		log('Mounting globe...');

		const GlobeClass = (await import('globe.gl')).default;

		globeInstance = new GlobeClass(globeElement, {
			waitForGlobeReady: false,
			animateIn: false
		})
			.globeImageUrl('earth-blue-marble.jpg')
			.backgroundImageUrl('night-sky.png')
			.pointOfView(MAP_CENTER, 0.1)
			.arcsData(globeData)
			.arcAltitude(0)
			.arcStroke((d) => {
				if (selectedArc) return d === selectedArc ? 0.5 : 0.1; // thick selected, slim others
				if (hoveredArc) return d === hoveredArc ? 0.5 : 0.1; // highlight hover
				return d.stroke;
			})
			.arcColor((d) => {
				if (selectedArc) return d === selectedArc ? d.color : '#888888';
				if (hoveredArc) return d === hoveredArc ? d.color : '#888888';
				return d.color;
			})
			.arcsTransitionDuration(0) // instant update, no animation
			.arcLabel(
				(arc) =>
					`${arc.startName} → ${arc.endName}<br>Flights: ${arc.flightCount}<br>Distance: ${arc.distance} km`
			)
			.labelsData(labelData)
			.labelLat('lat')
			.labelLng('lng')
			.labelText('name')
			.labelSize('size')
			.labelDotRadius('dotRadius')
			.labelColor('color')
			.labelResolution(2);

		globeInstance(globeElement);

		globeInstance.onArcHover((arc: ArcData | null) => {
			if (!selectedArc) hoveredArc = arc;
			globeInstance.arcsData(globeData);
		});

		globeInstance.onArcClick((arc: ArcData) => {
			selectedArc = arc;
			hoveredArc = null;
			globeInstance.arcsData(globeData);
		});

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
	<div
		class="w-full max-w-7xl h-[90vh] rounded-2xl shadow-xl bg-base-400 p-4 bg-primary relative overflow-hidden"
	>
		<div bind:this={globeElement} id="helloWorld" class="w-full h-full"></div>

		<!-- Hover / selected overlay -->
		{#if selectedArc || hoveredArc}
			<div
				class="absolute top-6 right-6 p-3 bg-white bg-opacity-90 rounded-lg shadow-lg text-sm z-50 max-w-[30%]"
			>
				<p>
					<strong
						>{(selectedArc || hoveredArc).startName} → {(selectedArc || hoveredArc).endName}</strong
					>
				</p>
				<p>Flights: {(selectedArc || hoveredArc).flightCount}</p>
				<p>Distance: {(selectedArc || hoveredArc).distance} km</p>
				{#if selectedArc}
					<button
						class="mt-2 px-2 py-1 bg-red-500 text-white rounded hover:bg-red-600"
						on:click={clearSelection}
					>
						Clear Selection
					</button>
				{/if}
			</div>
		{/if}
	</div>
</div>
