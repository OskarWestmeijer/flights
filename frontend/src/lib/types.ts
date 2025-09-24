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

export interface ArcData {
	startName: string;
	startLat: string;
	startLng: string;
	endName: string;
	endLat: string;
	endLng: string;
	color: string[];
	flightCount: number;
	distance: number;
	stroke: number;
}

export interface LabelData {
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

export interface GlobeDataTuple {
	arcData: ArcData[];
	labelData: LabelData[];
	connectionsCount: number;
	flightsCount: number;
	apiImportedAt: string;
}
