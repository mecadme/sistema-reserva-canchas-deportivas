import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';

interface Sport {
  name: string;
  emoji: string;
  description: string;
}

@Component({
  selector: 'app-landing',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './landing.html',
  styleUrl: './landing.css',
})
export class Landing {
  protected readonly sports: Sport[] = [
    { name: 'Pádel', emoji: '🎾', description: 'Canchas cubiertas y al aire libre, listas todo el año.' },
    { name: 'Tenis', emoji: '🎾', description: 'Superficies rápidas con horarios desde temprano.' },
    { name: 'Básquet', emoji: '🏀', description: 'Canchas full con marcador para tus partidos.' },
  ];

  protected readonly steps = [
    { title: 'Elegí tu cancha', description: 'Consultá disponibilidad por deporte, fecha y horario.' },
    { title: 'Reservá en segundos', description: 'Confirmá el bloque horario que más te convenga.' },
    { title: 'Juega', description: 'Gestioná y cancelá tus reservas cuando quieras.' },
  ];
}
