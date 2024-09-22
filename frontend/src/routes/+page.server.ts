import type { PageLoad } from './$types';
import type { GeolibInputCoordinates } from 'geolib/es/types';
import { getDistance, convertDistance } from 'geolib';
import type { Connection, ConnectionsResponse } from './global';

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
			apiUrl = 'http://flights-api:8080/connections';
		} else {
			console.log("Development run. Requesting localhost.")
			apiUrl = 'http://localhost:8080/connections';
		}

		const res = await fetch(apiUrl);
		const response: ConnectionsResponse = await res.json();
		arcData = computeArcData(response);
		labelData = computeLabelData(arcData);
		importedAt = response.importedAt;
	}

	return {
		props: {
			arcData: arcData,
			labelData: labelData,
			importedAt: importedAt
		}
	};
};

function computeArcData(response: ConnectionsResponse): GlobeData[] {
	return response.connections.map((connection: Connection) => {
		const from: GeolibInputCoordinates = {
			latitude: connection.hamAirport.latitude,
			longitude: connection.hamAirport.longitude
		};
		const to: GeolibInputCoordinates = {
			latitude: connection.connectionAirport.latitude,
			longitude: connection.connectionAirport.longitude
		};

		const distKm: number = Math.floor(convertDistance(getDistance(from, to), 'km'));
		const tmpGlobeData: GlobeData = {
			startName: connection.hamAirport.airportCode,
			startLat: connection.hamAirport.latitude,
			startLng: connection.hamAirport.longitude,
			endName: connection.connectionAirport.airportCode,
			endLat: connection.connectionAirport.latitude,
			endLng: connection.connectionAirport.longitude,
			color: [`rgba(0, 255, 0, 0.35)`, `rgba(255, 0, 0, 0.4)`],
			flightCount: connection.totalFlightCount,
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
