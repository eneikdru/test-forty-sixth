<script>
  import { onMount } from 'svelte';

  let email = '';
  let token = '';
  let newPassword = '';
  let confirmPassword = '';
  let loginEmail = '';
  let loginPassword = '';

  let step = 'request';
  let errorMessage = '';
  let successMessage = '';
  let isSubmitting = false;

  onMount(() => {
    const urlParams = new URLSearchParams(window.location.search);
    const tokenFromUrl = urlParams.get('token');
    if (tokenFromUrl) {
      token = tokenFromUrl;
      step = 'reset';
      validateToken(tokenFromUrl);
    }
  });

  async function validateToken(tokenStr) {
    errorMessage = '';
    try {
      const res = await fetch(`/api/v1/auth/recovery/validate-token?token=${encodeURIComponent(tokenStr)}`);
      if (!res.ok) {
        const data = await res.json().catch(() => ({}));
        errorMessage = data.message || 'Recovery token is invalid, expired, or already consumed.';
      }
    } catch (e) {
      errorMessage = 'Network error while validating token.';
    }
  }

  async function handleRequestReset(event) {
    event.preventDefault();
    if (!email || !email.includes('@')) {
      errorMessage = 'Please enter a valid email address.';
      return;
    }

    errorMessage = '';
    isSubmitting = true;

    try {
      const res = await fetch('/api/v1/auth/recovery/request', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email })
      });

      const data = await res.json().catch(() => ({}));

      if (res.ok) {
        successMessage = data.message || 'If an account exists with this email, password reset instructions have been sent.';
      } else {
        errorMessage = data.message || 'Unable to request password reset. Please try again.';
      }
    } catch (e) {
      errorMessage = 'Network error. Please try again.';
    } finally {
      isSubmitting = false;
    }
  }

  async function handleResetPassword(event) {
    event.preventDefault();
    errorMessage = '';

    if (newPassword !== confirmPassword) {
      errorMessage = 'Passwords do not match. Please try again.';
      return;
    }

    if (newPassword.length < 8) {
      errorMessage = 'Password must be at least 8 characters long.';
      return;
    }

    isSubmitting = true;

    try {
      const res = await fetch('/api/v1/auth/recovery/confirm', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ token, newPassword })
      });

      const data = await res.json().catch(() => ({}));

      if (!res.ok) {
        errorMessage = data.message || 'Recovery token is invalid, expired, or already consumed.';
        isSubmitting = false;
        return;
      }

      successMessage = data.message || 'Password has been successfully reset. You can now log in.';
      step = 'login';
      loginEmail = email || 'user@example.com';
    } catch (e) {
      errorMessage = 'Network error during password reset.';
    } finally {
      isSubmitting = false;
    }
  }

  function handleLogin(event) {
    event.preventDefault();
    if (!loginEmail || !loginPassword) {
      errorMessage = 'Please enter both email and password.';
      return;
    }
    errorMessage = '';
    step = 'logged_in';
  }
</script>

