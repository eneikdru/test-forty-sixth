import { test, expect } from '@playwright/test';
import AxeBuilder from '@axe-core/playwright';

test.describe('Active Modules Scroll Indicators QA & Accessibility', () => {
  test('Given active modules on non-touch desktop, explicit scroll indicators and buttons are visible and accessible', async ({ page }) => {
    // Navigate to QuantumCore Home page using search param ?view=home
    await page.goto('/?view=home');

    // Wait for the QuantumCore container to mount
    await expect(page.locator('h1')).toContainText('QuantumCore');

    // Verify active modules region
    const region = page.locator('section[aria-label="Active Modules Region"]');
    await expect(region).toBeVisible();

    // Verify indicator badge is visible on non-touch device
    const scrollBadge = page.locator('.scroll-indicator-badge');
    await expect(scrollBadge).toBeVisible();
    await expect(scrollBadge).toHaveText('Scroll Region');

    // Verify left and right scroll buttons exist
    const scrollLeftBtn = page.getByRole('button', { name: 'Scroll left in active modules' });
    const scrollRightBtn = page.getByRole('button', { name: 'Scroll right in active modules' });

    await expect(scrollRightBtn).toBeVisible();
    await expect(scrollRightBtn).toBeEnabled();

    // Scroll right via button click
    await scrollRightBtn.click();
    await page.waitForTimeout(300);

    // Left button should now be enabled
    await expect(scrollLeftBtn).toBeVisible();
    await expect(scrollLeftBtn).toBeEnabled();

    // Scrollable region is visible
    const scrollableList = page.getByRole('region', { name: 'Active modules scrollable list' });
    await expect(scrollableList).toBeVisible();

    // Run Axe accessibility scan for contrast and WCAG compliance
    const accessibilityScanResults = await new AxeBuilder({ page })
      .include('section[aria-label="Active Modules Region"]')
      .analyze();

    const violations = accessibilityScanResults.violations;
    expect(violations, `Accessibility violations found: ${JSON.stringify(violations, null, 2)}`).toEqual([]);
  });
});
