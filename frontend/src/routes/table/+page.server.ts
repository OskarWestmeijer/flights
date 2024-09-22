import type { PageLoad } from './$types';
import type { ConnectionsResponse } from './global';

export const load: PageLoad = async ({ fetch }) => {
	let apiUrl;
	if (process.env.NODE_ENV === 'production') {
		apiUrl = 'http://flights-api:8080/connections';
	} else {
		console.log("Development run. Requesting localhost.")
		apiUrl = 'http://localhost:8080/connections';
	}

	const res = await fetch(apiUrl);
	const response: ConnectionsResponse = await res.json();

	const responseData = response;
	return {
		props: {
			responseData: responseData,
		}
	};
};
