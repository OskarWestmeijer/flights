import type { PlaywrightTestConfig } from '@playwright/test';

const config: PlaywrightTestConfig = {
	webServer: {
		command: 'npm run build && npm run preview',
		port: 4173
	},
	testDir: 'playwright',
	projects: [
		{
			name: 'chromium',
			use: {
				browserName: 'chromium',
				headless: true // optional
			}
		}
	]
};

export default config;
