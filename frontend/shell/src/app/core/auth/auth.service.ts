import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable, inject, signal } from '@angular/core';
import { Observable, catchError, map, switchMap, throwError } from 'rxjs';
import { environment } from '../../../environments/environment';
import { AuthSession, AuthUser, UserRole } from './user.model';

const SESSION_KEY = 'auth-session';

interface TokenResponse {
  accessToken: string;
  tokenType: string;
  expiresInSeconds: number;
}

interface UsuarioResponse {
  id: string;
  nombre: string;
  email: string;
  rol: 'USUARIO' | 'ADMINISTRADOR';
  activo: boolean;
  creadoEn: string;
}

function mapRol(rol: UsuarioResponse['rol']): UserRole {
  return rol === 'ADMINISTRADOR' ? 'admin' : 'cliente';
}

/**
 * Único punto de autenticación del shell (secc. 4.1: el shell orquesta "layout, navegación,
 * autenticación"). Solo vive aquí porque es el host de Module Federation: la sesión
 * (usuario + token) se guarda en `localStorage` para sobrevivir recargas y se expone como
 * signal para que guards, interceptor y layout reaccionen a login/logout sin recargar la app.
 * Los microfrontends remotos no implementan su propio login; consumen esta sesión indirectamente
 * a través de las llamadas HTTP que pasan por {@link authInterceptor}.
 */
@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly sessionSignal = signal<AuthSession | null>(this.restoreSession());

  readonly session = this.sessionSignal.asReadonly();

  get currentUser(): AuthUser | null {
    return this.sessionSignal()?.user ?? null;
  }

  isAuthenticated(): boolean {
    return this.sessionSignal() !== null;
  }

  login(email: string, password: string): Observable<AuthSession> {
    return this.http
      .post<TokenResponse>(`${environment.usuariosApiUrl}/api/v1/auth/login`, { email, password })
      .pipe(
        switchMap((token) => this.buildSession(token)),
        catchError((err: HttpErrorResponse) =>
          throwError(() =>
            new Error(err.status === 401 ? 'Correo o contraseña incorrectos.' : 'No se pudo iniciar sesión.'),
          ),
        ),
      );
  }

  register(nombre: string, email: string, password: string): Observable<AuthSession> {
    return this.http
      .post<UsuarioResponse>(`${environment.usuariosApiUrl}/api/v1/auth/registro`, { nombre, email, password })
      .pipe(
        switchMap(() => this.login(email, password)),
        catchError((err: HttpErrorResponse) =>
          throwError(() =>
            new Error(
              err.status === 409 ? 'Ya existe una cuenta registrada con ese correo.' : 'No se pudo completar el registro.',
            ),
          ),
        ),
      );
  }

  setSession(session: AuthSession): void {
    this.sessionSignal.set(session);
    localStorage.setItem(SESSION_KEY, JSON.stringify(session));
  }

  logout(): void {
    this.sessionSignal.set(null);
    localStorage.removeItem(SESSION_KEY);
  }

  // ms-usuarios/auth/login solo devuelve el JWT, no el perfil del usuario; por eso se
  // encadena una segunda llamada a /usuarios/me (con el token recién emitido) para poder
  // conocer el rol y así decidir a qué rutas (RN de la secc. 3.1) puede navegar el usuario.
  private buildSession(token: TokenResponse): Observable<AuthSession> {
    return this.http
      .get<UsuarioResponse>(`${environment.usuariosApiUrl}/api/v1/usuarios/me`, {
        headers: { Authorization: `${token.tokenType} ${token.accessToken}` },
      })
      .pipe(
        map((usuario) => ({
          user: { id: usuario.id, nombre: usuario.nombre, email: usuario.email, role: mapRol(usuario.rol) },
          token: token.accessToken,
        })),
      );
  }

  private restoreSession(): AuthSession | null {
    const raw = localStorage.getItem(SESSION_KEY);
    return raw ? (JSON.parse(raw) as AuthSession) : null;
  }
}
