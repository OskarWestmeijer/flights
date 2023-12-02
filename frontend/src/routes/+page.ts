import type { PageLoad } from './$types';

export const load: PageLoad = async ({ fetch }) => {
	const res = await fetch('http://localhost:8080/routes');
	const data = await res.json();

	/*
	data.routes.map();
	console.log(data.routes[0].id);
	console.log('fetched routes: ' + JSON.stringify(data));

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
    */
	return data;
};
