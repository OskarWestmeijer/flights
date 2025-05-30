import { expect, test } from '@playwright/test';

test('Loads globe element', async ({ page }) => {
	await page.goto('/');
	await expect(page.locator('#helloWorld')).toBeVisible();
});

test('Shows connections count', async ({ page }) => {
	await page.goto('/');
	await expect(page.locator('text=HAM connections today: 102')).toBeVisible();
});