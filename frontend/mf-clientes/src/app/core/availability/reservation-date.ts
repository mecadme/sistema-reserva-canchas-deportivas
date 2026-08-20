export interface ReservationDate {
  iso: string;
  weekday: string;
  dayNumber: number;
}

const WEEKDAY_FORMATTER = new Intl.DateTimeFormat('es-MX', { weekday: 'short' });

export function buildUpcomingDates(count: number, from: Date = new Date()): ReservationDate[] {
  return Array.from({ length: count }, (_, offset) => {
    const date = new Date(from);
    date.setDate(date.getDate() + offset);
    return {
      iso: toIsoDate(date),
      weekday: capitalize(WEEKDAY_FORMATTER.format(date).replace('.', '')),
      dayNumber: date.getDate(),
    };
  });
}

function toIsoDate(date: Date): string {
  const year = date.getFullYear();
  const month = `${date.getMonth() + 1}`.padStart(2, '0');
  const day = `${date.getDate()}`.padStart(2, '0');
  return `${year}-${month}-${day}`;
}

function capitalize(value: string): string {
  return value.charAt(0).toUpperCase() + value.slice(1);
}
