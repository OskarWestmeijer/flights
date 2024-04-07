<script lang="ts">
	import type { PageData } from './$types';
	import type { FlightRoute } from './global';

	export let data: PageData;
	const importedAt: string = data.props.responseData.importedAt;
	const routes: FlightRoute[] = data.props.responseData.flightRoutes;
	console.log(importedAt);
</script>

<div class="container pt-20 bg-primary">
	<div class="overflow-x-auto">
		<table class="table-xs text-white">
			<thead class="text-white">
				<tr>
					<th>Destination</th>
					<th>Departures today</th>
				</tr>
			</thead>
			<tbody>
				{#each routes as route}
					<tr>
						<td>{route.connectionAirport.airportCode}</td>
						<td>{route.flightCount}</td>
					</tr>
				{/each}
			</tbody>
		</table>
	</div>
</div>
<div id="connectionsCount" class="text-white">
	<p>HAM destinations count: {routes.length}</p>
	<p>Imported at: {importedAt}</p>
</div>

<style>
	.container {
		display: flex;
		justify-content: center;
		align-items: center;
	}

	#connectionsCount {
		position: fixed;
		bottom: 1%;
		left: 1%;
		z-index: 9998;
	}
</style>
