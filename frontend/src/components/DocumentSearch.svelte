<script>
  import { onMount } from 'svelte';

  let searchQuery = '';
  let selectedPathogen = '';
  let searchResults = [];
  let isSearching = false;
  let hasSearched = false;
  let searchError = null;

  let recentSearches = [
    'Influenza Protocol',
    'Outbreak 2026',
    'Cholera Report',
    'Surveillance Draft'
  ];

  const pathogenOptions = [
    { label: 'All Types', value: '' },
    { label: 'Virus', value: 'VIRUS' },
    { label: 'Bacteria', value: 'BACTERIA' },
    { label: 'Parasite', value: 'PARASITE' },
    { label: 'Fungi', value: 'FUNGI' },
    { label: 'Other', value: 'OTHER' }
  ];

  async function handleSearch() {
    isSearching = true;
    searchError = null;
    hasSearched = true;

    try {
      const params = new URLSearchParams();
      if (searchQuery.trim()) {
        params.append('query', searchQuery.trim());
      }
      if (selectedPathogen) {
        params.append('pathogenType', selectedPathogen);
      }

      const response = await fetch(`/api/v1/materials/search?${params.toString()}`);

      if (!response.ok) {
        throw new Error(`Search request failed (Status ${response.status})`);
      }

      const data = await response.json();
      searchResults = data.content || [];

      // Add to recent searches if non-empty query
      if (searchQuery.trim() && !recentSearches.includes(searchQuery.trim())) {
        recentSearches = [searchQuery.trim(), ...recentSearches.slice(0, 3)];
      }
    } catch (err) {
      searchError = err.message || 'An error occurred while searching.';
      searchResults = [];
    } finally {
      isSearching = false;
    }
  }

  function handleClear() {
    searchQuery = '';
    searchResults = [];
    hasSearched = false;
    searchError = null;
  }

  function selectRecentSearch(query) {
    searchQuery = query;
    handleSearch();
  }

  function selectPathogenFilter(pathogenValue) {
    selectedPathogen = pathogenValue;
    handleSearch();
  }

  function formatDate(isoString) {
    if (!isoString) return '';
    try {
      return new Date(isoString).toLocaleDateString(undefined, {
        year: 'numeric',
        month: 'short',
        day: 'numeric'
      });
    } catch (e) {
      return isoString;
    }
  }
</script>

