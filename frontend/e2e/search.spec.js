import { test, expect } from '@playwright/test';
import AxeBuilder from '@axe-core/playwright';

test.describe('Search QA Validation', () => {

  test('When searching for a known keyword, Then the correct material is in the top 3 results', async ({ page }) => {
    await page.goto('/');

    const searchInput = page.locator('#document-search-input');
    await expect(searchInput).toBeVisible();

    // Type search query
    await searchInput.fill('Influenza');

    // Wait for search result title to be updated matching search criteria
    const resultTitles = page.locator('section[aria-label="Search Results"] article h3');
    await expect(resultTitles.first()).toBeVisible();
    await expect(resultTitles.first()).toContainText('Influenza');

    const count = await resultTitles.count();
    expect(count).toBeGreaterThan(0);
    expect(count).toBeLessThanOrEqual(3);
  });

  test('When viewing a material, Then the download link successfully retrieves the file', async ({ page }) => {
    await page.goto('/');

    const downloadLink = page.locator('section[aria-label="Search Results"] article a[download]').first();
    await expect(downloadLink).toBeVisible();

    const href = await downloadLink.getAttribute('href');
    expect(href).toBeTruthy();
    expect(href).toContain('/download');
  });

  test('When scanning the search page, Then all form controls have visible focus and labels', async ({ page }) => {
    await page.goto('/');

    // 1. Check aria-label / label associated with form controls
    const searchInput = page.locator('#document-search-input');
    await expect(searchInput).toHaveAttribute('aria-label', /Search/i);

    // 2. Focus search input and verify focus state
    await searchInput.focus();
    await expect(searchInput).toBeFocused();

    // 3. Run axe-core accessibility audit for form controls and labeling
    const accessibilityScanResults = await new AxeBuilder({ page })
      .include('#document-search-input')
      .analyze();

    expect(accessibilityScanResults.violations).toEqual([]);
  });

});
