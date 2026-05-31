import { Component, Input, Output, EventEmitter, OnChanges } from '@angular/core';
import { NgFor, NgIf } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatSliderModule } from '@angular/material/slider';
import { Ingredient } from '../../core/models/ingredient.model';

export interface SidebarFilters {
  includeIngredientIds: number[];
  excludeIngredientIds: number[];
  tags: string[];
  cuisine: string | null;
  difficulty: string | null;
  minPrepTime: number | null;
  maxPrepTime: number | null;
  maxCalories: number | null;
  minProtein: number | null;
  maxCarbs: number | null;
  maxFat: number | null;
}

const DIETARY_TAGS = [
  'Vegetarian',
  'Vegan',
  'Gluten-Free',
  'Dairy-Free',
  'High-Protein',
  'Low-Carb',
  'Low-Calorie'
];
const DIFFICULTIES = ['Easy', 'Medium', 'Hard'];
const CUISINES = [
  'Italian',
  'Asian',
  'American',
  'Mexican',
  'Mediterranean',
  'French',
  'Indian',
  'Japanese'
];

@Component({
  selector: 'app-recipe-sidebar',
  standalone: true,
  imports: [NgFor, NgIf, FormsModule, MatSliderModule],
  templateUrl: './recipe-sidebar.component.html'
})
export class RecipeSidebarComponent implements OnChanges {
  @Input() allIngredients: Ingredient[] = [];
  @Input() filters!: SidebarFilters;
  @Output() filtersChange = new EventEmitter<SidebarFilters>();

  readonly dietaryTags = DIETARY_TAGS;
  readonly difficulties = DIFFICULTIES;
  readonly cuisines = CUISINES;

  includeSearch = '';
  excludeSearch = '';
  minTime = 0;
  maxTime = 240;

  open = {
    include: true,
    exclude: true,
    dietary: true,
    cuisine: true,
    time: true,
    difficulty: true,
    calories: true,
    macros: true
  };

  maxCal = 1500;
  minProt = 0;
  maxCarbs = 150;
  maxFat = 80;

  get visibleInclude(): Ingredient[] {
    const q = this.includeSearch.toLowerCase();
    const list = q
      ? this.allIngredients.filter((i) => i.name.toLowerCase().includes(q))
      : this.allIngredients;
    const selected = list.filter((i) => this.filters.includeIngredientIds.includes(i.id));
    const unselected = list
      .filter((i) => !this.filters.includeIngredientIds.includes(i.id))
      .slice(0, 5);
    return [...selected, ...unselected];
  }

  get visibleExclude(): Ingredient[] {
    const q = this.excludeSearch.toLowerCase();
    const list = q
      ? this.allIngredients.filter((i) => i.name.toLowerCase().includes(q))
      : this.allIngredients;
    const selected = list.filter((i) => this.filters.excludeIngredientIds.includes(i.id));
    const unselected = list
      .filter((i) => !this.filters.excludeIngredientIds.includes(i.id))
      .slice(0, 5);
    return [...selected, ...unselected];
  }

  ngOnChanges(): void {
    this.minTime = this.filters.minPrepTime ?? 0;
    this.maxTime = this.filters.maxPrepTime ?? 240;
    this.maxCal = this.filters.maxCalories ?? 1500;
    this.minProt = this.filters.minProtein ?? 0;
    this.maxCarbs = this.filters.maxCarbs ?? 150;
    this.maxFat = this.filters.maxFat ?? 80;
  }

  onCalChange(): void {
    this.emit({ maxCalories: this.maxCal < 1500 ? this.maxCal : null });
  }
  setCalPreset(max: number): void {
    this.maxCal = max;
    this.onCalChange();
  }
  onProtChange(): void {
    this.emit({ minProtein: this.minProt > 0 ? this.minProt : null });
  }
  onCarbsChange(): void {
    this.emit({ maxCarbs: this.maxCarbs < 150 ? this.maxCarbs : null });
  }
  onFatChange(): void {
    this.emit({ maxFat: this.maxFat < 80 ? this.maxFat : null });
  }

  toggleInclude(ingredient: Ingredient): void {
    const ids = this.filters.includeIngredientIds;
    this.emit({
      includeIngredientIds: ids.includes(ingredient.id)
        ? ids.filter((x) => x !== ingredient.id)
        : [...ids, ingredient.id]
    });
  }

  toggleExclude(ingredient: Ingredient): void {
    const ids = this.filters.excludeIngredientIds;
    this.emit({
      excludeIngredientIds: ids.includes(ingredient.id)
        ? ids.filter((x) => x !== ingredient.id)
        : [...ids, ingredient.id]
    });
  }

  removeInclude(id: number): void {
    this.emit({ includeIngredientIds: this.filters.includeIngredientIds.filter((x) => x !== id) });
  }

  removeExclude(id: number): void {
    this.emit({ excludeIngredientIds: this.filters.excludeIngredientIds.filter((x) => x !== id) });
  }

  toggleTag(tag: string): void {
    const tags = this.filters.tags.includes(tag)
      ? this.filters.tags.filter((t) => t !== tag)
      : [...this.filters.tags, tag];
    this.emit({ tags });
  }

  toggleCuisine(c: string): void {
    this.emit({ cuisine: this.filters.cuisine === c ? null : c });
  }

  toggleDifficulty(d: string): void {
    this.emit({ difficulty: this.filters.difficulty === d ? null : d });
  }

  onTimeChange(): void {
    this.emit({
      minPrepTime: this.minTime > 0 ? this.minTime : null,
      maxPrepTime: this.maxTime < 240 ? this.maxTime : null
    });
  }

  setTimePreset(min: number, max: number): void {
    this.minTime = min;
    this.maxTime = max;
    this.onTimeChange();
  }

  reset(): void {
    this.includeSearch = '';
    this.excludeSearch = '';
    this.minTime = 0;
    this.maxTime = 240;
    this.maxCal = 1500;
    this.minProt = 0;
    this.maxCarbs = 150;
    this.maxFat = 80;
    this.filtersChange.emit({
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
    });
  }

  get activeCount(): number {
    return (
      this.filters.includeIngredientIds.length +
      this.filters.excludeIngredientIds.length +
      this.filters.tags.length +
      (this.filters.cuisine ? 1 : 0) +
      (this.filters.difficulty ? 1 : 0) +
      (this.filters.minPrepTime != null || this.filters.maxPrepTime != null ? 1 : 0) +
      (this.filters.maxCalories != null ? 1 : 0) +
      (this.filters.minProtein != null ? 1 : 0) +
      (this.filters.maxCarbs != null ? 1 : 0) +
      (this.filters.maxFat != null ? 1 : 0)
    );
  }

  private emit(patch: Partial<SidebarFilters>): void {
    this.filtersChange.emit({ ...this.filters, ...patch });
  }
}
