import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable, map } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Slot } from './slot.model';

interface SlotResponseDto {
  inicio: string;
  fin: string;
  estado: 'DISPONIBLE' | 'OCUPADO' | 'BLOQUEADO';
}

interface DisponibilidadResponseDto {
  canchaId: string;
  fecha: string;
  bloques: SlotResponseDto[];
}

@Injectable({ providedIn: 'root' })
export class AvailabilityService {
  private readonly http = inject(HttpClient);

  getSlots(canchaId: string, fechaIso: string): Observable<Slot[]> {
    return this.http
      .get<DisponibilidadResponseDto>(`${environment.reservasApiUrl}/api/v1/disponibilidad`, {
        params: { canchaId, fecha: fechaIso },
      })
      .pipe(
        map((res) =>
          res.bloques.map((bloque) => ({
            hora: bloque.inicio.slice(11, 16),
            disponible: bloque.estado === 'DISPONIBLE',
          })),
        ),
      );
  }
}
