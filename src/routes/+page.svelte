<script lang="ts">
	import { onMount } from 'svelte';
	import './styles.css';

	onMount(async () => {
		const Globe = await import('globe.gl');

		// connect hamburg with helsinki
		const gData: object[] = [
			[
				// Coordinates for Hamburg (ham)
				[53.551086, 9.993682, 0],
				// Coordinates for Helsinki (hel)
				[60.192059, 24.945831, 0]
			]
		];
		console.log(gData);

		const globeElement = document.getElementById('helloWorld') as HTMLElement;

		// //cdn.jsdelivr.net/npm/three-globe/example/img/earth-night.jpg
		// //unpkg.com/three-globe/example/img/earth-night.jpg
		// https://unpkg.com/three-globe/example/img/earth-blue-marble.jpg

		Globe.default()
			.globeImageUrl('https://unpkg.com/three-globe/example/img/earth-blue-marble.jpg')
			//.pointsData(gData)
			.backgroundImageUrl('https://unpkg.com/three-globe/example/img/night-sky.png')
			.bumpImageUrl('//unpkg.com/three-globe/example/img/earth-topology.png')
			.pointOfView({lat: 53.551086, lng: 9.993682, altitude: 1},1)
			.pointAltitude('size')
			.pathsData(gData)
			.pathStroke(10)
			.pathColor(() => ['rgba(0,0,255,0.6)', 'rgba(255,0,0,0.6)'])
			.pathDashLength(0.01)
			.pathDashGap(0.004)
			.pathDashAnimateTime(100000)(globeElement);

		setTimeout(() => {
			Globe.default()
				.pathPointAlt((pnt) => pnt[2]) // set altitude accessor
				.pathTransitionDuration(4000);
		}, 6000);
	});
</script>

<div id="helloWorld"></div>
