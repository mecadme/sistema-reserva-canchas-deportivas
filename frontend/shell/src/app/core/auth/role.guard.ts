import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from './auth.service';
import { UserRole } from './user.model';

/**
 * Control de acceso por rol (secc. 3.1) en el lado del cliente: decide si se permite
 * navegar a una rama de rutas antes de descargar el microfrontend remoto correspondiente.
 * Esto es solo UX (evita cargar un remote que el usuario no debería ver); la autorización
 * real y no evadible ocurre en cada microservicio backend vía `hasRole(...)` sobre el JWT.
 */
export function roleGuard(allowedRoles: UserRole[]): CanActivateFn {
  return () => {
    const auth = inject(AuthService);
    const router = inject(Router);
    const user = auth.currentUser;

    if (!user) {
      return router.createUrlTree(['/login']);
    }
    if (!allowedRoles.includes(user.role)) {
      return router.createUrlTree(['/']);
    }
    return true;
  };
}
