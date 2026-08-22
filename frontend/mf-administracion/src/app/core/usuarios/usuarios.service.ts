import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable, map } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Usuario, UsuarioRol } from './usuario.model';

interface UsuarioResponseDto {
  id: string;
  nombre: string;
  email: string;
  rol: 'USUARIO' | 'ADMINISTRADOR';
  activo: boolean;
  creadoEn: string;
}

function mapRol(rol: UsuarioResponseDto['rol']): UsuarioRol {
  return rol === 'ADMINISTRADOR' ? 'admin' : 'cliente';
}

function toUsuario(dto: UsuarioResponseDto): Usuario {
  return { id: dto.id, nombre: dto.nombre, email: dto.email, rol: mapRol(dto.rol), activo: dto.activo };
}

@Injectable({ providedIn: 'root' })
export class UsuariosService {
  private readonly http = inject(HttpClient);
  private readonly base = `${environment.usuariosApiUrl}/api/v1/usuarios`;

  list(): Observable<Usuario[]> {
    return this.http.get<UsuarioResponseDto[]>(this.base).pipe(map((usuarios) => usuarios.map(toUsuario)));
  }

  cambiarEstado(id: string, activo: boolean): Observable<Usuario> {
    return this.http.patch<UsuarioResponseDto>(`${this.base}/${id}/estado`, { activo }).pipe(map(toUsuario));
  }
}
