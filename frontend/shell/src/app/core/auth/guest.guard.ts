import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from './auth.service';
import { roleHomeRoute } from './role-home-route';

export const guestGuard: CanActivateFn = () => {
  const auth = inject(AuthService);
  const router = inject(Router);
  const user = auth.currentUser;

  if (user) {
    return router.createUrlTree([roleHomeRoute(user.role)]);
  }
  return true;
};
