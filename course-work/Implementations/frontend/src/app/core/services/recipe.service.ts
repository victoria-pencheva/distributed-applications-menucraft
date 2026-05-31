import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Recipe, RecipeRequest, RecipeSearchParams } from '../models/recipe.model';
import { Page } from '../models/page.model';
import { environment } from '../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class RecipeService {
  private base = `${environment.apiUrl}/api/recipes`;
  constructor(private http: HttpClient) {}

  getAll(page = 0, size = 10): Observable<Page<Recipe>> {
    return this.http.get<Page<Recipe>>(`${this.base}?page=${page}&size=${size}`);
  }

  getById(id: number): Observable<Recipe> {
    return this.http.get<Recipe>(`${this.base}/${id}`);
  }

  search(params: RecipeSearchParams): Observable<Page<Recipe>> {
    let p = new HttpParams();
    Object.entries(params).forEach(([k, v]) => {
      if (v == null) return;
      if (k === 'sort') {
        p = p.set('sort', v as string);
      } else if (k === 'tags' && Array.isArray(v)) {
        (v as string[]).forEach((t) => (p = p.append('tag', t)));
      } else if (Array.isArray(v)) {
        (v as (string | number)[]).forEach((item) => (p = p.append(k, String(item))));
      } else {
        p = p.set(k, String(v));
      }
    });
    return this.http.get<Page<Recipe>>(`${this.base}/search`, { params: p });
  }

  create(req: RecipeRequest): Observable<Recipe> {
    return this.http.post<Recipe>(this.base, req);
  }
  update(id: number, req: RecipeRequest): Observable<Recipe> {
    return this.http.put<Recipe>(`${this.base}/${id}`, req);
  }
  updateFull(id: number, req: any): Observable<Recipe> {
    return this.http.put<Recipe>(`${this.base}/${id}/full`, req);
  }
  delete(id: number, force = false): Observable<void> {
    const url = force ? `${this.base}/${id}?force=true` : `${this.base}/${id}`;
    return this.http.delete<void>(url);
  }

  addIngredient(
    recipeId: number,
    req: { ingredientId: number; quantity: number; unit: string }
  ): Observable<any> {
    return this.http.post(`${this.base}/${recipeId}/ingredients`, req);
  }

  removeIngredient(recipeId: number, ingredientId: number): Observable<void> {
    return this.http.delete<void>(`${this.base}/${recipeId}/ingredients/${ingredientId}`);
  }

  addStep(recipeId: number, req: { stepNumber: number; description: string }): Observable<any> {
    return this.http.post(`${this.base}/${recipeId}/steps`, req);
  }

  removeAllSteps(recipeId: number): Observable<void> {
    return this.http.delete<void>(`${this.base}/${recipeId}/steps`);
  }

  removeStep(recipeId: number, stepId: number): Observable<void> {
    return this.http.delete<void>(`${this.base}/${recipeId}/steps/${stepId}`);
  }
}
