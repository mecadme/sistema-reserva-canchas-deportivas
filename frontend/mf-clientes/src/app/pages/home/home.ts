import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { AvailabilityService } from '../../core/availability/availability.service';
import { Cancha } from '../../core/availability/cancha.model';
import { CanchasService } from '../../core/availability/canchas.service';
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
  private readonly canchasService = inject(CanchasService);
  private readonly reservations = inject(ReservationsService);
  private readonly router = inject(Router);

  protected readonly dates: ReservationDate[] = buildUpcomingDates(7);
  protected readonly canchas = signal<Cancha[]>([]);
  protected readonly loadingCanchas = signal(true);

  protected readonly selectedDate = signal(this.dates[0].iso);
  protected readonly selectedCanchaId = signal<string | null>(null);
  protected readonly selectedSlot = signal<string | null>(null);

  protected readonly slots = signal<Slot[]>([]);
  protected readonly loadingSlots = signal(false);
  protected readonly reserving = signal(false);
  protected readonly reserveError = signal<string | null>(null);

  protected readonly confirmedReservationsCount = computed(
    () => this.reservations.reservations().filter((r) => r.estado === 'Confirmada').length,
  );

  ngOnInit(): void {
    this.canchasService.listar().subscribe((canchas) => {
      this.canchas.set(canchas);
      this.loadingCanchas.set(false);
      if (canchas.length > 0) {
        this.selectedCanchaId.set(canchas[0].id);
        this.loadSlots();
      }
    });
    this.reservations.list().subscribe();
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
    this.reserveError.set(null);
  }

  reservar(): void {
    const hora = this.selectedSlot();
    const canchaId = this.selectedCanchaId();
    if (!hora || !canchaId || this.reserving()) {
      return;
    }
    this.reserving.set(true);
    this.reserveError.set(null);

    this.reservations
      .create({ canchaId, fecha: this.selectedDate(), hora })
      .subscribe({
        next: () => {
          this.reserving.set(false);
          this.router.navigateByUrl('/clientes/reservas');
        },
        error: (err: Error) => {
          this.reserving.set(false);
          this.reserveError.set(err.message);
          this.loadSlots();
        },
      });
  }

  private loadSlots(): void {
    const canchaId = this.selectedCanchaId();
    if (!canchaId) {
      return;
    }
    this.selectedSlot.set(null);
    this.loadingSlots.set(true);

    this.availability.getSlots(canchaId, this.selectedDate()).subscribe((slots) => {
      this.slots.set(slots);
      this.loadingSlots.set(false);
    });
  }
}
