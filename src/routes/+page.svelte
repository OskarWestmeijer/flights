<script lang="ts">
	import { onMount } from 'svelte';
	import './styles.css';

	onMount(async () => {
		const Globe = await import('globe.gl');

		// belgrade
		const MAP_CENTER = { lat: 44.787197, lng: 20.457273, altitude: 1.25 };

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
			// ... (more objects representing different flight routes)
		];

		const globeElement = document.getElementById('helloWorld') as HTMLElement;

		// //cdn.jsdelivr.net/npm/three-globe/example/img/earth-night.jpg
		// //unpkg.com/three-globe/example/img/earth-night.jpg
		// https://unpkg.com/three-globe/example/img/earth-blue-marble.jpg

		/*Globe.default()
			.globeImageUrl('https://unpkg.com/three-globe/example/img/earth-blue-marble.jpg')
			.pointsData(gData)
			.backgroundImageUrl('https://unpkg.com/three-globe/example/img/night-sky.png')
			.bumpImageUrl('//unpkg.com/three-globe/example/img/earth-topology.png')
			.pointOfView({ lat: 53.551086, lng: 9.993682, altitude: 1 }, 0.1)
			.pointAltitude('size')
			.pathsData(pathData)
			.pathStroke(10)
			.pathColor(() => ['rgba(0,0,255,0.6)', 'rgba(255,0,0,0.6)'])
			.pathDashLength(0.01)
			.pathDashGap(0.004)
			.pathDashAnimateTime(100000)(globeElement);
	});*/

		Globe.default()
			.globeImageUrl('BlackMarble_2016_3km.jpg')
			//.globeImageUrl('earth-night.jpg')
			.backgroundImageUrl('https://unpkg.com/three-globe/example/img/night-sky.png')
			//.bumpImageUrl('//unpkg.com/three-globe/example/img/earth-topology.png')
			.pointOfView(MAP_CENTER, 0.1)
			.arcsData(arcsData)
			.arcStroke(0.5)
			.arcColor('color')
			.arcDashLength(1)
			.arcDashGap(0.35)
			.arcLabel((arc) => `${arc.startName} - ${arc.endName}`)
			.arcDashAnimateTime(2000)(globeElement);
	});
</script>

<div id="helloWorld"></div>
