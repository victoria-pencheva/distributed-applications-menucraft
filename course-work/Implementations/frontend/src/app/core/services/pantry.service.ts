import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { PantryItem } from '../models/pantry.model';
import { environment } from '../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class PantryService {
  private http = inject(HttpClient);
  private base = `${environment.apiUrl}/api/pantry`;

  private pantryCountSubject = new BehaviorSubject<number>(0);
  pantryCount$ = this.pantryCountSubject.asObservable();

  getAll(): Observable<PantryItem[]> {
    return this.http
      .get<PantryItem[]>(this.base)
      .pipe(tap((items) => this.pantryCountSubject.next(items.length)));
  }

  resetCount(): void {
    this.pantryCountSubject.next(0);
  }

  upsert(ingredientId: number, quantity: number, unit: string): Observable<PantryItem> {
    return this.http.post<PantryItem>(this.base, { ingredientId, quantity, unit });
  }

  increment(itemId: number, quantity: number, unit: string): Observable<PantryItem> {
    return this.http.patch<PantryItem>(`${this.base}/${itemId}`, { quantity, unit });
  }

  clearAll(): Observable<void> {
    return this.http.delete<void>(this.base);
  }

  delete(itemId: number): Observable<void> {
    return this.http.delete<void>(`${this.base}/${itemId}`);
  }
}
