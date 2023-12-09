<script lang="ts">
	import { onMount } from 'svelte';
	import { getDistance, convertDistance } from 'geolib';
	import './styles.css';
	import type { GeolibInputCoordinates } from 'geolib/es/types';
	import type { PageData } from './$types';
	import type { GlobeInstance } from 'globe.gl';

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
			distance: distKm,
			name: route.to.airportCode
		};
	});

	const labelsData: object[] = arcsData.map((arc) => {
		return { lat: arc.endLat, lng: arc.endLng, name: arc.endName };
	});

	onMount(async () => {
		const Globe = await import('globe.gl');

		// belgrade
		const MAP_CENTER = { lat: 44.787197, lng: 20.457273, altitude: 0.85 };

		const globeElement = document.getElementById('helloWorld') as HTMLElement;

		const instance: GlobeInstance = Globe.default()
			.globeImageUrl('earth-night.jpg')
			.backgroundImageUrl('night-sky.png')
			.bumpImageUrl('earth-topology.png')
			.pointOfView(MAP_CENTER, 0.1)
			.arcsData(arcsData)
			.arcStroke(0.15)
			.arcDashLength(1)
			.arcDashGap(0.8)
			.arcDashInitialGap(() => Math.random())
			.arcColor((d) => [`rgba(0, 255, 0, 0.35)`, `rgba(255, 0, 0, 0.4)`])
			.arcsTransitionDuration(0)
			.arcDashAnimateTime(4000)
			.arcLabel((arc) => `${arc.startName} - ${arc.endName} ${arc.distance} ` + ' km')

			// city labels
			.labelsData(labelsData)
			.labelLat('lat')
			.labelLng('lng')
			.labelText('name')
			.labelSize(0.5)
			.labelDotRadius(0.2)
			.labelColor(() => 'rgba(255, 165, 0, 0.75)')
			.labelResolution(2);

		// init globe
		instance(globeElement)

		// example of programatic access
		// instance.onArcHover((hover) => console.log('hovering over ' + JSON.stringify(hover)));
	});
</script>

<div id="helloWorld"></div>
