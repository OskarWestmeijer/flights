import type { PageServerLoad } from './$types';
import { fetchConnections } from '$lib/api-connections-client';
import { createLogger } from '$lib/logger';
import type { ConnectionsResponse } from '$lib/types';

const log = createLogger('table.server');

export const load: PageServerLoad = async () => {
	log('Initate fetch');
	const response: ConnectionsResponse = await fetchConnections();
	log('Pass response as props to page');
	return {
		props: {
			responseData: response
		}
	};
};
