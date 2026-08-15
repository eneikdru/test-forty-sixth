import TelemetryDispatch from './components/TelemetryDispatch.svelte';

const app = new TelemetryDispatch({
  target: document.getElementById('app'),
});

export default app;
