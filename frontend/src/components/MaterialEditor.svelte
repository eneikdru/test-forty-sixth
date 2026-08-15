<script>
  import { onMount } from 'svelte';

  export let materialId = '123e4567-e89b-12d3-a456-426614174000';

  // Form state
  let id = materialId;
  let title = 'Q3 Design System & Outbreak Protocols';
  let pathogenType = 'VIRUS';
  let content = 'We are rolling out the newest iteration of epidemiological containment protocols and shared components library. This update focuses heavily on increasing contrast ratios across the dashboard and introducing more robust tonal elevation layers for outbreak mapping.';
  let metadataText = '{"author": "EpiTeam", "department": "Public Health"}';
  let tags = ['UI/UX', 'Release Notes'];
  let newTagInput = '';
  let status = 'DRAFT'; // DRAFT, PUBLISHED, UNPUBLISHED

  // UI / UX state
  let isSaving = false;
  let isPublishing = false;
  let isUnpublishing = false;
  let showUnpublishModal = false;
  let unpublishReason = '';
  let successMessage = '';

  // Server error and validation state
  let serverError = null;
  let validationErrors = {}; // e.g. { title: "Title is required", pathogenType: "Must select a valid pathogen" }

  // Rich text simulated format state
  let activeFormats = {
    bold: false,
    italic: true,
    bulleted: false
  };

  onMount(async () => {
    if (id) {
      await fetchMaterialDetails(id);
    }
  });

  async function fetchMaterialDetails(targetId) {
    try {
      const response = await fetch(`/api/v1/materials/${targetId}`);
      if (response.ok) {
        const data = await response.json();
        id = data.id || targetId;
        title = data.title || title;
        pathogenType = data.pathogenType || pathogenType;
        content = data.content || content;
        status = data.status || status;
        if (data.metadata) {
          metadataText = typeof data.metadata === 'string' ? data.metadata : JSON.stringify(data.metadata);
        }
      }
    } catch (err) {
      console.log('Using initial default state for editor mockup:', err);
    }
  }

  function addTag() {
    const trimmed = newTagInput.trim();
    if (trimmed && !tags.includes(trimmed)) {
      tags = [...tags, trimmed];
      newTagInput = '';
    }
  }

  function removeTag(tagToRemove) {
    tags = tags.filter(t => t !== tagToRemove);
  }

  function toggleFormat(fmt) {
    activeFormats[fmt] = !activeFormats[fmt];
    activeFormats = { ...activeFormats };
  }

  // Handle Save / Update Material
  async function handleSave() {
    isSaving = true;
    serverError = null;
    validationErrors = {};
    successMessage = '';

    // Client pre-validation
    const clientErrs = {};
    if (!title.trim()) clientErrs.title = 'Title is required and cannot be empty.';
    if (!pathogenType) clientErrs.pathogenType = 'Pathogen type is required.';
    if (!content.trim()) clientErrs.content = 'Content description is required.';

    if (Object.keys(clientErrs).length > 0) {
      validationErrors = clientErrs;
      serverError = 'Please fix the form validation errors highlighted below.';
      isSaving = false;
      return;
    }

    try {
      const payload = {
        title: title.trim(),
        pathogenType: pathogenType,
        content: content.trim(),
        metadata: metadataText.trim()
      };

      const response = await fetch(id ? `/api/v1/materials/${id}` : '/api/v1/materials', {
        method: id ? 'PUT' : 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
      });

      if (!response.ok) {
        const errData = await response.json().catch(() => ({}));
        serverError = errData.message || `Failed to save material (Status ${response.status})`;
        if (errData.details && Array.isArray(errData.details)) {
          errData.details.forEach(d => {
            if (d.toLowerCase().includes('title')) clientErrs.title = d;
            if (d.toLowerCase().includes('pathogen')) clientErrs.pathogenType = d;
            if (d.toLowerCase().includes('content')) clientErrs.content = d;
          });
          validationErrors = clientErrs;
        }
        return;
      }

      const resData = await response.json();
      if (resData.id) id = resData.id;
      if (resData.status) status = resData.status;
      successMessage = 'Material saved successfully as draft.';
    } catch (err) {
      serverError = `Network / server error: ${err.message}`;
    } finally {
      isSaving = false;
    }
  }

  // Handle Publish Material
  async function handlePublish() {
    isPublishing = true;
    serverError = null;
    validationErrors = {};
    successMessage = '';

    if (!id) {
      await handleSave();
      if (serverError) {
        isPublishing = false;
        return;
      }
    }

    try {
      const response = await fetch(`/api/v1/materials/${id}/publish`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          status: 'PUBLISHED',
          comment: 'Published via Material Management Editor UI'
        })
      });

      if (!response.ok) {
        const errData = await response.json().catch(() => ({}));
        serverError = errData.message || `Failed to publish material (Status ${response.status})`;
        return;
      }

      const data = await response.json();
      status = data.status || 'PUBLISHED';
      successMessage = 'Material has been published successfully!';
    } catch (err) {
      serverError = `Error publishing: ${err.message}`;
    } finally {
      isPublishing = false;
    }
  }

  // Open Unpublish Confirmation Modal
  function openUnpublishModal() {
    showUnpublishModal = true;
    unpublishReason = '';
    serverError = null;
  }

  // Close Unpublish Confirmation Modal
  function closeUnpublishModal() {
    showUnpublishModal = false;
    unpublishReason = '';
  }

  // Confirm Unpublish Execution
  async function confirmUnpublish() {
    isUnpublishing = true;
    serverError = null;
    successMessage = '';

    try {
      const response = await fetch(`/api/v1/materials/${id}/unpublish`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          confirmationState: 'CONFIRMED',
          reason: unpublishReason.trim() || 'Unpublished by editor user'
        })
      });

      if (!response.ok) {
        const errData = await response.json().catch(() => ({}));
        serverError = errData.message || `Failed to unpublish (Status ${response.status})`;
        showUnpublishModal = false;
        return;
      }

      const data = await response.json();
      status = data.status || 'UNPUBLISHED';
      showUnpublishModal = false;
      successMessage = 'Material has been unpublished and reverted to UNPUBLISHED status.';
    } catch (err) {
      serverError = `Error unpublishing: ${err.message}`;
      showUnpublishModal = false;
    } finally {
      isUnpublishing = false;
    }
  }

  // Helper to trigger simulated server validation error
  function simulateServerError() {
    serverError = 'Server Validation Error (400): The submitted material fields failed strict contract validation rules.';
    validationErrors = {
      title: 'Title must be at least 10 characters and contain valid characters.',
      pathogenType: 'Pathogen type is invalid or deprecated for this sector.',
      content: 'Content text must conform to clinical guidelines contrast standards.'
    };
  }
