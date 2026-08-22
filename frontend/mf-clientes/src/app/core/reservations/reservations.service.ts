import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable, inject, signal } from '@angular/core';
import { Observable, catchError, map, tap, throwError } from 'rxjs';
import { environment } from '../../../environments/environment';
import { DEPORTE_EMOJIS, Deporte } from '../availability/cancha.model';
import { NewReservationInput, Reservation, ReservationStatus } from './reservation.model';

const BUSINESS_UTC_OFFSET = '-05:00';

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

const ESTADO_LABELS: Record<ReservaResponseDto['estado'], ReservationStatus> = {
  CONFIRMADA: 'Confirmada',
  CANCELADA: 'Cancelada',
  FINALIZADA: 'Finalizada',
};

@Injectable({ providedIn: 'root' })
export class ReservationsService {
  private readonly http = inject(HttpClient);
  private readonly reservationsSignal = signal<Reservation[]>([]);

  readonly reservations = this.reservationsSignal.asReadonly();

  list(): Observable<Reservation[]> {
    return this.http
      .get<ReservaResponseDto[]>(`${environment.reservasApiUrl}/api/v1/reservas/mias`)
      .pipe(
        map((reservas) => reservas.map(toReservation).sort(byMostRecent)),
        tap((reservations) => this.reservationsSignal.set(reservations)),
      );
  }

  create(input: NewReservationInput): Observable<Reservation> {
    return this.http
      .post<ReservaResponseDto>(`${environment.reservasApiUrl}/api/v1/reservas`, {
        canchaId: input.canchaId,
        inicio: `${input.fecha}T${input.hora}:00${BUSINESS_UTC_OFFSET}`,
      })
      .pipe(
        map(toReservation),
        catchError((err: HttpErrorResponse) =>
          throwError(() => new Error(mensajeError(err))),
        ),
      );
  }

  cancel(id: string): Observable<Reservation> {
    return this.http
      .patch<ReservaResponseDto>(`${environment.reservasApiUrl}/api/v1/reservas/${id}/cancelacion`, {})
      .pipe(
        map(toReservation),
        tap((cancelled) => {
          this.reservationsSignal.update((current) =>
            current.map((r) => (r.id === cancelled.id ? cancelled : r)),
          );
        }),
        catchError((err: HttpErrorResponse) =>
          throwError(() => new Error(mensajeErrorCancelacion(err))),
        ),
      );
  }
}

function toReservation(dto: ReservaResponseDto): Reservation {
  const [fecha, horaFull] = dto.inicio.split('T');
  return {
    id: dto.id,
    canchaId: dto.canchaId,
    canchaNombre: dto.canchaNombre,
    canchaEmoji: DEPORTE_EMOJIS[dto.deporte] ?? '🏟️',
    fecha,
    hora: horaFull.slice(0, 5),
    estado: ESTADO_LABELS[dto.estado],
  };
}

function byMostRecent(a: Reservation, b: Reservation): number {
  return `${a.fecha}${a.hora}`.localeCompare(`${b.fecha}${b.hora}`);
}

function mensajeError(err: HttpErrorResponse): string {
  if (err.status === 409) {
    return err.error?.message ?? 'Ese bloque horario ya fue reservado. Elige otro horario.';
  }
  if (err.status === 400) {
    return err.error?.message ?? 'Datos de la reserva inválidos.';
  }
  return 'No se pudo completar la reserva. Intenta nuevamente.';
}

function mensajeErrorCancelacion(err: HttpErrorResponse): string {
  if (err.status === 403) {
    return err.error?.message ?? 'Solo puedes cancelar tus propias reservas.';
  }
  if (err.status === 409) {
    return err.error?.message ?? 'Esta reserva ya no se puede cancelar.';
  }
  return 'No se pudo cancelar la reserva. Intenta nuevamente.';
}
