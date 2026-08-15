<script>
  let token = '';
  let newPassword = '';
  let confirmPassword = '';
  let error = '';
  let loading = false;
  let success = false;

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (newPassword !== confirmPassword) {
      error = "Passwords do not match.";
      return;
    }

    error = '';

    // Simulate token for UI testing purposes
    const urlParams = new URLSearchParams(window.location.search);
    token = urlParams.get('token') || 'dummy-token';

    loading = true;
    try {
      const res = await fetch('/api/v1/auth/recovery/confirm', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ token, newPassword })
      });

      if (!res.ok) {
        const data = await res.json().catch(() => ({}));
        error = data.message || "Password reset failed.";
      } else {
        success = true;
      }
    } catch (err) {
      error = "An unexpected error occurred.";
    } finally {
      loading = false;
    }
  };
</script>

<div class="reset-container">
  {#if success}
    <div class="success-message">
      <span class="material-symbols-outlined icon">check_circle</span>
      <p>Your password has been successfully reset.</p>
    </div>
  {:else}
    <form on:submit={handleSubmit} class="reset-form">
      <h2>Reset Password</h2>

      {#if error}
        <div class="error-banner">
          <span class="material-symbols-outlined icon">error</span>
          <span>{error}</span>
        </div>
      {/if}

      <div class="form-group">
        <label for="new-password">New Password</label>
        <input
          id="new-password"
          type="password"
          bind:value={newPassword}
          required
          class="touch-input"
        />
      </div>

      <div class="form-group">
        <label for="confirm-password">Confirm Password</label>
        <input
          id="confirm-password"
          type="password"
          bind:value={confirmPassword}
          required
          class="touch-input"
        />
      </div>

      <button type="submit" disabled={loading} class="submit-button">
        {loading ? 'Resetting...' : 'Reset Password'}
      </button>
    </form>
  {/if}
</div>

<style>
  .reset-container {
    width: 100%;
    max-width: 448px;
    margin: 0 auto;
    padding: 24px;
    background-color: var(--color-surface-container-lowest, #ffffff);
    border-radius: 0.75rem;
    box-shadow: 0 1px 2px 0 rgba(0, 0, 0, 0.05);
    border: 1px solid var(--color-surface-container-low, #f3f4f5);
  }

  .reset-form {
    display: flex;
    flex-direction: column;
    gap: 24px;
  }

  h2 {
    font-family: var(--font-headline-lg, Inter);
    font-size: 24px;
    font-weight: 700;
    color: var(--color-on-surface, #191c1d);
    text-align: center;
    margin: 0;
  }

  .form-group {
    display: flex;
    flex-direction: column;
    gap: 8px;
  }

  label {
    font-family: var(--font-label-bold, Inter);
    font-size: 14px;
    font-weight: 600;
    color: var(--color-on-surface-variant, #414754);
  }

  .touch-input {
    height: 56px; /* Touch-friendly height */
    padding: 0 16px;
    font-family: var(--font-body-lg, Inter);
    font-size: 16px;
    border: 1px solid var(--color-outline, #727785);
    border-radius: 4px;
    background-color: var(--color-surface, #f8f9fa);
    color: var(--color-on-surface, #191c1d);
    transition: all 0.2s;
  }

  .touch-input:focus {
    outline: none;
    border-width: 2px;
    border-color: var(--color-primary, #005bbf);
  }

  .submit-button {
    height: 56px; /* Touch-friendly height */
    background-color: var(--color-primary, #005bbf);
    color: var(--color-on-primary, #ffffff);
    font-family: var(--font-label-bold, Inter);
    font-size: 14px;
    font-weight: 600;
    text-transform: uppercase;
    border: none;
    border-radius: 9999px;
    cursor: pointer;
    transition: background-color 0.2s;
  }

  .submit-button:hover:not(:disabled) {
    background-color: var(--color-on-primary-fixed-variant, #004493);
  }

  .submit-button:disabled {
    opacity: 0.7;
    cursor: not-allowed;
  }

  .error-banner {
    display: flex;
    align-items: center;
    gap: 8px;
    padding: 12px 16px;
    background-color: var(--color-error-container, #ffdad6);
    color: var(--color-on-error-container, #93000a);
    border-radius: 4px;
    font-family: var(--font-body-sm, Inter);
    font-size: 14px;
  }

  .success-message {
    text-align: center;
    color: var(--color-primary, #005bbf);
  }

  .icon {
    font-size: 20px;
  }
</style>
