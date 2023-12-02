<script lang="ts">
	import { onMount } from 'svelte';
	import { getDistance, convertDistance } from 'geolib';
	import './styles.css';
	import type { GeolibInputCoordinates } from 'geolib/es/types';
	import type { PageData } from './$types';

	export let data: PageData;

	onMount(async () => {
		const Globe = await import('globe.gl');

		// belgrade
		const MAP_CENTER = { lat: 44.787197, lng: 20.457273, altitude: 1.25 };

		const from: GeolibInputCoordinates = {
			latitude: data.routes.from.latitude,
			longitude: data.routes.from.longitude
		};
		const to: GeolibInputCoordinates = {
			latitude: data.routes.to.latitude,
			longitude: data.routes.to.longitude
		};

		const distKm: number = Math.floor(convertDistance(getDistance(from, to), 'km'));

		data.routes.from.aiportCode;
		const arcsData: object[] = [
			{
				startName: data.routes.from.aiportCode,
				startLat: data.routes.from.latitude,
				startLng: data.routes.from.longitude,
				endName: data.routes.to.aiportCode,
				endLat: data.routes.to.latitude,
				endLng: data.routes.to.longitude,
				color: ['green', 'green'],
				distance: distKm
			}
		];

		console.log(arcsData);

		const globeElement = document.getElementById('helloWorld') as HTMLElement;

		Globe.default()
			//.globeImageUrl('BlackMarble_2016_3km.jpg')
			.globeImageUrl('earth-night.jpg')
			.backgroundImageUrl('https://unpkg.com/three-globe/example/img/night-sky.png')
			.bumpImageUrl('//unpkg.com/three-globe/example/img/earth-topology.png')
			.pointOfView(MAP_CENTER, 0.1)
			.arcsData(arcsData)
			.arcStroke(0.5)
			.arcColor('color')
			.arcDashLength(1)
			.arcDashGap(0.35)
			.arcLabel((arc) => `${arc.startName} - ${arc.endName} ${arc.distance} ` + ' km')(
			//.arcDashAnimateTime(2000)
			globeElement
		);
	});
</script>

<div id="helloWorld"></div>
