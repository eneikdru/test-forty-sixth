<script>
  import { onMount } from 'svelte';

  let searchQuery = '';
  let pathogenFilter = '';
  let searchResults = [];
  let isSearching = false;
  let searchError = null;
  let dispatchedEvents = [];

  // Material Publishing state
  let publishMaterialId = '123e4567-e89b-12d3-a456-426614174001';
  let publishTitle = 'Influenza Surveillance Protocol 2026';
  let publishPathogen = 'VIRUS';
  let publishStatus = '';
  let isPublishing = false;

  // Background non-blocking dispatch function
  function dispatchTelemetryEvent(eventType, payload) {
    const eventRecord = {
      id: crypto.randomUUID ? crypto.randomUUID() : 'evt-' + Date.now(),
      eventType,
      timestamp: new Date().toISOString(),
      payload,
      status: 'DISPATCHING...'
    };

    dispatchedEvents = [eventRecord, ...dispatchedEvents];

    // Fire and forget background fetch
    fetch('/api/v1/telemetry/events', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        id: eventRecord.id,
        eventType: eventRecord.eventType,
        timestamp: eventRecord.timestamp,
        payload: eventRecord.payload
      })
    })
    .then(async (res) => {
      if (res.ok) {
        eventRecord.status = 'DISPATCHED (201 Created)';
      } else {
        const errText = await res.text();
        eventRecord.status = `FAILED (${res.status}): ${errText}`;
      }
      dispatchedEvents = [...dispatchedEvents];
    })
    .catch((err) => {
      eventRecord.status = `DISPATCH_ERROR: ${err.message}`;
      dispatchedEvents = [...dispatchedEvents];
    });
  }

  async function handleSearch() {
    isSearching = true;
    searchError = null;
    const startTime = performance.now();

    try {
      const params = new URLSearchParams();
      if (searchQuery.trim()) params.append('query', searchQuery.trim());
      if (pathogenFilter) params.append('pathogenType', pathogenFilter);

      const response = await fetch(`/api/v1/materials/search?${params.toString()}`);
      const endTime = performance.now();
      const durationMs = Math.round(endTime - startTime);

      if (!response.ok) {
        throw new Error(`Search failed with status ${response.status}`);
      }

      const data = await response.json();
      searchResults = data.content || [];

      // Requirement 1: Given a user performs a search, When results appear, Then the duration is measured and dispatched.
      // Requirement 3: Given a zero-result search, When it occurs, Then the query text and zero-result event are dispatched.
      const telemetryPayload = {
        query: searchQuery.trim(),
        latencyMs: durationMs,
        latency: durationMs,
        resultCount: searchResults.length,
        pathogenType: pathogenFilter || undefined
      };

      dispatchTelemetryEvent('SEARCH', telemetryPayload);

    } catch (err) {
      searchError = err.message;
      searchResults = [];
    } finally {
      isSearching = false;
    }
  }

  async function handlePublish() {
    isPublishing = true;
    publishStatus = '';

    try {
      const response = await fetch(`/api/v1/materials/${publishMaterialId}/publish`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          status: 'PUBLISHED',
          comment: 'Published via Telemetry UI Dispatcher'
        })
      });

      if (!response.ok) {
        const errData = await response.json().catch(() => ({}));
        throw new Error(errData.message || `Publishing failed with status ${response.status}`);
      }

      publishStatus = 'Material successfully published!';

      // Requirement 2: Given a user publishes a material, When successful, Then a publication event is dispatched.
      dispatchTelemetryEvent('PUBLICATION', {
        materialId: publishMaterialId,
        title: publishTitle,
        pathogenType: publishPathogen,
        action: 'PUBLISH'
      });

    } catch (err) {
      publishStatus = `Error: ${err.message}`;
    } finally {
      isPublishing = false;
    }
  }
</script>

