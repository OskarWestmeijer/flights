import { expect, test } from './fixtures';

test('Shows page header', async ({ page }) => {
	await page.goto('/connections');
	await expect(page.locator('text=Hamburg Airport (HAM) connections — 24.09.25')).toBeVisible();
	await expect(page.locator('text=Total connections: 69, Total flights: 355')).toBeVisible();
});

test('shows table with HEL entry', async ({ page }) => {
	await page.goto('/connections');
	await expect(page.locator('text=Helsinki Airport (Helsinki-Vantaa Airport)')).toBeVisible();
});
