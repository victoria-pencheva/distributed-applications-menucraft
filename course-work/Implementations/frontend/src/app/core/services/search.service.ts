import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class SearchService {
  private q$ = new BehaviorSubject<string>('');
  readonly query$ = this.q$.asObservable();

  set(q: string): void {
    this.q$.next(q);
  }
  clear(): void {
    this.q$.next('');
  }
  get snapshot(): string {
    return this.q$.value;
  }
}
