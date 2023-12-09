/** @type {import('tailwindcss').Config} */
export default {
	content: ['./src/**/*.{html,js,svelte,ts}'],
	theme: {
		extend: {}
	},
	daisyui: {
		themes: [
			{
				mytheme: {
					// Navy #0a192f
					// light navy #112240
					// lightest navy #233554
					// Green #64ffda
					primary: '#112240',
					secondary: '#233554',
					accent: '#64ffda',
					accentlight: '#64ffda',
					neutral: '#c5fff0',
					'base-100': '#ffffff'
				}
			}
		]
	},
	plugins: [require('daisyui')]
};
