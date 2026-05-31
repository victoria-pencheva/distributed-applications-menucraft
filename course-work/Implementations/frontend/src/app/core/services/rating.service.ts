import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class RatingService {
  private http = inject(HttpClient);
  private base = `${environment.apiUrl}/api/recipes`;

  rate(recipeId: number, stars: number): Observable<void> {
    return this.http.post<void>(`${this.base}/${recipeId}/ratings`, { stars });
  }
}
