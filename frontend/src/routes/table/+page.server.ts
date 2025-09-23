import type { PageLoad } from './$types';
import { fetchConnections } from '$lib/api-connections-client';
import { createLogger } from '$lib/logger';
import type { ConnectionsResponse } from './global';

const log = createLogger("table.server");

export const load: PageLoad = async () => {
	log("Initate fetch")
	const response: ConnectionsResponse = await fetchConnections();
	log("Pass response as props to page")
	return {
		props: {
			responseData: response,
		}
	};
};
