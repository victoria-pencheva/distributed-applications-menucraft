import { Component, OnInit, inject } from '@angular/core';
import { NgFor, NgIf, DecimalPipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { PantryItem } from '../core/models/pantry.model';
import { Ingredient } from '../core/models/ingredient.model';
import { Recipe, RecipeIngredient } from '../core/models/recipe.model';
import { PantryService } from '../core/services/pantry.service';
import { IngredientService } from '../core/services/ingredient.service';
import { RecipeService } from '../core/services/recipe.service';

export interface RecipeWithCoverage {
  recipe: Recipe;
  pantryCount: number;
  total: number;
  pct: number;
  missingIngredients: RecipeIngredient[];
  missingCount: number;
}

export interface CategoryGroup {
  label: string;
  categories: string[];
}

@Component({
  selector: 'app-pantry',
  standalone: true,
  imports: [NgFor, NgIf, FormsModule, RouterLink, DecimalPipe],
  templateUrl: './pantry.component.html'
})
export class PantryComponent implements OnInit {
  private pantryService = inject(PantryService);
  private ingredientService = inject(IngredientService);
  private recipeService = inject(RecipeService);

  items: PantryItem[] = [];
  allIngredients: Ingredient[] = [];
  recipesWithCoverage: RecipeWithCoverage[] = [];
  loading = false;

  ingredientSearch = '';
  selectedIngredientId: number | null = null;
  addQty = 1;
  addUnit = '';
  showDrop = false;

  readonly GROUPS: CategoryGroup[] = [
    { label: 'Produce', categories: ['PRODUCE'] },
    { label: 'Protein', categories: ['MEAT', 'POULTRY', 'SEAFOOD'] },
    { label: 'Dairy & Eggs', categories: ['DAIRY', 'EGGS'] },
    { label: 'Grains', categories: ['GRAINS'] },
    { label: 'Legumes', categories: ['LEGUMES'] },
    { label: 'Spices & Oils', categories: ['SPICES', 'OILS'] },
    { label: 'Other', categories: ['OTHER'] }
  ];

  collapsed: Record<string, boolean> = {};
  catSearch: Record<string, string> = {};
  catShowDrop: Record<string, boolean> = {};

  incrementingId: number | null = null;
  incrQty: number | null = null;
  incrUnit = '';

  private recipesLoadedOnce = false;

  ngOnInit(): void {
    this.GROUPS.forEach((g) => {
      this.collapsed[g.label] = false;
      this.catSearch[g.label] = '';
      this.catShowDrop[g.label] = false;
    });
    this.ingredientService.getAll(0, 200).subscribe((p) => {
      this.allIngredients = p.content;
    });
    this.loadPantry();
  }

  get pantryIds(): Set<number> {
    return new Set(this.items.map((i) => i.ingredientId));
  }

  get filteredIngredients(): Ingredient[] {
    const q = this.ingredientSearch.toLowerCase();
    return this.allIngredients
      .filter((i) => !this.pantryIds.has(i.id) && (!q || i.name.toLowerCase().includes(q)))
      .slice(0, 8);
  }

  private resolvedCat(ing: Ingredient | undefined): string {
    return (ing?.category ?? 'OTHER').toUpperCase();
  }

  groupItems(g: CategoryGroup): PantryItem[] {
    return this.items.filter((item) => {
      const ing = this.allIngredients.find((i) => i.id === item.ingredientId);
      return g.categories.includes(this.resolvedCat(ing));
    });
  }

  catFilteredIngredients(g: CategoryGroup): Ingredient[] {
    const q = (this.catSearch[g.label] ?? '').toLowerCase();
    if (!q) return [];
    return this.allIngredients
      .filter((i) => {
        if (this.pantryIds.has(i.id)) return false;
        if (!g.categories.includes(this.resolvedCat(i))) return false;
        return i.name.toLowerCase().includes(q);
      })
      .slice(0, 6);
  }

  toggleGroup(g: CategoryGroup): void {
    this.collapsed[g.label] = !this.collapsed[g.label];
  }

  catQuickAdd(ing: Ingredient, g: CategoryGroup): void {
    this.pantryService.upsert(ing.id, 1, ing.defaultUnit || 'g').subscribe(() => {
      this.catSearch[g.label] = '';
      this.catShowDrop[g.label] = false;
      this.loadPantry();
    });
  }

  loadPantry(): void {
    this.loading = true;
    this.pantryService.getAll().subscribe({
      next: (items) => {
        this.items = items;
        this.loading = false;
        if (!this.recipesLoadedOnce) {
          this.recipesLoadedOnce = true;
          this.loadRecipes();
        }
      },
      error: () => {
        this.loading = false;
      }
    });
  }

  private loadRecipes(): void {
    const ids = new Set(this.items.map((i) => i.ingredientId));
    this.recipeService.search({ page: 0, size: 50 }).subscribe((page) => {
      this.recipesWithCoverage = page.content
        .filter((r) => r.ingredients?.length > 0)
        .map((r) => {
          const inPantry = r.ingredients.filter((i) => ids.has(i.ingredientId));
          const missing = r.ingredients.filter((i) => !ids.has(i.ingredientId));
          return {
            recipe: r,
            pantryCount: inPantry.length,
            total: r.ingredients.length,
            pct: Math.round((inPantry.length / r.ingredients.length) * 100),
            missingIngredients: missing,
            missingCount: missing.length
          };
        })
        .sort((a, b) => a.missingCount - b.missingCount);
    });
  }

  selectIngredient(ing: Ingredient): void {
    this.showDrop = false;
    this.pantryService.upsert(ing.id, 1, ing.defaultUnit || 'g').subscribe(() => {
      this.ingredientSearch = '';
      this.selectedIngredientId = null;
      this.loadPantry();
    });
  }

  startIncrement(item: PantryItem): void {
    this.incrementingId = item.id;
    this.incrQty = null;
    this.incrUnit = item.unit || 'g';
  }

  cancelIncrement(): void {
    this.incrementingId = null;
    this.incrQty = null;
    this.incrUnit = '';
  }

  confirmIncrement(item: PantryItem): void {
    if (!this.incrQty || this.incrQty <= 0) {
      this.cancelIncrement();
      return;
    }
    this.pantryService.increment(item.id, this.incrQty, this.incrUnit).subscribe(() => {
      this.cancelIncrement();
      this.loadPantry();
    });
  }

  clearAll(): void {
    if (!confirm('Clear entire pantry?')) return;
    this.pantryService.clearAll().subscribe(() => this.loadPantry());
  }

  remove(item: PantryItem): void {
    this.pantryService.delete(item.id).subscribe(() => this.loadPantry());
  }

  get readyNow(): RecipeWithCoverage[] {
    return this.recipesWithCoverage.filter((r) => r.missingCount === 0);
  }

  get missingFew(): RecipeWithCoverage[] {
    return this.recipesWithCoverage.filter(
      (r) => r.pantryCount > 0 && r.missingCount >= 1 && r.missingCount <= 2
    );
  }

  get stretch(): RecipeWithCoverage[] {
    return this.recipesWithCoverage.filter(
      (r) => r.pantryCount > 0 && r.missingCount >= 3 && r.missingCount <= 5
    );
  }
}
