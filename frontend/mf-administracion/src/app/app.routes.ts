import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: '',
    loadComponent: () => import('./pages/home/home').then((m) => m.Home),
  },
  {
    path: 'canchas',
    loadComponent: () => import('./pages/canchas/canchas').then((m) => m.Canchas),
  },
  {
    path: 'usuarios',
    loadComponent: () => import('./pages/usuarios/usuarios').then((m) => m.Usuarios),
  },
  {
    path: 'reservas',
    loadComponent: () => import('./pages/reservas/reservas').then((m) => m.Reservas),
  },
];
