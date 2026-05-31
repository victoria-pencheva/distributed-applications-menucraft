import { Component, OnInit, DestroyRef, inject } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { NgFor, NgIf, DecimalPipe, UpperCasePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MealPlanEntry, MealType } from '../core/models/meal-plan.model';
import { Recipe } from '../core/models/recipe.model';
import { MealPlanService } from '../core/services/meal-plan.service';
import { RecipeService } from '../core/services/recipe.service';

interface DayRow {
  date: string;
  dayName: string;
  dayLabel: string;
  entries: Record<MealType, MealPlanEntry | null>;
  totalKcal: number;
  totalProtein: number;
  filledCount: number;
}

@Component({
  selector: 'app-meal-plan',
  standalone: true,
  imports: [NgFor, NgIf, FormsModule, DecimalPipe, UpperCasePipe],
  templateUrl: './meal-plan.component.html'
})
export class MealPlanComponent implements OnInit {
  private mealPlanService = inject(MealPlanService);
  private recipeService = inject(RecipeService);
  private destroyRef = inject(DestroyRef);

  readonly mealTypes: MealType[] = ['BREAKFAST', 'LUNCH', 'DINNER'];
  readonly mealLabels: Record<MealType, string> = {
    BREAKFAST: 'Breakfast',
    LUNCH: 'Lunch',
    DINNER: 'Dinner'
  };

  weekStart: Date = this.getMonday(new Date());
  days: string[] = [];
  dayRows: DayRow[] = [];

  libraryRecipes: Recipe[] = [];
  librarySearch = '';
  librarySort = 'new';
  libraryPage = 0;
  libraryTotalPages = 0;
  libraryTotalElements = 0;

  addingDate = '';
  addingMeal: MealType = 'BREAKFAST';
  addingServings = 1;

  dragRecipe: Recipe | null = null;
  dragOverDate = '';
  dragOverMeal: MealType = 'BREAKFAST';

  get weeklyStats() {
    const all = this.dayRows.flatMap(
      (d) => this.mealTypes.map((mt) => d.entries[mt]).filter(Boolean) as MealPlanEntry[]
    );
    const filledCount = all.length;
    const totalKcal = all.reduce((s, e) => s + (e.calories ?? 0), 0);
    const avgKcal = filledCount > 0 ? Math.round(totalKcal / 7) : 0;
    const totalProtein = all.reduce((s, e) => s + (e.protein ?? 0), 0);
    const totalCarbs = all.reduce((s, e) => s + (e.carbs ?? 0), 0);
    const totalFat = all.reduce((s, e) => s + (e.fat ?? 0), 0);
    const totalServings = all.reduce((s, e) => s + e.servings, 0);
    return { filledCount, avgKcal, totalProtein, totalCarbs, totalFat, totalServings };
  }

  ngOnInit(): void {
    this.buildWeek();
    this.load();
    this.loadLibrary();
  }

  private getMonday(d: Date): Date {
    const day = d.getDay();
    const diff = day === 0 ? -6 : 1 - day;
    const mon = new Date(d);
    mon.setDate(d.getDate() + diff);
    mon.setHours(0, 0, 0, 0);
    return mon;
  }

  private toDateStr(d: Date): string {
    const y = d.getFullYear();
    const m = String(d.getMonth() + 1).padStart(2, '0');
    const day = String(d.getDate()).padStart(2, '0');
    return `${y}-${m}-${day}`;
  }

  private buildWeek(): void {
    const dayNames = ['MON', 'TUE', 'WED', 'THU', 'FRI', 'SAT', 'SUN'];
    this.days = [];
    this.dayRows = [];
    for (let i = 0; i < 7; i++) {
      const d = new Date(this.weekStart);
      d.setDate(this.weekStart.getDate() + i);
      const dateStr = this.toDateStr(d);
      this.days.push(dateStr);
      this.dayRows.push({
        date: dateStr,
        dayName: dayNames[i],
        dayLabel: d.toLocaleDateString('en-US', { month: 'short', day: 'numeric' }),
        entries: { BREAKFAST: null, LUNCH: null, DINNER: null },
        totalKcal: 0,
        totalProtein: 0,
        filledCount: 0
      });
    }
  }

