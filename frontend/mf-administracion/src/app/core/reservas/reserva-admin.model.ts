export type ReservaEstado = 'Confirmada' | 'Cancelada' | 'Finalizada';

export interface ReservaAdmin {
  id: string;
  usuarioNombre: string;
  canchaNombre: string;
  canchaEmoji: string;
  fecha: string;
  hora: string;
  estado: ReservaEstado;
}
