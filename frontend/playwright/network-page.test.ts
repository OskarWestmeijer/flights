import { expect, test } from './fixtures';

test('Loads globe element', async ({ page }) => {
	await page.goto('/network');
	await expect(page.locator('#helloWorld')).toBeVisible();
});

test('Shows the globe caption', async ({ page }) => {
	await page.goto('/network');
	await expect(page.locator('text=Hamburg Airport (HAM) network')).toBeVisible();
	await expect(page.locator('text=Total connections: 69, Total flights: 355')).toBeVisible();
});

test('Renders the globe canvas', async ({ page }) => {
	await page.goto('/network');
	await expect(page.locator('#helloWorld canvas')).toBeVisible();
});