  load(): void {
    this.mealPlanService
      .getWeek(this.toDateStr(this.weekStart))
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe((entries) => {
        this.dayRows.forEach((row) => {
          row.entries = { BREAKFAST: null, LUNCH: null, DINNER: null };
          this.mealTypes.forEach((mt) => {
            row.entries[mt] = entries.find((e) => e.date === row.date && e.mealType === mt) ?? null;
          });
          const rowEntries = this.mealTypes
            .map((mt) => row.entries[mt])
            .filter(Boolean) as MealPlanEntry[];
          row.totalKcal = Math.round(rowEntries.reduce((s, e) => s + (e.calories ?? 0), 0));
          row.totalProtein = Math.round(rowEntries.reduce((s, e) => s + (e.protein ?? 0), 0));
          row.filledCount = rowEntries.length;
        });
      });
  }

  loadLibrary(): void {
    this.recipeService
      .search({
        name: this.librarySearch || undefined,
        sort: this.librarySort,
        page: this.libraryPage,
        size: 9
      })
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe((p) => {
        this.libraryRecipes = p.content;
        this.libraryTotalPages = p.totalPages;
        this.libraryTotalElements = p.totalElements;
      });
  }

  librarySearchChanged(): void {
    this.libraryPage = 0;
    this.loadLibrary();
  }

  librarySortChanged(): void {
    this.libraryPage = 0;
    this.loadLibrary();
  }

  libraryPrev(): void {
    if (this.libraryPage > 0) {
      this.libraryPage--;
      this.loadLibrary();
    }
  }

  libraryNext(): void {
    if (this.libraryPage < this.libraryTotalPages - 1) {
      this.libraryPage++;
      this.loadLibrary();
    }
  }

  prevWeek(): void {
    this.weekStart = new Date(this.weekStart);
    this.weekStart.setDate(this.weekStart.getDate() - 7);
    this.buildWeek();
    this.load();
  }

  nextWeek(): void {
    this.weekStart = new Date(this.weekStart);
    this.weekStart.setDate(this.weekStart.getDate() + 7);
    this.buildWeek();
    this.load();
  }

  today(): void {
    this.weekStart = this.getMonday(new Date());
    this.buildWeek();
    this.load();
  }

  get weekLabel(): string {
    const end = new Date(this.weekStart);
    end.setDate(this.weekStart.getDate() + 6);
    const fmt = (d: Date) =>
      d.toLocaleDateString('en-US', { month: 'long', day: 'numeric', year: 'numeric' });
    const start = this.weekStart;
    return `${start.toLocaleDateString('en-US', { month: 'long' })} ${start.getDate()}–${end.getDate()}, ${end.getFullYear()}`;
  }

  startAdding(date: string, meal: MealType): void {
    this.addingDate = date;
    this.addingMeal = meal;
    this.addingServings = 1;
  }

  addFromLibrary(recipe: Recipe): void {
    if (!this.addingDate) return;
    this.mealPlanService
      .add(recipe.id, this.addingDate, this.addingMeal, this.addingServings)
      .subscribe(() => {
        this.addingDate = '';
        this.addingServings = 1;
        this.load();
      });
  }

  onDragStart(e: DragEvent, recipe: Recipe): void {
    this.dragRecipe = recipe;
    e.dataTransfer!.effectAllowed = 'copy';
  }

  onDragOver(e: DragEvent, date: string, mt: MealType): void {
    e.preventDefault();
    e.dataTransfer!.dropEffect = 'copy';
    this.dragOverDate = date;
    this.dragOverMeal = mt;
  }

  onDragLeave(e: DragEvent): void {
    const rel = e.relatedTarget as Element | null;
    if (!rel || !(e.currentTarget as Element).contains(rel)) {
      this.dragOverDate = '';
    }
  }

  onDrop(e: DragEvent, date: string, mt: MealType): void {
    e.preventDefault();
    this.dragOverDate = '';
    if (!this.dragRecipe) return;
    const recipe = this.dragRecipe;
    this.dragRecipe = null;
    this.mealPlanService.add(recipe.id, date, mt, 1).subscribe(() => this.load());
  }

  removeEntry(entry: MealPlanEntry): void {
    this.mealPlanService.delete(entry.id).subscribe(() => this.load());
  }

  adjustServings(entry: MealPlanEntry, delta: number): void {
    const newVal = Math.max(1, entry.servings + delta);
    this.mealPlanService.updateServings(entry.id, newVal).subscribe(() => this.load());
  }

  mealColor(mt: MealType): string {
    return { BREAKFAST: 'var(--accent)', LUNCH: 'var(--amber)', DINNER: 'var(--rust)' }[mt];
  }
}
