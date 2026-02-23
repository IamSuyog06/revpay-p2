import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';

export const authGuard: CanActivateFn = () => {
  const router = inject(Router);
  const token = localStorage.getItem('token');

  if (token) {
    return true;
  }

  // no token - redirect to login
  router.navigate(['/login']);
  return false;
};

// guard for business only pages
export const businessGuard: CanActivateFn = () => {
  const router = inject(Router);
  const role = localStorage.getItem('role');

  if (role === 'BUSINESS') {
    return true;
  }

  // not a business user - redirect to dashboard
  router.navigate(['/dashboard']);
  return false;
};