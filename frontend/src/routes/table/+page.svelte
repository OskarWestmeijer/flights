<script lang="ts">
	import type { PageData } from './$types';
	import type { Connection } from './global';

	export let data: PageData;
	const importedAt: string = data.props.responseData.importedAt;
	const routes: Connection[] = data.props.responseData.connections;
	const connectionsCount = routes.length;
</script>

<div class="flex flex-col items-center text-center py-4">
	<p class="text-lg font-semibold">Todays Hamburg airport (HAM) connections</p>
	<p>Connections count: {connectionsCount}</p>
	<p class="text-sm text-gray-400">Updated at: {importedAt}</p>
</div>

<div class="container pb-8">
	<div class="overflow-x-auto">
		<table class="table">
			<thead class="">
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
</div>

<style>
	.container {
		display: flex;
		justify-content: center;
		align-items: center;
		margin: auto;
		width: 100%;
	}
</style>
