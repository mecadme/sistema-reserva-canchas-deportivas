import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from './auth.service';
import { UserRole } from './user.model';

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
