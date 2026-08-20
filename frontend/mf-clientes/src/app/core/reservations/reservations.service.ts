import { Injectable, signal } from '@angular/core';
import { Observable, delay, of } from 'rxjs';
import { reservationStartDate } from './reservation-time';
import { NewReservationInput, Reservation } from './reservation.model';

const STORAGE_KEY = 'mf-clientes-reservations';
const MOCK_LATENCY_MS = 300;

@Injectable({ providedIn: 'root' })
export class ReservationsService {
  private readonly reservationsSignal = signal<Reservation[]>(this.restore());

  readonly reservations = this.reservationsSignal.asReadonly();

  list(): Observable<Reservation[]> {
    return of(sortByMostRecent(this.reservationsSignal())).pipe(delay(MOCK_LATENCY_MS));
  }

  create(input: NewReservationInput): Observable<Reservation> {
    const reservation: Reservation = {
      id: `res-${Date.now()}`,
      ...input,
      estado: 'Confirmada',
      creadaEn: new Date().toISOString(),
    };
    const updated = [reservation, ...this.reservationsSignal()];
    this.reservationsSignal.set(updated);
    this.persist(updated);
    return of(reservation).pipe(delay(MOCK_LATENCY_MS));
  }

  cancel(id: string): Observable<Reservation | null> {
    let cancelled: Reservation | null = null;
    const updated = this.reservationsSignal().map((reservation) => {
      if (reservation.id === id) {
        cancelled = { ...reservation, estado: 'Cancelada' as const };
        return cancelled;
      }
      return reservation;
    });
    this.reservationsSignal.set(updated);
    this.persist(updated);
    return of(cancelled).pipe(delay(MOCK_LATENCY_MS));
  }

  private restore(): Reservation[] {
    const raw = localStorage.getItem(STORAGE_KEY);
    return raw ? (JSON.parse(raw) as Reservation[]) : [];
  }

  private persist(reservations: Reservation[]): void {
    localStorage.setItem(STORAGE_KEY, JSON.stringify(reservations));
  }
}

function sortByMostRecent(reservations: Reservation[]): Reservation[] {
  return [...reservations].sort(
    (a, b) => reservationStartDate(a.fecha, a.hora).getTime() - reservationStartDate(b.fecha, b.hora).getTime(),
  );
}
