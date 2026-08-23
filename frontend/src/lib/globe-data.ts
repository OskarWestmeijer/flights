import { createLogger } from '$lib/logger';
import { getDistance, convertDistance } from 'geolib';
import type { GeolibInputCoordinates } from 'geolib/es/types';
import type { Connection, ConnectionsResponse, GlobeDataTuple, NetworkNode } from '$lib/types';
import { fetchConnections } from '$lib/api-connections-client';
import { getFlightsCount } from './flights';

const log = createLogger('globe-data');

// fallback when the API returns no connections at all — Hamburg Airport
const HAM_FALLBACK = { lat: 53.6304, lng: 9.9882 };

// cache of computed globe data
let cachedTuple: GlobeDataTuple | null = null;
// remember which API response it was computed from
let lastApiResponse: ConnectionsResponse | null = null;

export async function fetchGlobeDataTuple(): Promise<GlobeDataTuple> {
	// fetch (or get cached) API data
	const response = await fetchConnections();

	// only recompute if this API response is new
	if (cachedTuple && lastApiResponse === response) {
		log('Returning cached globe data (API unchanged)');
		return cachedTuple;
	}

	log('API changed or first run — computing globe data');
	const nodes = computeNodes(response);
	const ham = response.connections[0]?.hamAirport;

	cachedTuple = {
		nodes,
		hamLat: ham ? parseFloat(ham.latitude) : HAM_FALLBACK.lat,
		hamLng: ham ? parseFloat(ham.longitude) : HAM_FALLBACK.lng,
		connectionsCount: response.connections.length,
		flightsCount: getFlightsCount(response.connections),
		apiImportedAt: response.importedAt
	};
	lastApiResponse = response;

	log('Return new computed cache.');
	return cachedTuple;
}

// --- helpers ---

/**
 * One node per connection, carrying everything the globe needs.
 *
 * The API returns latitude/longitude as strings ("53.6304"); they are parsed to numbers
 * exactly once, here, so nothing downstream has to remember to.
 */
export function computeNodes(response: ConnectionsResponse): NetworkNode[] {
	return response.connections.map((connection: Connection) => {
		const from: GeolibInputCoordinates = {
			latitude: connection.hamAirport.latitude,
			longitude: connection.hamAirport.longitude
		};
		const to: GeolibInputCoordinates = {
			latitude: connection.connectionAirport.latitude,
			longitude: connection.connectionAirport.longitude
		};

		return {
			code: connection.connectionAirport.airportCode,
			lat: parseFloat(connection.connectionAirport.latitude),
			lng: parseFloat(connection.connectionAirport.longitude),
			distance: Math.floor(convertDistance(getDistance(from, to), 'km')),
			flightCount: connection.totalFlightCount,
			connection: connection
		};
	});
}
