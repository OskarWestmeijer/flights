<script lang="ts">
	import { onMount } from 'svelte';
	import { getDistance, convertDistance } from 'geolib';
	import './styles.css';
	import type { GeolibInputCoordinates } from 'geolib/es/types';

	onMount(async () => {
		const Globe = await import('globe.gl');

		// belgrade
		const MAP_CENTER = { lat: 44.787197, lng: 20.457273, altitude: 1.25 };

		const ham: GeolibInputCoordinates = { latitude: 51.5103, longitude: 7.49347 };
		const hel: GeolibInputCoordinates = { latitude: 60.192059, longitude: 24.945831 };

		// Distance = Speed × Time
		const distM: number = getDistance(ham, hel);
		const distKm: number = Math.floor(convertDistance(distM, 'km'));

		const planeSpeedKmH = 840;
		const planeSpeedKmMs = planeSpeedKmH / 3.6e6;

		const flightDurationHours = distKm / planeSpeedKmH;
		const flightDurationMs = flightDurationHours * 60 * 60 * 1000;

		// Remaining Distance = Total Distance - (Speed of Airplane * Time Left)
		const oneHourMs = 60 * 60 * 1000;
		const arrivalTimeMs = Date.now() + oneHourMs + oneHourMs;
		const currentTimeMs = Date.now();
		const tackeOffTimeMs = arrivalTimeMs - flightDurationMs;

		if (arrivalTimeMs < currentTimeMs) {
			console.log('Plane has arrived');
		} else if (currentTimeMs < tackeOffTimeMs) {
			console.log('Plane still waiting for take off');
		} else {
			console.log('Plane in flight.');
			const remainingDistKm: number = calculateRemainingDistance();
			console.log('remainingDistKm: ' + remainingDistKm);
		}

		function calculateRemainingDistance() {
			const timeTillArrival = arrivalTimeMs - currentTimeMs;
			return distKm - planeSpeedKmMs * timeTillArrival;
		}

		// connect hamburg with helsinki
		const pathData: object[] = [
			[
				// Coordinates for Hamburg (ham)
				[53.551086, 9.993682, 0],
				// Coordinates for Helsinki (hel)
				[60.192059, 24.945831, 0]
			]
		];

		const arcsData: object[] = [
			{
				startName: 'HAM',
				startLat: 53.551086,
				startLng: 9.993682,
				endName: 'HEL',
				endLat: 60.192059,
				endLng: 24.945831,
				color: ['red', 'blue']
			}
		];

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
			// TODO: you know the plane location. display it on the arc.
			.arcDashLength(0.5)
			.arcDashGap(0.35)
			.arcLabel((arc) => `${arc.startName} - ${arc.endName} ` + distKm + ' km')
			//.arcDashAnimateTime(2000)
			(globeElement);
	});
</script>

<div id="helloWorld"></div>
