<script lang="ts">
	import type { PageData } from './$types';
	import type { Connection } from './global';

	export let data: PageData;
	const importedAt: string = data.props.responseData.importedAt;
	const routes: Connection[] = data.props.responseData.connections;
	const connectionsCount = routes.length;

	let expandedRow: string | null = null; // track which row is expanded

	function formatPlannedTime(time: string): string {
		// Remove the zone ID "[Europe/Berlin]" because JS Date can't parse it
		const sanitized = time.replace(/\[.*\]/, '');
		const date = new Date(sanitized);

		return new Intl.DateTimeFormat('de-DE', {
			hour: '2-digit',
			minute: '2-digit',
			day: '2-digit',
			month: '2-digit',
			year: '2-digit'
		}).format(date);
	}
</script>

<div class="flex flex-col items-center text-center py-4">
	<p class="text-lg font-semibold">Todays Hamburg airport (HAM) connections</p>
	<p>Connections count: {connectionsCount}</p>
	<p class="text-sm text-gray-400">Updated at: {importedAt}</p>
</div>

<div class="w-full flex justify-center pb-8">
	<div class="overflow-x-auto max-w-5xl w-full">
		<table class="table table-zebra w-full">
			<thead>
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
					<tr
						class="hover cursor-pointer"
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
						<td>{route.departureFlightCount}</td>
						<td>{route.arrivalFlightCount}</td>
						<td>{route.totalFlightCount}</td>
					</tr>

					<!-- start of arrival and departure subtable-->
					{#if expandedRow === route.connectionAirport.airportCode}
						<tr class="bg-gray-100">
							<td colspan="7" class="p-4 text-left">
								<div class="h-96 overflow-x-auto">
									<table class="table table-xs table-pin-rows w-full">
										<!-- global header (only once) -->
										<thead>
											<tr>
												<th>Flight number</th>
												<th>Airline</th>
												<th>Planned Time</th>
											</tr>
										</thead>

										<tbody>
											<!-- Departures heading -->
											<tr class="bg-base-200">
												<td colspan="3" class="font-semibold">Departures</td>
											</tr>
											{#each route.flights
												.filter((f) => f.flightType === 'DEPARTURE_HAM')
												.sort((a, b) => a.plannedTime.localeCompare(b.plannedTime)) as flight}
												<tr>
													<td>{flight.flightNumber}</td>
													<td>{flight.airlineName}</td>
													<td>{formatPlannedTime(flight.plannedTime)}</td>
												</tr>
											{:else}
												<tr><td colspan="3" class="text-gray-500">No departing flights</td></tr>
											{/each}

											<!-- Arrivals heading -->
											<tr class="bg-base-200">
												<td colspan="3" class="font-semibold">Arrivals</td>
											</tr>
											{#each route.flights
												.filter((f) => f.flightType === 'ARRIVAL_HAM')
												.sort((a, b) => a.plannedTime.localeCompare(b.plannedTime)) as flight}
												<tr>
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
				{/each}
			</tbody>
		</table>
	</div>
</div>
