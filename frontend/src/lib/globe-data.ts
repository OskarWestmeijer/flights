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
	const labelData = computeLabelData(arcData);
	const connectionsCount = response.connections.length;

	cachedTuple = {
		arcData,
		labelData,
		connectionsCount,
		apiImportedAt: response.importedAt
	};
	lastApiResponse = response;

	log('Return new computed cache.');
	return cachedTuple;
}

// --- helpers ---

function computeArcData(response: ConnectionsResponse): ArcData[] {
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

		const tmpGlobeData: ArcData = {
			startName: connection.hamAirport.airportCode,
			startLat: connection.hamAirport.latitude,
			startLng: connection.hamAirport.longitude,
			endName: connection.connectionAirport.airportCode,
			endLat: connection.connectionAirport.latitude,
			endLng: connection.connectionAirport.longitude,
			color: [`rgba(0, 255, 0, 0.35)`, `rgba(255, 0, 0, 0.4)`],
			flightCount: connection.totalFlightCount,
			stroke: 0,
			distance: distKm
		};

		return styleArc(tmpGlobeData);
	});
}

function computeLabelData(arcData: ArcData[]): LabelData[] {
	return arcData.map((d) => {
		const tmpLabelData: LabelData = {
			lat: d.endLat,
			lng: d.endLng,
			name: d.endName,
			size: 0.5,
			dotRadius: 0.2,
			color: 'rgba(255, 165, 0, 0.75)',
			resolution: 2,
			flightCount: d.flightCount,
			distance: d.distance
		};
		return styleLabel(tmpLabelData);
	});
}

function styleArc(arc: ArcData): ArcData {
	if (arc.flightCount > 10) {
		arc.stroke = 0.35;
		arc.color = [`rgba(0, 255, 0, 1)`, `rgba(255, 0, 0, 1)`];
	} else if (arc.flightCount > 5) {
		arc.stroke = 0.2;
		arc.color = [`rgba(0, 255, 0, 1)`, `rgba(255, 0, 0, 1)`];
	} else {
		arc.stroke = 0.1;
		arc.color = [`rgba(0, 255, 0, 1)`, `rgba(255, 0, 0, 1)`];
	}
	return arc;
}

function styleLabel(label: LabelData): LabelData {
	if (label.flightCount > 10) {
		label.dotRadius = 0.35;
		label.size = 0.5;
		label.color = 'rgba(255, 165, 0, 1)';
	} else if (label.flightCount > 5) {
		label.dotRadius = 0.25;
		label.size = 0.4;
		label.color = 'rgba(255, 165, 0, 1)';
	} else {
		label.dotRadius = 0.15;
		label.size = 0.3;
		label.color = 'rgba(255, 165, 0, 1)';
	}
	return label;
}
