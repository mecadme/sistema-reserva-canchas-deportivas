import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable, map } from 'rxjs';
import { environment } from '../../environments/environment';
import { Reporte } from './reporte.model';

interface OcupacionCanchaDto {
  cancha: string;
  reservasOcupadas: number;
  bloquesDisponibles: number;
  porcentajeOcupacion: number;
}

interface ReporteResponseDto {
  desde: string;
  hasta: string;
  totalReservas: number;
  confirmadas: number;
  canceladas: number;
  finalizadas: number;
  ocupacionPorCancha: OcupacionCanchaDto[];
  reservasPorDeporte: Record<string, number>;
}

@Injectable({ providedIn: 'root' })
export class ReportesService {
  private readonly http = inject(HttpClient);

  generar(desde: string, hasta: string): Observable<Reporte> {
    return this.http
      .get<ReporteResponseDto>(`${environment.reportesApiUrl}/api/v1/reportes/ocupacion`, {
        params: { desde, hasta },
      })
      .pipe(map(toReporte));
  }
}

function toReporte(dto: ReporteResponseDto): Reporte {
  return {
    desde: dto.desde,
    hasta: dto.hasta,
    total: dto.totalReservas,
    confirmadas: dto.confirmadas,
    canceladas: dto.canceladas,
    finalizadas: dto.finalizadas,
    ocupacionPorCancha: dto.ocupacionPorCancha.map((o) => ({
      canchaNombre: o.cancha,
      reservasOcupadas: o.reservasOcupadas,
      bloquesDisponibles: o.bloquesDisponibles,
      porcentajeOcupacion: o.porcentajeOcupacion,
    })),
    reservasPorDeporte: dto.reservasPorDeporte,
  };
}
