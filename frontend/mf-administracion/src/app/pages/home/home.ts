import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { Cancha } from '../../core/canchas/cancha.model';
import { CanchasService } from '../../core/canchas/canchas.service';
import { ReservaAdmin } from '../../core/reservas/reserva-admin.model';
import { ReservasAdminService } from '../../core/reservas/reservas-admin.service';
import { Usuario } from '../../core/usuarios/usuario.model';
import { UsuariosService } from '../../core/usuarios/usuarios.service';

@Component({
  selector: 'app-home',
  imports: [],
  templateUrl: './home.html',
  styleUrl: './home.css',
})
export class Home implements OnInit {
  private readonly canchasService = inject(CanchasService);
  private readonly usuariosService = inject(UsuariosService);
  private readonly reservasService = inject(ReservasAdminService);

  protected readonly loading = signal(true);
  protected readonly canchas = signal<Cancha[]>([]);
  protected readonly usuarios = signal<Usuario[]>([]);
  protected readonly reservas = signal<ReservaAdmin[]>([]);

  protected readonly canchasActivas = computed(() => this.canchas().filter((c) => c.activa).length);
  protected readonly usuariosActivos = computed(() => this.usuarios().filter((u) => u.activo).length);
  protected readonly reservasConfirmadas = computed(
    () => this.reservas().filter((r) => r.estado === 'Confirmada').length,
  );
  protected readonly reservasCanceladas = computed(
    () => this.reservas().filter((r) => r.estado === 'Cancelada').length,
  );

  ngOnInit(): void {
    this.canchasService.list().subscribe((canchas) => this.canchas.set(canchas));
    this.usuariosService.list().subscribe((usuarios) => this.usuarios.set(usuarios));
    this.reservasService.list().subscribe((reservas) => {
      this.reservas.set(reservas);
      this.loading.set(false);
    });
  }
}
