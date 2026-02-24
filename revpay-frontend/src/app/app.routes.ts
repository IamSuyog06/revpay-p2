import { Routes } from '@angular/router';
import { authGuard, businessGuard } from './core/guards/auth-guard';

export const routes: Routes = [
  {
    path: '',
    redirectTo: 'login',
    pathMatch: 'full'
  },
  {
    path: 'login',
    loadComponent: () =>
      import('./pages/auth/login/login').then(m => m.LoginComponent)
  },
  {
    path: 'register',
    loadComponent: () =>
      import('./pages/auth/register/register').then(m => m.RegisterComponent)
  },
  {
    path: 'dashboard',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./pages/dashboard/dashboard').then(m => m.DashboardComponent)
  },
  {
    path: 'wallet',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./pages/wallet/wallet').then(m => m.WalletComponent)
  },
  {
    path: 'transactions',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./pages/transactions/transactions').then(m => m.TransactionsComponent)
  },
  {
    path: 'payment-methods',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./pages/payment-methods/payment-methods').then(m => m.PaymentMethodsComponent)
  },
  {
    path: 'notifications',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./pages/notifications/notifications').then(m => m.NotificationsComponent)
  },
  {
    path: 'profile',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./pages/profile/profile').then(m => m.ProfileComponent)
  },
  {
    path: 'invoices',
    canActivate: [authGuard, businessGuard],
    loadComponent: () =>
      import('./pages/invoices/invoices').then(m => m.InvoicesComponent)
  },
  {
    path: 'loans',
    canActivate: [authGuard, businessGuard],
    loadComponent: () =>
      import('./pages/loans/loans').then(m => m.LoansComponent)
  },
  {
    path: 'analytics',
    canActivate: [authGuard, businessGuard],
    loadComponent: () =>
      import('./pages/analytics/analytics').then(m => m.AnalyticsComponent)
  },
  { 
    path: 'admin', 
    canActivate: [authGuard],
    loadComponent: () => 
    import('./pages/admin/admin').then(m => m.AdminComponent)
 },
  {
    path: '**',
    redirectTo: 'login'
  }
];