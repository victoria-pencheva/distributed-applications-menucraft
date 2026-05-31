import { Component, Input, Output, EventEmitter, HostListener, inject } from '@angular/core';
import { NgIf, NgFor, DecimalPipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { Recipe, RecipeStep } from '../../core/models/recipe.model';
import { MealPlanPickerComponent } from '../../shared/meal-plan-picker/meal-plan-picker.component';
import { RecipeService } from '../../core/services/recipe.service';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-recipe-preview',
  standalone: true,
  imports: [NgIf, NgFor, DecimalPipe, RouterLink, MealPlanPickerComponent],
  templateUrl: './recipe-preview.component.html'
})
export class RecipePreviewComponent {
  private recipeService = inject(RecipeService);
  private auth = inject(AuthService);

  @Input() recipe: Recipe | null = null;
  @Input() includedIngredientIds: number[] = [];
  @Input() excludedIngredientIds: number[] = [];
  @Output() close = new EventEmitter<void>();
  @Output() openFull = new EventEmitter<Recipe>();
  @Output() deleted = new EventEmitter<void>();

  showPicker = false;
  planAdded = false;
  deleteError = '';

  get canEdit(): boolean {
    const user = this.auth.currentUser;
    if (!user || !this.recipe) return false;
    return user.role === 'ADMIN' || user.username === this.recipe.username;
  }

  get canDelete(): boolean {
    return this.canEdit;
  }

  deleteRecipe(): void {
    if (!this.recipe || !confirm(`Delete "${this.recipe.name}"?`)) return;
    this.deleteError = '';
    this.recipeService.delete(this.recipe.id).subscribe({
      next: () => {
        this.close.emit();
        this.deleted.emit();
      },
      error: (e: any) => {
        this.deleteError = e?.error?.message || e?.error?.error || `Delete failed (${e?.status})`;
      }
    });
  }

  @HostListener('document:keydown.escape')
  onEsc(): void {
    if (this.showPicker) {
      this.showPicker = false;
      return;
    }
    if (this.recipe) this.close.emit();
  }

  get totalTime(): number {
    return (this.recipe?.prepTime ?? 0) + (this.recipe?.cookTime ?? 0);
  }

  get visibleSteps(): RecipeStep[] {
    return this.recipe?.steps?.slice(0, 3) ?? [];
  }

  get hiddenStepCount(): number {
    return Math.max(0, (this.recipe?.steps?.length ?? 0) - 3);
  }

  get pantryCount(): number {
    return (
      this.recipe?.ingredients?.filter((i) => this.includedIngredientIds.includes(i.ingredientId))
        .length ?? 0
    );
  }

  get pantryPct(): number {
    const total = this.recipe?.ingredients?.length ?? 0;
    return total > 0 ? Math.round((this.pantryCount / total) * 100) : 0;
  }

  isInPantry(ingredientId: number): boolean {
    return this.includedIngredientIds.includes(ingredientId);
  }

  placeholderColor(): string {
    if (!this.recipe) return '#8B4A2B';
    const colors = ['#5C3D2E', '#8B4A2B', '#6B5744', '#4A3728'];
    let hash = 0;
    for (const ch of this.recipe.name) hash = (hash * 31 + ch.charCodeAt(0)) & 0xffff;
    return colors[hash % colors.length];
  }

  onPickerAdded(): void {
    this.showPicker = false;
    this.planAdded = true;
  }
}
