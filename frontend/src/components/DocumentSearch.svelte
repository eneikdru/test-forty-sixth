<script>
  import { onMount } from 'svelte';

  // Search parameters
  let searchQuery = '';
  let selectedPathogenType = ''; // 'VIRUS', 'BACTERIA', 'PARASITE', 'FUNGI', 'OTHER' or ''
  let selectedCategory = ''; // Quick filter category if selected
  let page = 0;
  let size = 20;

  // Search state
  let isLoading = false;
  let hasSearched = false;
  let results = [];
  let totalElements = 0;
  let totalPages = 0;
  let serverError = null;

  // Recent search queries
  let recentSearches = ['Project Alpha', 'Invoices 2023', 'Q3 Report Drafts', 'Meeting Notes'];

  // Refine By Type Categories
  const categories = [
    { id: 'VIRUS', label: 'PDFs & Virus', sublabel: 'Documents & Forms', icon: 'picture_as_pdf', colorClass: 'bg-red-100 text-red-800 hover:bg-red-600 hover:text-white' },
    { id: 'BACTERIA', label: 'Sheets & Bacteria', sublabel: 'Data & Finance', icon: 'table', colorClass: 'bg-emerald-100 text-emerald-800 hover:bg-emerald-700 hover:text-white' },
    { id: 'PARASITE', label: 'Decks & Parasite', sublabel: 'Presentations', icon: 'slideshow', colorClass: 'bg-yellow-100 text-yellow-800 hover:bg-yellow-600 hover:text-white' },
    { id: 'FUNGI', label: 'Images & Fungi', sublabel: 'Assets & Media', icon: 'image', colorClass: 'bg-blue-100 text-blue-800 hover:bg-blue-600 hover:text-white' }
  ];

  async function executeSearch(query = searchQuery, pathogen = selectedPathogenType, pageNum = 0) {
    isLoading = true;
    serverError = null;
    hasSearched = true;
    page = pageNum;

    try {
      const params = new URLSearchParams();
      if (query && query.trim()) params.append('query', query.trim());
      if (pathogen && pathogen.trim()) params.append('pathogenType', pathogen.trim());
      params.append('page', pageNum.toString());
      params.append('size', size.toString());

      const response = await fetch(`/api/v1/materials/search?${params.toString()}`);
      if (!response.ok) {
        const errData = await response.json().catch(() => ({}));
        serverError = errData.message || `Search failed with status ${response.status}`;
        results = [];
        totalElements = 0;
        totalPages = 0;
      } else {
        const data = await response.json();
        results = data.content || [];
        totalElements = data.totalElements || results.length;
        totalPages = data.totalPages || 1;

        // Save search to recent searches list if non-empty
        if (query && query.trim() && !recentSearches.includes(query.trim())) {
          recentSearches = [query.trim(), ...recentSearches.slice(0, 5)];
        }
      }
    } catch (err) {
      serverError = `Network / server error during search: ${err.message}`;
      results = [];
      totalElements = 0;
      totalPages = 0;
    } finally {
      isLoading = false;
    }
  }

  function handleSearchSubmit(e) {
    if (e) e.preventDefault();
    executeSearch(searchQuery, selectedPathogenType, 0);
  }

  function clearSearch() {
    searchQuery = '';
    selectedPathogenType = '';
    selectedCategory = '';
    results = [];
    hasSearched = false;
    serverError = null;
  }

  function applyRecentSearch(queryStr) {
    searchQuery = queryStr;
    executeSearch(queryStr, selectedPathogenType, 0);
  }

  function toggleCategoryFilter(catId) {
    if (selectedPathogenType === catId) {
      selectedPathogenType = '';
      selectedCategory = '';
    } else {
      selectedPathogenType = catId;
      selectedCategory = catId;
    }
    executeSearch(searchQuery, selectedPathogenType, 0);
  }

  function setPathogenFilter(type) {
    selectedPathogenType = type;
    executeSearch(searchQuery, selectedPathogenType, 0);
  }

  onMount(() => {
    // Initial fetch to load available materials or default search state
    executeSearch('', '', 0);
  });
</script>

<svelte:head>
  <link href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:wght,FILL@100..700,0..1&display=swap" rel="stylesheet"/>
</svelte:head>

