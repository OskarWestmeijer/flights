import { test as base } from '@playwright/test';

// The umami script in app.html is deferred, so the browser holds back the load
// event until it is fetched. CI runners cannot reach that host, which makes
// page.goto() hang until the test times out. Analytics adds nothing to the e2e
// runs, so drop those requests before they are made.
export const test = base.extend({
	page: async ({ page }, use) => {
		await page.route('https://umami.oskar-westmeijer.com/**', (route) => route.abort());
		await use(page);
	}
});

export { expect } from '@playwright/test';
