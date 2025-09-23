<script lang="ts">
	import { onMount } from 'svelte';
	import type { PageData } from './$types';
	import './styles.css';
	import type { GlobeInstance } from 'globe.gl';
	import { createLogger } from '$lib/logger';
	import type { ArcData, LabelData, GlobeDataTuple } from '$lib/types';

	const log = createLogger('globe.page');

	export let data: PageData;
	const globeDataTuple: GlobeDataTuple = data.props.globeDataTuple;
	const globeData: ArcData[] = globeDataTuple.arcData;
	const labelData: LabelData[] = globeDataTuple.labelData;
	const importedAt: string = globeDataTuple.apiImportedAt;
	const connectionsCount = globeDataTuple.connectionsCount;

	onMount(async () => {
		log('On mount start');
		const Globe = await import('globe.gl');

		// belgrade
		const MAP_CENTER = { lat: 44.787197, lng: 20.457273, altitude: 0.9 };

		const globeElement = document.getElementById('helloWorld') as HTMLElement;

		const instance: GlobeInstance = Globe.default()
			.globeImageUrl('earth-night.jpg')
			.backgroundImageUrl('night-sky.png')
			.pointOfView(MAP_CENTER, 0.1)
			.arcsData(globeData)
			.arcStroke((d) => d.stroke)
			.arcDashLength(1)
			.arcDashGap(0.8)
			.arcDashInitialGap(() => Math.random())
			.arcColor((d) => d.color)
			.arcsTransitionDuration(0)
			.arcDashAnimateTime(4000)
			.arcLabel(
				(arc) =>
					`${arc.startName} - ${arc.endName}<br>total flights: ${arc.flightCount}<br>distance: ${arc.distance} km`
			)

			// city labels
			.labelsData(labelData)
			.labelLat('lat')
			.labelLng('lng')
			.labelText('name')
			.labelSize('size')
			.labelDotRadius('dotRadius')
			.labelColor('color')
			.labelResolution(2);

		// init globe
		instance(globeElement);

		const resizeObserver = new ResizeObserver(() => {
			instance.width(globeElement.clientWidth);
			instance.height(globeElement.clientHeight);
			log('Resizing globe.');
		});
		resizeObserver.observe(globeElement);

		// example of programatic access
		// instance.onArcHover((hover) => console.log('hovering over ' + JSON.stringify(hover)));
		// instance.onLabelHover((label) => console.log(label));
		log('End of mount');
	});
</script>

<div class="flex flex-col items-center text-center py-4">
	<p class="text-lg font-semibold">Todays Hamburg airport (HAM) connections</p>
	<p>Connections count: {connectionsCount}</p>
	<p class="text-sm text-gray-400">Updated at: {importedAt}</p>
</div>

<div class="flex justify-center">
	<div class="w-full max-w-7xl h-[90vh] rounded-2xl shadow-xl bg-base-400 p-4 bg-primary">
		<div id="helloWorld" class="w-full h-full"></div>
	</div>
</div>
