<script>
  import { onMount } from 'svelte';
</script>

<svelte:head>
  <title>QuantumCore - Home</title>
  <link href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:wght,FILL@100..700,0..1&display=swap" rel="stylesheet" />
  <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;600;700&display=swap" rel="stylesheet" />
</svelte:head>

<div class="dark min-h-screen bg-[#0F172A] text-[#e4e2e4] antialiased flex flex-col pt-16 pb-24 md:pb-0 overflow-x-hidden selection:bg-primary/30 font-sans">
  <!-- TopAppBar -->
  <header class="fixed top-0 w-full z-50 bg-surface/80 dark:bg-surface/80 backdrop-blur-xl border-b border-white/10 shadow-[0_0_30px_0_rgba(0,0,0,0.2)]">
    <div class="flex justify-between items-center px-edge-margin h-16 w-full max-w-7xl mx-auto">
      <div class="flex items-center gap-sm">
        <img
          alt="User Profile"
          class="w-8 h-8 rounded-full border border-white/20 object-cover"
          src="https://lh3.googleusercontent.com/aida-public/AB6AXuDnfgDIh5icIWXYetT5NqAcq8WB5HvitNgpb-JDEd9aPl-WmHp9Kt_VHqnk8Y5Q_xoHml7wDnTCWD1f_uvmDpHMcoKyi_nUSb5t6Br_jU8-WZ1cBnMeagwnoSmezQ-hKx5MUpa5DJnVphkaDfG-GV2kk5EBups4wU0J2kX3nhku6kNB2lzoSEEepEBb49xtIV1Q8Qx68mB-yZbOt35Mr-n4ME_uFA6rjdhvKhojx6gDUBDH-tutRjn9UBwvmnFgR4uJ8IwkoZT0Z6T9"
        />
      </div>
      <h1 class="font-headline-lg-mobile text-headline-lg-mobile md:font-headline-lg md:text-headline-lg font-display-lg text-display-lg bg-clip-text text-transparent bg-gradient-to-r from-primary to-secondary">
        QuantumCore
      </h1>
      <button class="hover:opacity-80 transition-opacity active:scale-95 duration-300 ease-out p-2 rounded-full glass-panel flex items-center justify-center cursor-pointer">
        <span class="material-symbols-outlined text-primary" style="font-variation-settings: 'FILL' 0;">notifications</span>
      </button>
    </div>
  </header>

  <!-- Main Content Canvas -->
  <main class="flex-1 w-full max-w-7xl mx-auto px-edge-margin flex flex-col gap-xl mt-lg relative">
    <!-- Background Ambient Glow -->
    <div class="ambient-glow top-0 left-10"></div>
    <div class="ambient-glow bottom-40 right-20 bg-secondary"></div>

    <!-- Hero Section: Performance Snapshot -->
    <section class="flex flex-col gap-md relative w-full">
      <div class="flex flex-col gap-xs">
        <h2 class="font-title-md text-title-md text-on-surface">System Status</h2>
        <p class="font-body-sm text-body-sm text-on-surface-variant">Real-time telemetry and core metrics.</p>
      </div>

      <!-- Bento Grid Layout for Stats -->
      <div class="grid grid-cols-2 md:grid-cols-4 gap-sm">
        <!-- Stat Card 1 -->
        <div class="glass-card rounded-xl p-sm flex flex-col gap-xs relative overflow-hidden group">
          <div class="absolute inset-0 bg-gradient-to-br from-primary/5 to-transparent opacity-0 group-hover:opacity-100 transition-opacity duration-300"></div>
          <span class="material-symbols-outlined text-primary mb-2" style="font-variation-settings: 'FILL' 1;">speed</span>
          <span class="font-label-caps text-label-caps text-on-surface-variant uppercase">Core Velocity</span>
          <div class="flex items-baseline gap-2">
            <span class="font-headline-lg-mobile text-headline-lg-mobile md:font-headline-lg md:text-headline-lg text-on-surface font-bold">99.8</span>
            <span class="font-body-sm text-body-sm text-primary">%</span>
          </div>
        </div>

        <!-- Stat Card 2 -->
        <div class="glass-card rounded-xl p-sm flex flex-col gap-xs relative overflow-hidden group">
          <div class="absolute inset-0 bg-gradient-to-br from-secondary/5 to-transparent opacity-0 group-hover:opacity-100 transition-opacity duration-300"></div>
          <span class="material-symbols-outlined text-secondary mb-2" style="font-variation-settings: 'FILL' 1;">memory</span>
          <span class="font-label-caps text-label-caps text-on-surface-variant uppercase">Memory Load</span>
          <div class="flex items-baseline gap-2">
            <span class="font-headline-lg-mobile text-headline-lg-mobile md:font-headline-lg md:text-headline-lg text-on-surface font-bold">42</span>
            <span class="font-body-sm text-body-sm text-secondary">GB</span>
          </div>
          <!-- Micro Progress Line -->
          <div class="w-full h-[2px] bg-surface-container rounded-full mt-2 overflow-hidden">
            <div class="h-full bg-secondary w-[42%]"></div>
          </div>
        </div>

        <!-- Stat Card 3 (Spans 2 columns on mobile) -->
        <div class="glass-card rounded-xl p-sm flex flex-col gap-xs col-span-2 relative overflow-hidden group justify-between">
          <div class="absolute inset-0 bg-gradient-to-br from-primary/10 to-transparent opacity-0 group-hover:opacity-100 transition-opacity duration-300"></div>
          <div class="flex justify-between items-start">
            <span class="font-label-caps text-label-caps text-on-surface-variant uppercase">Network Latency</span>
            <span class="material-symbols-outlined text-on-surface-variant" style="font-variation-settings: 'FILL' 0;">wifi_tethering</span>
          </div>
          <div class="flex items-center gap-md mt-4">
            <div class="flex items-baseline gap-1">
              <span class="font-display-lg text-display-lg text-primary font-bold tracking-tight">12</span>
              <span class="font-body-sm text-body-sm text-on-surface-variant">ms</span>
            </div>
            <div class="flex-1 h-8 flex items-end gap-1 opacity-70">
              <!-- Simulated sparkline -->
              <div class="w-full h-[20%] bg-primary/40 rounded-t-sm"></div>
              <div class="w-full h-[40%] bg-primary/40 rounded-t-sm"></div>
              <div class="w-full h-[30%] bg-primary/40 rounded-t-sm"></div>
              <div class="w-full h-[60%] bg-primary/40 rounded-t-sm"></div>
              <div class="w-full h-[80%] bg-primary rounded-t-sm"></div>
              <div class="w-full h-[50%] bg-primary/40 rounded-t-sm"></div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- Horizontal Scrolling: Top Features -->
    <section class="flex flex-col gap-sm w-full -mx-edge-margin px-edge-margin">
      <div class="flex justify-between items-end">
        <h3 class="font-title-md text-title-md text-on-surface">Active Modules</h3>
        <button class="font-label-caps text-label-caps text-primary hover:opacity-80 transition-opacity uppercase cursor-pointer">View All</button>
      </div>
      <div class="flex overflow-x-auto hide-scrollbar gap-sm pb-4 snap-x snap-mandatory">
        <!-- Feature Card 1 -->
        <div class="snap-start shrink-0 w-[240px] glass-card rounded-xl p-md flex flex-col gap-md relative group cursor-pointer hover:border-primary/50 transition-colors duration-300">
          <div class="w-12 h-12 rounded-full bg-primary/10 flex items-center justify-center border border-primary/20">
            <span class="material-symbols-outlined text-primary" style="font-variation-settings: 'FILL' 0;">hub</span>
          </div>
          <div>
            <h4 class="font-body-lg text-body-lg font-bold text-on-surface mb-1">Neural Routing</h4>
            <p class="font-body-sm text-body-sm text-on-surface-variant line-clamp-2">Optimizing data pathways using predictive algorithms.</p>
          </div>
        </div>

        <!-- Feature Card 2 -->
        <div class="snap-start shrink-0 w-[240px] glass-card rounded-xl p-md flex flex-col gap-md relative group cursor-pointer hover:border-secondary/50 transition-colors duration-300">
          <div class="w-12 h-12 rounded-full bg-secondary/10 flex items-center justify-center border border-secondary/20">
            <span class="material-symbols-outlined text-secondary" style="font-variation-settings: 'FILL' 0;">shield_lock</span>
          </div>
          <div>
            <h4 class="font-body-lg text-body-lg font-bold text-on-surface mb-1">Quantum Vault</h4>
            <p class="font-body-sm text-body-sm text-on-surface-variant line-clamp-2">End-to-end encryption with zero-trust architecture.</p>
          </div>
        </div>

        <!-- Feature Card 3 -->
        <div class="snap-start shrink-0 w-[240px] glass-card rounded-xl p-md flex flex-col gap-md relative group cursor-pointer hover:border-primary/50 transition-colors duration-300">
          <div class="w-12 h-12 rounded-full bg-primary/10 flex items-center justify-center border border-primary/20">
            <span class="material-symbols-outlined text-primary" style="font-variation-settings: 'FILL' 0;">insights</span>
          </div>
          <div>
            <h4 class="font-body-lg text-body-lg font-bold text-on-surface mb-1">Deep Analytics</h4>
            <p class="font-body-sm text-body-sm text-on-surface-variant line-clamp-2">Real-time pattern recognition and anomaly detection.</p>
          </div>
        </div>
      </div>
    </section>

    <!-- Vertical Feed: Recent Activity -->
    <section class="flex flex-col gap-md w-full mb-xl">
      <div class="flex justify-between items-end border-b border-white/5 pb-2">
        <h3 class="font-title-md text-title-md text-on-surface">Event Log</h3>
        <span class="material-symbols-outlined text-on-surface-variant text-sm">filter_list</span>
      </div>
      <div class="flex flex-col">
        <!-- Activity Item 1 -->
        <div class="flex items-start gap-md py-sm border-b border-white/5 group hover:bg-white/[0.02] transition-colors px-2 rounded-lg -mx-2">
          <div class="w-10 h-10 rounded-full bg-surface-container flex items-center justify-center shrink-0 border border-white/10 group-hover:border-primary/30 transition-colors">
            <span class="material-symbols-outlined text-on-surface-variant text-sm" style="font-variation-settings: 'FILL' 1;">update</span>
          </div>
          <div class="flex-1 flex flex-col gap-1">
            <div class="flex justify-between items-start">
              <span class="font-body-sm text-body-sm font-bold text-on-surface">System Core Updated</span>
              <span class="font-label-caps text-label-caps text-on-surface-variant/60">2m ago</span>
            </div>
            <p class="font-body-sm text-body-sm text-on-surface-variant">Patch v4.2.1 deployed successfully across all nodes.</p>
            <div class="flex gap-2 mt-2">
              <span class="inline-flex px-2 py-1 bg-primary/15 text-primary rounded-full font-label-caps text-[10px] uppercase border border-primary/20">System</span>
              <span class="inline-flex px-2 py-1 bg-secondary/15 text-secondary rounded-full font-label-caps text-[10px] uppercase border border-secondary/20">Success</span>
            </div>
          </div>
        </div>

        <!-- Activity Item 2 -->
        <div class="flex items-start gap-md py-sm border-b border-white/5 group hover:bg-white/[0.02] transition-colors px-2 rounded-lg -mx-2">
          <div class="w-10 h-10 rounded-full bg-surface-container flex items-center justify-center shrink-0 border border-white/10 group-hover:border-error/30 transition-colors">
            <span class="material-symbols-outlined text-error text-sm" style="font-variation-settings: 'FILL' 1;">warning</span>
          </div>
          <div class="flex-1 flex flex-col gap-1">
            <div class="flex justify-between items-start">
              <span class="font-body-sm text-body-sm font-bold text-on-surface">Anomaly Detected</span>
              <span class="font-label-caps text-label-caps text-on-surface-variant/60">15m ago</span>
            </div>
            <p class="font-body-sm text-body-sm text-on-surface-variant">Unusual traffic pattern observed on Node 7. Auto-mitigation engaged.</p>
            <div class="flex gap-2 mt-2">
              <span class="inline-flex px-2 py-1 bg-error/15 text-error rounded-full font-label-caps text-[10px] uppercase border border-error/20">Security</span>
            </div>
          </div>
        </div>

        <!-- Activity Item 3 -->
        <div class="flex items-start gap-md py-sm group hover:bg-white/[0.02] transition-colors px-2 rounded-lg -mx-2">
          <div class="w-10 h-10 rounded-full bg-surface-container flex items-center justify-center shrink-0 border border-white/10 group-hover:border-primary/30 transition-colors">
            <span class="material-symbols-outlined text-on-surface-variant text-sm" style="font-variation-settings: 'FILL' 1;">person_add</span>
          </div>
          <div class="flex-1 flex flex-col gap-1">
            <div class="flex justify-between items-start">
              <span class="font-body-sm text-body-sm font-bold text-on-surface">New Admin Provisioned</span>
              <span class="font-label-caps text-label-caps text-on-surface-variant/60">1h ago</span>
            </div>
            <p class="font-body-sm text-body-sm text-on-surface-variant">Access granted to generic.user@quantum.core.</p>
          </div>
        </div>
      </div>
    </section>
  </main>

  <!-- BottomNavBar (Mobile Only) -->
  <nav class="md:hidden fixed bottom-0 w-full z-50 rounded-t-xl bg-surface/80 dark:bg-surface/80 backdrop-blur-xl border-t border-white/10 shadow-[0_-4px_20px_0_rgba(0,0,0,0.1)]">
    <div class="flex justify-around items-center h-20 pb-safe px-4">
      <!-- Home (Active) -->
      <button class="flex flex-col items-center justify-center text-primary font-bold scale-110 active:scale-90 transition-transform duration-300 hover:text-primary/80 group cursor-pointer">
        <div class="px-4 py-1 rounded-full bg-primary/10 mb-1">
          <span class="material-symbols-outlined" style="font-variation-settings: 'FILL' 1;">home</span>
        </div>
        <span class="font-label-caps text-label-caps">Home</span>
      </button>

      <!-- Features -->
      <button class="flex flex-col items-center justify-center text-on-surface-variant/60 active:scale-90 transition-transform duration-300 hover:text-primary/80 group cursor-pointer">
        <div class="px-4 py-1 rounded-full group-hover:bg-white/5 transition-colors mb-1">
          <span class="material-symbols-outlined" style="font-variation-settings: 'FILL' 0;">rebase_edit</span>
        </div>
        <span class="font-label-caps text-label-caps">Features</span>
      </button>

      <!-- Dashboard -->
      <button class="flex flex-col items-center justify-center text-on-surface-variant/60 active:scale-90 transition-transform duration-300 hover:text-primary/80 group cursor-pointer">
        <div class="px-4 py-1 rounded-full group-hover:bg-white/5 transition-colors mb-1">
          <span class="material-symbols-outlined" style="font-variation-settings: 'FILL' 0;">dashboard</span>
        </div>
        <span class="font-label-caps text-label-caps">Dashboard</span>
      </button>

      <!-- Settings -->
      <button class="flex flex-col items-center justify-center text-on-surface-variant/60 active:scale-90 transition-transform duration-300 hover:text-primary/80 group cursor-pointer">
        <div class="px-4 py-1 rounded-full group-hover:bg-white/5 transition-colors mb-1">
          <span class="material-symbols-outlined" style="font-variation-settings: 'FILL' 0;">settings</span>
        </div>
        <span class="font-label-caps text-label-caps">Settings</span>
      </button>
    </div>
  </nav>
