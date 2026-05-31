import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, map, tap } from 'rxjs';
import { AuthResponse, LoginRequest, RegisterRequest } from '../models/user.model';
import { environment } from '../../../environments/environment';

export const Role = { ADMIN: 'ADMIN', USER: 'USER' } as const;

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly TOKEN_KEY = 'jwt_token';
  private readonly USER_KEY = 'current_user';
  private currentUserSubject = new BehaviorSubject<AuthResponse | null>(this.storedUser());
  currentUser$ = this.currentUserSubject.asObservable();

  constructor(private http: HttpClient) {}

  register(req: RegisterRequest): Observable<AuthResponse> {
    return this.http
      .post<AuthResponse>(`${environment.apiUrl}/auth/register`, req)
      .pipe(tap((res) => this.store(res)));
  }

  login(req: LoginRequest): Observable<AuthResponse> {
    return this.http
      .post<AuthResponse>(`${environment.apiUrl}/auth/login`, req)
      .pipe(tap((res) => this.store(res)));
  }

  checkUsername(username: string): Observable<boolean> {
    return this.http
      .get<{
        available: boolean;
      }>(`${environment.apiUrl}/auth/check-username`, { params: { username } })
      .pipe(map((r) => r.available));
  }

  checkEmail(email: string): Observable<boolean> {
    return this.http
      .get<{ available: boolean }>(`${environment.apiUrl}/auth/check-email`, { params: { email } })
      .pipe(map((r) => r.available));
  }

  logout(): void {
    localStorage.removeItem(this.TOKEN_KEY);
    localStorage.removeItem(this.USER_KEY);
    this.currentUserSubject.next(null);
  }

  getToken(): string | null {
    return localStorage.getItem(this.TOKEN_KEY);
  }
  isLoggedIn(): boolean {
    return !!this.getToken();
  }
  isAdmin(): boolean {
    return this.currentUserSubject.value?.role === Role.ADMIN;
  }
  get currentUser(): AuthResponse | null {
    return this.currentUserSubject.value;
  }

  private store(res: AuthResponse): void {
    localStorage.setItem(this.TOKEN_KEY, res.token);
    localStorage.setItem(this.USER_KEY, JSON.stringify(res));
    this.currentUserSubject.next(res);
  }

  private storedUser(): AuthResponse | null {
    const s = localStorage.getItem(this.USER_KEY);
    return s ? JSON.parse(s) : null;
  }
}
