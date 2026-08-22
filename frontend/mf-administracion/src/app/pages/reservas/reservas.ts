import { Component, OnInit, inject, signal } from '@angular/core';
import { isCancelable } from '../../core/reservas/reservation-time';
import { ReservaAdmin } from '../../core/reservas/reserva-admin.model';
import { ReservasAdminService } from '../../core/reservas/reservas-admin.service';

@Component({
  selector: 'app-reservas',
  imports: [],
  templateUrl: './reservas.html',
  styleUrl: './reservas.css',
})
export class Reservas implements OnInit {
  private readonly reservasService = inject(ReservasAdminService);

  protected readonly loading = signal(true);
  protected readonly reservas = signal<ReservaAdmin[]>([]);
  protected readonly cancelingIds = signal<Set<string>>(new Set());
  protected readonly cancelError = signal<string | null>(null);

  ngOnInit(): void {
    this.reservasService.list().subscribe((reservas) => {
      this.reservas.set(reservas);
      this.loading.set(false);
    });
  }

  isCancelable(reserva: ReservaAdmin): boolean {
    return isCancelable(reserva);
  }

  isCanceling(id: string): boolean {
    return this.cancelingIds().has(id);
  }

  cancelar(id: string): void {
    if (this.isCanceling(id)) {
      return;
    }
    this.cancelingIds.update((current) => new Set(current).add(id));
    this.cancelError.set(null);

    this.reservasService.cancelar(id, 'Cancelada por administrador').subscribe({
      next: (cancelled) => {
        this.cancelingIds.update((current) => {
          const next = new Set(current);
          next.delete(id);
          return next;
        });
        this.reservas.update((current) =>
          current.map((r) => (r.id === id ? { ...cancelled, usuarioNombre: r.usuarioNombre } : r)),
        );
      },
      error: (err: Error) => {
        this.cancelingIds.update((current) => {
          const next = new Set(current);
          next.delete(id);
          return next;
        });
        this.cancelError.set(err.message);
      },
    });
  }
}