</div>

<style>
  .glass-panel {
    background: rgba(15, 23, 42, 0.4);
    backdrop-filter: blur(20px);
    -webkit-backdrop-filter: blur(20px);
    border: 1px solid rgba(255, 255, 255, 0.1);
  }

  .glass-card {
    background: rgba(30, 41, 59, 0.5);
    backdrop-filter: blur(20px);
    -webkit-backdrop-filter: blur(20px);
    border-top: 1px solid rgba(255, 255, 255, 0.15);
    border-left: 1px solid rgba(255, 255, 255, 0.05);
    border-right: 1px solid rgba(255, 255, 255, 0.05);
    border-bottom: 1px solid rgba(255, 255, 255, 0.02);
    box-shadow: 0 4px 30px rgba(0, 0, 0, 0.1);
  }

  .ambient-glow {
    position: absolute;
    width: 150px;
    height: 150px;
    background: #bec6e0;
    filter: blur(100px);
    opacity: 0.15;
    border-radius: 50%;
    z-index: -1;
  }

  /* Hide scrollbar for horizontal scrolling but keep functionality */
  .hide-scrollbar::-webkit-scrollbar {
    display: none;
  }
  .hide-scrollbar {
    -ms-overflow-style: none;
    scrollbar-width: none;
  }
</style>
