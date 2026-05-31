import { Component, OnInit, inject } from '@angular/core';
import { NgIf, NgFor } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { forkJoin } from 'rxjs';
import { Ingredient } from '../../core/models/ingredient.model';
import { IngredientService } from '../../core/services/ingredient.service';
import { PantryService } from '../../core/services/pantry.service';
import { AuthService } from '../../core/services/auth.service';
import { PaginationComponent } from '../../shared/pagination/pagination.component';

const CATEGORY_COLORS: Record<string, string> = {
  PRODUCE: '#6B7D4A',
  LEGUMES: '#4A6B3D',
  MEAT: '#8B3D3D',
  POULTRY: '#C4894A',
  SEAFOOD: '#4A7D8B',
  DAIRY: '#B8A86E',
  EGGS: '#D4924A',
  GRAINS: '#B8974A',
  SPICES: '#7D4A2E',
  OILS: '#C4A84A',
  OTHER: '#8B8B7A'
};

@Component({
  selector: 'app-ingredient-list',
  standalone: true,
  imports: [NgIf, NgFor, FormsModule, RouterLink, PaginationComponent],
  templateUrl: './ingredient-list.component.html'
})
export class IngredientListComponent implements OnInit {
  private ingredientService = inject(IngredientService);
  private pantryService = inject(PantryService);
  private auth = inject(AuthService);

  allIngredients: Ingredient[] = [];
  pantryIds = new Set<number>();
  isAdmin = this.auth.isAdmin();

  search = '';
  activeCategory = 'ALL';
  sort: 'az' | 'calories' | 'pantry' = 'az';

  currentPage = 0;
  readonly PAGE_SIZE = 50;

  ngOnInit(): void {
    forkJoin({
      ings: this.ingredientService.getAll(0, 500),
      pantry: this.pantryService.getAll()
    }).subscribe(({ ings, pantry }) => {
      this.allIngredients = ings.content;
      this.pantryIds = new Set(pantry.map((p) => p.ingredientId));
    });
  }

  get categories(): { key: string; label: string; count: number }[] {
    const map = new Map<string, number>();
    for (const i of this.allIngredients) {
      const k = i.category ?? 'OTHER';
      map.set(k, (map.get(k) ?? 0) + 1);
    }
    return [...map.entries()]
      .sort((a, b) => b[1] - a[1])
      .map(([key, count]) => ({ key, label: this.catLabel(key), count }));
  }

  get filtered(): Ingredient[] {
    let list = this.allIngredients;
    const q = this.search.toLowerCase().trim();
    if (q) list = list.filter((i) => i.name.toLowerCase().includes(q));
    if (this.activeCategory !== 'ALL')
      list = list.filter((i) => (i.category ?? 'OTHER') === this.activeCategory);
    if (this.sort === 'az') list = [...list].sort((a, b) => a.name.localeCompare(b.name));
    else if (this.sort === 'calories')
      list = [...list].sort((a, b) => (b.calories ?? 0) - (a.calories ?? 0));
    else if (this.sort === 'pantry')
      list = [...list].sort(
        (a, b) => (this.pantryIds.has(b.id) ? 1 : 0) - (this.pantryIds.has(a.id) ? 1 : 0)
      );
    return list;
  }

  get paginated(): Ingredient[] {
    const start = this.currentPage * this.PAGE_SIZE;
    return this.filtered.slice(start, start + this.PAGE_SIZE);
  }

  get totalPages(): number {
    return Math.ceil(this.filtered.length / this.PAGE_SIZE);
  }

  onSearchChange(): void {
    this.currentPage = 0;
  }
  setCategory(key: string): void {
    this.activeCategory = key;
    this.currentPage = 0;
  }
  setSort(s: 'az' | 'calories' | 'pantry'): void {
    this.sort = s;
    this.currentPage = 0;
  }
  load(page: number): void {
    this.currentPage = page;
  }

  catColor(category?: string): string {
    return CATEGORY_COLORS[category ?? 'OTHER'] ?? '#8B8B7A';
  }

  catLabel(key: string): string {
    const map: Record<string, string> = {
      PRODUCE: 'Produce',
      LEGUMES: 'Legumes',
      MEAT: 'Meat',
      POULTRY: 'Poultry',
      SEAFOOD: 'Seafood',
      DAIRY: 'Dairy',
      EGGS: 'Eggs',
      GRAINS: 'Grains',
      SPICES: 'Spices',
      OILS: 'Oils',
      OTHER: 'Other'
    };
    return map[key] ?? key;
  }

  delete(id: number): void {
    if (confirm('Delete ingredient?')) {
      this.ingredientService.delete(id).subscribe(() => {
        this.allIngredients = this.allIngredients.filter((i) => i.id !== id);
      });
    }
  }

  macroTotal(i: Ingredient): number {
    return (i.protein ?? 0) * 4 + (i.carbs ?? 0) * 4 + (i.fat ?? 0) * 9;
  }

  macroWidth(i: Ingredient, macro: 'protein' | 'carbs' | 'fat'): number {
    const total = this.macroTotal(i);
    if (total === 0) return 0;
    const kcal =
      macro === 'fat'
        ? (i.fat ?? 0) * 9
        : macro === 'protein'
          ? (i.protein ?? 0) * 4
          : (i.carbs ?? 0) * 4;
    return Math.round((kcal / total) * 100);
  }
}
