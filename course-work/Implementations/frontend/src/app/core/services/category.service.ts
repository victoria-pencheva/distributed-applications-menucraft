import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Category, CategoryRequest } from '../models/category.model';
import { Page } from '../models/page.model';
import { environment } from '../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class CategoryService {
  private base = `${environment.apiUrl}/api/categories`;
  constructor(private http: HttpClient) {}

  getAll(page = 0, size = 100): Observable<Page<Category>> {
    return this.http.get<Page<Category>>(`${this.base}?page=${page}&size=${size}`);
  }
  getById(id: number): Observable<Category> {
    return this.http.get<Category>(`${this.base}/${id}`);
  }
  create(req: CategoryRequest): Observable<Category> {
    return this.http.post<Category>(this.base, req);
  }
  update(id: number, req: CategoryRequest): Observable<Category> {
    return this.http.put<Category>(`${this.base}/${id}`, req);
  }
  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.base}/${id}`);
  }
}
