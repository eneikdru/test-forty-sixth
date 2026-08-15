<script>
  import { onMount, onDestroy } from 'svelte';

  // Search parameters state
  let query = '';
  let selectedPathogen = ''; // '', 'VIRUS', 'BACTERIA', 'PARASITE', 'FUNGI', 'OTHER'
  let page = 0;
  let size = 10;
  let sort = 'createdAt,desc';

  // Async search state
  let isLoading = false;
  let hasSearched = false;
  let searchResults = [];
  let totalElements = 0;
  let totalPages = 0;
  let errorMessage = null;

  // Recent searches tracking
  let recentSearches = ['Outbreak Protocol', 'Influenza 2026', 'COVID-19', 'Salmonella'];

  // Pathogen filter options
  const pathogenTypes = [
    { label: 'All Types', value: '' },
    { label: 'Virus', value: 'VIRUS' },
    { label: 'Bacteria', value: 'BACTERIA' },
    { label: 'Parasite', value: 'PARASITE' },
    { label: 'Fungi', value: 'FUNGI' },
    { label: 'Other', value: 'OTHER' }
  ];

  let debounceTimer = null;
  let currentAbortController = null;

  function handleInput() {
    isLoading = true;
    if (debounceTimer) clearTimeout(debounceTimer);
    debounceTimer = setTimeout(() => {
      executeSearch();
    }, 200);
  }

  function handleRecentClick(term) {
    query = term;
    executeSearch();
  }

  function selectPathogen(pathogenValue) {
    selectedPathogen = pathogenValue;
    executeSearch();
  }

  function clearSearch() {
    if (currentAbortController) {
      currentAbortController.abort();
      currentAbortController = null;
    }
    query = '';
    selectedPathogen = '';
    searchResults = [];
    hasSearched = false;
    errorMessage = null;
  }

  async function executeSearch() {
    if (currentAbortController) {
      currentAbortController.abort();
    }
    currentAbortController = new AbortController();
    const signal = currentAbortController.signal;

    isLoading = true;
    errorMessage = null;
    hasSearched = true;

    try {
      const params = new URLSearchParams();
      if (query.trim()) params.append('query', query.trim());
      if (selectedPathogen) params.append('pathogenType', selectedPathogen);
      params.append('page', page.toString());
      params.append('size', size.toString());
      params.append('sort', sort);

      const response = await fetch(`/api/v1/materials/search?${params.toString()}`, { signal });

      if (!response.ok) {
        throw new Error(`Search request failed with status ${response.status}`);
      }

      const data = await response.json();
      if (!signal.aborted) {
        searchResults = data.content || [];
        totalElements = data.totalElements || 0;
        totalPages = data.totalPages || 0;

        // Add query to recent searches if unique and non-empty
        if (query.trim() && !recentSearches.includes(query.trim())) {
          recentSearches = [query.trim(), ...recentSearches.slice(0, 3)];
        }
      }
    } catch (err) {
      if (err.name === 'AbortError') {
        return; // Request was cancelled by a newer search
      }
      console.warn('Search API network/server fallback:', err);
      // Fallback mock data when running standalone or offline
      if (!signal.aborted) {
        performLocalMockSearch();
      }
    } finally {
      if (currentAbortController && currentAbortController.signal === signal) {
        isLoading = false;
      }
    }
  }

  function performLocalMockSearch() {
    const mockMaterials = [
      {
        id: 101,
        title: 'Influenza Surveillance & Containment Protocol 2026',
        pathogenType: 'VIRUS',
        createdAt: '2026-08-14T23:52:20Z',
        downloadUrl: '/api/v1/materials/101/download'
      },
      {
        id: 102,
        title: 'Bacterial Outbreak Assessment and Water Quality Guidance',
        pathogenType: 'BACTERIA',
        createdAt: '2026-08-12T14:10:00Z',
        downloadUrl: '/api/v1/materials/102/download'
      },
      {
        id: 103,
        title: 'Parasitic Vector Control in Agricultural Regions',
        pathogenType: 'PARASITE',
        createdAt: '2026-08-10T09:30:00Z',
        downloadUrl: '/api/v1/materials/103/download'
      },
      {
        id: 104,
        title: 'Fungal Infection Monitoring in Healthcare Settings',
        pathogenType: 'FUNGI',
        createdAt: '2026-08-08T16:45:00Z',
        downloadUrl: '/api/v1/materials/104/download'
      }
    ];

    let filtered = mockMaterials;
    if (query.trim()) {
      const q = query.trim().toLowerCase();
      filtered = filtered.filter(m => m.title.toLowerCase().includes(q));
    }
    if (selectedPathogen) {
      filtered = filtered.filter(m => m.pathogenType === selectedPathogen);
    }

    searchResults = filtered;
    totalElements = filtered.length;
    totalPages = filtered.length > 0 ? 1 : 0;
  }

  onMount(() => {
    // Initial search load
    executeSearch();
  });

  onDestroy(() => {
    if (currentAbortController) {
      currentAbortController.abort();
    }
  });
