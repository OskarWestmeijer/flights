// src/lib/api-connections-client.ts
import type { ConnectionsResponse } from '$lib/types';
import { dev } from '$app/environment';
import { createLogger } from '$lib/logger';

const log = createLogger('api-connections-client');

// cache variables
let cachedResponse: ConnectionsResponse | null = null;
let lastFetched: number | null = null;

// 10 minutes in milliseconds
const CACHE_TTL = 10 * 60 * 1000;

export async function fetchConnections(): Promise<ConnectionsResponse> {
	const now = Date.now();

	if (cachedResponse && lastFetched && now - lastFetched < CACHE_TTL) {
		log('Returning cached connections');
		return cachedResponse;
	}

	log('Cache empty or expired. Requesting backend for new connections.');

	let apiUrl: string;
	if (process.env.NODE_ENV === 'production') {
		apiUrl = 'http://flights-api:8080/connections';
	} else {
		log('Development run. Requesting localhost.');
		apiUrl = 'http://localhost:8080/connections';
	}

	const res = await fetch(apiUrl);
	if (!res.ok) throw new Error(`Failed to fetch connections: ${res.statusText}`);

	const data: ConnectionsResponse = await res.json();

	// update cache
	cachedResponse = data;
	lastFetched = now;

	return data;
}