<main class="min-h-screen bg-[#f9f9ff] text-[#111c2d] p-4 md:p-8 max-w-6xl mx-auto">
  <header class="mb-8 border-b border-[#c2c6d8] pb-4">
    <div class="flex items-center justify-between">
      <div>
        <h1 class="text-2xl md:text-3xl font-bold text-[#0050cb]">Epidemiology Knowledge Base</h1>
        <p class="text-sm md:text-base text-[#424656] mt-1">Telemetry Frontend Dispatcher Module</p>
      </div>
      <span class="bg-[#e7eeff] text-[#0050cb] text-xs font-semibold px-3 py-1 rounded-full border border-[#b3c5ff]">
        BARCAN-TAG-11 Active
      </span>
    </div>
  </header>

  <div class="grid grid-cols-1 lg:grid-cols-3 gap-8">
    <!-- Main Interaction Panel -->
    <section class="lg:col-span-2 space-y-6">
      <!-- Search Section -->
      <div class="bg-white rounded-xl p-6 shadow-sm border border-[#c2c6d8]">
        <h2 class="text-xl font-semibold mb-4 text-[#111c2d]">Search Epidemiological Materials</h2>

        <form on:submit|preventDefault={handleSearch} class="space-y-4">
          <div class="flex flex-col sm:flex-row gap-3">
            <div class="flex-1">
              <label for="search-query" class="block text-xs font-medium text-[#424656] mb-1">Query String</label>
              <input
                id="search-query"
                type="text"
                bind:value={searchQuery}
                placeholder="e.g. influenza, cholera, outbreak..."
                class="w-full px-4 py-2 border border-[#727687] rounded-lg focus:ring-2 focus:ring-[#0050cb] focus:outline-none text-sm"
              />
            </div>
            <div class="w-full sm:w-48">
              <label for="pathogen-filter" class="block text-xs font-medium text-[#424656] mb-1">Pathogen Filter</label>
              <select
                id="pathogen-filter"
                bind:value={pathogenFilter}
                class="w-full px-3 py-2 border border-[#727687] rounded-lg focus:ring-2 focus:ring-[#0050cb] focus:outline-none text-sm"
              >
                <option value="">All Pathogens</option>
                <option value="VIRUS">VIRUS</option>
                <option value="BACTERIA">BACTERIA</option>
                <option value="PARASITE">PARASITE</option>
                <option value="FUNGI">FUNGI</option>
                <option value="OTHER">OTHER</option>
              </select>
            </div>
          </div>

          <div class="flex justify-end gap-2">
            <button
              type="button"
              on:click={() => { searchQuery = 'nonexistent_zero_result_query'; handleSearch(); }}
              class="px-4 py-2 text-xs font-medium text-[#515f74] bg-[#d5e3fc] hover:bg-[#b9c7df] rounded-lg transition-colors"
            >
              Simulate Zero-Result Search
            </button>
            <button
              type="submit"
              disabled={isSearching}
              class="px-6 py-2 text-sm font-semibold text-white bg-[#0050cb] hover:bg-[#003fa4] rounded-lg transition-colors disabled:opacity-50"
            >
              {isSearching ? 'Searching...' : 'Search'}
            </button>
          </div>
        </form>

        <!-- Search Results List -->
        <div class="mt-6 border-t border-[#e7eeff] pt-4">
          <h3 class="text-sm font-semibold text-[#424656] mb-3">
            Search Results ({searchResults.length})
          </h3>

          {#if searchError}
            <div class="p-3 bg-[#ffdad6] text-[#93000a] rounded-lg text-sm border border-[#ba1a1a]">
              {searchError}
            </div>
          {:else if searchResults.length === 0}
            <div class="p-4 bg-[#f9f9ff] text-[#515f74] text-center rounded-lg border border-dashed border-[#c2c6d8] text-sm">
              No materials found. Perform a search above to see background telemetry dispatching in action.
            </div>
          {:else}
            <ul class="space-y-3">
              {#each searchResults as item}
                <li class="p-4 bg-[#f9f9ff] border border-[#d5e3fc] rounded-lg hover:border-[#0050cb] transition-colors">
                  <div class="flex justify-between items-start">
                    <div>
                      <h4 class="font-semibold text-[#0050cb]">{item.title}</h4>
                      <p class="text-xs text-[#515f74] mt-1">{item.content || 'No content preview available'}</p>
                    </div>
                    <span class="text-xs font-bold px-2 py-0.5 rounded bg-[#dee8ff] text-[#003fa4]">
                      {item.pathogenType}
                    </span>
                  </div>
                </li>
              {/each}
            </ul>
          {/if}
        </div>
      </div>

      <!-- Publication Section -->
      <div class="bg-white rounded-xl p-6 shadow-sm border border-[#c2c6d8]">
        <h2 class="text-xl font-semibold mb-4 text-[#111c2d]">Material Publication</h2>

        <div class="space-y-4">
          <div class="grid grid-cols-1 md:grid-cols-2 gap-3">
            <div>
              <label for="material-id" class="block text-xs font-medium text-[#424656] mb-1">Material UUID</label>
              <input
                id="material-id"
                type="text"
                bind:value={publishMaterialId}
                class="w-full px-3 py-2 border border-[#727687] rounded-lg text-sm focus:ring-2 focus:ring-[#0050cb]"
              />
            </div>
            <div>
              <label for="material-title" class="block text-xs font-medium text-[#424656] mb-1">Title</label>
              <input
                id="material-title"
                type="text"
                bind:value={publishTitle}
                class="w-full px-3 py-2 border border-[#727687] rounded-lg text-sm focus:ring-2 focus:ring-[#0050cb]"
              />
            </div>
          </div>

          <div class="flex items-center justify-between pt-2">
            {#if publishStatus}
              <span class="text-xs font-medium text-[#0050cb]">{publishStatus}</span>
            {:else}
              <span></span>
            {/if}

            <button
              on:click={handlePublish}
              disabled={isPublishing}
              class="px-5 py-2 text-sm font-semibold text-white bg-[#0050cb] hover:bg-[#003fa4] rounded-lg transition-colors disabled:opacity-50"
            >
              {isPublishing ? 'Publishing...' : 'Publish Material'}
            </button>
          </div>
        </div>
      </div>
    </section>

    <!-- Telemetry Log Monitor Panel -->
    <aside class="bg-white rounded-xl p-6 shadow-sm border border-[#c2c6d8] flex flex-col h-[600px]">
      <div class="flex items-center justify-between mb-4 border-b pb-2">
        <h2 class="text-lg font-bold text-[#111c2d]">Background Telemetry Dispatcher</h2>
        <span class="text-xs bg-[#dee8ff] text-[#0050cb] px-2 py-0.5 rounded font-mono">Async Pipeline</span>
      </div>

      <div class="flex-1 overflow-y-auto space-y-3 font-mono text-xs">
        {#if dispatchedEvents.length === 0}
          <div class="text-[#727687] text-center py-12 italic">
            No telemetry events dispatched yet. Perform a search or publication action.
          </div>
        {:else}
          {#each dispatchedEvents as evt}
            <div class="p-3 rounded-lg border bg-[#f9f9ff] border-[#d5e3fc]">
              <div class="flex justify-between items-center mb-1">
                <span class="font-bold text-[#0050cb]">{evt.eventType}</span>
                <span class="text-[10px] text-[#727687]">{new Date(evt.timestamp).toLocaleTimeString()}</span>
              </div>
              <div class="text-[11px] text-[#424656] mb-2 font-semibold">{evt.status}</div>
              <pre class="bg-slate-900 text-slate-100 p-2 rounded text-[10px] overflow-x-auto">{JSON.stringify(evt.payload, null, 2)}</pre>
            </div>
          {/each}
        {/if}
      </div>
    </aside>
  </div>
</main>
