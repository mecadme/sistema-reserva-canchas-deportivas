import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable, map } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Cancha, DEPORTE_EMOJIS, Deporte } from './cancha.model';

interface CanchaResponseDto {
  id: string;
  nombre: string;
  deporte: Deporte;
  horaApertura: string;
  horaCierre: string;
  activa: boolean;
}

@Injectable({ providedIn: 'root' })
export class CanchasService {
  private readonly http = inject(HttpClient);

  listar(): Observable<Cancha[]> {
    return this.http
      .get<CanchaResponseDto[]>(`${environment.canchasApiUrl}/api/v1/canchas`)
      .pipe(
        map((canchas) =>
          canchas.map((c) => ({ id: c.id, nombre: c.nombre, emoji: DEPORTE_EMOJIS[c.deporte] ?? '🏟️' })),
        ),
      );
  }
}
