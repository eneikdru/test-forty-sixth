<script>
  import { onMount } from 'svelte';

  export let apiBaseUrl = '/api/v1/materials/search';

  let query = '';
  let selectedPathogen = '';
  let isLoading = false;
  let hasSearched = false;
  let results = [];
  let totalElements = 0;
  let errorMessage = '';

  const pathogenOptions = [
    { label: 'All Pathogens', value: '' },
    { label: 'Virus', value: 'VIRUS' },
    { label: 'Bacteria', value: 'BACTERIA' },
    { label: 'Parasite', value: 'PARASITE' },
    { label: 'Fungi', value: 'FUNGI' },
    { label: 'Other', value: 'OTHER' }
  ];

  async function performSearch() {
    isLoading = true;
    errorMessage = '';
    hasSearched = true;

    try {
      const params = new URLSearchParams();
      if (query.trim()) params.append('query', query.trim());
      if (selectedPathogen) params.append('pathogenType', selectedPathogen);
      params.append('page', '0');
      params.append('size', '20');

      const response = await fetch(`${apiBaseUrl}?${params.toString()}`);
      if (!response.ok) {
        throw new Error(`Search failed: ${response.statusText}`);
      }

      const data = await response.json();
      results = data.content || [];
      totalElements = data.totalElements || 0;
    } catch (err) {
      console.error(err);
      errorMessage = 'Failed to load search results. Please try again.';
      results = [];
    } finally {
      isLoading = false;
    }
  }

  function handleKeydown(event) {
    if (event.key === 'Enter') {
      performSearch();
    }
  }

  function selectFilter(pathogen) {
    selectedPathogen = pathogen;
    performSearch();
  }

  function clearQuery() {
    query = '';
    performSearch();
  }

  onMount(() => {
    performSearch();
  });
</script>

