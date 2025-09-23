import type { PageLoad } from './$types';
import { fetchGlobeDataTuple } from '$lib/globe-data';
import { createLogger } from '$lib/logger';

const log = createLogger('globe.server');

export const load: PageLoad = async () => {
	const globeDataTuple = await fetchGlobeDataTuple();
	log('Passing props.');
	return {
		props: {
			arcData: globeDataTuple.arcData,
			labelData: globeDataTuple.labelData,
			importedAt: globeDataTuple.apiImportedAt
		}
	};
};
