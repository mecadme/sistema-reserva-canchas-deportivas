import { ReservaAdmin } from './reserva-admin.model';

export function reservationStartDate(fecha: string, hora: string): Date {
  const [year, month, day] = fecha.split('-').map(Number);
  const [hour] = hora.split(':').map(Number);
  return new Date(year, month - 1, day, hour, 0, 0, 0);
}

export function isCancelable(reserva: ReservaAdmin, now: Date = new Date()): boolean {
  if (reserva.estado !== 'Confirmada') {
    return false;
  }
  return reservationStartDate(reserva.fecha, reserva.hora) > now;
}