<div class="search-ui-container min-h-screen bg-surface font-body-md text-on-surface antialiased flex flex-col pt-14 pb-16">
  <!-- Top Header -->
  <header class="fixed top-0 w-full z-50 bg-surface border-b border-outline-variant flex items-center justify-between px-margin-mobile h-14" role="banner">
    <button
      class="text-on-surface-variant hover:bg-surface-container-high p-3 rounded-full min-w-[44px] min-h-[44px] flex items-center justify-center focus:outline-none focus:ring-2 focus:ring-primary"
      aria-label="Back"
      type="button"
    >
      <span class="material-symbols-outlined" aria-hidden="true">arrow_back</span>
    </button>
    <h1 class="font-headline-md text-headline-md font-semibold text-primary text-center flex-1">
      Epidemiological Knowledge Base
    </h1>
    <button
      class="text-on-surface-variant hover:bg-surface-container-high p-3 rounded-full min-w-[44px] min-h-[44px] flex items-center justify-center focus:outline-none focus:ring-2 focus:ring-primary"
      aria-label="Close"
      type="button"
    >
      <span class="material-symbols-outlined" aria-hidden="true">close</span>
    </button>
  </header>

  <!-- Main Content -->
  <main class="flex-1 w-full max-w-container-max-width mx-auto px-margin-mobile md:px-margin-desktop py-stack-lg flex flex-col gap-stack-lg" role="main">
    <!-- Search Input Section -->
    <section aria-label="Search Form" class="w-full">
      <form on:submit|preventDefault={performSearch} class="search-input-wrapper relative flex items-center w-full h-[56px] bg-white border border-outline-variant rounded-lg focus-within:border-primary focus-within:ring-2 focus-within:ring-primary/20 transition-all">
        <span class="material-symbols-outlined text-outline ml-4" aria-hidden="true">search</span>
        <input
          bind:value={query}
          on:keydown={handleKeydown}
          type="search"
          class="flex-1 h-full bg-transparent border-none focus:ring-0 px-4 font-body-lg text-on-surface placeholder-outline-variant"
          placeholder="Search materials by protocol title, pathogen, or keywords..."
          aria-label="Search materials"
        />
        {#if query}
          <button
            type="button"
            on:click={clearQuery}
            class="mr-3 text-outline hover:text-primary min-w-[44px] min-h-[44px] flex items-center justify-center rounded-full focus:outline-none focus:ring-2 focus:ring-primary"
            aria-label="Clear search query"
          >
            <span class="material-symbols-outlined" aria-hidden="true">cancel</span>
          </button>
        {/if}
        <button
          type="submit"
          class="mr-2 px-4 py-2 bg-primary text-on-primary rounded-md font-medium min-h-[44px] flex items-center gap-2 hover:bg-primary/90 focus:outline-none focus:ring-2 focus:ring-primary"
          aria-label="Submit search"
        >
          <span>Search</span>
        </button>
      </form>
    </section>

    <!-- Pathogen Quick Filters -->
    <section aria-label="Pathogen Filters" class="flex flex-col gap-stack-sm">
      <h2 class="font-label-md text-label-md text-outline uppercase tracking-wider">
        Filter by Pathogen
      </h2>
      <div class="flex flex-wrap gap-2" role="group" aria-label="Pathogen categories">
        {#each pathogenOptions as opt}
          <button
            type="button"
            class={`px-4 py-2.5 rounded-full font-body-md flex items-center gap-2 border transition-colors min-h-[44px] focus:outline-none focus:ring-2 focus:ring-primary ${
              selectedPathogen === opt.value
                ? 'bg-primary text-on-primary border-primary font-semibold'
                : 'bg-white text-on-surface border-outline-variant/50 hover:bg-surface-variant'
            }`}
            aria-pressed={selectedPathogen === opt.value}
            on:click={() => selectFilter(opt.value)}
          >
            <span>{opt.label}</span>
          </button>
        {/each}
      </div>
    </section>

    <!-- Results / Feedback Area -->
    <section aria-live="polite" aria-atomic="true" class="flex flex-col gap-stack-md mt-2">
      {#if isLoading}
        <!-- Visible Loading Indicator -->
        <div class="loading-container flex flex-col items-center justify-center p-12 bg-white border border-outline-variant/30 rounded-lg text-center" role="status" id="loading-indicator">
          <div class="w-10 h-10 border-4 border-primary/30 border-t-primary rounded-full animate-spin mb-4" aria-hidden="true"></div>
          <p class="font-body-lg text-primary font-medium">Searching epidemiological documents...</p>
          <span class="sr-only">Loading results</span>
        </div>
      {:else if errorMessage}
        <div class="p-6 bg-error-container text-on-error-container border border-error/20 rounded-lg text-center" role="alert">
          <p class="font-headline-md text-headline-md font-semibold mb-1">Error</p>
          <p class="font-body-md">{errorMessage}</p>
        </div>
      {:else if hasSearched && results.length === 0}
        <!-- Clear No Results Found Message -->
        <div class="no-results-container flex flex-col items-center justify-center p-12 bg-white border border-outline-variant/30 rounded-lg text-center gap-3" id="no-results-message" role="region" aria-label="No results">
          <span class="material-symbols-outlined text-4xl text-outline" aria-hidden="true">search_off</span>
          <h3 class="font-headline-md text-headline-md font-semibold text-on-surface">No results found</h3>
          <p class="font-body-md text-outline max-w-md">
            No epidemiological materials matched your query. Try adjusting your search term or clearing filters.
          </p>
          <button
            type="button"
            on:click={() => { query = ''; selectedPathogen = ''; performSearch(); }}
            class="mt-2 px-5 py-2.5 bg-secondary-container text-on-secondary-container rounded-lg font-medium min-h-[44px] hover:bg-secondary-container/80 focus:outline-none focus:ring-2 focus:ring-primary"
          >
            Clear Filters & Retry
          </button>
        </div>
      {:else if results.length > 0}
        <!-- Results List -->
        <div class="flex items-center justify-between pb-2 border-b border-outline-variant/30">
          <p class="font-label-md text-outline uppercase tracking-wider">
            Found {totalElements} {totalElements === 1 ? 'document' : 'documents'}
          </p>
        </div>
        <div class="grid grid-cols-1 gap-4" id="results-list">
          {#each results as item (item.id)}
            <article class="bg-white border border-outline-variant/50 p-5 rounded-lg flex flex-col md:flex-row md:items-center justify-between gap-4 hover:shadow-md transition-shadow">
              <div class="flex flex-col gap-1">
                <div class="flex items-center gap-2">
                  <span class="px-2.5 py-0.5 rounded-full text-xs font-semibold bg-primary-fixed text-on-primary-fixed uppercase tracking-wide">
                    {item.pathogenType}
                  </span>
                  <span class="text-xs text-outline">
                    ID: {item.id}
                  </span>
                </div>
                <h3 class="font-headline-md text-headline-md text-on-surface font-semibold mt-1">
                  {item.title}
                </h3>
                {#if item.createdAt}
                  <p class="font-label-sm text-outline mt-1">
                    Cataloged: {new Date(item.createdAt).toLocaleDateString()}
                  </p>
                {/if}
              </div>

              {#if item.downloadUrl}
                <a
                  href={item.downloadUrl}
                  target="_blank"
                  rel="noopener noreferrer"
                  class="self-start md:self-center px-4 py-2.5 bg-primary text-on-primary rounded-lg font-medium text-sm flex items-center gap-2 min-h-[44px] hover:bg-primary/90 focus:outline-none focus:ring-2 focus:ring-primary active:scale-95 transition-transform"
                  aria-label={`Download document ${item.title}`}
                >
                  <span class="material-symbols-outlined text-[20px]" aria-hidden="true">download</span>
                  <span>Download</span>
                </a>
              {/if}
            </article>
          {/each}
        </div>
      {/if}
    </section>
  </main>
</div>

<style>
  .sr-only {
    position: absolute;
    width: 1px;
    height: 1px;
    padding: 0;
    margin: -1px;
    overflow: hidden;
    clip: rect(0, 0, 0, 0);
    white-space: nowrap;
    border-width: 0;
  }
</style>
