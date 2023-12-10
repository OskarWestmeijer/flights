<script lang="ts">
	import { onMount } from 'svelte';
	import './styles.css';
	import type { PageData } from './$types';
	import type { GlobeInstance } from 'globe.gl';

	export let data: PageData;

	const globeData: GlobeData[] = data.props.arcData;
	const labelData: LabelData[] = data.props.labelData;

	onMount(async () => {
		const Globe = await import('globe.gl');

		// belgrade
		const MAP_CENTER = { lat: 44.787197, lng: 20.457273, altitude: 0.9 };

		const globeElement = document.getElementById('helloWorld') as HTMLElement;

		const instance: GlobeInstance = Globe.default()
			.globeImageUrl('earth-night.jpg')
			.backgroundImageUrl('night-sky.png')
			.bumpImageUrl('earth-topology.png')
			.pointOfView(MAP_CENTER, 0.1)
			.arcsData(globeData)
			.arcStroke((d) => d.stroke)
			.arcDashLength(1)
			.arcDashGap(0.8)
			.arcDashInitialGap(() => Math.random())
			.arcColor((d) => d.color)
			.arcsTransitionDuration(0)
			.arcDashAnimateTime(4000)
			.arcLabel((arc) => `${arc.startName} - ${arc.endName} ${arc.distance} ` + ' km')

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

		// example of programatic access
		// instance.onArcHover((hover) => console.log('hovering over ' + JSON.stringify(hover)));
		// instance.onLabelHover((label) => console.log(label));
	});
</script>

<div id="helloWorld"></div>
