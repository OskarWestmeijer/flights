<script lang="ts">
	import type { PageData } from './$types';
	import type { Flight } from '$lib/types';
	import { FlightType } from '$lib/types';
	import { createLogger } from '$lib/logger';

	const log = createLogger('flights.page');

	export let data: PageData;

	const flightsCount: number = data.props.flightsCount;
	const importedAt: string = data.props.responseData.importedAt;
	const arrivalFlights: Flight[] = data.props.arrivalFlights;
	const departureFlights: Flight[] = data.props.departureFlights;

	let currentTab: FlightType = FlightType.ARRIVAL_HAM;

	function formatPlannedTime(time: string): string {
		const sanitized = time.replace(/\[.*\]/, '');
		const date = new Date(sanitized);

		return new Intl.DateTimeFormat('de-DE', {
			hour: '2-digit',
			minute: '2-digit'
		}).format(date);
	}

	function formatDate(time: string): string {
		const sanitized = time.replace(/\[.*\]/, '');
		const date = new Date(sanitized);

		return new Intl.DateTimeFormat('de-DE', {
			day: '2-digit',
			month: '2-digit',
			year: '2-digit'
		}).format(date);
	}

	// reactive sorted flights depending on the currentTab
	$: displayedFlights =
		currentTab === FlightType.ARRIVAL_HAM
			? [...arrivalFlights].sort((a, b) => a.plannedTime.localeCompare(b.plannedTime))
			: [...departureFlights].sort((a, b) => a.plannedTime.localeCompare(b.plannedTime));
</script>

<div class="flex flex-col items-center text-center py-6 space-y-1">
	<h2 class="text-xl font-bold">
		Hamburg Airport (HAM) flights — {formatDate(importedAt)}
	</h2>
	<p class="text-base">Total flights: {flightsCount}</p>
	<p class="text-xs text-gray-400">Last updated: {importedAt}</p>
</div>

<!-- Toggle Tabs -->
<div class="flex justify-center pb-6">
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
</div>
<!-- Flights Table -->
<div class="w-full flex justify-center">
	<div class="overflow-x-auto max-w-6xl w-full">
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
				{#each displayedFlights as flight}
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
						<td colspan="6" class="text-center text-gray-500"> No flights found for this type </td>
					</tr>
				{/each}
			</tbody>
		</table>
	</div>
</div>
