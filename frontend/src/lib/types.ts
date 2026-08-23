// src/lib/types.ts

export interface Airport {
	airportCode: string;
	airportName: string;
	countryCode: string;
	latitude: string;
	longitude: string;
}

export interface Flight {
	flightType: FlightType;
	flightNumber: string;
	airlineName: string;
	plannedTime: string;
	connectionAirport: Airport;
}

export enum FlightType {
	ARRIVAL_HAM = 'ARRIVAL_HAM',
	DEPARTURE_HAM = 'DEPARTURE_HAM'
}

export interface Connection {
	hamAirport: Airport;
	connectionAirport: Airport;
	departureFlightCount: number;
	arrivalFlightCount: number;
	totalFlightCount: number;
	flights: Flight[];
}

export interface ConnectionsResponse {
	connections: Connection[];
	importedAt: string;
}

export interface NetworkNode {
	/** connectionAirport.airportCode — stable identity for hover/selection */
	code: string;
	/** Airport.latitude/longitude arrive from the API as strings; parsed once here */
	lat: number;
	lng: number;
	/** great-circle distance from HAM in km */
	distance: number;
	/** hoisted from connection.totalFlightCount for arc/point scaling */
	flightCount: number;
	connection: Connection;
}

export interface GlobeDataTuple {
	nodes: NetworkNode[];
	hamLat: number;
	hamLng: number;
	connectionsCount: number;
	flightsCount: number;
	apiImportedAt: string;
}
