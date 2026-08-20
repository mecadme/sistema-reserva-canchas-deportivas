export type UserRole = 'cliente' | 'admin';

export interface AuthUser {
  id: string;
  nombre: string;
  email: string;
  role: UserRole;
}

export interface AuthSession {
  user: AuthUser;
  token: string;
}
