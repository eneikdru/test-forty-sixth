import { test, expect } from '@playwright/test';

test.describe('Recovery QA Validation', () => {

  test('Given an E2E test, When a user requests a reset and clicks the link, Then they can successfully set a new password and log in', async ({ page }) => {
    // 1. Mock API endpoints for recovery request, token validation, and password reset confirm
    await page.route('/api/v1/auth/recovery/request', async (route) => {
      await route.fulfill({
        status: 200,
        contentType: 'application/json',
        body: JSON.stringify({
          status: 'ACCEPTED',
          message: 'If an account exists with this email, password reset instructions have been sent.',
          expiresInMinutes: 30
        })
      });
    });

    await page.route('/api/v1/auth/recovery/validate-token*', async (route) => {
      await route.fulfill({
        status: 200,
        contentType: 'application/json',
        body: JSON.stringify({
          valid: true,
          maskedEmail: 'u***r@example.com',
          message: 'Token is valid and ready for password reset.'
        })
      });
    });

    await page.route('/api/v1/auth/recovery/confirm', async (route) => {
      await route.fulfill({
        status: 200,
        contentType: 'application/json',
        body: JSON.stringify({
          status: 'SUCCESS',
          message: 'Password has been successfully reset.'
        })
      });
    });

    // 2. Navigate to recovery view
    await page.goto('/?view=recovery');

    // 3. Request password reset
    const emailInput = page.locator('#request-email-input');
    await expect(emailInput).toBeVisible();
    await emailInput.fill('user@example.com');

    const sendBtn = page.locator('#send-reset-request-btn');
    await sendBtn.click();

    // 4. Verify request success feedback message
    const successMsg = page.locator('#success-message');
    await expect(successMsg).toBeVisible();
    await expect(successMsg).toContainText('password reset instructions have been sent');

    // 5. Navigate via the password reset link with a valid token
    await page.goto('/?token=rcv_tok_valid_123');

    // 6. Set new password and confirm
    const newPasswordInput = page.locator('#newPassword');
    const confirmPasswordInput = page.locator('#confirmPassword');
    await expect(newPasswordInput).toBeVisible();

    await newPasswordInput.fill('NewSecurePass2026!');
    await confirmPasswordInput.fill('NewSecurePass2026!');

    const resetSubmitBtn = page.locator('#reset-password-submit-btn');
    await resetSubmitBtn.click();

    // 7. Verify reset success message and login form transition
    const resetSuccessAlert = page.locator('#reset-success-alert');
    await expect(resetSuccessAlert).toBeVisible();
    await expect(resetSuccessAlert).toContainText('Password has been successfully reset');

    // 8. Log in with new credentials
    const loginEmailInput = page.locator('#login-email');
    const loginPasswordInput = page.locator('#login-password');
    await expect(loginEmailInput).toBeVisible();

    await loginEmailInput.fill('user@example.com');
    await loginPasswordInput.fill('NewSecurePass2026!');

    const loginBtn = page.locator('#login-submit-btn');
    await loginBtn.click();

    // 9. Verify user is successfully logged in
    const loggedInStatus = page.locator('#logged-in-status');
    await expect(loggedInStatus).toBeVisible();
    await expect(loggedInStatus).toContainText('Successfully Logged In!');
  });

  test('Given an E2E test, When a user submits an expired token, Then the system gracefully rejects the attempt with clear UI feedback', async ({ page }) => {
    // 1. Mock API endpoints for expired token validation and failed reset confirmation
    await page.route('/api/v1/auth/recovery/validate-token*', async (route) => {
      await route.fulfill({
        status: 404,
        contentType: 'application/json',
        body: JSON.stringify({
          code: 'TOKEN_EXPIRED_OR_INVALID',
          message: 'Recovery token is invalid, expired, or already consumed.'
        })
      });
    });

    await page.route('/api/v1/auth/recovery/confirm', async (route) => {
      await route.fulfill({
        status: 401,
        contentType: 'application/json',
        body: JSON.stringify({
          code: 'INVALID_TOKEN',
          message: 'Recovery token is invalid, expired, or already consumed.'
        })
      });
    });

    // 2. Navigate directly with an expired token parameter
    await page.goto('/?token=tok_expired_123');

    // 3. Fill in new password and confirm password
    const newPasswordInput = page.locator('#newPassword');
    const confirmPasswordInput = page.locator('#confirmPassword');
    await expect(newPasswordInput).toBeVisible();

    await newPasswordInput.fill('NewSecurePass2026!');
    await confirmPasswordInput.fill('NewSecurePass2026!');

    const resetSubmitBtn = page.locator('#reset-password-submit-btn');
    await resetSubmitBtn.click();

    // 4. Verify system gracefully rejects the attempt with clear UI feedback error message
    const errorMessage = page.locator('#error-message');
    await expect(errorMessage).toBeVisible();
    await expect(errorMessage).toContainText('Recovery token is invalid, expired, or already consumed.');
  });

});
