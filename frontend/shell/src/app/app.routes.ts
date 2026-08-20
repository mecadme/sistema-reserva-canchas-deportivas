import { Routes } from '@angular/router';
import { guestGuard } from './core/auth/guest.guard';
import { roleGuard } from './core/auth/role.guard';

export const routes: Routes = [
  {
    path: '',
    loadComponent: () => import('./pages/landing/landing').then((m) => m.Landing),
    canActivate: [guestGuard],
  },
  {
    path: 'login',
    loadComponent: () => import('./pages/login/login').then((m) => m.Login),
    canActivate: [guestGuard],
  },
  {
    path: 'registro',
    loadComponent: () => import('./pages/register/register').then((m) => m.Register),
    canActivate: [guestGuard],
  },
  {
    path: '',
    loadComponent: () =>
      import('./layout/authenticated-layout/authenticated-layout').then((m) => m.AuthenticatedLayout),
    children: [
      {
        path: 'clientes',
        canActivate: [roleGuard(['cliente'])],
        loadComponent: () => import('mfClientes/Component').then((m) => m.App),
      },
      {
        path: 'admin',
        canActivate: [roleGuard(['admin'])],
        loadComponent: () =>
          import('./pages/admin-placeholder/admin-placeholder').then((m) => m.AdminPlaceholder),
      },
    ],
  },
  { path: '**', redirectTo: '' },
];
