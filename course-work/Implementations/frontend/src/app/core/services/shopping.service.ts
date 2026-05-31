import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ShoppingItem } from '../models/shopping.model';
import { environment } from '../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class ShoppingService {
  private http = inject(HttpClient);
  private base = `${environment.apiUrl}/api/shopping`;

  getAll(): Observable<ShoppingItem[]> {
    return this.http.get<ShoppingItem[]>(this.base);
  }

  add(body: {
    ingredientId?: number;
    name?: string;
    quantity?: number;
    unit?: string;
  }): Observable<ShoppingItem> {
    return this.http.post<ShoppingItem>(this.base, body);
  }

  toggle(itemId: number): Observable<void> {
    return this.http.patch<void>(`${this.base}/${itemId}/toggle`, {});
  }

  delete(itemId: number): Observable<void> {
    return this.http.delete<void>(`${this.base}/${itemId}`);
  }

  clearAll(): Observable<void> {
    return this.http.delete<void>(this.base);
  }

  clearChecked(): Observable<void> {
    return this.http.delete<void>(`${this.base}/checked`);
  }

  generateFromMealPlan(weekStart: string): Observable<ShoppingItem[]> {
    return this.http.post<ShoppingItem[]>(`${this.base}/generate`, null, {
      params: new HttpParams().set('weekStart', weekStart)
    });
  }
}
