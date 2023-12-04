import type { PageLoad } from './$types';

export const load: PageLoad = async ({ fetch }) => {
	const res = await fetch('https://api.maps.oskar-westmeijer.com/flight-routes');
	const data = await res.json();

	return data;
};
