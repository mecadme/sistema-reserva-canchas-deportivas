export type UsuarioRol = 'admin' | 'cliente';

export interface Usuario {
  id: string;
  nombre: string;
  email: string;
  rol: UsuarioRol;
  activo: boolean;
}
