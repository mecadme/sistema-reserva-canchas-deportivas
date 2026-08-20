import { AuthUser, UserRole } from './user.model';

interface MockUserRecord extends AuthUser {
  password: string;
}

const SEED_USERS: MockUserRecord[] = [
  {
    id: 'admin-1',
    nombre: 'Administrador General',
    email: 'admin@canchas.com',
    password: 'Admin123',
    role: 'admin',
  },
  {
    id: 'cliente-1',
    nombre: 'Enzo Aliatis',
    email: 'cliente@canchas.com',
    password: 'Cliente123',
    role: 'cliente',
  },
];

const STORAGE_KEY = 'mock-users';

function readUsers(): MockUserRecord[] {
  const raw = localStorage.getItem(STORAGE_KEY);
  if (!raw) {
    localStorage.setItem(STORAGE_KEY, JSON.stringify(SEED_USERS));
    return SEED_USERS;
  }
  return JSON.parse(raw) as MockUserRecord[];
}

function writeUsers(users: MockUserRecord[]): void {
  localStorage.setItem(STORAGE_KEY, JSON.stringify(users));
}

export function findByCredentials(email: string, password: string): AuthUser | null {
  const match = readUsers().find(
    (u) => u.email.toLowerCase() === email.toLowerCase() && u.password === password,
  );
  return match ? { id: match.id, nombre: match.nombre, email: match.email, role: match.role } : null;
}

export function emailExists(email: string): boolean {
  return readUsers().some((u) => u.email.toLowerCase() === email.toLowerCase());
}

export function registerUser(nombre: string, email: string, password: string, role: UserRole = 'cliente'): AuthUser {
  const users = readUsers();
  const newUser: MockUserRecord = {
    id: `${role}-${Date.now()}`,
    nombre,
    email,
    password,
    role,
  };
  users.push(newUser);
  writeUsers(users);
  return { id: newUser.id, nombre: newUser.nombre, email: newUser.email, role: newUser.role };
}
