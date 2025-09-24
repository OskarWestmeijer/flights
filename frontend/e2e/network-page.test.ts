import { expect, test } from '@playwright/test';

test('Loads globe element', async ({ page }) => {
	await page.goto('/network');
	await expect(page.locator('#helloWorld')).toBeVisible();
});

test('Shows page header', async ({ page }) => {
	await page.goto('/network');
	await expect(page.locator('text=Hamburg Airport (HAM) network — 24.09.25')).toBeVisible();
	await expect(page.locator('text=Total connections: 69, Total flights: 355')).toBeVisible();
});
