import { Component, inject, OnInit, OnDestroy } from '@angular/core';
import { Router, RouterLink, RouterLinkActive, NavigationEnd } from '@angular/router';
import { AsyncPipe, NgIf } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatMenuModule } from '@angular/material/menu';
import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import { map, filter, distinctUntilChanged, Subscription } from 'rxjs';
import { AuthService } from '../../core/services/auth.service';
import { SearchService } from '../../core/services/search.service';
import { PantryService } from '../../core/services/pantry.service';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [
    RouterLink,
    RouterLinkActive,
    AsyncPipe,
    NgIf,
    FormsModule,
    MatButtonModule,
    MatMenuModule
  ],
  templateUrl: './navbar.component.html'
})
export class NavbarComponent implements OnInit, OnDestroy {
  private auth = inject(AuthService);
  private router = inject(Router);
  private searchService = inject(SearchService);
  private pantryService = inject(PantryService);
  currentUser$ = this.auth.currentUser$;
  isMobile$ = inject(BreakpointObserver)
    .observe([Breakpoints.Handset, Breakpoints.TabletPortrait])
    .pipe(map((r) => r.matches));

  isRecipesRoute = false;
  searchQuery = '';
  pantryCount = 0;
  private routerSub?: Subscription;

  ngOnInit(): void {
    this.updateRoute(this.router.url);
    this.routerSub = this.router.events
      .pipe(filter((e) => e instanceof NavigationEnd))
      .subscribe((e: any) => {
        this.updateRoute(e.urlAfterRedirects ?? e.url);
      });
    this.pantryService.pantryCount$.subscribe((count) => (this.pantryCount = count));
    this.auth.currentUser$
      .pipe(distinctUntilChanged((prev, curr) => prev?.username === curr?.username))
      .subscribe((user) => {
        if (user) {
          this.pantryService.getAll().subscribe();
        } else {
          this.pantryService.resetCount();
        }
      });
  }

  ngOnDestroy(): void {
    this.routerSub?.unsubscribe();
  }

  private updateRoute(url: string): void {
    const onRecipes = url.startsWith('/recipes');
    if (!onRecipes) {
      this.searchQuery = '';
      this.searchService.clear();
    }
    this.isRecipesRoute = onRecipes;
  }

  onSearch(): void {
    this.searchService.set(this.searchQuery);
  }

  initials(username: string): string {
    return username.slice(0, 2).toUpperCase();
  }

  logout(): void {
    this.auth.logout();
    this.router.navigate(['/login']);
  }
}
