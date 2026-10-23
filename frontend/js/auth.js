// auth.js — Authentication state management

export const auth = {
  setUser(token, user) {
    localStorage.setItem('pc_token', token);
    localStorage.setItem('pc_user', JSON.stringify(user));
    window.dispatchEvent(new CustomEvent('auth-change', { detail: user }));
  },

  logout() {
    localStorage.removeItem('pc_token');
    localStorage.removeItem('pc_user');
    window.dispatchEvent(new CustomEvent('auth-change', { detail: null }));
  },

  getToken() {
    return localStorage.getItem('pc_token');
  },

  getUser() {
    const u = localStorage.getItem('pc_user');
    return u ? JSON.parse(u) : null;
  },

  isLoggedIn() {
    return !!this.getToken();
  },

  requireAuth(redirectTo = '/pages/login.html') {
    if (!this.isLoggedIn()) {
      window.location.href = redirectTo;
      return false;
    }
    return true;
  },

  redirectIfLoggedIn(to = '/index.html') {
    if (this.isLoggedIn()) {
      window.location.href = to;
      return true;
    }
    return false;
  }
};
