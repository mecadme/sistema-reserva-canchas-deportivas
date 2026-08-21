import { Component, OnInit, inject, signal } from '@angular/core';
import { Usuario } from '../../core/usuarios/usuario.model';
import { UsuariosService } from '../../core/usuarios/usuarios.service';

@Component({
  selector: 'app-usuarios',
  imports: [],
  templateUrl: './usuarios.html',
  styleUrl: './usuarios.css',
})
export class Usuarios implements OnInit {
  private readonly usuariosService = inject(UsuariosService);

  protected readonly loading = signal(true);
  protected readonly usuarios = signal<Usuario[]>([]);
  protected readonly savingIds = signal<Set<string>>(new Set());

  ngOnInit(): void {
    this.usuariosService.list().subscribe((usuarios) => {
      this.usuarios.set(usuarios);
      this.loading.set(false);
    });
  }

  isSaving(id: string): boolean {
    return this.savingIds().has(id);
  }

  cambiarEstado(usuario: Usuario): void {
    if (this.isSaving(usuario.id)) {
      return;
    }
    this.savingIds.update((current) => new Set(current).add(usuario.id));

    this.usuariosService.cambiarEstado(usuario.id, !usuario.activo).subscribe({
      next: (updated) => {
        this.savingIds.update((current) => {
          const next = new Set(current);
          next.delete(usuario.id);
          return next;
        });
        this.usuarios.update((current) => current.map((u) => (u.id === updated.id ? updated : u)));
      },
      error: () => {
        this.savingIds.update((current) => {
          const next = new Set(current);
          next.delete(usuario.id);
          return next;
        });
      },
    });
  }
}
