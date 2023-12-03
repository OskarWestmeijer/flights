<script lang="ts">
	import { onMount } from 'svelte';
	import { getDistance, convertDistance } from 'geolib';
	import './styles.css';
	import type { GeolibInputCoordinates } from 'geolib/es/types';
	import type { PageData } from './$types';

	export let data: PageData;

	// map response in required globe arcs format
	const arcsData: object[] = data.flightRoutes.map((route: any) => {
		const from: GeolibInputCoordinates = {
			latitude: route.from.latitude,
			longitude: route.from.longitude
		};
		const to: GeolibInputCoordinates = {
			latitude: route.to.latitude,
			longitude: route.to.longitude
		};

		const distKm: number = Math.floor(convertDistance(getDistance(from, to), 'km'));
		return {
			startName: route.from.airportCode,
			startLat: route.from.latitude,
			startLng: route.from.longitude,
			endName: route.to.airportCode,
			endLat: route.to.latitude,
			endLng: route.to.longitude,
			color: ['green', 'green'],
			distance: distKm
		};
	});

	onMount(async () => {
		const Globe = await import('globe.gl');

		// belgrade
		const MAP_CENTER = { lat: 44.787197, lng: 20.457273, altitude: 1.25 };

		const globeElement = document.getElementById('helloWorld') as HTMLElement;

		Globe.default()
			//.globeImageUrl('BlackMarble_2016_3km.jpg')
			.globeImageUrl('earth-night.jpg')
			.backgroundImageUrl('https://unpkg.com/three-globe/example/img/night-sky.png')
			.bumpImageUrl('//unpkg.com/three-globe/example/img/earth-topology.png')
			.pointOfView(MAP_CENTER, 0.1)
			.arcsData(arcsData)
			.arcStroke(0.15)
			.arcDashLength(1)
			.arcDashGap(0.8)
			.arcDashInitialGap(() => Math.random())
			.arcDashAnimateTime(4000)
			.arcColor((d) => [`rgba(0, 255, 0, 0.4)`, `rgba(255, 0, 0, 0.4)`])
			.arcsTransitionDuration(0)
			.arcLabel((arc) => `${arc.startName} - ${arc.endName} ${arc.distance} ` + ' km')(
			globeElement
		);
	});
</script>

<div id="helloWorld"></div>
