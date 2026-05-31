import { Component, OnInit, OnDestroy, inject } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { NgFor, NgIf, AsyncPipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import { debounceTime, map, switchMap, Subscription } from 'rxjs';
import { Recipe } from '../../core/models/recipe.model';
import { Ingredient } from '../../core/models/ingredient.model';
import { RecipeService } from '../../core/services/recipe.service';
import { IngredientService } from '../../core/services/ingredient.service';
import { PantryService } from '../../core/services/pantry.service';
import { SearchService } from '../../core/services/search.service';
import { RecipeCardComponent } from '../recipe-card/recipe-card.component';
import { RecipeSidebarComponent, SidebarFilters } from '../recipe-sidebar/recipe-sidebar.component';
import { RecipePreviewComponent } from '../recipe-preview/recipe-preview.component';
import { PaginationComponent } from '../../shared/pagination/pagination.component';

@Component({
  selector: 'app-recipe-list',
  standalone: true,
  imports: [
    NgFor,
    NgIf,
    AsyncPipe,
    FormsModule,
    RouterLink,
    MatFormFieldModule,
    MatInputModule,
    RecipeCardComponent,
    RecipeSidebarComponent,
    RecipePreviewComponent,
    PaginationComponent
  ],
  templateUrl: './recipe-list.component.html'
})
export class RecipeListComponent implements OnInit, OnDestroy {
  private router = inject(Router);
  private recipeService = inject(RecipeService);
  private ingredientService = inject(IngredientService);
  private pantryService = inject(PantryService);
  private searchService = inject(SearchService);
  isMobile$ = inject(BreakpointObserver)
    .observe([Breakpoints.Handset, Breakpoints.TabletPortrait])
    .pipe(map((r) => r.matches));

  allIngredients: Ingredient[] = [];
  allRecipes: Recipe[] = [];
  pantryIngredientIds: number[] = [];
  totalElements = 0;
  totalPages = 0;
  currentPage = 0;

  view: 'grid' | 'list' = 'grid';
  sort = 'new';
  nameSearch = '';
  showMobileSidebar = false;
  previewRecipe: Recipe | null = null;

  filters: SidebarFilters = {
    includeIngredientIds: [],
    excludeIngredientIds: [],
    tags: [],
    cuisine: null,
    difficulty: null,
    minPrepTime: null,
    maxPrepTime: null,
    maxCalories: null,
    minProtein: null,
    maxCarbs: null,
    maxFat: null
  };

  private searchSub?: Subscription;

  get displayedRecipes(): Recipe[] {
    return this.allRecipes;
  }

  get activeChips(): { label: string; remove: () => void }[] {
    const chips: { label: string; remove: () => void }[] = [];
    this.allIngredients
      .filter((i) => this.filters.includeIngredientIds.includes(i.id))
      .forEach((i) => chips.push({ label: `+ ${i.name}`, remove: () => this.removeInclude(i.id) }));
    this.allIngredients
      .filter((i) => this.filters.excludeIngredientIds.includes(i.id))
      .forEach((i) => chips.push({ label: `− ${i.name}`, remove: () => this.removeExclude(i.id) }));
    this.filters.tags.forEach((t) => chips.push({ label: t, remove: () => this.removeTag(t) }));
    if (this.filters.difficulty)
      chips.push({
        label: this.filters.difficulty,
        remove: () => this.updateFilters({ difficulty: null })
      });
    if (this.filters.minPrepTime != null || this.filters.maxPrepTime != null)
      chips.push({
        label: `${this.filters.minPrepTime ?? 0}–${this.filters.maxPrepTime ?? 240} min`,
        remove: () => this.updateFilters({ minPrepTime: null, maxPrepTime: null })
      });
    return chips;
  }

  ngOnInit(): void {
    this.ingredientService.getAll(0, 200).subscribe((p) => (this.allIngredients = p.content));
    this.pantryService
      .getAll()
      .subscribe((items) => (this.pantryIngredientIds = items.map((i) => i.ingredientId)));
    this.nameSearch = this.searchService.snapshot;
    this.load(0);
    this.searchSub = this.searchService.query$
      .pipe(
        debounceTime(300),
        switchMap((q) => {
          this.nameSearch = q;
          return this.doSearch(0);
        })
      )
      .subscribe({
        next: (p) => this.handlePage(p, 0),
        error: (err) => console.error('[RecipeList] search failed', err)
      });
  }

  ngOnDestroy(): void {
    this.searchSub?.unsubscribe();
  }

  load(page: number): void {
    this.doSearch(page).subscribe({
      next: (p) => this.handlePage(p, page),
      error: (err) => console.error('[RecipeList] search failed', err)
    });
  }

  private doSearch(page: number) {
    return this.recipeService.search({
      name: this.nameSearch || undefined,
      difficulty: this.filters.difficulty || undefined,
      cuisine: this.filters.cuisine || undefined,
      tags: this.filters.tags.length ? this.filters.tags : undefined,
      includeIngredientIds: this.filters.includeIngredientIds.length
        ? this.filters.includeIngredientIds
        : undefined,
      excludeIngredientIds: this.filters.excludeIngredientIds.length
        ? this.filters.excludeIngredientIds
        : undefined,
      minPrepTime: this.filters.minPrepTime ?? undefined,
      maxPrepTime: this.filters.maxPrepTime ?? undefined,
      maxCalories: this.filters.maxCalories ?? undefined,
      minProtein: this.filters.minProtein ?? undefined,
      maxCarbs: this.filters.maxCarbs ?? undefined,
      maxFat: this.filters.maxFat ?? undefined,
      sort: this.sort,
      page,
      size: 24
    });
  }

  private handlePage(p: any, page: number): void {
    this.allRecipes = p.content;
    this.totalPages = p.totalPages;
    this.totalElements = p.totalElements;
    this.currentPage = page;
  }

  updateFilters(patch: Partial<SidebarFilters>): void {
    this.filters = { ...this.filters, ...patch };
    this.load(0);
  }

  onFiltersChange(f: SidebarFilters): void {
    this.filters = f;
    this.load(0);
  }

  openPreview(recipe: Recipe): void {
    this.previewRecipe = recipe;
  }
  closePreview(): void {
    this.previewRecipe = null;
  }
  openFull(recipe: Recipe): void {
    this.router.navigate(['/recipes', recipe.id]);
  }

  private removeInclude(id: number): void {
    this.updateFilters({
      includeIngredientIds: this.filters.includeIngredientIds.filter((x) => x !== id)
    });
  }
  private removeExclude(id: number): void {
    this.updateFilters({
      excludeIngredientIds: this.filters.excludeIngredientIds.filter((x) => x !== id)
    });
  }
  private removeTag(tag: string): void {
    this.updateFilters({ tags: this.filters.tags.filter((t) => t !== tag) });
  }
}
