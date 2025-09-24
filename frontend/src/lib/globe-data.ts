import { createLogger } from '$lib/logger';
import { getDistance, convertDistance } from 'geolib';
import type { GeolibInputCoordinates } from 'geolib/es/types';
import type {
	Connection,
	ConnectionsResponse,
	GlobeDataTuple,
	ArcData,
	LabelData
} from '$lib/types';
import { fetchConnections } from '$lib/api-connections-client';
import { getFlightsCount } from './flights';

const log = createLogger('globe-data');

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
	const arcData = computeArcData(response);
	const labelData = computeLabelData(response);
	const connectionsCount = response.connections.length;
	const flightsCount = getFlightsCount(response.connections);

	cachedTuple = {
		arcData,
		labelData,
		connectionsCount,
		flightsCount,
		apiImportedAt: response.importedAt
	};
	lastApiResponse = response;

	log('Return new computed cache.');
	return cachedTuple;
}

// --- helpers ---

function computeArcData(response: ConnectionsResponse): ArcData[] {
	return response.connections.map((connection: Connection) => {
		return {
			startLat: connection.hamAirport.latitude,
			startLng: connection.hamAirport.longitude,
			endLat: connection.connectionAirport.latitude,
			endLng: connection.connectionAirport.longitude,
			connection: connection,
			stroke: 0.1,
			color: [`rgba(255, 0, 0, 0.5)`, `rgba(255, 0, 0, 0.5)`]
		};
	});
}

function computeLabelData(response: ConnectionsResponse): LabelData[] {
	return response.connections.map((connection: Connection) => {
		const from: GeolibInputCoordinates = {
			latitude: connection.hamAirport.latitude,
			longitude: connection.hamAirport.longitude
		};
		const to: GeolibInputCoordinates = {
			latitude: connection.connectionAirport.latitude,
			longitude: connection.connectionAirport.longitude
		};

		const distKm: number = Math.floor(convertDistance(getDistance(from, to), 'km'));

		return {
			distance: distKm,
			connection: connection
		};
	});
}