</script>

<div class="bg-surface font-body-md text-on-surface antialiased min-h-screen flex flex-col pt-14 pb-16">
  <!-- TopAppBar -->
  <header class="fixed top-0 w-full z-50 bg-surface dark:bg-surface-container border-b border-outline-variant dark:border-outline flex items-center justify-between px-4 h-14 w-full">
    <button
      type="button"
      on:click={clearSearch}
      aria-label="Back to default view"
      class="text-on-surface-variant hover:bg-surface-container-high transition-colors active:scale-95 p-2 min-w-[44px] min-h-[44px] rounded-full flex items-center justify-center focus:outline-none focus:ring-2 focus:ring-primary focus:ring-opacity-50"
    >
      <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24" aria-hidden="true">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10 19l-7-7m0 0l7-7m-7 7h18" />
      </svg>
    </button>
    <h1 class="font-headline-md text-xl font-semibold text-primary text-center flex-1">
      Search Epidemiological Documents
    </h1>
    <button
      type="button"
      on:click={clearSearch}
      aria-label="Clear all inputs"
      class="text-on-surface-variant hover:bg-surface-container-high transition-colors active:scale-95 p-2 min-w-[44px] min-h-[44px] rounded-full flex items-center justify-center focus:outline-none focus:ring-2 focus:ring-primary focus:ring-opacity-50"
    >
      <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24" aria-hidden="true">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
      </svg>
    </button>
  </header>

  <!-- Main Content Canvas -->
  <main class="flex-1 w-full max-w-5xl mx-auto px-4 md:px-8 py-6 flex flex-col gap-6">
    <!-- Search Input Area -->
    <section aria-label="Search Input">
      <div class="search-input-wrapper relative flex items-center w-full h-[56px] bg-white border border-outline-variant rounded-lg shadow-xs focus-within:border-primary focus-within:ring-2 focus-within:ring-primary/20 transition-all duration-200">
        <div class="ml-4 text-outline flex items-center justify-center">
          <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24" aria-hidden="true">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
          </svg>
        </div>
        <input
          id="document-search-input"
          aria-label="Search files, protocols, or pathogens"
          bind:value={query}
          on:input={handleInput}
          class="flex-1 h-full bg-transparent border-none focus:ring-0 px-4 font-body-lg text-on-surface placeholder:text-outline-variant text-base outline-none"
          placeholder="Search files, protocols, or pathogens..."
          type="search"
        />
        {#if query}
          <button
            type="button"
            aria-label="Clear search input"
            on:click={clearSearch}
            class="mr-3 text-outline hover:text-primary transition-colors focus:outline-none p-2 min-w-[44px] min-h-[44px] flex items-center justify-center rounded-full"
          >
            <svg class="w-5 h-5" fill="currentColor" viewBox="0 0 20 20" aria-hidden="true">
              <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 00-1.414-1.414L10 8.586 8.707 7.293z" clip-rule="evenodd" />
            </svg>
          </button>
        {/if}
      </div>
    </section>

    <!-- Pathogen Quick Filters -->
    <section aria-label="Pathogen Category Quick Filters" class="flex flex-col gap-2">
      <h2 class="font-label-md text-xs font-semibold text-on-surface-variant uppercase tracking-wider">
        Pathogen Type Filter
      </h2>
      <div class="flex flex-wrap gap-2" role="group" aria-label="Pathogen Types">
        {#each pathogenTypes as pathogen}
          <button
            type="button"
            aria-pressed={selectedPathogen === pathogen.value}
            on:click={() => selectPathogen(pathogen.value)}
            class="px-4 py-2 min-h-[44px] rounded-full text-sm font-medium transition-all border flex items-center justify-center active:scale-95 focus:outline-none focus:ring-2 focus:ring-primary
              {selectedPathogen === pathogen.value
                ? 'bg-primary text-white border-primary shadow-xs'
                : 'bg-white text-on-surface border-outline-variant hover:bg-surface-container'}"
          >
            {pathogen.label}
          </button>
        {/each}
      </div>
    </section>

    <!-- Recent Searches -->
    {#if recentSearches.length > 0}
      <section aria-label="Recent Searches" class="flex flex-col gap-2">
        <h2 class="font-label-md text-xs font-semibold text-on-surface-variant uppercase tracking-wider">
          Recent Searches
        </h2>
        <div class="flex flex-wrap gap-2">
          {#each recentSearches as term}
            <button
              type="button"
              on:click={() => handleRecentClick(term)}
              class="bg-white hover:bg-surface-container transition-colors px-4 py-2 min-h-[44px] rounded-full text-sm text-on-surface flex items-center gap-2 border border-outline-variant focus:outline-none focus:ring-2 focus:ring-primary"
            >
              <svg class="w-4 h-4 text-on-surface-variant" fill="none" stroke="currentColor" viewBox="0 0 24 24" aria-hidden="true">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
              </svg>
              <span>{term}</span>
            </button>
          {/each}
        </div>
      </section>
    {/if}

    <!-- Search Results / Loading / Empty States -->
    <section aria-label="Search Results" aria-busy={isLoading} class="flex flex-col gap-4 mt-2">
      <div class="flex items-center justify-between border-b border-outline-variant pb-2">
        <h2 class="font-headline-md text-lg font-semibold text-on-surface">
          Results {#if hasSearched && !isLoading}({totalElements}){/if}
        </h2>
        {#if isLoading}
          <span class="text-xs font-medium text-primary flex items-center gap-1.5">
            Searching...
          </span>
        {/if}
      </div>

      <!-- Loading Indicator -->
      {#if isLoading}
        <div
          role="status"
          aria-live="polite"
          aria-label="Loading search results"
          class="flex flex-col items-center justify-center py-12 px-4 bg-white rounded-xl border border-outline-variant shadow-xs gap-3"
        >
          <div class="w-10 h-10 border-4 border-primary/30 border-t-primary rounded-full animate-spin"></div>
          <p class="text-sm font-medium text-on-surface-variant">Searching documents, please wait...</p>
        </div>
      {:else if hasSearched && searchResults.length === 0}
        <!-- Clear 'no results found' message -->
        <div
          role="status"
          aria-live="polite"
          class="flex flex-col items-center justify-center py-12 px-4 bg-white rounded-xl border border-outline-variant shadow-xs text-center gap-3"
        >
          <div class="w-12 h-12 rounded-full bg-error-container/30 text-error flex items-center justify-center">
            <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24" aria-hidden="true">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9.172 16.172a4 4 0 015.656 0M9 10h.01M15 10h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
            </svg>
          </div>
          <h3 class="font-bold text-lg text-on-surface">No results found</h3>
          <p class="text-sm text-on-surface-variant max-w-md">
            We couldn't find any documents matching your criteria
            {#if query}<strong>"{query}"</strong>{/if}
            {#if selectedPathogen}with pathogen filter <strong>{selectedPathogen}</strong>{/if}.
            Try checking for spelling errors or adjusting filters.
          </p>
          <button
            type="button"
            on:click={clearSearch}
            class="mt-2 px-5 py-2.5 min-h-[44px] text-sm font-semibold text-primary bg-primary-container hover:bg-primary/20 rounded-lg transition-colors focus:outline-none focus:ring-2 focus:ring-primary"
          >
            Reset Search Filters
          </button>
        </div>
      {:else if searchResults.length > 0}
        <!-- Search Results List -->
        <div class="grid grid-cols-1 gap-3">
          {#each searchResults as item}
            <article class="bg-white border border-outline-variant p-4 rounded-xl shadow-xs hover:border-primary transition-all flex flex-col sm:flex-row sm:items-center justify-between gap-4">
              <div class="flex items-start gap-3">
                <div class="w-10 h-10 rounded-lg bg-surface-container-high text-primary flex items-center justify-center font-bold text-xs shrink-0 mt-0.5">
                  {item.pathogenType ? item.pathogenType.substring(0, 3) : 'DOC'}
                </div>
                <div>
                  <h3 class="font-semibold text-base text-on-surface leading-snug">
                    {item.title}
                  </h3>
                  <div class="flex flex-wrap items-center gap-2 mt-1.5 text-xs text-on-surface-variant font-medium">
                    <span class="inline-block px-2 py-0.5 rounded bg-surface-container font-medium text-on-surface-variant">
                      {item.pathogenType || 'UNSPECIFIED'}
                    </span>
                    <span>•</span>
                    <span class="metadata-id">ID: {item.id}</span>
                    <span>•</span>
                    <span class="metadata-timestamp">{new Date(item.createdAt).toLocaleDateString()}</span>
                  </div>
                </div>
              </div>

              <a
                href={item.downloadUrl || '#'}
                download
                class="min-h-[44px] min-w-[44px] inline-flex items-center justify-center px-4 py-2 bg-primary text-white text-sm font-semibold rounded-lg hover:bg-primary/90 transition-colors focus:outline-none focus:ring-2 focus:ring-primary/50 shrink-0 self-end sm:self-center"
              >
                Download Document
              </a>
            </article>
          {/each}
        </div>
      {/if}
    </section>
  </main>

  <!-- BottomNavBar for mobile navigation -->
  <nav class="md:hidden fixed bottom-0 left-0 w-full flex justify-around items-center h-16 px-2 bg-surface border-t border-outline-variant z-50">
    <button
      type="button"
      on:click={clearSearch}
      class="flex flex-col items-center justify-center text-on-surface-variant hover:bg-surface-container transition-colors p-2 rounded-lg flex-1 min-h-[44px]"
    >
      <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24" aria-hidden="true">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
      </svg>
      <span class="font-label-md text-xs mt-0.5">Recent</span>
    </button>

    <button
      type="button"
      class="flex flex-col items-center justify-center bg-primary text-white rounded-full px-5 py-1.5 flex-1 max-w-[120px] min-h-[44px]"
    >
      <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24" aria-hidden="true">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
      </svg>
      <span class="font-label-md text-xs font-semibold mt-0.5">Search</span>
    </button>

    <button
      type="button"
      class="flex flex-col items-center justify-center text-on-surface-variant hover:bg-surface-container transition-colors p-2 rounded-lg flex-1 min-h-[44px]"
    >
      <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24" aria-hidden="true">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z" />
      </svg>
      <span class="font-label-md text-xs mt-0.5">Shared</span>
    </button>

    <button
      type="button"
      class="flex flex-col items-center justify-center text-on-surface-variant hover:bg-surface-container transition-colors p-2 rounded-lg flex-1 min-h-[44px]"
    >
      <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24" aria-hidden="true">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 7v10a2 2 0 002 2h14a2 2 0 002-2V9a2 2 0 00-2-2h-6l-2-2H5a2 2 0 00-2 2z" />
      </svg>
      <span class="font-label-md text-xs mt-0.5">Files</span>
    </button>
  </nav>
</div>
