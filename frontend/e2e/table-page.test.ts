import { expect, test } from '@playwright/test';

test('shows table with HEL entry', async ({ page }) => {
    await page.goto('/table');
    await expect(page.locator('text=Helsinki Airport (Helsinki-Vantaa Airport)	')).toBeVisible();
});
