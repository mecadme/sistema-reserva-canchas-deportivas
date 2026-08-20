import { Injectable } from '@angular/core';
import { Observable, delay, of } from 'rxjs';
import { Slot } from './slot.model';

const MOCK_LATENCY_MS = 400;
const OPENING_HOUR = 7;
const CLOSING_HOUR = 22;

@Injectable({ providedIn: 'root' })
export class AvailabilityService {
  getSlots(canchaId: string, fechaIso: string): Observable<Slot[]> {
    return of(buildSlots(canchaId, fechaIso)).pipe(delay(MOCK_LATENCY_MS));
  }
}

function buildSlots(canchaId: string, fechaIso: string): Slot[] {
  const slots: Slot[] = [];
  for (let hour = OPENING_HOUR; hour < CLOSING_HOUR; hour++) {
    const hora = `${`${hour}`.padStart(2, '0')}:00`;
    const seed = hashString(`${canchaId}|${fechaIso}|${hora}`);
    slots.push({ hora, disponible: seed % 3 !== 0 });
  }
  return slots;
}

function hashString(input: string): number {
  let hash = 0;
  for (let i = 0; i < input.length; i++) {
    hash = (hash << 5) - hash + input.charCodeAt(i);
    hash |= 0;
  }
  return Math.abs(hash);
}
