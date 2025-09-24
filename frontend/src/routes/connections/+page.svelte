<script lang="ts">
	import type { PageData } from './$types';
	import type { Connection, ConnectionsResponse } from '$lib/types';
	import { createLogger, formatPlannedTime, formatDate } from '$lib/logger';

	const log = createLogger('table.page');

	export let data: PageData;
	const connectionsResponse: ConnectionsResponse = data.props.responseData;
	const flightsCount: number = data.props.flightsCount;
	log('Received props');

	const importedAt: string = connectionsResponse.importedAt;
	const connections: Connection[] = connectionsResponse.connections;
	const connectionsCount = connections.length;

	let expandedRow: string | null = null; // track which row is expanded
	let searchTerm: string = ''; // search input binding

	// reactive filtered list
	$: filteredConnections = connections.filter((c) => {
		if (!searchTerm) return true;
		const term = searchTerm.toLowerCase();
		return (
			c.connectionAirport.airportName.toLowerCase().includes(term) ||
			c.connectionAirport.airportCode.toLowerCase().includes(term) ||
			c.connectionAirport.countryCode.toLowerCase().includes(term)
		);
	});
</script>

<div class="flex flex-col items-center text-center py-6 space-y-1">
	<h2 class="text-xl font-bold">
		Hamburg Airport (HAM) connections — {formatDate(importedAt)}
	</h2>
	<p class="text-base">Total connections: {connectionsCount}, Total flights: {flightsCount}</p>
	<p class="text-xs text-gray-400">Last updated: {importedAt}</p>
</div>

<!-- Search input -->
<div class="flex justify-center pb-4">
	<input
		type="text"
		placeholder="Search by airport, code, or country..."
		bind:value={searchTerm}
		class="input input-bordered w-full max-w-md"
	/>
</div>

<div class="w-full flex justify-center pb-8">
	<div class="overflow-x-auto max-w-5xl w-full">
		<table class="table table-zebra w-full">
			<thead>
				<tr>
					<th></th>
					<th>Connection airport</th>
					<th>Airport code</th>
					<th>Country Code</th>
					<th>Total flights</th>
				</tr>
			</thead>
			<tbody>
				{#each filteredConnections as route}
					<tr
						class="cursor-pointer hover:bg-gray-200 {expandedRow ===
						route.connectionAirport.airportCode
							? 'bg-gray-200'
							: ''}"
						on:click={() =>
							(expandedRow =
								expandedRow === route.connectionAirport.airportCode
									? null
									: route.connectionAirport.airportCode)}
					>
						<td class="w-6 text-center">
							{expandedRow === route.connectionAirport.airportCode ? '⯆' : '⯈'}
						</td>
						<td>{route.connectionAirport.airportName}</td>
						<td>{route.connectionAirport.airportCode}</td>
						<td>{route.connectionAirport.countryCode}</td>
						<td>{route.totalFlightCount}</td>
					</tr>

					<!-- start of arrival and departure subtable-->
					{#if expandedRow === route.connectionAirport.airportCode}
						<tr class="bg-gray-100">
							<td colspan="7" class="p-4 text-left">
								<div class="h-auto max-h-[40rem] overflow-y-auto relative">
									<table class="table table-xs table-pin-rows w-full">
										<thead class="sticky top-0 z-20 bg-base-100">
											<tr>
												<th>Flight number</th>
												<th>Airline</th>
												<th>Planned Time</th>
											</tr>
										</thead>
										<tbody>
											<tr class="bg-base-200 sticky top-[1.8rem] z-10">
												<td colspan="3" class="font-semibold text-sm">Departures 🛫</td>
											</tr>
											{#each route.flights
												.filter((f) => f.flightType === 'DEPARTURE_HAM')
												.sort((a, b) => a.plannedTime.localeCompare(b.plannedTime)) as flight}
												<tr class="bg-base-200 hover:!bg-gray-200 transition-colors">
													<td>{flight.flightNumber}</td>
													<td>{flight.airlineName}</td>
													<td>{formatPlannedTime(flight.plannedTime)}</td>
												</tr>
											{:else}
												<tr><td colspan="3" class="text-gray-500">No departing flights</td></tr>
											{/each}

											<tr class="bg-base-200 sticky top-[1.8rem] z-10">
												<td colspan="3" class="font-semibold text-sm">Arrivals 🛬</td>
											</tr>
											{#each route.flights
												.filter((f) => f.flightType === 'ARRIVAL_HAM')
												.sort((a, b) => a.plannedTime.localeCompare(b.plannedTime)) as flight}
												<tr class="bg-base-200 hover:!bg-gray-200 transition-colors">
													<td>{flight.flightNumber}</td>
													<td>{flight.airlineName}</td>
													<td>{formatPlannedTime(flight.plannedTime)}</td>
												</tr>
											{:else}
												<tr><td colspan="3" class="text-gray-500">No arriving flights</td></tr>
											{/each}
										</tbody>
									</table>
								</div>
							</td>
						</tr>
					{/if}
					<!-- end of arrival and departure subtable-->
				{:else}
					<tr>
						<td colspan="5" class="text-center text-gray-500">
							No connections match your search
						</td>
					</tr>
				{/each}
			</tbody>
		</table>
	</div>
</div>
