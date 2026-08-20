import { UserRole } from './user.model';

export function roleHomeRoute(role: UserRole): string {
  return role === 'admin' ? '/admin' : '/clientes';
}