</script>

<div class="min-h-screen bg-[#faf8ff] text-[#131b2e] flex flex-col font-sans selection:bg-[#dbe1ff] selection:text-[#00174b] overflow-x-hidden">
  <!-- Top App Bar Header -->
  <header class="bg-[#faf8ff] sticky top-0 w-full z-40 border-b border-[#c3c6d7] px-4 py-3 shadow-xs">
    <div class="flex justify-between items-center max-w-6xl mx-auto gap-2">
      <button
        type="button"
        on:click={() => { title = ''; content = ''; validationErrors = {}; serverError = null; }}
        class="text-[#434655] font-medium text-sm hover:bg-[#d3e4fe] transition-colors active:scale-95 px-3 py-2 rounded-lg"
      >
        Reset
      </button>

      <div class="flex items-center gap-2">
        <h1 class="font-bold text-lg md:text-xl text-[#131b2e] text-center truncate">
          Material Editor
        </h1>
        <span class="text-xs font-semibold px-2.5 py-0.5 rounded-full border border-[#c3c6d7] uppercase tracking-wide
          {status === 'PUBLISHED' ? 'bg-[#dbe1ff] text-[#003ea8] border-[#b4c5ff]' : 'bg-[#eaedff] text-[#434655]'}">
          {status}
        </span>
      </div>

      <div class="flex items-center gap-2">
        {#if status === 'PUBLISHED'}
          <button
            type="button"
            on:click={openUnpublishModal}
            class="text-[#690005] bg-[#ffdad6] hover:bg-[#ffb4ab] font-bold text-sm transition-colors active:scale-95 px-3 py-2 rounded-lg border border-[#ba1a1a]"
          >
            Unpublish
          </button>
        {:else}
          <button
            type="button"
            on:click={handleSave}
            disabled={isSaving}
            class="text-[#0053db] hover:bg-[#eaedff] font-semibold text-sm transition-colors px-3 py-2 rounded-lg"
          >
            {isSaving ? 'Saving...' : 'Save Draft'}
          </button>
          <button
            type="button"
            on:click={handlePublish}
            disabled={isPublishing}
            class="text-white bg-[#004ac6] hover:bg-[#003ea8] font-bold text-sm transition-colors active:scale-95 px-4 py-2 rounded-lg shadow-xs"
          >
            {isPublishing ? 'Publishing...' : 'Publish'}
          </button>
        {/if}
      </div>
    </div>
  </header>

  <!-- Main Content Canvas -->
  <main class="flex-1 max-w-4xl mx-auto w-full px-4 sm:px-6 py-6 pb-24">
    <!-- Success Banner -->
    {#if successMessage}
      <div class="mb-6 p-4 bg-[#d0e1fb] text-[#0b1c30] rounded-xl border border-[#b7c8e1] flex items-center justify-between" role="alert">
        <div class="flex items-center gap-2">
          <svg class="w-5 h-5 text-[#004ac6]" fill="currentColor" viewBox="0 0 20 20">
            <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clip-rule="evenodd"/>
          </svg>
          <span class="font-medium text-sm">{successMessage}</span>
        </div>
        <button on:click={() => successMessage = ''} class="text-[#434655] hover:text-[#131b2e] font-bold text-sm px-2">×</button>
      </div>
    {/if}

    <!-- High-contrast Clear Validation / Server Error Banner -->
    {#if serverError}
      <div class="mb-6 p-4 bg-[#ffdad6] text-[#93000a] rounded-xl border-2 border-[#ba1a1a] shadow-xs" role="alert">
        <div class="flex items-start gap-3">
          <svg class="w-6 h-6 text-[#ba1a1a] shrink-0 mt-0.5" fill="currentColor" viewBox="0 0 20 20">
            <path fill-rule="evenodd" d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7 4a1 1 0 11-2 0 1 1 0 012 0zm-1-9a1 1 0 00-1 1v4a1 1 0 102 0V6a1 1 0 00-1-1z" clip-rule="evenodd"/>
          </svg>
          <div>
            <h3 class="font-bold text-base text-[#93000a] mb-1">Form Validation Error</h3>
            <p class="text-sm text-[#730006] leading-relaxed font-medium">{serverError}</p>
          </div>
        </div>
      </div>
    {/if}

    <!-- Document Context Bento Card -->
    <div class="mb-6 bg-[#f2f3ff] rounded-xl border border-[#c3c6d7] p-4 flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4">
      <div class="flex items-center gap-3">
        <div class="w-12 h-12 rounded-lg bg-[#2563eb] text-white flex items-center justify-center font-bold text-lg shrink-0">
          EPI
        </div>
        <div>
          <p class="text-xs uppercase tracking-wider text-[#434655] font-medium">
            {status} • ID: <span class="font-mono text-[11px] text-[#131b2e]">{id || 'New Material'}</span>
          </p>
          <div class="flex items-center gap-2 mt-0.5">
            <span class="text-sm font-semibold text-[#505f76]">Epidemiology KB Material Collection</span>
          </div>
        </div>
      </div>

      <div class="flex items-center gap-2 self-end sm:self-center">
        <button
          type="button"
          on:click={simulateServerError}
          class="text-xs font-medium text-[#943700] bg-[#ffdbcd] hover:bg-[#ffb596] px-3 py-1.5 rounded-lg border border-[#bc4800] transition-colors"
          title="Simulate validation response from server to verify typed data persistence and contrast error labels"
        >
          Test Validation State
        </button>
      </div>
    </div>

    <!-- Form Container -->
    <form on:submit|preventDefault={handleSave} class="space-y-6">
      <!-- Title Input -->
      <div>
        <label for="material-title" class="block text-xs font-bold uppercase tracking-wider text-[#434655] mb-1.5">
          Material Title <span class="text-[#ba1a1a]">*</span>
        </label>
        <input
          id="material-title"
          type="text"
          bind:value={title}
          placeholder="e.g. Influenza Outbreak Protocol 2026"
          class="w-full bg-transparent border-0 border-b-2 {validationErrors.title ? 'border-[#ba1a1a] bg-[#ffdad6]/30' : 'border-[#c3c6d7] focus:border-[#004ac6]'} px-1 py-2 font-bold text-xl sm:text-2xl text-[#131b2e] placeholder-[#434655] focus:ring-0 focus:outline-none transition-colors"
        />
        {#if validationErrors.title}
          <p class="mt-1.5 text-xs font-bold text-[#ba1a1a] flex items-center gap-1">
            <span>⚠</span> {validationErrors.title}
          </p>
        {/if}
      </div>

      <!-- Pathogen Type & Metadata Grid -->
      <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
        <div>
          <label for="pathogen-type" class="block text-xs font-bold uppercase tracking-wider text-[#434655] mb-1.5">
            Pathogen Type <span class="text-[#ba1a1a]">*</span>
          </label>
          <select
            id="pathogen-type"
            bind:value={pathogenType}
            class="w-full bg-white border {validationErrors.pathogenType ? 'border-[#ba1a1a] bg-[#ffdad6]/20' : 'border-[#737686]'} rounded-lg px-3 py-2.5 text-sm font-medium text-[#131b2e] focus:ring-2 focus:ring-[#004ac6] focus:outline-none"
          >
            <option value="VIRUS">VIRUS</option>
            <option value="BACTERIA">BACTERIA</option>
            <option value="PARASITE">PARASITE</option>
            <option value="FUNGI">FUNGI</option>
            <option value="PRION">PRION</option>
            <option value="OTHER">OTHER</option>
          </select>
          {#if validationErrors.pathogenType}
            <p class="mt-1.5 text-xs font-bold text-[#ba1a1a] flex items-center gap-1">
              <span>⚠</span> {validationErrors.pathogenType}
            </p>
          {/if}
        </div>

        <div>
          <label for="metadata-json" class="block text-xs font-bold uppercase tracking-wider text-[#434655] mb-1.5">
            Metadata (JSON / Text)
          </label>
          <input
            id="metadata-json"
            type="text"
            bind:value={metadataText}
            placeholder="&#123;&quot;author&quot;: &quot;EpiTeam&quot;&#125;"
            class="w-full bg-white border border-[#737686] rounded-lg px-3 py-2.5 text-sm font-mono text-[#131b2e] focus:ring-2 focus:ring-[#004ac6] focus:outline-none"
          />
        </div>
      </div>

      <!-- Tags / Metadata Chips -->
      <div>
        <span class="block text-xs font-bold uppercase tracking-wider text-[#434655] mb-2">
          Category Tags
        </span>
        <div class="flex flex-wrap items-center gap-2">
          {#each tags as tag}
            <span class="inline-flex items-center gap-1.5 bg-[#eaedff] text-[#131b2e] px-3 py-1 rounded-full text-xs font-medium border border-[#c3c6d7]">
              #{tag}
              <button
                type="button"
                on:click={() => removeTag(tag)}
                class="hover:text-[#ba1a1a] font-bold text-xs ml-0.5"
                title="Remove tag"
              >×</button>
            </span>
          {/each}

          <div class="inline-flex items-center gap-1">
            <input
              type="text"
              bind:value={newTagInput}
              on:keydown={(e) => e.key === 'Enter' && (e.preventDefault(), addTag())}
              placeholder="Add tag..."
              class="border border-dashed border-[#0053db] bg-white rounded-full px-3 py-1 text-xs text-[#131b2e] focus:outline-none focus:ring-1 focus:ring-[#0053db] w-28"
            />
            <button
              type="button"
              on:click={addTag}
              class="text-xs font-semibold text-[#0053db] bg-[#dbe1ff] hover:bg-[#b4c5ff] px-2.5 py-1 rounded-full border border-[#0053db]"
            >
              +
            </button>
          </div>
        </div>
      </div>

      <!-- Content Text Area / Rich Text Canvas -->
      <div>
        <label for="material-content" class="block text-xs font-bold uppercase tracking-wider text-[#434655] mb-1.5">
          Material Content & Guidelines <span class="text-[#ba1a1a]">*</span>
        </label>
        <textarea
          id="material-content"
          rows="10"
          bind:value={content}
          placeholder="Start writing epidemiological content, surveillance protocols, clinical guidelines..."
          class="w-full bg-white border-2 {validationErrors.content ? 'border-[#ba1a1a] bg-[#ffdad6]/20' : 'border-[#c3c6d7] focus:border-[#004ac6]'} rounded-xl p-4 text-base leading-relaxed text-[#131b2e] placeholder-[#434655] focus:ring-0 focus:outline-none transition-colors
          {activeFormats.bold ? 'font-bold' : ''} {activeFormats.italic ? 'italic' : ''}"
        ></textarea>
        {#if validationErrors.content}
          <p class="mt-1.5 text-xs font-bold text-[#ba1a1a] flex items-center gap-1">
            <span>⚠</span> {validationErrors.content}
          </p>
        {/if}
      </div>
    </form>
  </main>

  <!-- Bottom Nav Formatting Toolbar -->
  <nav class="bg-[#eaedff] border-t border-[#c3c6d7] fixed bottom-0 w-full z-30 h-14">
    <div class="flex justify-around items-center max-w-lg mx-auto px-4 h-full">
      <button
        type="button"
        on:click={() => toggleFormat('bold')}
        class="w-10 h-10 rounded-full flex items-center justify-center transition-all active:scale-90
        {activeFormats.bold ? 'bg-[#2563eb] text-white shadow-xs' : 'text-[#434655] hover:bg-[#d0e1fb]'}"
        title="Bold text"
      >
        <span class="font-bold text-sm">B</span>
      </button>

      <button
        type="button"
        on:click={() => toggleFormat('italic')}
        class="w-10 h-10 rounded-full flex items-center justify-center transition-all active:scale-90
        {activeFormats.italic ? 'bg-[#2563eb] text-white shadow-xs' : 'text-[#434655] hover:bg-[#d0e1fb]'}"
        title="Italic text"
      >
        <span class="italic font-serif font-bold text-base">I</span>
      </button>

      <button
        type="button"
        on:click={() => toggleFormat('bulleted')}
        class="w-10 h-10 rounded-full flex items-center justify-center transition-all active:scale-90
        {activeFormats.bulleted ? 'bg-[#2563eb] text-white shadow-xs' : 'text-[#434655] hover:bg-[#d0e1fb]'}"
        title="Bulleted list"
      >
        <span class="font-bold text-sm">•≡</span>
      </button>

      <div class="w-px h-6 bg-[#c3c6d7]"></div>

      <button
        type="button"
        on:click={handleSave}
        class="text-xs font-semibold text-[#004ac6] hover:bg-[#d0e1fb] px-3 py-1.5 rounded-full transition-colors"
      >
        Quick Save
      </button>
    </div>
  </nav>

  <!-- Unpublish Confirmation Modal -->
  {#if showUnpublishModal}
    <div
      class="fixed inset-0 z-50 bg-slate-900/60 backdrop-blur-xs flex items-center justify-center p-4"
      role="dialog"
      aria-modal="true"
      aria-labelledby="unpublish-modal-title"
    >
      <div class="bg-white rounded-2xl max-w-md w-full p-6 shadow-xl border border-[#c3c6d7] space-y-4 animate-in fade-in zoom-in duration-150">
        <div class="flex items-center gap-3 text-[#ba1a1a]">
          <div class="w-10 h-10 rounded-full bg-[#ffdad6] flex items-center justify-center shrink-0">
            <svg class="w-6 h-6 text-[#ba1a1a]" fill="currentColor" viewBox="0 0 20 20">
              <path fill-rule="evenodd" d="M8.257 3.099c.765-1.36 2.722-1.36 3.486 0l5.58 9.92c.75 1.334-.213 2.98-1.742 2.98H4.42c-1.53 0-2.493-1.646-1.743-2.98l5.58-9.92zM11 13a1 1 0 11-2 0 1 1 0 012 0zm-1-8a1 1 0 00-1 1v3a1 1 0 102 0V6a1 1 0 00-1-1z" clip-rule="evenodd"/>
            </svg>
          </div>
          <div>
            <h3 id="unpublish-modal-title" class="text-lg font-bold text-[#131b2e]">Confirm Unpublish Action</h3>
            <p class="text-xs text-[#434655]">This material will no longer be public in the Knowledge Base.</p>
          </div>
        </div>

        <p class="text-sm text-[#131b2e] leading-relaxed">
          Are you sure you want to unpublish <strong>"{title}"</strong>? This will revert the material status to UNPUBLISHED.
        </p>

        <div>
          <label for="unpublish-reason" class="block text-xs font-semibold text-[#434655] mb-1">
            Reason for Unpublishing (Optional)
          </label>
          <input
            id="unpublish-reason"
            type="text"
            bind:value={unpublishReason}
            placeholder="e.g. Superseded by newer outbreak protocol version"
            class="w-full border border-[#737686] rounded-lg px-3 py-2 text-sm text-[#131b2e] focus:ring-2 focus:ring-[#ba1a1a] focus:outline-none"
          />
        </div>

        <div class="flex justify-end gap-3 pt-2 border-t border-[#eaedff]">
          <button
            type="button"
            on:click={closeUnpublishModal}
            class="px-4 py-2 text-sm font-semibold text-[#434655] hover:bg-[#eaedff] rounded-lg transition-colors"
          >
            Cancel
          </button>
          <button
            type="button"
            on:click={confirmUnpublish}
            disabled={isUnpublishing}
            class="px-5 py-2 text-sm font-bold text-white bg-[#ba1a1a] hover:bg-[#93000a] rounded-lg transition-colors active:scale-95 disabled:opacity-50"
          >
            {isUnpublishing ? 'Unpublishing...' : 'Confirm Unpublish'}
          </button>
        </div>
      </div>
    </div>
  {/if}
</div>
