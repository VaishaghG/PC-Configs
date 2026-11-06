// api.js — Base API communication layer

const API_BASE = 'http://localhost:8080/api';

function getAuthHeaders() {
  const token = localStorage.getItem('pc_token');
  const headers = { 'Content-Type': 'application/json' };
  if (token) headers['Authorization'] = `Bearer ${token}`;
  return headers;
}

async function request(method, path, body = null) {
  const opts = {
    method,
    headers: getAuthHeaders(),
  };
  if (body !== null) opts.body = JSON.stringify(body);

  const response = await fetch(`${API_BASE}${path}`, opts);
  const text = await response.text();
  let data;
  try { data = JSON.parse(text); } catch { data = text; }

  if (!response.ok) {
    if (response.status === 401 || response.status === 403) {
      const isLogged = !!localStorage.getItem('pc_token');
      if (!isLogged) {
        throw new Error('Please log in to continue.');
      }
      throw new Error('Session expired. Please log in again.');
    }
    const msg = data?.error || data?.message || `HTTP ${response.status}`;
    throw new Error(msg);
  }
  return data;
}

export const api = {
  get:    (path)        => request('GET',    path),
  post:   (path, body)  => request('POST',   path, body),
  put:    (path, body)  => request('PUT',    path, body),
  patch:  (path, body)  => request('PATCH',  path, body),
  delete: (path)        => request('DELETE', path),

  // Auth
  register: (data)       => request('POST', '/auth/register', data),
  login:    (data)       => request('POST', '/auth/login',    data),
  logout:   ()           => request('POST', '/auth/logout'),
  me:       ()           => request('GET',  '/auth/me'),

  // Products
  getProducts: (params = {}) => {
    const q = new URLSearchParams(params).toString();
    return request('GET', `/products${q ? '?' + q : ''}`);
  },
  getProduct:  (id) => request('GET', `/products/${id}`),
  searchProducts: (q) => request('GET', `/products?search=${encodeURIComponent(q)}`),

  // Cart
  getCart:        ()              => request('GET',    '/cart'),
  addToCart:      (productId, qty=1) => request('POST', '/cart/add', { productId, quantity: qty }),
  updateCartItem: (itemId, qty)   => request('PUT',    `/cart/${itemId}`, { quantity: qty }),
  removeCartItem: (itemId)        => request('DELETE', `/cart/${itemId}`),
  clearCart:      ()              => request('DELETE', '/cart/clear'),

  // Orders
  createOrder: (data)  => request('POST', '/orders', data),
  getOrders:   ()      => request('GET',  '/orders'),
  getOrder:    (id)    => request('GET',  `/orders/${id}`),

  // Builds
  createBuild: (data)  => request('POST', '/builds', data),
  getBuilds:   ()      => request('GET',  '/builds'),
  getBuild:    (id)    => request('GET',  `/builds/${id}`),
  updateBuild: (id, d) => request('PUT',  `/builds/${id}`, d),
  deleteBuild: (id)    => request('DELETE',`/builds/${id}`),
  greedyBuild: (budget) => request('POST', '/builds/greedy', { budget }),

  // Dashboard
  getDashboard: () => request('GET', '/dashboard'),

  // Algorithms
  algoDemo: () => request('GET', '/algorithms/demo'),
};
