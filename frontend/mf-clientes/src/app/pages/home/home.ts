import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { AvailabilityService } from '../../core/availability/availability.service';
import { CANCHAS, Cancha } from '../../core/availability/cancha.model';
import { ReservationDate, buildUpcomingDates } from '../../core/availability/reservation-date';
import { Slot } from '../../core/availability/slot.model';
import { ReservationsService } from '../../core/reservations/reservations.service';

@Component({
  selector: 'app-home',
  imports: [RouterLink],
  templateUrl: './home.html',
  styleUrl: './home.css',
})
export class Home implements OnInit {
  private readonly availability = inject(AvailabilityService);
  private readonly reservations = inject(ReservationsService);
  private readonly router = inject(Router);

  protected readonly dates: ReservationDate[] = buildUpcomingDates(7);
  protected readonly canchas: Cancha[] = CANCHAS;

  protected readonly selectedDate = signal(this.dates[0].iso);
  protected readonly selectedCanchaId = signal(this.canchas[0].id);
  protected readonly selectedSlot = signal<string | null>(null);

  protected readonly slots = signal<Slot[]>([]);
  protected readonly loadingSlots = signal(false);
  protected readonly reserving = signal(false);

  protected readonly confirmedReservationsCount = computed(
    () => this.reservations.reservations().filter((r) => r.estado === 'Confirmada').length,
  );

  ngOnInit(): void {
    this.loadSlots();
  }

  selectDate(iso: string): void {
    if (this.selectedDate() === iso) {
      return;
    }
    this.selectedDate.set(iso);
    this.loadSlots();
  }

  selectCancha(id: string): void {
    if (this.selectedCanchaId() === id) {
      return;
    }
    this.selectedCanchaId.set(id);
    this.loadSlots();
  }

  selectSlot(hora: string): void {
    this.selectedSlot.set(hora);
  }

  reservar(): void {
    const hora = this.selectedSlot();
    const cancha = this.canchas.find((c) => c.id === this.selectedCanchaId());
    if (!hora || !cancha || this.reserving()) {
      return;
    }
    this.reserving.set(true);

    this.reservations
      .create({
        canchaId: cancha.id,
        canchaNombre: cancha.nombre,
        canchaEmoji: cancha.emoji,
        fecha: this.selectedDate(),
        hora,
      })
      .subscribe(() => {
        this.reserving.set(false);
        this.router.navigateByUrl('/clientes/reservas');
      });
  }

  private loadSlots(): void {
    this.selectedSlot.set(null);
    this.loadingSlots.set(true);

    this.availability.getSlots(this.selectedCanchaId(), this.selectedDate()).subscribe((slots) => {
      this.slots.set(slots);
      this.loadingSlots.set(false);
    });
  }
}
