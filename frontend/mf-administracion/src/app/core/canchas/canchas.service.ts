import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable, catchError, map, throwError } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Bloqueo, BloqueoInput } from './bloqueo.model';
import { Cancha, CanchaInput } from './cancha.model';

const BUSINESS_UTC_OFFSET = '-05:00';

interface MantenimientoResponseDto {
  id: string;
  canchaId: string;
  inicio: string;
  fin: string;
  motivo: string;
}

@Injectable({ providedIn: 'root' })
export class CanchasService {
  private readonly http = inject(HttpClient);
  private readonly base = `${environment.canchasApiUrl}/api/v1/canchas`;

  list(): Observable<Cancha[]> {
    return this.http.get<Cancha[]>(this.base, { params: { soloActivas: 'false' } });
  }

  create(input: CanchaInput): Observable<Cancha> {
    return this.http.post<Cancha>(this.base, input).pipe(catchError(rethrow));
  }

  update(id: string, input: CanchaInput): Observable<Cancha> {
    return this.http.put<Cancha>(`${this.base}/${id}`, input).pipe(catchError(rethrow));
  }

  cambiarEstado(id: string, activa: boolean): Observable<Cancha> {
    return this.http.patch<Cancha>(`${this.base}/${id}/estado`, { activa }).pipe(catchError(rethrow));
  }

  bloqueosDe(canchaId: string): Observable<Bloqueo[]> {
    return this.http
      .get<MantenimientoResponseDto[]>(`${this.base}/${canchaId}/mantenimientos`)
      .pipe(map((bloqueos) => bloqueos.map(toBloqueo)));
  }

  crearBloqueo(canchaId: string, input: BloqueoInput): Observable<Bloqueo> {
    return this.http
      .post<MantenimientoResponseDto>(`${this.base}/${canchaId}/mantenimientos`, {
        inicio: `${input.desde}:00${BUSINESS_UTC_OFFSET}`,
        fin: `${input.hasta}:00${BUSINESS_UTC_OFFSET}`,
        motivo: input.motivo,
      })
      .pipe(map(toBloqueo), catchError(rethrow));
  }
}

function toBloqueo(dto: MantenimientoResponseDto): Bloqueo {
  return { id: dto.id, canchaId: dto.canchaId, desde: dto.inicio, hasta: dto.fin, motivo: dto.motivo };
}

function rethrow(err: HttpErrorResponse) {
  return throwError(() => new Error(err.error?.message ?? 'No se pudo completar la operación.'));
}
