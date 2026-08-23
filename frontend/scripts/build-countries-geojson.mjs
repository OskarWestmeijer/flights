#!/usr/bin/env node
/**
 * Regenerates static/ne_50m_countries.geojson — the land outlines the /network globe
 * turns into dots.
 *
 * Natural Earth 1:50m is used rather than 1:110m because at hexPolygonResolution(4) the
 * H3 cells are ~22km and the 110m outlines are too generalized to shape a coastline.
 * The raw file is 3MB, so it is reduced here:
 *
 *   1. properties dropped entirely (Antarctica is filtered out at build time instead)
 *   2. Douglas-Peucker simplified to 0.05deg (~5km) — well under the 22km cell size,
 *      so it is invisible in the render but removes ~85% of the vertices
 *   3. coordinates rounded to 2dp (~1.1km)
 *
 * Result: ~139KB gzipped, no larger than the 1:110m file it replaced, but with 1:50m shape.
 *
 * Usage: node scripts/build-countries-geojson.mjs
 */
import { writeFileSync } from 'node:fs';

const SRC =
	'https://raw.githubusercontent.com/nvkelso/natural-earth-vector/master/geojson/ne_50m_admin_0_countries.geojson';
const OUT = new URL('../static/ne_50m_countries.geojson', import.meta.url);
const TOLERANCE = 0.05; // degrees
const PRECISION = 100; // 2dp

const round = (n) => Math.round(n * PRECISION) / PRECISION;

function sqSegDist(p, a, b) {
	let x = a[0];
	let y = a[1];
	let dx = b[0] - x;
	let dy = b[1] - y;
	if (dx || dy) {
		const t = ((p[0] - x) * dx + (p[1] - y) * dy) / (dx * dx + dy * dy);
		if (t > 1) {
			x = b[0];
			y = b[1];
		} else if (t > 0) {
			x += dx * t;
			y += dy * t;
		}
	}
	dx = p[0] - x;
	dy = p[1] - y;
	return dx * dx + dy * dy;
}

/** Iterative Douglas-Peucker — recursion blows the stack on the larger rings. */
function simplify(points, tolerance) {
	if (points.length <= 3) return points;
	const sqTol = tolerance * tolerance;
	const keep = new Uint8Array(points.length);
	keep[0] = keep[points.length - 1] = 1;
	const stack = [[0, points.length - 1]];
	while (stack.length) {
		const [first, last] = stack.pop();
		let index = -1;
		let max = sqTol;
		for (let i = first + 1; i < last; i++) {
			const d = sqSegDist(points[i], points[first], points[last]);
			if (d > max) {
				max = d;
				index = i;
			}
		}
		if (index > 0) {
			keep[index] = 1;
			stack.push([first, index], [index, last]);
		}
	}
	return points.filter((_, i) => keep[i]);
}

function ring(points) {
	const simplified = simplify(points, TOLERANCE).map((p) => [round(p[0]), round(p[1])]);
	// rounding can collapse neighbours onto each other
	const out = [];
	for (const p of simplified) {
		const last = out.at(-1);
		if (!last || last[0] !== p[0] || last[1] !== p[1]) out.push(p);
	}
	if (out.length >= 3) {
		const [first, last] = [out[0], out.at(-1)];
		if (first[0] !== last[0] || first[1] !== last[1]) out.push([first[0], first[1]]);
	}
	return out;
}

const polygon = (rings) => rings.map(ring).filter((r) => r.length >= 4);

const geometry = (g) =>
	g.type === 'Polygon'
		? { type: 'Polygon', coordinates: polygon(g.coordinates) }
		: { type: 'MultiPolygon', coordinates: g.coordinates.map(polygon).filter((p) => p.length) };

const source = await fetch(SRC).then((r) => {
	if (!r.ok) throw new Error(`${SRC} -> ${r.status}`);
	return r.json();
});

const features = source.features
	.filter((f) => f.properties.ISO_A2 !== 'AQ') // Antarctica: distracting, and always edge-on
	.map((f) => ({ type: 'Feature', properties: {}, geometry: geometry(f.geometry) }))
	.filter((f) => f.geometry.coordinates.length);

writeFileSync(OUT, JSON.stringify({ type: 'FeatureCollection', features }));
console.log(`wrote ${features.length} features to ${OUT.pathname}`);
