<script>
  import ResetPassword from './components/ResetPassword.svelte';
</script>

<main class="app-main">
  <ResetPassword />
</main>

<style>
  .app-main {
    min-height: 100vh;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: 24px;
    background-color: var(--color-background, #f8f9fa);
  }
</style>
