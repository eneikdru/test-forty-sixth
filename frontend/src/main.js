import DocumentSearch from './components/DocumentSearch.svelte';
import PasswordReset from './components/PasswordReset.svelte';

const path = window.location.pathname;
const search = window.location.search;

let Component = DocumentSearch;
if (path.includes('recovery') || path.includes('reset') || search.includes('token') || search.includes('view=recovery')) {
  Component = PasswordReset;
}

const app = new Component({
  target: document.getElementById('app'),
});

export default app;
