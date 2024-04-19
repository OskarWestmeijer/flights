declare global {
	interface Airport {
		airportCode: string;
		airportName: string;
		countryCode: string;
		latitude: string;
		longitude: string;
	}

	interface FlightRoute {
		hamAirport: Airport;
		connectionAirport: Airport;
		flightCount: number;
	}

	interface FlightRoutesResponse {
		flightRoutes: FlightRoute[];
		importedAt: string;
	}

	interface ArcData {
		startName: string;
		startLat: string;
		startLng: string;
		endName: string;
		endLat: string;
		endLng: string;
		color: string[];
		flightCount: number;
		distance: number;
	}

	interface LabelData {
		lat: string;
		lng: string;
		name: string;
		size: number;
		dotRadius: number;
		color: string;
		resolution: number;
		flightCount: number;
		distance: number;
	}
}
