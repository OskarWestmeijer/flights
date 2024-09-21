<script lang="ts">
	import type { PageData } from './$types';
	import type { FlightRoute } from './global';

	export let data: PageData;
	const importedAt: string = data.props.responseData.importedAt;
	const routes: FlightRoute[] = data.props.responseData.flightRoutes;
	console.log(importedAt);
</script>

<div class="container pt-20 bg-primary pb-8">
	<div class="overflow-x-auto">
		<table class="table text-white">
			<thead class="text-white">
				<tr>
					<th>Connection airport</th>
					<th>Airport code</th>
					<th>Country Code</th>
					<th>Departures</th>
					<th>Arrivals</th>
					<th>Total flights</th>
				</tr>
			</thead>
			<tbody>
				{#each routes as route}
					<tr>
						<td>{route.connectionAirport.airportName}</td>
						<td>{route.connectionAirport.airportCode}</td>
						<td>{route.connectionAirport.countryCode}</td>
						<td>{route.departureFlightCount}</td>
						<td>{route.arrivalFlightCount}</td>
						<td>{route.totalFlightCount}</td>
					</tr>
				{/each}
			</tbody>
		</table>
	</div>
	<div id="connectionsCount" class="text-white">
		<p>HAM connections today: {routes.length}</p>
		<p>Imported at: {importedAt}</p>
	</div>
</div>

<style>
	.container {
		display: flex;
		justify-content: center;
		align-items: center;
		margin: auto;
		width: 100%;
	}

	#connectionsCount {
		position: fixed;
		bottom: 1%;
		left: 1%;
		z-index: 9998;
	}
</style>
