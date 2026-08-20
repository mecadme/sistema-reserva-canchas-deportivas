import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { isCancelable } from '../../core/reservations/reservation-time';
import { Reservation } from '../../core/reservations/reservation.model';
import { ReservationsService } from '../../core/reservations/reservations.service';

@Component({
  selector: 'app-reservas',
  imports: [RouterLink],
  templateUrl: './reservas.html',
  styleUrl: './reservas.css',
})
export class Reservas implements OnInit {
  private readonly reservationsService = inject(ReservationsService);

  protected readonly reservations = signal<Reservation[]>([]);
  protected readonly loading = signal(false);
  protected readonly cancelingIds = signal<Set<string>>(new Set());

  protected readonly confirmedReservationsCount = computed(
    () => this.reservations().filter((r) => r.estado === 'Confirmada').length,
  );

  ngOnInit(): void {
    this.loading.set(true);
    this.reservationsService.list().subscribe((reservations) => {
      this.reservations.set(reservations);
      this.loading.set(false);
    });
  }

  isCancelable(reservation: Reservation): boolean {
    return isCancelable(reservation);
  }

  isCanceling(id: string): boolean {
    return this.cancelingIds().has(id);
  }

  cancelar(id: string): void {
    if (this.isCanceling(id)) {
      return;
    }
    this.cancelingIds.update((current) => new Set(current).add(id));

    this.reservationsService.cancel(id).subscribe((cancelled) => {
      this.cancelingIds.update((current) => {
        const next = new Set(current);
        next.delete(id);
        return next;
      });
      if (cancelled) {
        this.reservations.update((current) =>
          current.map((reservation) => (reservation.id === id ? cancelled : reservation)),
        );
      }
    });
  }

  formatFecha(fecha: string): string {
    const [year, month, day] = fecha.split('-').map(Number);
    const date = new Date(year, month - 1, day);
    const formatted = new Intl.DateTimeFormat('es-MX', {
      weekday: 'long',
      day: 'numeric',
      month: 'long',
    }).format(date);
    return formatted.charAt(0).toUpperCase() + formatted.slice(1);
  }
}
