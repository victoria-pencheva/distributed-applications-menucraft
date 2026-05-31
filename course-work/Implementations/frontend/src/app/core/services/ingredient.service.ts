import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Ingredient, IngredientRequest } from '../models/ingredient.model';
import { Page } from '../models/page.model';
import { environment } from '../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class IngredientService {
  private base = `${environment.apiUrl}/api/ingredients`;
  constructor(private http: HttpClient) {}

  getAll(page = 0, size = 100): Observable<Page<Ingredient>> {
    return this.http.get<Page<Ingredient>>(`${this.base}?page=${page}&size=${size}`);
  }
  getById(id: number): Observable<Ingredient> {
    return this.http.get<Ingredient>(`${this.base}/${id}`);
  }
  create(req: IngredientRequest): Observable<Ingredient> {
    return this.http.post<Ingredient>(this.base, req);
  }
  update(id: number, req: IngredientRequest): Observable<Ingredient> {
    return this.http.put<Ingredient>(`${this.base}/${id}`, req);
  }
  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.base}/${id}`);
  }
}