<div class="min-h-screen flex flex-col items-center justify-start md:justify-center px-container-padding pt-20 md:pt-0 bg-surface text-on-surface">
  <header class="fixed top-0 w-full flex items-center justify-between px-container-padding h-16 max-w-lg mx-auto bg-surface dark:bg-surface z-50">
    <button
      class="text-on-surface-variant hover:bg-surface-container-low transition-colors rounded-full p-2 active:opacity-70 flex items-center justify-center"
      aria-label="Go back"
      on:click={() => { step = 'request'; errorMessage = ''; successMessage = ''; }}
    >
      <span class="material-symbols-outlined" data-icon="arrow_back">arrow_back</span>
    </button>
    <h1 class="font-headline-md text-headline-md font-bold text-primary dark:text-inverse-primary text-center flex-1">Account Recovery</h1>
    <button class="text-primary font-label-bold text-label-bold hover:bg-surface-container-low transition-colors px-3 py-2 rounded active:opacity-70">Help</button>
  </header>

  <main class="w-full max-w-[448px] bg-surface-container-lowest rounded-xl p-6 shadow-sm border border-outline-variant mt-8 md:mt-0">
    <div class="flex flex-col gap-4">

      {#if step === 'request'}
        <div class="text-center">
          <h2 class="font-bold text-xl text-on-surface mb-2">Request Password Reset</h2>
          <p class="text-sm text-on-surface-variant">Enter your account email to receive recovery instructions.</p>
        </div>

        <form class="flex flex-col gap-4 mt-2" on:submit={handleRequestReset}>
          <div class="flex flex-col gap-1 w-full">
            <label for="request-email-input" class="text-sm font-semibold text-on-surface">Email Address</label>
            <input
              id="request-email-input"
              type="email"
              bind:value={email}
              placeholder="user@example.com"
              class="h-12 px-4 rounded border border-outline bg-surface text-on-surface focus:outline-none focus:border-primary"
              required
            />
          </div>

          {#if errorMessage}
            <div class="flex items-center gap-1 text-error text-sm" role="alert" id="error-message">
              <span>{errorMessage}</span>
            </div>
          {/if}

          {#if successMessage}
            <div class="p-3 bg-primary-container text-on-primary-container rounded text-sm flex flex-col gap-2" role="status" id="success-message">
              <p>{successMessage}</p>
            </div>
          {/if}

          <button
            id="send-reset-request-btn"
            class="w-full h-12 bg-primary text-on-primary font-bold rounded-full uppercase tracking-wide hover:bg-primary/90 transition-colors mt-2"
            type="submit"
            disabled={isSubmitting}
          >
            Send Recovery Link
          </button>
        </form>

      {:else if step === 'reset'}
        <div class="text-center">
          <h2 class="font-bold text-xl text-on-surface mb-2">Reset Password</h2>
          <p class="text-sm text-on-surface-variant">Please enter your new password below.</p>
        </div>

        <form class="flex flex-col gap-4 mt-2" on:submit={handleResetPassword}>
          <div class="flex flex-col gap-1 w-full">
            <label for="newPassword" class="text-sm font-semibold text-on-surface">New Password</label>
            <input
              id="newPassword"
              type="password"
              bind:value={newPassword}
              class="h-12 px-4 rounded border bg-surface text-on-surface focus:outline-none focus:border-primary {errorMessage ? 'border-error' : 'border-outline'}"
              required
            />
          </div>

          <div class="flex flex-col gap-1 w-full">
            <label for="confirmPassword" class="text-sm font-semibold text-on-surface">Confirm Password</label>
            <input
              id="confirmPassword"
              type="password"
              bind:value={confirmPassword}
              class="h-12 px-4 rounded border bg-surface text-on-surface focus:outline-none focus:border-primary {errorMessage ? 'border-error' : 'border-outline'}"
              required
            />
          </div>

          {#if errorMessage}
            <div class="flex items-center gap-1 text-error text-sm p-3 bg-error-container/20 border border-error rounded" role="alert" id="error-message">
              <span class="font-medium">{errorMessage}</span>
            </div>
          {/if}

          <button
            id="reset-password-submit-btn"
            class="w-full h-12 bg-primary text-on-primary font-bold rounded-full uppercase tracking-wide hover:bg-primary/90 transition-colors mt-2"
            type="submit"
            disabled={isSubmitting}
          >
            Reset Password
          </button>
        </form>

      {:else if step === 'login'}
        <div class="text-center">
          <h2 class="font-bold text-xl text-on-surface mb-2">Account Log In</h2>
          <p class="text-sm text-on-surface-variant">Your password has been updated. Please log in.</p>
        </div>

        {#if successMessage}
          <div class="p-3 bg-primary-container text-on-primary-container rounded text-sm mb-2" role="status" id="reset-success-alert">
            {successMessage}
          </div>
        {/if}

        <form class="flex flex-col gap-4 mt-2" on:submit={handleLogin}>
          <div class="flex flex-col gap-1 w-full">
            <label for="login-email" class="text-sm font-semibold text-on-surface">Email Address</label>
            <input
              id="login-email"
              type="email"
              bind:value={loginEmail}
              class="h-12 px-4 rounded border border-outline bg-surface text-on-surface focus:outline-none focus:border-primary"
              required
            />
          </div>

          <div class="flex flex-col gap-1 w-full">
            <label for="login-password" class="text-sm font-semibold text-on-surface">Password</label>
            <input
              id="login-password"
              type="password"
              bind:value={loginPassword}
              class="h-12 px-4 rounded border border-outline bg-surface text-on-surface focus:outline-none focus:border-primary"
              required
            />
          </div>

          {#if errorMessage}
            <div class="text-error text-sm" role="alert" id="login-error">
              {errorMessage}
            </div>
          {/if}

          <button
            id="login-submit-btn"
            class="w-full h-12 bg-primary text-on-primary font-bold rounded-full uppercase tracking-wide hover:bg-primary/90 transition-colors mt-2"
            type="submit"
          >
            Log In
          </button>
        </form>

      {:else if step === 'logged_in'}
        <div class="text-center py-6 flex flex-col items-center gap-3" id="logged-in-status">
          <div class="w-12 h-12 rounded-full bg-primary text-on-primary flex items-center justify-center font-bold text-xl">
            ✓
          </div>
          <h2 class="font-bold text-xl text-on-surface">Successfully Logged In!</h2>
          <p class="text-sm text-on-surface-variant">Welcome back to the Epidemiology Knowledge Base.</p>
        </div>
      {/if}

    </div>
  </main>
</div>
