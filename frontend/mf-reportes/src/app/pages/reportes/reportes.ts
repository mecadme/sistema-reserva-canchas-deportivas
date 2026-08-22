import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { DEPORTE_LABELS } from '../../core/deporte.model';
import { Reporte } from '../../core/reporte.model';
import { ReportesService } from '../../core/reportes.service';

@Component({
  selector: 'app-reportes',
  imports: [],
  templateUrl: './reportes.html',
  styleUrl: './reportes.css',
})
export class Reportes implements OnInit {
  private readonly reportesService = inject(ReportesService);

  protected readonly deporteLabels = DEPORTE_LABELS;

  protected readonly desde = signal(isoDaysAgo(7));
  protected readonly hasta = signal(isoDaysAgo(0));
  protected readonly loading = signal(true);
  protected readonly reporte = signal<Reporte | null>(null);
  protected readonly error = signal<string | null>(null);

  protected readonly canchaMasDemandada = computed(() => this.reporte()?.ocupacionPorCancha[0] ?? null);
  protected readonly canchaMenosDemandada = computed(() => {
    const ocupacion = this.reporte()?.ocupacionPorCancha ?? [];
    return ocupacion.length > 0 ? ocupacion[ocupacion.length - 1] : null;
  });

  ngOnInit(): void {
    this.generar();
  }

  setDesde(value: string): void {
    this.desde.set(value);
  }

  setHasta(value: string): void {
    this.hasta.set(value);
  }

  generar(): void {
    this.loading.set(true);
    this.error.set(null);
    this.reportesService.generar(this.desde(), this.hasta()).subscribe({
      next: (reporte) => {
        this.reporte.set(reporte);
        this.loading.set(false);
      },
      error: () => {
        this.loading.set(false);
        this.error.set('No se pudo generar el reporte. Intenta nuevamente.');
      },
    });
  }

  deportesEntries(): [string, number][] {
    return Object.entries(this.reporte()?.reservasPorDeporte ?? {});
  }
}

function isoDaysAgo(days: number): string {
  const date = new Date();
  date.setDate(date.getDate() - days);
  return date.toISOString().slice(0, 10);
}
