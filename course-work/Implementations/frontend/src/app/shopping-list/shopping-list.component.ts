import { Component, OnInit, inject } from '@angular/core';
import { NgFor, NgIf } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ShoppingItem } from '../core/models/shopping.model';
import { Ingredient } from '../core/models/ingredient.model';
import { ShoppingService } from '../core/services/shopping.service';
import { PantryService } from '../core/services/pantry.service';
import { IngredientService } from '../core/services/ingredient.service';

export interface CategoryGroup {
  label: string;
  categories: string[];
}

@Component({
  selector: 'app-shopping-list',
  standalone: true,
  imports: [NgFor, NgIf, FormsModule],
  templateUrl: './shopping-list.component.html'
})
export class ShoppingListComponent implements OnInit {
  private shoppingService = inject(ShoppingService);
  private pantryService = inject(PantryService);
  private ingredientService = inject(IngredientService);

  items: ShoppingItem[] = [];
  pantryIngredientIds: Set<number> = new Set();
  allIngredients: Ingredient[] = [];

  hidePantry = false;
  loading = false;
  generating = false;
  weekStart = this.currentWeekStart();

  manualSearch = '';
  manualShowDrop = false;
  manualSelected: Ingredient | null = null;
  addQty: number | null = null;
  addUnit = '';

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

  private currentWeekStart(): string {
    const d = new Date();
    const day = d.getDay();
    const diff = day === 0 ? -6 : 1 - day;
    d.setDate(d.getDate() + diff);
    const y = d.getFullYear();
    const m = String(d.getMonth() + 1).padStart(2, '0');
    const day2 = String(d.getDate()).padStart(2, '0');
    return `${y}-${m}-${day2}`;
  }

  get manualFiltered(): Ingredient[] {
    const q = this.manualSearch.toLowerCase();
    if (!q) return [];
    return this.allIngredients.filter((i) => i.name.toLowerCase().includes(q)).slice(0, 8);
  }

  ngOnInit(): void {
    this.GROUPS.forEach((g) => (this.collapsed[g.label] = false));
    this.pantryService.getAll().subscribe((items) => {
      this.pantryIngredientIds = new Set(items.map((i) => i.ingredientId));
    });
    this.ingredientService.getAll(0, 200).subscribe((p) => {
      this.allIngredients = p.content;
    });
    this.load();
  }

  selectManualIngredient(ing: Ingredient): void {
    this.manualSelected = ing;
    this.manualSearch = ing.name;
    this.addUnit = ing.defaultUnit || 'g';
    this.addQty = null;
    this.manualShowDrop = false;
  }

  load(): void {
    this.loading = true;
    this.shoppingService.getAll().subscribe({
      next: (items) => {
        this.items = items;
        this.loading = false;
      },
      error: () => {
        this.loading = false;
      }
    });
  }

  get totalCount(): number {
    return this.items.length;
  }
  get checkedCount(): number {
    return this.items.filter((i) => i.checked).length;
  }
  get toBuyCount(): number {
    return this.items.filter((i) => !i.checked).length;
  }
  get pantryCount(): number {
    return this.items.filter((i) => i.ingredientId && this.pantryIngredientIds.has(i.ingredientId))
      .length;
  }
  get checkedPct(): number {
    return this.totalCount ? Math.round((this.checkedCount / this.totalCount) * 100) : 0;
  }

  private get visibleItems(): ShoppingItem[] {
    if (!this.hidePantry) return this.items;
    return this.items.filter(
      (i) => !i.ingredientId || !this.pantryIngredientIds.has(i.ingredientId)
    );
  }

  private resolvedCat(item: ShoppingItem): string {
    return (item.category ?? 'OTHER').toUpperCase();
  }

  groupUnchecked(g: CategoryGroup): ShoppingItem[] {
    return this.visibleItems.filter(
      (i) => !i.checked && g.categories.includes(this.resolvedCat(i))
    );
  }

  groupChecked(g: CategoryGroup): ShoppingItem[] {
    return this.visibleItems.filter((i) => i.checked && g.categories.includes(this.resolvedCat(i)));
  }

  hasAnyItems(g: CategoryGroup): boolean {
    return this.groupUnchecked(g).length > 0 || this.groupChecked(g).length > 0;
  }

  toggleGroup(label: string): void {
    this.collapsed[label] = !this.collapsed[label];
  }

  displayName(item: ShoppingItem): string {
    return item.ingredientName ?? item.name ?? '—';
  }

  toggle(item: ShoppingItem): void {
    this.shoppingService.toggle(item.id).subscribe(() => this.load());
  }

  remove(item: ShoppingItem): void {
    this.shoppingService.delete(item.id).subscribe(() => this.load());
  }

  clearAll(): void {
    if (!confirm('Clear entire shopping list?')) return;
    this.shoppingService.clearAll().subscribe(() => this.load());
  }

  clearChecked(): void {
    this.shoppingService.clearChecked().subscribe(() => this.load());
  }

  addManual(): void {
    if (!this.manualSelected) return;
    this.shoppingService
      .add({
        ingredientId: this.manualSelected.id,
        quantity: this.addQty ?? undefined,
        unit: this.addUnit || this.manualSelected.defaultUnit
      })
      .subscribe(() => {
        this.manualSearch = '';
        this.manualSelected = null;
        this.addQty = null;
        this.addUnit = '';
        this.load();
      });
  }

  generate(): void {
    this.generating = true;
    this.shoppingService.generateFromMealPlan(this.weekStart).subscribe({
      next: () => {
        this.generating = false;
        this.load();
      },
      error: () => {
        this.generating = false;
      }
    });
  }
}
