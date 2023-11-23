<script lang="ts">
	import { onMount } from 'svelte';
	import './styles.css';
  
	onMount(async () => {
		const Globe = await import('globe.gl');
		const N = 300;
		const gData = [...Array(N).keys()].map(() => ({
			lat: (Math.random() - 0.5) * 180,
			lng: (Math.random() - 0.5) * 360,
			size: Math.random() / 3,
			color: ['red', 'white', 'blue', 'green'][Math.round(Math.random() * 3)]
		}));

		const globeElement = document.getElementById('helloWorld') as HTMLElement;

		// //cdn.jsdelivr.net/npm/three-globe/example/img/earth-night.jpg
		// //unpkg.com/three-globe/example/img/earth-night.jpg
		// https://unpkg.com/three-globe/example/img/earth-blue-marble.jpg

		Globe.default()
			.globeImageUrl('https://unpkg.com/three-globe/example/img/earth-blue-marble.jpg')
			//.pointsData(gData)
			.backgroundImageUrl('https://unpkg.com/three-globe/example/img/night-sky.png')
			.pointAltitude('size')
			.atmosphereColor('lightskyblue')
			.atmosphereAltitude(.15)
			.pointColor('color')(globeElement);
	});
</script>

<div id="helloWorld"></div>
