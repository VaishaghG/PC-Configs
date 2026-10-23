// utils.js — Shared utility functions

export function formatPrice(amount) {
  return '₹' + Number(amount).toLocaleString('en-IN', {
    minimumFractionDigits: 0,
    maximumFractionDigits: 0,
  });
}

export function formatDate(dateStr) {
  if (!dateStr) return '—';
  return new Date(dateStr).toLocaleDateString('en-IN', {
    day: 'numeric', month: 'short', year: 'numeric'
  });
}

export function stockBadge(qty) {
  if (qty === 0) return `<span class="badge badge-danger">Out of Stock</span>`;
  if (qty < 5)  return `<span class="badge badge-warning">Low Stock (${qty})</span>`;
  return `<span class="badge badge-success">In Stock</span>`;
}

export function stockClass(qty) {
  if (qty === 0) return 'stock-out';
  if (qty < 5)  return 'stock-low';
  return 'stock-in';
}

export function stockLabel(qty) {
  if (qty === 0) return 'Out of Stock';
  if (qty < 5)  return `Low Stock (${qty})`;
  return `In Stock`;
}

export function categoryIcon(cat) {
  const icons = { CPU:'🔧', GPU:'🎮', RAM:'💾', STORAGE:'💽', CABINET:'🖥' };
  return icons[cat] || '📦';
}

export function orderStatusBadge(status) {
  const map = {
    PENDING:    'badge-warning',
    CONFIRMED:  'badge-info',
    PROCESSING: 'badge-info',
    SHIPPED:    'badge-accent',
    DELIVERED:  'badge-success',
    CANCELLED:  'badge-danger',
  };
  return `<span class="badge ${map[status] || 'badge-info'}">${status}</span>`;
}

export function paymentStatusBadge(status) {
  const map = {
    PENDING:   'badge-warning',
    COMPLETED: 'badge-success',
    FAILED:    'badge-danger',
    REFUNDED:  'badge-info',
  };
  return `<span class="badge ${map[status] || 'badge-warning'}">${status}</span>`;
}

export function imgFallback(url, cat) {
  // Spring Boot's WebConfig serves /assets/** directly from the filesystem,
  // so all asset paths should be root-relative (no ../.. prefix needed).
  if (url) {
    if (url.startsWith('http://') || url.startsWith('https://')) return url;
    if (url.startsWith('/assets/')) return url;
    if (url.startsWith('assets/')) return `/${url}`;
    return url;
  }

  const categoryPlaceholders = {
    CPU:     '/assets/products/prod_1_intel_i9_13900k.jpg',
    GPU:     '/assets/products/prod_5_nvidia_rtx_4090.jpg',
    RAM:     '/assets/products/prod_9_corsair_dominator_32gb.jpg',
    STORAGE: '/assets/products/prod_13_samsung_990pro_2tb.jpg',
    CABINET: '/assets/products/prod_17_lianli_o11d_evo.jpg',
  };

  return categoryPlaceholders[cat] || '/assets/showcase/rig_primary.jpg';
}

/**
 * Returns an onerror attribute value to gracefully handle 404 product images.
 */
export function imgOnError(cat) {
  const fallback = {
    CPU:     '/assets/products/prod_1_intel_i9_13900k.jpg',
    GPU:     '/assets/products/prod_5_nvidia_rtx_4090.jpg',
    RAM:     '/assets/products/prod_9_corsair_dominator_32gb.jpg',
    STORAGE: '/assets/products/prod_13_samsung_990pro_2tb.jpg',
    CABINET: '/assets/products/prod_17_lianli_o11d_evo.jpg',
  }[cat] || '/assets/showcase/rig_primary.jpg';
  return `this.onerror=null; this.src='${fallback}';`;
}

export function getShowcaseRig() {
  const isPages = window.location.pathname.includes('/pages/');
  return isPages ? '../assets/showcase/rig_primary.jpg' : './assets/showcase/rig_primary.jpg';
}

export function getShowcaseRoom() {
  const isPages = window.location.pathname.includes('/pages/');
  return isPages ? '../assets/showcase/room_hero.jpg' : './assets/showcase/room_hero.jpg';
}

export function setLoading(btn, loading, text = null) {
  if (loading) {
    btn.disabled = true;
    btn.dataset.originalText = btn.innerHTML;
    btn.innerHTML = `<span class="spinner"></span> ${text || 'Loading...'}`;
  } else {
    btn.disabled = false;
    btn.innerHTML = btn.dataset.originalText || btn.innerHTML;
  }
}

export function debounce(fn, delay) {
  let t;
  return (...args) => { clearTimeout(t); t = setTimeout(() => fn(...args), delay); };
}
