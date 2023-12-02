import type { PageLoad } from './$types';

export const load: PageLoad = ({ params }) => {
	return {
		routes: {
			id: 1,
			from: {
				aiportCode: 'HEL',
				latitude: '60.3172',
				longitude: '24.9633'
			},
			to: {
				aiportCode: 'HAM',
				latitude: '53.6304',
				longitude: '9.98823'
			}
		}
	};
};
