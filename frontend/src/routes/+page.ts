import type { PageLoad } from './$types';

export const load: PageLoad = async ({ fetch }) => {
	const res = await fetch('http://localhost:8080/flight-routes');
	const data = await res.json();

	return data;
};
