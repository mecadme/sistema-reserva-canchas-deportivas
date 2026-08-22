import { Routes } from '@angular/router';
import { guestGuard } from './core/auth/guest.guard';
import { roleGuard } from './core/auth/role.guard';

// Mapea los roles/permisos de la secc. 3.1: 'clientes' (Usuario Final) solo lo puede
// activar un rol 'cliente'; 'admin' y 'reportes' solo un rol 'admin'. Cada rama usa
// `loadChildren` apuntando a un especificador tipo `mfX/Routes` en vez de una ruta de
// archivo: eso es lo que Module Federation resuelve en tiempo de ejecución contra el
// remoteEntry.js configurado en webpack.config.js, así que estos remotes no se compilan
// dentro del bundle del shell — solo se descargan cuando el usuario navega ahí.
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
        loadChildren: () => import('mfClientes/Routes').then((m) => m.routes),
      },
      {
        path: 'admin',
        canActivate: [roleGuard(['admin'])],
        loadChildren: () => import('mfAdministracion/Routes').then((m) => m.routes),
      },
      {
        path: 'reportes',
        canActivate: [roleGuard(['admin'])],
        loadChildren: () => import('mfReportes/Routes').then((m) => m.routes),
      },
    ],
  },
  { path: '**', redirectTo: '' },
];
