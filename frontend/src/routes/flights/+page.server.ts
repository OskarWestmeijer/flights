import type { PageServerLoad } from './$types';
import { fetchConnections } from '$lib/api-connections-client';
import { createLogger } from '$lib/logger';
import { FlightType, type ConnectionsResponse, type Flight, type Connection } from '$lib/types';
import { getFlightsCount, getFlightsByType } from '$lib/flights';

const log = createLogger('flights.server');

export const load: PageServerLoad = async () => {
	const response: ConnectionsResponse = await fetchConnections();
	const connections: Connection[] = response.connections;
	const flightsCount: number = getFlightsCount(connections);
	const connectionsCount: number = connections.length
	const arrivalFlights: Flight[] = getFlightsByType(connections, FlightType.ARRIVAL_HAM);
	const departureFlights: Flight[] = getFlightsByType(connections, FlightType.DEPARTURE_HAM);
	return {
		props: {
			responseData: response,
			flightsCount: flightsCount,
			connectionsCount: connectionsCount,
			arrivalFlights: arrivalFlights,
			departureFlights: departureFlights
		}
	};
};
