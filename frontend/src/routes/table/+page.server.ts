import type { PageLoad } from './$types';
import type { FlightRoute, FlightRoutesResponse } from './global';

export const load: PageLoad = async ({ fetch }) => {
	let apiUrl;
	if (process.env.NODE_ENV === 'production') {
		apiUrl = 'http://maps-api:8080/flight-routes';
	} else {
		console.log("Development run. Requesting localhost.")
		apiUrl = 'http://localhost:8080/flight-routes';
	}

	const res = await fetch(apiUrl);
	const response: FlightRoutesResponse = await res.json();

	const responseData = response;
	return {
		props: {
			responseData: responseData,
		}
	};
};
