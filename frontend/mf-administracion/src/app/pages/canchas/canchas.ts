import { Component, OnInit, inject, signal } from '@angular/core';
import { Bloqueo, BloqueoInput } from '../../core/canchas/bloqueo.model';
import { Cancha, CanchaInput, DEPORTES, DEPORTE_LABELS, Deporte } from '../../core/canchas/cancha.model';
import { CanchasService } from '../../core/canchas/canchas.service';

@Component({
  selector: 'app-canchas',
  imports: [],
  templateUrl: './canchas.html',
  styleUrl: './canchas.css',
})
export class Canchas implements OnInit {
  private readonly canchasService = inject(CanchasService);

  protected readonly deportes = DEPORTES;
  protected readonly deporteLabels = DEPORTE_LABELS;

  protected readonly loading = signal(true);
  protected readonly canchas = signal<Cancha[]>([]);
  protected readonly savingIds = signal<Set<string>>(new Set());

  protected readonly formOpen = signal(false);
  protected readonly editingId = signal<string | null>(null);
  protected readonly formSaving = signal(false);
  protected readonly formError = signal<string | null>(null);
  protected readonly formNombre = signal('');
  protected readonly formDeporte = signal<Deporte>('PADEL');
  protected readonly formApertura = signal('07:00');
  protected readonly formCierre = signal('22:00');

  protected readonly bloqueoCanchaId = signal<string | null>(null);
  protected readonly bloqueosCache = signal<Record<string, Bloqueo[]>>({});
  protected readonly bloqueosLoading = signal(false);
  protected readonly bloqueoDesde = signal('');
  protected readonly bloqueoHasta = signal('');
  protected readonly bloqueoMotivo = signal('');
  protected readonly bloqueoError = signal<string | null>(null);
  protected readonly bloqueoSaving = signal(false);

  ngOnInit(): void {
    this.cargarCanchas();
  }

  abrirNuevaCancha(): void {
    this.editingId.set(null);
    this.formNombre.set('');
    this.formDeporte.set('PADEL');
    this.formApertura.set('07:00');
    this.formCierre.set('22:00');
    this.formError.set(null);
    this.formOpen.set(true);
  }

  editarCancha(cancha: Cancha): void {
    this.editingId.set(cancha.id);
    this.formNombre.set(cancha.nombre);
    this.formDeporte.set(cancha.deporte);
    this.formApertura.set(cancha.horaApertura);
    this.formCierre.set(cancha.horaCierre);
    this.formError.set(null);
    this.formOpen.set(true);
  }

  cerrarFormulario(): void {
    this.formOpen.set(false);
    this.formError.set(null);
  }

  guardarCancha(): void {
    const nombre = this.formNombre().trim();
    const apertura = this.formApertura();
    const cierre = this.formCierre();

    if (!nombre) {
      this.formError.set('El nombre es obligatorio.');
      return;
    }
    if (apertura >= cierre) {
      this.formError.set('La hora de apertura debe ser anterior a la de cierre.');
      return;
    }

    const input: CanchaInput = { nombre, deporte: this.formDeporte(), horaApertura: apertura, horaCierre: cierre };
    this.formSaving.set(true);
    this.formError.set(null);

    const editingId = this.editingId();
    const request$ = editingId
      ? this.canchasService.update(editingId, input)
      : this.canchasService.create(input);

    request$.subscribe({
      next: () => {
        this.formSaving.set(false);
        this.formOpen.set(false);
        this.cargarCanchas();
      },
      error: (err: Error) => {
        this.formSaving.set(false);
        this.formError.set(err.message);
      },
    });
  }

  cambiarEstado(cancha: Cancha): void {
    if (this.savingIds().has(cancha.id)) {
      return;
    }
    this.savingIds.update((current) => new Set(current).add(cancha.id));

    this.canchasService.cambiarEstado(cancha.id, !cancha.activa).subscribe({
      next: () => {
        this.savingIds.update((current) => {
          const next = new Set(current);
          next.delete(cancha.id);
          return next;
        });
        this.cargarCanchas();
      },
      error: () => {
        this.savingIds.update((current) => {
          const next = new Set(current);
          next.delete(cancha.id);
          return next;
        });
      },
    });
  }

  isSaving(id: string): boolean {
    return this.savingIds().has(id);
  }

  toggleBloqueos(canchaId: string): void {
    const opening = this.bloqueoCanchaId() !== canchaId;
    this.bloqueoCanchaId.set(opening ? canchaId : null);
    this.bloqueoDesde.set('');
    this.bloqueoHasta.set('');
    this.bloqueoMotivo.set('');
    this.bloqueoError.set(null);

    if (opening) {
      this.cargarBloqueos(canchaId);
    }
  }

  bloqueosDe(canchaId: string): Bloqueo[] {
    return this.bloqueosCache()[canchaId] ?? [];
  }

  crearBloqueo(canchaId: string): void {
    const desde = this.bloqueoDesde();
    const hasta = this.bloqueoHasta();
    const motivo = this.bloqueoMotivo().trim();

    if (!desde || !hasta) {
      this.bloqueoError.set('Debe indicar el inicio y el fin del bloqueo.');
      return;
    }
    if (desde >= hasta) {
      this.bloqueoError.set('El inicio del bloqueo debe ser anterior al fin.');
      return;
    }
    if (!motivo) {
      this.bloqueoError.set('Debe indicar el motivo del bloqueo.');
      return;
    }

    const input: BloqueoInput = { desde, hasta, motivo };
    this.bloqueoSaving.set(true);
    this.bloqueoError.set(null);

    this.canchasService.crearBloqueo(canchaId, input).subscribe({
      next: () => {
        this.bloqueoSaving.set(false);
        this.bloqueoDesde.set('');
        this.bloqueoHasta.set('');
        this.bloqueoMotivo.set('');
        this.cargarBloqueos(canchaId);
      },
      error: (err: Error) => {
        this.bloqueoSaving.set(false);
        this.bloqueoError.set(err.message);
      },
    });
  }

  private cargarCanchas(): void {
    this.canchasService.list().subscribe((canchas) => {
      this.canchas.set(canchas);
      this.loading.set(false);
    });
  }

  private cargarBloqueos(canchaId: string): void {
    this.bloqueosLoading.set(true);
    this.canchasService.bloqueosDe(canchaId).subscribe((bloqueos) => {
      this.bloqueosCache.update((current) => ({ ...current, [canchaId]: bloqueos }));
      this.bloqueosLoading.set(false);
    });
  }
}