<div class="min-h-screen bg-[#f9f9ff] text-[#111c2d] flex flex-col font-sans">
  <!-- Top Navigation Header -->
  <header class="sticky top-0 z-40 bg-white border-b border-[#c2c6d8] shadow-sm px-4 h-14 flex items-center justify-between">
    <div class="flex items-center gap-3">
      <button
        type="button"
        aria-label="Go back"
        class="min-h-[44px] min-w-[44px] flex items-center justify-center rounded-full text-[#424656] hover:bg-[#e7eeff] focus:outline-none focus:ring-2 focus:ring-[#0050cb]"
      >
        <span class="material-symbols-outlined">arrow_back</span>
      </button>
      <h1 class="text-lg md:text-xl font-semibold text-[#0050cb]">
        Search Documents
      </h1>
    </div>

    <span class="text-xs font-semibold px-2.5 py-1 bg-[#e7eeff] text-[#0050cb] rounded-full border border-[#b3c5ff]">
      Epidemiology KB
    </span>
  </header>

  <!-- Main Content Area -->
  <main class="flex-1 max-w-5xl w-full mx-auto px-4 py-6 flex flex-col gap-6 pb-20 md:pb-8">
    <!-- Search Input Form -->
    <section aria-label="Search form">
      <form on:submit|preventDefault={handleSearch} class="flex flex-col gap-3">
        <div class="relative flex items-center w-full bg-white border border-[#727687] rounded-lg shadow-sm focus-within:border-[#0050cb] focus-within:ring-2 focus-within:ring-[#0050cb]/20 transition-all">
          <label for="doc-search-input" class="sr-only">Search epidemiological documents</label>
          <span class="material-symbols-outlined text-[#727687] ml-3.5 select-none" aria-hidden="true">search</span>
          <input
            id="doc-search-input"
            type="text"
            bind:value={searchQuery}
            placeholder="Find files, protocols, or epidemiological data..."
            class="w-full h-12 bg-transparent border-none focus:outline-none px-3 text-base text-[#111c2d] placeholder-[#727687]"
          />

          {#if searchQuery}
            <button
              type="button"
              on:click={handleClear}
              aria-label="Clear search text"
              class="min-h-[44px] min-w-[44px] flex items-center justify-center text-[#727687] hover:text-[#0050cb] focus:outline-none focus:ring-2 focus:ring-[#0050cb]"
            >
              <span class="material-symbols-outlined">cancel</span>
            </button>
          {/if}

          <button
            type="submit"
            disabled={isSearching}
            aria-label="Execute search"
            class="min-h-[44px] px-5 mr-1 bg-[#0050cb] hover:bg-[#003fa4] text-white font-medium text-sm rounded-md transition-colors flex items-center gap-2 disabled:opacity-50 focus:outline-none focus:ring-2 focus:ring-offset-1 focus:ring-[#0050cb]"
          >
            {#if isSearching}
              <span class="inline-block animate-spin w-4 h-4 border-2 border-white border-t-transparent rounded-full" aria-hidden="true"></span>
              <span>Searching</span>
            {:else}
              <span class="material-symbols-outlined text-lg" aria-hidden="true">search</span>
              <span class="hidden sm:inline">Search</span>
            {/if}
          </button>
        </div>
      </form>
    </section>

    <!-- Pathogen Filters -->
    <section aria-label="Pathogen Classification Filter" class="flex flex-col gap-2">
      <h2 class="text-xs font-semibold text-[#424656] uppercase tracking-wider">
        Pathogen Filter
      </h2>
      <div class="flex flex-wrap gap-2">
        {#each pathogenOptions as opt}
          <button
            type="button"
            on:click={() => selectPathogenFilter(opt.value)}
            class="min-h-[44px] px-4 py-2 rounded-full text-sm font-medium transition-colors border focus:outline-none focus:ring-2 focus:ring-[#0050cb] {selectedPathogen === opt.value ? 'bg-[#0050cb] text-white border-[#0050cb]' : 'bg-white text-[#424656] border-[#c2c6d8] hover:bg-[#e7eeff]'}"
          >
            {opt.label}
          </button>
        {/each}
      </div>
    </section>

    <!-- Recent Searches Section -->
    {#if recentSearches.length > 0}
      <section aria-label="Recent searches" class="flex flex-col gap-2">
        <h2 class="text-xs font-semibold text-[#424656] uppercase tracking-wider">
          Recent Searches
        </h2>
        <div class="flex flex-wrap gap-2">
          {#each recentSearches as item}
            <button
              type="button"
              on:click={() => selectRecentSearch(item)}
              class="min-h-[44px] px-3.5 py-2 rounded-full text-sm font-normal bg-white text-[#424656] border border-[#c2c6d8] hover:bg-[#e7eeff] hover:border-[#0050cb] transition-colors flex items-center gap-1.5 focus:outline-none focus:ring-2 focus:ring-[#0050cb]"
            >
              <span class="material-symbols-outlined text-base text-[#727687]" aria-hidden="true">history</span>
              <span>{item}</span>
            </button>
          {/each}
        </div>
      </section>
    {/if}

    <!-- Search Results / Loading / Empty State Area -->
    <section aria-label="Search results" class="flex flex-col gap-4 mt-2">
      <div class="flex items-center justify-between border-b border-[#c2c6d8] pb-2">
        <h2 class="text-lg font-semibold text-[#111c2d]">
          Document Results
        </h2>
        {#if hasSearched && !isSearching}
          <span class="text-xs text-[#515f74] font-medium">
            Found {searchResults.length} {searchResults.length === 1 ? 'material' : 'materials'}
          </span>
        {/if}
      </div>

      <!-- State 1: Searching Loading Indicator -->
      {#if isSearching}
        <div
          role="status"
          aria-live="polite"
          class="flex flex-col items-center justify-center p-12 bg-white rounded-xl border border-[#c2c6d8] text-center gap-3"
        >
          <div class="inline-block animate-spin w-8 h-8 border-4 border-[#0050cb] border-t-transparent rounded-full" aria-hidden="true"></div>
          <p class="text-sm font-medium text-[#0050cb]">Searching epidemiological documents...</p>
          <span class="sr-only">Search operation in progress, please wait.</span>
        </div>

      <!-- State 2: Search Error -->
      {:else if searchError}
        <div
          role="alert"
          class="p-4 bg-[#ffdad6] text-[#93000a] rounded-xl border border-[#ba1a1a] flex items-start gap-3"
        >
          <span class="material-symbols-outlined text-xl" aria-hidden="true">error</span>
          <div>
            <p class="font-semibold text-sm">Error executing search</p>
            <p class="text-xs mt-0.5">{searchError}</p>
          </div>
        </div>

      <!-- State 3: Completed with No Results Found -->
      {:else if hasSearched && searchResults.length === 0}
        <div
          role="region"
          aria-label="No results"
          class="flex flex-col items-center justify-center p-10 bg-white rounded-xl border border-dashed border-[#c2c6d8] text-center gap-3"
        >
          <div class="p-3 bg-[#e7eeff] text-[#0050cb] rounded-full select-none">
            <span class="material-symbols-outlined text-3xl" aria-hidden="true">find_in_page</span>
          </div>
          <h3 class="text-base font-semibold text-[#111c2d]">No results found</h3>
          <p class="text-sm text-[#515f74] max-w-md">
            We couldn't find any epidemiological materials matching "{searchQuery || selectedPathogen}".
            Try adjusting your search terms or selecting a different pathogen filter.
          </p>
          <button
            type="button"
            on:click={handleClear}
            class="min-h-[44px] px-4 py-2 mt-2 text-sm font-medium text-[#0050cb] bg-[#e7eeff] hover:bg-[#dae1ff] rounded-lg transition-colors focus:outline-none focus:ring-2 focus:ring-[#0050cb]"
          >
            Reset Filters
          </button>
        </div>

      <!-- State 4: Default Initial State (Before Search) -->
      {:else if !hasSearched}
        <div class="p-8 bg-white rounded-xl border border-[#c2c6d8] text-center text-[#515f74]">
          <span class="material-symbols-outlined text-4xl text-[#727687] mb-2" aria-hidden="true">feature_search</span>
          <p class="text-sm font-medium">Enter a query or select a pathogen filter above to start searching documents.</p>
        </div>

      <!-- State 5: Results Display List -->
      {:else}
        <ul class="flex flex-col gap-3" aria-label="Document search results list">
          {#each searchResults as item (item.id)}
            <li class="bg-white p-4 rounded-xl border border-[#c2c6d8] hover:border-[#0050cb] transition-colors shadow-sm flex flex-col sm:flex-row sm:items-center justify-between gap-4">
              <div class="flex items-start gap-3 flex-1">
                <div class="p-2.5 bg-[#e7eeff] text-[#0050cb] rounded-lg select-none shrink-0 mt-0.5">
                  <span class="material-symbols-outlined text-xl" aria-hidden="true">description</span>
                </div>
                <div class="flex flex-col gap-1">
                  <h3 class="text-base font-semibold text-[#111c2d] leading-snug">
                    {item.title}
                  </h3>
                  {#if item.content}
                    <p class="text-xs text-[#424656] line-clamp-2">{item.content}</p>
                  {/if}
                  <div class="flex flex-wrap items-center gap-2 mt-1">
                    <span class="text-xs font-semibold px-2 py-0.5 rounded bg-[#dae1ff] text-[#003fa4]">
                      {item.pathogenType}
                    </span>
                    {#if item.createdAt}
                      <span class="text-xs text-[#727687]">
                        Added: {formatDate(item.createdAt)}
                      </span>
                    {/if}
                  </div>
                </div>
              </div>

              <div class="shrink-0 flex items-center justify-end border-t sm:border-t-0 pt-3 sm:pt-0 border-[#c2c6d8]">
                {#if item.downloadUrl}
                  <a
                    href={item.downloadUrl}
                    target="_blank"
                    rel="noopener noreferrer"
                    aria-label={`Download document: ${item.title}`}
                    class="min-h-[44px] px-4 py-2 text-sm font-semibold text-white bg-[#0050cb] hover:bg-[#003fa4] rounded-lg transition-colors inline-flex items-center gap-1.5 focus:outline-none focus:ring-2 focus:ring-[#0050cb]"
                  >
                    <span class="material-symbols-outlined text-base" aria-hidden="true">download</span>
                    <span>Download</span>
                  </a>
                {/if}
              </div>
            </li>
          {/each}
        </ul>
      {/if}
    </section>
  </main>

  <!-- Mobile Touch Bottom Navigation Bar -->
  <nav
    aria-label="Mobile Navigation"
    class="md:hidden fixed bottom-0 left-0 w-full bg-white border-t border-[#c2c6d8] h-16 flex items-center justify-around px-2 z-40 shadow-lg"
  >
    <button
      type="button"
      class="flex flex-col items-center justify-center text-[#424656] hover:text-[#0050cb] p-2 min-h-[44px] min-w-[44px] rounded-lg focus:outline-none focus:ring-2 focus:ring-[#0050cb]"
    >
      <span class="material-symbols-outlined text-xl">history</span>
      <span class="text-[10px] font-medium mt-0.5">Recent</span>
    </button>
    <button
      type="button"
      aria-current="page"
      class="flex flex-col items-center justify-center bg-[#0050cb] text-white px-4 py-1.5 min-h-[44px] rounded-full shadow-sm focus:outline-none focus:ring-2 focus:ring-[#0050cb]"
    >
      <span class="material-symbols-outlined text-xl">search</span>
      <span class="text-[10px] font-semibold">Search</span>
    </button>
    <button
      type="button"
      class="flex flex-col items-center justify-center text-[#424656] hover:text-[#0050cb] p-2 min-h-[44px] min-w-[44px] rounded-lg focus:outline-none focus:ring-2 focus:ring-[#0050cb]"
    >
      <span class="material-symbols-outlined text-xl">group</span>
      <span class="text-[10px] font-medium mt-0.5">Shared</span>
    </button>
    <button
      type="button"
      class="flex flex-col items-center justify-center text-[#424656] hover:text-[#0050cb] p-2 min-h-[44px] min-w-[44px] rounded-lg focus:outline-none focus:ring-2 focus:ring-[#0050cb]"
    >
      <span class="material-symbols-outlined text-xl">folder</span>
      <span class="text-[10px] font-medium mt-0.5">Files</span>
    </button>
  </nav>
</div>
