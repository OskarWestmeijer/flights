import type { PageServerLoad } from './$types';
import { fetchGlobeDataTuple } from '$lib/globe-data';
import { createLogger } from '$lib/logger';

const log = createLogger('globe.server');

export const load: PageServerLoad = async () => {
	const globeDataTuple = await fetchGlobeDataTuple();
	log('Passing props.');

	return {
		arcData: globeDataTuple.arcData,
		labelData: globeDataTuple.labelData,
		importedAt: globeDataTuple.apiImportedAt
	};
};
