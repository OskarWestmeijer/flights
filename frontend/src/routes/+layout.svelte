<script lang="ts">
	import '../app.css';
	import { page } from '$app/state';
	let { children } = $props();

	// /network is a full-bleed globe; a footer under it would just add a scrollbar.
	const fullBleed = $derived(page.url.pathname === '/network');
</script>

<svelte:head>
	<link rel="icon" href="/favicon.ico" />
	<link rel="prefetch" as="fetch" href="/ne_50m_countries.geojson" crossorigin="anonymous" />
</svelte:head>

<div class="flex flex-col min-h-screen bg-base-100">
	<!-- Navbar -->
	<div class="navbar bg-secondary text-white shadow-sm">
		<!-- START: left side - hamburger (mobile) + brand (desktop) -->
		<div class="navbar-start">
			<!-- hamburger: mobile only -->
			<div class="dropdown lg:hidden">
				<!-- using the same DaisyUI pattern you provided -->
				<div
					tabindex="0"
					role="button"
					class="btn btn-ghost btn-circle text-white hover:text-black"
					aria-label="Open menu"
				>
					☰
				</div>
				<ul
					class="menu menu-lg dropdown-content bg-base-100 rounded-box z-50 mt-3 w-80 p-2 shadow text-black"
				>
					<li><a href="/">🛫 Flights</a></li>
					<li><a href="/connections">↔️ Connections</a></li>
					<li><a href="/network">🌍 Network</a></li>
					<li>
						<a
							href="https://github.com/OskarWestmeijer/flights"
							target="_blank"
							rel="noopener noreferrer"
						>
							<img alt="Github logo" class="size-4" src="/github.svg" /> Project on Github
						</a>
					</li>
				</ul>
			</div>

			<!-- desktop: keep the HAM-airport link in the left area (unchanged desktop styling) -->
			<div class="hidden lg:flex">
				<a
					href="https://flights.oskar-westmeijer.com"
					class="btn btn-ghost rounded-lg text-xl font-bold text-white hover:text-black"
					>✈️ HAM-airport</a
				>
			</div>
		</div>

		<!-- CENTER: brand shown on mobile (centered), links shown on desktop (center) -->
		<div class="navbar-center">
			<!-- mobile: central HAM-airport -->
			<a
				href="https://flights.oskar-westmeijer.com"
				class="btn btn-ghost rounded-lg text-xl font-bold text-white hover:text-black lg:hidden"
			>
				✈️ HAM-airport
			</a>

			<!-- desktop: original center nav links -->
			<div class="hidden lg:flex gap-2">
				<a href="/" class="btn btn-ghost text-xl text-white hover:text-black">🛫 Flights</a>
				<a href="/connections" class="btn btn-ghost text-xl text-white hover:text-black"
					>↔️ Connections</a
				>
				<a href="/network" class="btn btn-ghost text-xl text-white hover:text-black">🌍 Network</a>
			</div>
		</div>

		<!-- END: right side - keep Github button on desktop only -->
		<div class="navbar-end">
			<div class="hidden lg:flex">
				<a
					href="https://github.com/OskarWestmeijer/flights"
					aria-label="Oskar Westmeijer Github flights repository"
					target="_blank"
					rel="noopener noreferrer"
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
	{#if !fullBleed}
		<footer class="footer-center footer py-6">
			<a href="https://oskar-westmeijer.com" class="text-base">
				Created by Oskar Westmeijer 🐨 2025
			</a>
		</footer>
	{/if}
</div>
