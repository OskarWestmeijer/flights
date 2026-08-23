/**
 * Globe palette.
 *
 * DaisyUI emits its theme tokens as oklch custom properties, so reading them back with
 * getComputedStyle is more trouble than it's worth. These hexes are duplicated from the
 * `mytheme` block in `src/app.css` — keep the two in sync by hand.
 */
export const GLOBE = {
	/** deeper sibling of --color-primary (#112240), so the land dots read against it */
	ocean: '#0b1b34',
	/** mid steel-blue continents; brighter than you'd expect because the res-4 dots are tiny */
	landDot: '#2e66a3',
	/** rim glow — blue reads as atmosphere; the accent mint renders as a green halo */
	atmosphere: '#5b8def',
	/** resting state: every arc drawn, dimmed */
	arcDim: 'rgba(100, 255, 218, 0.38)',
	/** hovered/selected arc */
	arcActive: '#64ffda',
	/** destination airports */
	dot: 'rgba(100, 255, 218, 0.85)',
	dotActive: '#ffffff',
	/** the HAM hub itself, distinct from the destinations */
	hub: '#ffffff'
} as const;

/** Panel background behind the globe — matches GLOBE.ocean. */
export const GLOBE_PANEL_BG = '#0b1b34';
