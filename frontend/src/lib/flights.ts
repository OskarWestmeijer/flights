import type { Connection, Flight } from './types';

export function getFlights(connections: Connection[]): Flight[] {
	return connections.flatMap((connection) => connection.flights);
}

export function getFlightsCount(connections: Connection[]): number {
	return connections.reduce((sum, connection) => sum + connection.flights.length, 0);
}
