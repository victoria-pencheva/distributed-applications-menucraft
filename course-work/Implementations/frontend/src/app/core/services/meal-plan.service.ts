import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { MealPlanEntry } from '../models/meal-plan.model';
import { environment } from '../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class MealPlanService {
  private http = inject(HttpClient);
  private base = `${environment.apiUrl}/api/meal-plan`;

  getWeek(weekStart: string): Observable<MealPlanEntry[]> {
    return this.http.get<MealPlanEntry[]>(this.base, {
      params: new HttpParams().set('weekStart', weekStart)
    });
  }

  add(recipeId: number, date: string, mealType: string, servings = 1): Observable<MealPlanEntry> {
    return this.http.post<MealPlanEntry>(this.base, { recipeId, date, mealType, servings });
  }

  updateServings(entryId: number, servings: number): Observable<MealPlanEntry> {
    return this.http.patch<MealPlanEntry>(`${this.base}/${entryId}/servings`, { servings });
  }

  delete(entryId: number): Observable<void> {
    return this.http.delete<void>(`${this.base}/${entryId}`);
  }
}
