import MaterialEditor from './components/MaterialEditor.svelte';
import PasswordReset from './components/PasswordReset.svelte';
import TelemetryDispatch from './components/TelemetryDispatch.svelte';

let app;

if (window.location.pathname.startsWith('/reset-password')) {
  app = new PasswordReset({
    target: document.getElementById('app'),
  });
} else if (window.location.pathname.startsWith('/telemetry')) {
  app = new TelemetryDispatch({
    target: document.getElementById('app'),
  });
} else {
  app = new MaterialEditor({
    target: document.getElementById('app'),
  });
}

export default app;
