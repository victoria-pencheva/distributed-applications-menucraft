import { Component, Input, Output, EventEmitter, OnInit, inject } from '@angular/core';
import { NgIf, NgFor } from '@angular/common';
import { Recipe } from '../../core/models/recipe.model';
import { MealPlanEntry, MealType } from '../../core/models/meal-plan.model';
import { MealPlanService } from '../../core/services/meal-plan.service';
import { of, switchMap } from 'rxjs';

@Component({
  selector: 'app-meal-plan-picker',
  standalone: true,
  imports: [NgIf, NgFor],
  templateUrl: './meal-plan-picker.component.html'
})
export class MealPlanPickerComponent implements OnInit {
  @Input() recipe!: Recipe;
  @Output() close = new EventEmitter<void>();
  @Output() added = new EventEmitter<void>();

  private mealPlanService = inject(MealPlanService);

  readonly mealTypes: MealType[] = ['BREAKFAST', 'LUNCH', 'DINNER'];
  readonly dayNames = ['MON', 'TUE', 'WED', 'THU', 'FRI', 'SAT', 'SUN'];

  weekStart!: Date;
  entries: MealPlanEntry[] = [];
  selectedDate: string | null = null;
  selectedMealType: MealType | null = null;
  servings = 1;
  loading = false;
  error = '';

  ngOnInit(): void {
    this.servings = this.recipe.servings ?? 1;
    this.weekStart = this.getMondayOf(new Date());
    this.loadWeek();
  }

  get days(): { date: Date; dateStr: string; isToday: boolean }[] {
    const today = this.todayStr();
    return Array.from({ length: 7 }, (_, i) => {
      const d = new Date(this.weekStart);
      d.setDate(d.getDate() + i);
      const dateStr = this.toDateStr(d);
      return { date: d, dateStr, isToday: dateStr === today };
    });
  }

  get weekLabel(): string {
    return this.weekStart.toLocaleDateString('en-US', { month: 'short', day: 'numeric' });
  }

  getEntry(dateStr: string, mealType: MealType): MealPlanEntry | null {
    return this.entries.find((e) => e.date === dateStr && e.mealType === mealType) ?? null;
  }

  isSelected(dateStr: string, mealType: MealType): boolean {
    return this.selectedDate === dateStr && this.selectedMealType === mealType;
  }

  selectSlot(dateStr: string, mealType: MealType): void {
    this.selectedDate = dateStr;
    this.selectedMealType = mealType;
  }

  prevWeek(): void {
    this.weekStart = new Date(this.weekStart);
    this.weekStart.setDate(this.weekStart.getDate() - 7);
    this.selectedDate = null;
    this.selectedMealType = null;
    this.loadWeek();
  }

  nextWeek(): void {
    this.weekStart = new Date(this.weekStart);
    this.weekStart.setDate(this.weekStart.getDate() + 7);
    this.selectedDate = null;
    this.selectedMealType = null;
    this.loadWeek();
  }

  decServings(): void {
    if (this.servings > 1) this.servings--;
  }
  incServings(): void {
    this.servings++;
  }

  confirm(): void {
    if (!this.selectedDate || !this.selectedMealType || !this.recipe) return;
    this.error = '';
    const existing = this.getEntry(this.selectedDate, this.selectedMealType);

    const delete$ = existing ? this.mealPlanService.delete(existing.id) : of(void 0);

    delete$
      .pipe(
        switchMap(() =>
          this.mealPlanService.add(
            this.recipe.id,
            this.selectedDate!,
            this.selectedMealType!,
            this.servings
          )
        )
      )
      .subscribe({
        next: () => this.added.emit(),
        error: () => {
          this.error = 'Failed to add. Try again.';
        }
      });
  }

  private loadWeek(): void {
    this.loading = true;
    this.mealPlanService.getWeek(this.toDateStr(this.weekStart)).subscribe({
      next: (entries) => {
        this.entries = entries;
        this.loading = false;
      },
      error: () => {
        this.loading = false;
      }
    });
  }

  private getMondayOf(date: Date): Date {
    const d = new Date(date);
    const day = d.getDay();
    const diff = day === 0 ? -6 : 1 - day;
    d.setDate(d.getDate() + diff);
    d.setHours(0, 0, 0, 0);
    return d;
  }

  private todayStr(): string {
    return this.toDateStr(new Date());
  }

  private toDateStr(d: Date): string {
    const y = d.getFullYear();
    const m = String(d.getMonth() + 1).padStart(2, '0');
    const day = String(d.getDate()).padStart(2, '0');
    return `${y}-${m}-${day}`;
  }
}
