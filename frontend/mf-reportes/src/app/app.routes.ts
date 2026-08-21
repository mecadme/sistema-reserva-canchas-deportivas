import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: '',
    loadComponent: () => import('./pages/reportes/reportes').then((m) => m.Reportes),
  },
];
