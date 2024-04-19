import type { PageLoad } from './$types';
import type { GeolibInputCoordinates } from 'geolib/es/types';
import { getDistance, convertDistance } from 'geolib';
import type { FlightRoute, FlightRoutesResponse } from './global';

let importedAt;
let arcData: GlobeData[];
let labelData: LabelData[];

export const load: PageLoad = async ({ fetch }) => {
	const tenMinsAgoTs = new Date().getTime() - 10 * 60 * 1000;
	const importedAtTs = new Date(importedAt).getTime();
	console.log(tenMinsAgoTs);
	console.log(importedAtTs);
	if (arcData == null || labelData == null || importedAt == null || importedAtTs < tenMinsAgoTs) {
		let apiUrl;
		if (process.env.NODE_ENV === 'production') {
			apiUrl = 'https://maps-api:8080/flight-routes';
		} else {
			apiUrl = 'http://localhost:8080/flight-routes';
		}

		const res = await fetch(apiUrl);
		const response: FlightRoutesResponse = await res.json();
		arcData = computeArcData(response);
		labelData = computeLabelData(arcData);
		importedAt = response.importedAt;
	} else {
		// not updating data
	}

	return {
		props: {
			arcData: arcData,
			labelData: labelData,
			importedAt: importedAt
		}
	};
};

function computeArcData(response: FlightRoutesResponse): GlobeData[] {
	return response.flightRoutes.map((route: FlightRoute) => {
		const from: GeolibInputCoordinates = {
			latitude: route.hamAirport.latitude,
			longitude: route.hamAirport.longitude
		};
		const to: GeolibInputCoordinates = {
			latitude: route.connectionAirport.latitude,
			longitude: route.connectionAirport.longitude
		};

		const distKm: number = Math.floor(convertDistance(getDistance(from, to), 'km'));
		const tmpGlobeData: GlobeData = {
			startName: route.hamAirport.airportCode,
			startLat: route.hamAirport.latitude,
			startLng: route.hamAirport.longitude,
			endName: route.connectionAirport.airportCode,
			endLat: route.connectionAirport.latitude,
			endLng: route.connectionAirport.longitude,
			color: [`rgba(0, 255, 0, 0.35)`, `rgba(255, 0, 0, 0.4)`],
			flightCount: route.flightCount,
			stroke: 0,
			distance: distKm
		};

		return styleArc(tmpGlobeData);
	});
}

function computeLabelData(arcData: GlobeData[]): LabelData[] {
	return arcData.map((d) => {
		const tmpLabelData: LabelData = {
			lat: d.endLat,
			lng: d.endLng,
			name: d.endName,
			size: 0.5,
			dotRadius: 0.2,
			color: 'rgba(255, 165, 0, 0.75)',
			resolution: 2,
			flightCount: d.flightCount,
			distance: d.distance
		};
		return styleLabel(tmpLabelData);
	});
}

function styleArc(arc: GlobeData): GlobeData {
	if (arc.flightCount > 10) {
		arc.stroke = 0.35;
		arc.color = [`rgba(0, 255, 0, 1)`, `rgba(255, 0, 0, 1)`];
	} else if (arc.flightCount > 5) {
		arc.stroke = 0.2;
		arc.color = [`rgba(0, 255, 0, 1)`, `rgba(255, 0, 0, 1)`];
	} else {
		arc.stroke = 0.1;
		arc.color = [`rgba(0, 255, 0, 1)`, `rgba(255, 0, 0, 1)`];
	}

	return arc;
}

function styleLabel(label: LabelData): LabelData {
	if (label.flightCount > 10) {
		label.dotRadius = 0.35;
		label.size = 0.5;
		label.color = 'rgba(255, 165, 0, 1)';
	} else if (label.flightCount > 5) {
		label.dotRadius = 0.25;
		label.size = 0.4;
		label.color = 'rgba(255, 165, 0, 1)';
	} else {
		label.dotRadius = 0.15;
		label.size = 0.3;
		label.color = 'rgba(255, 165, 0, 1)';
	}

	return label;
}
