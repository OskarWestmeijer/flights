<script lang="ts">
	import type { PageData } from './$types';
	import type { Flight } from '$lib/types';
	import { FlightType } from '$lib/types';
	import { createLogger, formatDate, formatPlannedTime } from '$lib/logger';

	const log = createLogger('flights.page');

	export let data: PageData;

	const connectionsCount: number = data.props.connectionsCount;
	const flightsCount: number = data.props.flightsCount;
	const importedAt: string = data.props.responseData.importedAt;
	const arrivalFlights: Flight[] = data.props.arrivalFlights;
	const departureFlights: Flight[] = data.props.departureFlights;

	let currentTab: FlightType = FlightType.ARRIVAL_HAM;
	let searchTerm: string = '';

	// reactive sorted flights depending on the currentTab
	$: displayedFlights =
		currentTab === FlightType.ARRIVAL_HAM
			? [...arrivalFlights].sort((a, b) => a.plannedTime.localeCompare(b.plannedTime))
			: [...departureFlights].sort((a, b) => a.plannedTime.localeCompare(b.plannedTime));

	// filter flights based on search term
	$: filteredFlights = displayedFlights.filter((flight) => {
		if (!searchTerm) return true;
		const term = searchTerm.toLowerCase();
		return (
			flight.plannedTime.includes(term) ||
			flight.flightNumber.toLowerCase().includes(term) ||
			flight.airlineName.toLowerCase().includes(term) ||
			flight.connectionAirport.airportName.toLowerCase().includes(term) ||
			flight.connectionAirport.airportCode.toLowerCase().includes(term) ||
			flight.connectionAirport.countryCode.toLowerCase().includes(term)
		);
	});
</script>

<div class="flex flex-col items-center text-center py-6 space-y-1">
	<h2 class="text-xl font-bold">
		Hamburg Airport (HAM) flights — {formatDate(importedAt)}
	</h2>
	<p class="text-base">Total connections: {connectionsCount}, Total flights: {flightsCount}</p>
	<p class="text-xs text-gray-400">Last updated: {importedAt}</p>
</div>

<!-- Toggle Tabs -->
<div class="flex flex-col items-center pb-6 space-y-3">
	<div class="tabs tabs-boxed">
		<button
			type="button"
			class="tab {currentTab === FlightType.ARRIVAL_HAM ? 'tab-active' : ''}"
			on:click={() => (currentTab = FlightType.ARRIVAL_HAM)}
		>
			🛬 Arrivals ({arrivalFlights.length})
		</button>
		<button
			type="button"
			class="tab {currentTab === FlightType.DEPARTURE_HAM ? 'tab-active' : ''}"
			on:click={() => (currentTab = FlightType.DEPARTURE_HAM)}
		>
			🛫 Departures ({departureFlights.length})
		</button>
	</div>

	<!-- Search input -->
	<input
		type="text"
		placeholder="Search flights (number, airline, airport...)"
		bind:value={searchTerm}
		class="input input-bordered w-full max-w-md"
	/>
</div>

<!-- Flights Table -->
<div class="w-full flex justify-center">
	<div class="overflow-x-auto max-w-6xl w-full max-h-[70vh] overflow-y-auto">
		<table class="table table-zebra table-xs table-pin-rows w-full">
			<thead class="bg-base-200">
				<tr>
					<th class="sticky top-0 z-10">Planned Time</th>
					<th class="sticky top-0 z-10">Flight #</th>
					<th class="sticky top-0 z-10">Airline</th>
					<th class="sticky top-0 z-10">Airport Name</th>
					<th class="sticky top-0 z-10">Airport Code</th>
					<th class="sticky top-0 z-10">Country</th>
				</tr>
			</thead>
			<tbody>
				{#each filteredFlights as flight}
					<tr class="hover:bg-gray-200 transition-colors">
						<td>{formatPlannedTime(flight.plannedTime)}</td>
						<td>{flight.flightNumber}</td>
						<td>{flight.airlineName}</td>
						<td>{flight.connectionAirport.airportName}</td>
						<td>{flight.connectionAirport.airportCode}</td>
						<td>{flight.connectionAirport.countryCode}</td>
					</tr>
				{:else}
					<tr>
						<td colspan="6" class="text-center text-gray-500"> No flights match your search </td>
					</tr>
				{/each}
			</tbody>
		</table>
	</div>
</div>
