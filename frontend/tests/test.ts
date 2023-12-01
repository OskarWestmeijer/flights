import { expect, test } from '@playwright/test';

test('index page loads globe', async ({ page }) => {
	await page.goto('/');
	await expect(page.locator('#helloWorld')).toBeVisible();
});
