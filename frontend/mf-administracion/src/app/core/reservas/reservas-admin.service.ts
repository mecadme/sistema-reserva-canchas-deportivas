import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable, catchError, forkJoin, map, throwError } from 'rxjs';
import { environment } from '../../../environments/environment';
import { DEPORTE_EMOJIS, Deporte } from '../canchas/cancha.model';
import { UsuariosService } from '../usuarios/usuarios.service';
import { ReservaAdmin } from './reserva-admin.model';

interface ReservaResponseDto {
  id: string;
  usuarioId: string;
  canchaId: string;
  canchaNombre: string;
  deporte: Deporte;
  inicio: string;
  fin: string;
  estado: 'CONFIRMADA' | 'CANCELADA' | 'FINALIZADA';
  canceladaPor: string | null;
  motivoCancelacion: string | null;
}

const ESTADO_LABELS: Record<ReservaResponseDto['estado'], ReservaAdmin['estado']> = {
  CONFIRMADA: 'Confirmada',
  CANCELADA: 'Cancelada',
  FINALIZADA: 'Finalizada',
};

@Injectable({ providedIn: 'root' })
export class ReservasAdminService {
  private readonly http = inject(HttpClient);
  private readonly usuariosService = inject(UsuariosService);
  private readonly base = `${environment.reservasApiUrl}/api/v1/reservas`;

  list(): Observable<ReservaAdmin[]> {
    return forkJoin({
      reservas: this.http.get<ReservaResponseDto[]>(this.base),
      usuarios: this.usuariosService.list(),
    }).pipe(
      map(({ reservas, usuarios }) => {
        const nombreDeUsuario = new Map(usuarios.map((u) => [u.id, u.nombre]));
        return reservas
          .map((r) => toReservaAdmin(r, nombreDeUsuario.get(r.usuarioId) ?? 'Usuario'))
          .sort((a, b) => `${a.fecha}${a.hora}`.localeCompare(`${b.fecha}${b.hora}`));
      }),
    );
  }

  cancelar(id: string, motivo: string): Observable<ReservaAdmin> {
    return this.http
      .patch<ReservaResponseDto>(`${this.base}/${id}/cancelacion`, { motivo })
      .pipe(
        map((dto) => toReservaAdmin(dto, '')),
        catchError((err: HttpErrorResponse) =>
          throwError(() => new Error(err.error?.message ?? 'No se pudo cancelar la reserva.'))),
      );
  }
}

function toReservaAdmin(dto: ReservaResponseDto, usuarioNombre: string): ReservaAdmin {
  const [fecha, horaFull] = dto.inicio.split('T');
  return {
    id: dto.id,
    usuarioNombre,
    canchaNombre: dto.canchaNombre,
    canchaEmoji: DEPORTE_EMOJIS[dto.deporte] ?? '🏟️',
    fecha,
    hora: horaFull.slice(0, 5),
    estado: ESTADO_LABELS[dto.estado],
  };
}
