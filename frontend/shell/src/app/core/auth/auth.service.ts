import { Injectable, signal } from '@angular/core';
import { Observable, delay, of, throwError } from 'rxjs';
import { AuthSession, AuthUser } from './user.model';
import { emailExists, findByCredentials, registerUser } from './mock-users.store';

const SESSION_KEY = 'auth-session';
const MOCK_LATENCY_MS = 500;

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly sessionSignal = signal<AuthSession | null>(this.restoreSession());

  readonly session = this.sessionSignal.asReadonly();

  get currentUser(): AuthUser | null {
    return this.sessionSignal()?.user ?? null;
  }

  isAuthenticated(): boolean {
    return this.sessionSignal() !== null;
  }

  login(email: string, password: string): Observable<AuthSession> {
    const user = findByCredentials(email, password);
    if (!user) {
      return throwError(() => new Error('Correo o contraseña incorrectos.')).pipe(delay(MOCK_LATENCY_MS));
    }
    const session: AuthSession = { user, token: this.buildFakeToken(user.id) };
    return of(session).pipe(delay(MOCK_LATENCY_MS));
  }

  register(nombre: string, email: string, password: string): Observable<AuthSession> {
    if (emailExists(email)) {
      return throwError(() => new Error('Ya existe una cuenta registrada con ese correo.')).pipe(
        delay(MOCK_LATENCY_MS),
      );
    }
    const user = registerUser(nombre, email, password, 'cliente');
    const session: AuthSession = { user, token: this.buildFakeToken(user.id) };
    return of(session).pipe(delay(MOCK_LATENCY_MS));
  }

  setSession(session: AuthSession): void {
    this.sessionSignal.set(session);
    localStorage.setItem(SESSION_KEY, JSON.stringify(session));
  }

  logout(): void {
    this.sessionSignal.set(null);
    localStorage.removeItem(SESSION_KEY);
  }

  private restoreSession(): AuthSession | null {
    const raw = localStorage.getItem(SESSION_KEY);
    return raw ? (JSON.parse(raw) as AuthSession) : null;
  }

  private buildFakeToken(userId: string): string {
    return btoa(`${userId}:${Date.now()}`);
  }
}
