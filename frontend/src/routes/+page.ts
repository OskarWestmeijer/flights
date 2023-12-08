import type { PageLoad } from './$types';

export const load: PageLoad = async ({ fetch }) => {

  let apiUrl;
  if (process.env.NODE_ENV === 'production') {
    apiUrl = 'https://api.maps.oskar-westmeijer.com/flight-routes';
  } else {
    apiUrl = 'http://localhost:8080/flight-routes';
  }

	const res = await fetch(apiUrl);
	const data = await res.json();

	return data;
};
