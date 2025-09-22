<script lang="ts">
	import '../app.css';
	let { children } = $props();
</script>

<svelte:head>
	<link rel="icon" href="/favicon.ico" />
	<title>Flights</title>
</svelte:head>

<div class="flex flex-col min-h-screen bg-base-100">
	<!-- Navbar -->
	<div class="navbar bg-secondary text-white">
		<div class="navbar-start">
			<div class="mobile-hide">
				<a
					href="https://flights.oskar-westmeijer.com"
					class="btn btn-ghost rounded-lg text-xl font-bold">✈️ Flights</a
				>
			</div>
		</div>
		<div class="navbar-center">
			<a href="/" class="btn btn-ghost text-xl">🌍 Globe</a>
			<a href="/table" class="btn btn-ghost text-xl">📋 Table</a>
		</div>
		<div class="navbar-end">
			<div class="mobile-hide">
				<a
					href="https://github.com/OskarWestmeijer/flights"
					aria-label="Oskar Westmeijer Github flights repository"
				>
					<button class="btn rounded-lg">
						<img alt="Github logo" class="size-6" src="/github.svg" />
					</button>
				</a>
			</div>
		</div>
	</div>

	<!-- Main grows to push footer down -->
	<main class="flex-grow">
		{@render children?.()}
	</main>

	<!-- Footer -->
	<footer class="footer footer-center py-6">
		<a href="https://oskar-westmeijer.com" class="btn btn-ghost text-xl">
			Oskar Westmeijer 🐨 2025
		</a>
	</footer>
</div>
