<script>
  let newPassword = '';
  let confirmPassword = '';
  let errorMessage = '';

  function handleSubmit(event) {
    event.preventDefault();
    if (newPassword !== confirmPassword) {
      errorMessage = 'Passwords do not match. Please try again.';
    } else {
      errorMessage = '';
      // Proceed with actual reset logic if needed
    }
  }
</script>

<div class="min-h-screen flex flex-col items-center justify-start md:justify-center px-container-padding pt-20 md:pt-0">
  <!-- TopAppBar -->
  <header class="fixed top-0 w-full flex items-center justify-between px-container-padding h-16 max-w-lg mx-auto bg-surface dark:bg-surface z-50">
    <button class="text-on-surface-variant hover:bg-surface-container-low transition-colors rounded-full p-2 active:opacity-70 flex items-center justify-center" aria-label="Go back">
      <span class="material-symbols-outlined" data-icon="arrow_back">arrow_back</span>
    </button>
    <h1 class="font-headline-md text-headline-md font-bold text-primary dark:text-inverse-primary text-center flex-1">Account Recovery</h1>
    <button class="text-primary font-label-bold text-label-bold hover:bg-surface-container-low transition-colors px-3 py-2 rounded active:opacity-70">Help</button>
  </header>

  <!-- Main Content Canvas -->
  <main class="w-full max-w-[448px] bg-surface-container-lowest rounded-xl p-container-padding shadow-sm border border-surface-container-low mt-8 md:mt-0">
    <div class="flex flex-col gap-stack-gap-md">
      <!-- Header Section -->
      <div class="text-center">
        <h2 class="font-headline-lg-mobile text-headline-lg-mobile md:font-headline-lg md:text-headline-lg text-on-surface mb-2">Reset Password</h2>
        <p class="font-body-sm text-body-sm text-on-surface-variant">Please enter your new password below.</p>
      </div>

      <!-- Verification Form -->
      <form class="flex flex-col gap-stack-gap-lg mt-4" on:submit={handleSubmit}>
        <div class="flex flex-col gap-4 w-full">
          <div class="flex flex-col gap-1 w-full">
            <label for="newPassword" class="font-label-bold text-label-bold text-on-surface">New Password</label>
            <input
              id="newPassword"
              type="password"
              bind:value={newPassword}
              class="h-14 px-4 font-body-lg text-body-lg rounded border bg-surface text-on-surface transition-all focus:outline-none focus:border-primary focus:border-[2px] {errorMessage ? 'border-error border-[2px]' : 'border-outline'}"
              required
            />
          </div>

          <div class="flex flex-col gap-1 w-full">
            <label for="confirmPassword" class="font-label-bold text-label-bold text-on-surface">Confirm Password</label>
            <input
              id="confirmPassword"
              type="password"
              bind:value={confirmPassword}
              class="h-14 px-4 font-body-lg text-body-lg rounded border bg-surface text-on-surface transition-all focus:outline-none focus:border-primary focus:border-[2px] {errorMessage ? 'border-error border-[2px]' : 'border-outline'}"
              required
            />
          </div>

          {#if errorMessage}
            <div class="flex items-center gap-1 text-error mt-2 w-full justify-start" role="alert">
              <span class="material-symbols-outlined text-[16px]" data-icon="error">error</span>
              <span class="font-label-sm text-label-sm">{errorMessage}</span>
            </div>
          {/if}
        </div>

        <!-- Primary Button -->
        <button class="w-full h-12 bg-primary text-on-primary font-label-bold text-label-bold rounded-full uppercase tracking-wide hover:bg-on-primary-fixed-variant active:bg-on-primary-fixed transition-colors flex items-center justify-center mt-4" type="submit">
          Reset Password
        </button>
      </form>
    </div>
  </main>
</div>
