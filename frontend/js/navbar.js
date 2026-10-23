// navbar.js — Reusable nav component + cart badge sync

import { auth } from './auth.js';
import { api }  from './api.js';

export function renderNavbar(activePage = '') {
  const isLogged = auth.isLoggedIn();
  const user     = auth.getUser();
  const base     = getBase();

  const nav = document.createElement('nav');
  nav.className = 'navbar';
  nav.innerHTML = `
    <div class="container">
      <a href="${base}index.html" class="nav-brand">PC <span>CONFIGURATIONS</span></a>
      <button class="hamburger" id="hamburger" aria-label="Menu">
        <span></span><span></span><span></span>
      </button>
      <ul class="nav-links" id="nav-links">
        <li><a href="${base}pages/products.html" ${activePage==='products'?'class="active"':''}>Products</a></li>
        <li><a href="${base}pages/builder.html"  ${activePage==='builder' ?'class="active"':''}>PC Builder</a></li>
        <li><a href="${isLogged ? `${base}pages/dashboard.html?tab=builds` : `${base}pages/login.html`}" ${activePage==='builds'?'class="active"':''}>My Builds</a></li>
        <li><a href="${isLogged ? `${base}pages/orders.html` : `${base}pages/login.html`}" ${activePage==='orders'?'class="active"':''}>Orders</a></li>
      </ul>
      <div class="nav-actions">
        <button class="nav-cart-btn" id="nav-cart-btn" onclick="location.href='${base}pages/cart.html'" title="View shopping cart">
          <i class="bi bi-cart3"></i>
          Cart
          <span class="cart-badge" id="cart-badge" style="display:none">0</span>
        </button>
        ${isLogged
          ? `<button class="nav-user-btn" id="nav-user-btn">
               <i class="bi bi-person-fill"></i> ${user?.fullName?.split(' ')[0] || 'Account'}
             </button>`
          : `<a href="${base}pages/login.html" class="btn btn-primary btn-sm">
               <i class="bi bi-person"></i> Sign In
             </a>`
        }
      </div>
    </div>
  `;
  document.body.prepend(nav);

  // Hamburger
  document.getElementById('hamburger').addEventListener('click', () => {
    document.getElementById('nav-links').classList.toggle('open');
  });

  // User dropdown
  if (isLogged) {
    const btn = document.getElementById('nav-user-btn');
    btn.addEventListener('click', () => {
      const menu = document.createElement('div');
      menu.style.cssText = `
        position:fixed; top:var(--nav-h); right:24px; z-index:1100;
        background:var(--bg-card); border:1px solid var(--border);
        border-radius:var(--radius-sm); min-width:180px; overflow:hidden;
        box-shadow:var(--shadow-lg);
      `;
      menu.innerHTML = `
        <a href="${base}pages/dashboard.html" style="display:block;padding:12px 16px;font-size:.9rem;color:var(--text-primary);text-decoration:none;border-bottom:1px solid var(--border)">
          <i class="bi bi-person-circle me-2"></i> Dashboard
        </a>
        <button id="logout-btn" style="display:block;width:100%;padding:12px 16px;font-size:.9rem;color:var(--red);background:none;border:none;cursor:pointer;text-align:left">
          <i class="bi bi-box-arrow-right me-2"></i> Logout
        </button>
      `;
      document.body.appendChild(menu);
      document.getElementById('logout-btn').onclick = () => {
        auth.logout();
        menu.remove();
        window.location.href = base + 'index.html';
      };
      setTimeout(() => {
        document.addEventListener('click', () => menu.remove(), { once: true });
      }, 10);
    });
  }

  // Cart badge
  syncCartBadge();
  window.addEventListener('cart-update', syncCartBadge);
}

async function syncCartBadge() {
  if (!auth.isLoggedIn()) return;
  try {
    const cart = await api.getCart();
    const badge = document.getElementById('cart-badge');
    if (badge) {
      const count = cart.itemCount || 0;
      badge.textContent = count;
      badge.style.display = count > 0 ? 'flex' : 'none';
    }
  } catch {}
}

function getBase() {
  const path = window.location.pathname;
  if (path.includes('/pages/')) return '../';
  return '';
}