<div class="min-h-screen bg-[#f8fafc] text-[#334155] font-sans antialiased flex flex-col pt-14 pb-20 selection:bg-[#0050cb]/20 selection:text-[#0050cb]">
  <!-- Top Header App Bar -->
  <header class="fixed top-0 left-0 right-0 z-50 bg-white/95 backdrop-blur-md border-b border-slate-200 flex items-center justify-between px-4 sm:px-8 h-14 w-full shadow-xs">
    <button
      type="button"
      on:click={clearSearch}
      aria-label="Back to home"
      class="min-w-[44px] min-h-[44px] text-slate-600 hover:text-slate-900 hover:bg-slate-100 transition-colors rounded-full flex items-center justify-center focus:outline-none focus:ring-2 focus:ring-[#0050cb]"
    >
      <span class="material-symbols-outlined text-2xl">arrow_back</span>
    </button>
    <h1 class="text-lg sm:text-xl font-bold text-[#0050cb] text-center flex-1 truncate px-2">
      Search Documents
    </h1>
    <button
      type="button"
      on:click={clearSearch}
      aria-label="Clear search input and reset"
      class="min-w-[44px] min-h-[44px] text-slate-600 hover:text-slate-900 hover:bg-slate-100 transition-colors rounded-full flex items-center justify-center focus:outline-none focus:ring-2 focus:ring-[#0050cb]"
    >
      <span class="material-symbols-outlined text-2xl">close</span>
    </button>
  </header>

  <!-- Main Content Area -->
  <main class="flex-1 w-full max-w-6xl mx-auto px-4 sm:px-8 py-8 flex flex-col gap-6">
    <!-- Search Input Form -->
    <section aria-label="Search Form">
      <form on:submit={handleSearchSubmit} class="relative flex items-center w-full min-h-[56px] bg-white border-2 border-slate-300 focus-within:border-[#0050cb] focus-within:ring-4 focus-within:ring-[#0050cb]/20 rounded-xl transition-all shadow-xs">
        <span class="material-symbols-outlined text-slate-400 ml-4 text-2xl shrink-0" aria-hidden="true">search</span>
        <input
          type="text"
          bind:value={searchQuery}
          placeholder="Find files, folders, protocols, or pathogen materials..."
          aria-label="Search input"
          class="flex-1 h-full bg-transparent border-none focus:ring-0 focus:outline-none px-4 text-slate-800 text-base sm:text-lg placeholder-slate-400 font-medium"
        />
        {#if searchQuery}
          <button
            type="button"
            on:click={() => { searchQuery = ''; executeSearch('', selectedPathogenType, 0); }}
            aria-label="Clear search query"
            class="min-w-[44px] min-h-[44px] mr-1 text-slate-400 hover:text-[#0050cb] transition-colors flex items-center justify-center rounded-full focus:outline-none focus:ring-2 focus:ring-[#0050cb]"
          >
            <span class="material-symbols-outlined text-xl">cancel</span>
          </button>
        {/if}
        <button
          type="submit"
          aria-label="Perform search"
          class="min-w-[44px] min-h-[44px] mr-2 px-5 py-2.5 bg-[#0050cb] hover:bg-[#003fa4] active:scale-95 text-white font-semibold text-sm rounded-lg transition-all shadow-xs focus:outline-none focus:ring-2 focus:ring-[#0050cb] shrink-0 flex items-center gap-1"
        >
          <span>Search</span>
        </button>
      </form>
    </section>

    <!-- Pathogen Type Quick Selector Filter Chips -->
    <section aria-label="Pathogen Type Filters" class="flex flex-col gap-2">
      <h2 class="text-xs font-bold text-slate-500 uppercase tracking-wider">
        Filter By Pathogen Type
      </h2>
      <div class="flex flex-wrap gap-2 items-center">
        <button
          type="button"
          on:click={() => setPathogenFilter('')}
          class="min-h-[44px] px-4 py-2 rounded-full text-sm font-semibold transition-all border flex items-center gap-2 focus:outline-none focus:ring-2 focus:ring-[#0050cb]
            {selectedPathogenType === '' ? 'bg-[#0050cb] text-white border-[#0050cb] shadow-xs' : 'bg-white text-slate-700 border-slate-300 hover:bg-slate-100'}"
        >
          All Types
        </button>
        {#each ['VIRUS', 'BACTERIA', 'PARASITE', 'FUNGI', 'OTHER'] as type}
          <button
            type="button"
            on:click={() => setPathogenFilter(selectedPathogenType === type ? '' : type)}
            class="min-h-[44px] px-4 py-2 rounded-full text-sm font-semibold transition-all border flex items-center gap-2 focus:outline-none focus:ring-2 focus:ring-[#0050cb]
              {selectedPathogenType === type ? 'bg-[#0050cb] text-white border-[#0050cb] shadow-xs' : 'bg-white text-slate-700 border-slate-300 hover:bg-slate-100'}"
          >
            {type}
          </button>
        {/each}
      </div>
    </section>

    <!-- Recent Searches Chips -->
    {#if recentSearches.length > 0}
      <section aria-label="Recent Searches" class="flex flex-col gap-2">
        <h2 class="text-xs font-bold text-slate-500 uppercase tracking-wider">
          Recent Searches
        </h2>
        <div class="flex flex-wrap gap-2.5">
          {#each recentSearches as item}
            <button
              type="button"
              on:click={() => applyRecentSearch(item)}
              class="min-h-[44px] bg-slate-100 hover:bg-slate-200 text-slate-700 px-4 py-2 rounded-full text-sm font-medium flex items-center gap-2 border border-slate-300/60 transition-colors focus:outline-none focus:ring-2 focus:ring-[#0050cb] active:scale-95"
            >
              <span class="material-symbols-outlined text-lg text-slate-500" aria-hidden="true">history</span>
              <span>{item}</span>
            </button>
          {/each}
        </div>
      </section>
    {/if}

    <!-- Quick Refine Cards Category Section -->
    <section aria-label="Suggested Categories" class="flex flex-col gap-3">
      <h2 class="text-xs font-bold text-slate-500 uppercase tracking-wider">
        Refine By Type
      </h2>
      <div class="grid grid-cols-2 md:grid-cols-4 gap-3">
        {#each categories as cat}
          <button
            type="button"
            on:click={() => toggleCategoryFilter(cat.id)}
            class="bg-white border-2 p-4 rounded-xl flex items-start gap-3 text-left transition-all cursor-pointer select-none focus:outline-none focus:ring-2 focus:ring-[#0050cb]
              {selectedCategory === cat.id ? 'border-[#0050cb] ring-2 ring-[#0050cb]/20 bg-blue-50/40' : 'border-slate-200 hover:border-slate-300 hover:shadow-md'}"
          >
            <div class="p-2.5 rounded-lg shrink-0 transition-colors {cat.colorClass}">
              <span class="material-symbols-outlined text-2xl" aria-hidden="true">{cat.icon}</span>
            </div>
            <div class="min-w-0">
              <p class="font-bold text-slate-900 text-base leading-tight truncate">{cat.label}</p>
              <p class="text-xs text-slate-500 mt-1 truncate">{cat.sublabel}</p>
            </div>
          </button>
        {/each}
      </div>
    </section>

    <!-- Error Banner -->
    {#if serverError}
      <div class="p-4 bg-red-50 text-red-900 rounded-xl border border-red-300 flex items-start gap-3" role="alert">
        <span class="material-symbols-outlined text-red-600 text-2xl shrink-0">error</span>
        <div>
          <h3 class="font-bold text-sm">Search Error</h3>
          <p class="text-sm mt-0.5">{serverError}</p>
        </div>
      </div>
    {/if}

    <!-- Search Results / Loading / Empty State Section -->
    <section aria-label="Search Results" class="mt-4">
      <!-- Loading Indicator -->
      {#if isLoading}
        <div class="flex flex-col items-center justify-center py-16 gap-4 text-center" aria-live="polite" aria-busy="true">
          <div class="w-12 h-12 border-4 border-[#0050cb] border-t-transparent rounded-full animate-spin"></div>
          <p class="text-slate-600 font-semibold text-base animate-pulse">
            Searching knowledge base documents...
          </p>
        </div>
      {:else if hasSearched && results.length === 0}
        <!-- Clear No Results Message -->
        <div class="bg-white border-2 border-dashed border-slate-300 rounded-2xl p-12 text-center flex flex-col items-center gap-3 my-4">
          <div class="w-16 h-16 rounded-full bg-slate-100 flex items-center justify-center text-slate-400 mb-2">
            <span class="material-symbols-outlined text-4xl">search_off</span>
          </div>
          <h3 class="text-xl font-bold text-slate-800">No results found</h3>
          <p class="text-slate-500 text-sm max-w-md">
            We couldn't find any epidemiological materials matching "{searchQuery || selectedPathogenType}". Try adjusting your keywords or clearing pathogen filters.
          </p>
          <button
            type="button"
            on:click={clearSearch}
            class="min-h-[44px] px-6 py-2.5 bg-[#0050cb] hover:bg-[#003fa4] text-white font-semibold text-sm rounded-xl transition-all shadow-xs mt-2 focus:outline-none focus:ring-2 focus:ring-[#0050cb]"
          >
            Clear Search & Filters
          </button>
        </div>
      {:else if results.length > 0}
        <!-- Search Results Count -->
        <div class="flex items-center justify-between mb-4">
          <p class="text-sm font-semibold text-slate-600">
            Found <span class="text-[#0050cb] font-bold">{totalElements}</span> matching document{totalElements === 1 ? '' : 's'}
          </p>
          {#if selectedPathogenType}
            <span class="text-xs font-bold uppercase tracking-wider px-2.5 py-1 bg-blue-100 text-[#0050cb] rounded-md">
              Filter: {selectedPathogenType}
            </span>
          {/if}
        </div>

        <!-- Search Results List -->
        <div class="grid grid-cols-1 gap-4">
          {#each results as doc}
            <article class="bg-white border border-slate-200 rounded-xl p-5 hover:border-[#0050cb] hover:shadow-md transition-all flex flex-col gap-2">
              <div class="flex items-start justify-between gap-4">
                <h3 class="text-lg font-bold text-slate-900 leading-snug hover:text-[#0050cb] transition-colors cursor-pointer">
                  {doc.title || 'Untitled Document'}
                </h3>
                {#if doc.pathogenType}
                  <span class="text-xs font-bold px-3 py-1 rounded-full bg-slate-100 text-slate-700 border border-slate-200 shrink-0">
                    {doc.pathogenType}
                  </span>
                {/if}
              </div>

              {#if doc.content}
                <p class="text-slate-600 text-sm line-clamp-2 leading-relaxed">
                  {doc.content}
                </p>
              {/if}

              <div class="flex items-center justify-between pt-3 mt-1 border-t border-slate-100 text-xs text-slate-500 font-medium">
                <span class="flex items-center gap-1">
                  <span class="material-symbols-outlined text-sm" aria-hidden="true">calendar_today</span>
                  <span>ID: {doc.id || 'N/A'}</span>
                </span>
                {#if doc.status}
                  <span class="px-2.5 py-0.5 rounded-full font-semibold uppercase text-[10px]
                    {doc.status === 'PUBLISHED' ? 'bg-emerald-100 text-emerald-800' : 'bg-slate-100 text-slate-600'}">
                    {doc.status}
                  </span>
                {/if}
              </div>
            </article>
          {/each}
        </div>
      {/if}
    </section>
  </main>

  <!-- Bottom Navigation Bar (Mobile) -->
  <nav class="md:hidden fixed bottom-0 left-0 right-0 h-16 bg-white border-t border-slate-200 flex justify-around items-center px-2 z-50 shadow-lg">
    <button type="button" class="min-w-[44px] min-h-[44px] flex flex-col items-center justify-center text-slate-500 hover:text-[#0050cb] transition-colors flex-1 py-1 focus:outline-none">
      <span class="material-symbols-outlined text-2xl" aria-hidden="true">history</span>
      <span class="text-[11px] font-semibold mt-0.5">Recent</span>
    </button>
    <button type="button" class="min-w-[44px] min-h-[44px] flex flex-col items-center justify-center bg-[#0050cb] text-white rounded-full px-4 py-1.5 shadow-xs flex-1 max-w-[100px] focus:outline-none">
      <span class="material-symbols-outlined text-2xl" aria-hidden="true">search</span>
      <span class="text-[11px] font-bold">Search</span>
    </button>
    <button type="button" class="min-w-[44px] min-h-[44px] flex flex-col items-center justify-center text-slate-500 hover:text-[#0050cb] transition-colors flex-1 py-1 focus:outline-none">
      <span class="material-symbols-outlined text-2xl" aria-hidden="true">group</span>
      <span class="text-[11px] font-semibold mt-0.5">Shared</span>
    </button>
    <button type="button" class="min-w-[44px] min-h-[44px] flex flex-col items-center justify-center text-slate-500 hover:text-[#0050cb] transition-colors flex-1 py-1 focus:outline-none">
      <span class="material-symbols-outlined text-2xl" aria-hidden="true">folder</span>
      <span class="text-[11px] font-semibold mt-0.5">Files</span>
    </button>
  </nav>
</div>
