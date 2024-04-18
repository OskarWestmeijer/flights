import type { PageLoad } from './$types';
import type { FlightRoute, FlightRoutesResponse } from './global';

export const load: PageLoad = async ({ fetch }) => {
	let apiUrl;
	if (process.env.NODE_ENV === 'production') {
		apiUrl = 'https://api.maps.oskar-westmeijer.com/flight-routes';
	} else {
		apiUrl = 'http://localhost:8080/flight-routes';
	}

	const res = await fetch(apiUrl);
	const response: FlightRoutesResponse = await res.json();
	//console.log(response);
	console.log('done');

	const responseData = response;
	return {
		props: {
			responseData: responseData,
		}
	};
};
