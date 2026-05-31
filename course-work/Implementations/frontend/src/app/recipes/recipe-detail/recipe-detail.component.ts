import { Component, OnInit, inject } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { Location } from '@angular/common';
import { AsyncPipe, DecimalPipe, NgFor, NgIf } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { Recipe } from '../../core/models/recipe.model';
import { Ingredient } from '../../core/models/ingredient.model';
import { RecipeService } from '../../core/services/recipe.service';
import { IngredientService } from '../../core/services/ingredient.service';
import { AuthService } from '../../core/services/auth.service';
import { RatingService } from '../../core/services/rating.service';
import { StarRatingComponent } from '../../shared/star-rating/star-rating.component';

@Component({
  selector: 'app-recipe-detail',
  standalone: true,
  imports: [
    NgIf,
    NgFor,
    AsyncPipe,
    DecimalPipe,
    RouterLink,
    ReactiveFormsModule,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    StarRatingComponent
  ],
  templateUrl: './recipe-detail.component.html'
})
export class RecipeDetailComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private location = inject(Location);
  private fb = inject(FormBuilder);
  private recipeService = inject(RecipeService);
  private ingredientService = inject(IngredientService);
  private auth = inject(AuthService);
  private ratingService = inject(RatingService);

  recipe: Recipe | null = null;
  allIngredients: Ingredient[] = [];
  currentUser$ = this.auth.currentUser$;
  isAdmin = this.auth.isAdmin();
  showAddIngredient = false;
  showAddStep = false;

  displayServings = 1;

  checkedIngredients = new Set<number>();

  addIngredientForm = this.fb.group({
    ingredientId: [null as number | null, Validators.required],
    quantity: [null as number | null, [Validators.required, Validators.min(0.01)]],
    unit: ['', Validators.required]
  });

  addStepForm = this.fb.group({
    description: ['', Validators.required]
  });

  stepLabel(i: number): string {
    return String(i + 1).padStart(2, '0');
  }

  back(): void {
    this.location.back();
  }

  ngOnInit(): void {
    this.load();
    this.ingredientService.getAll(0, 200).subscribe((p) => (this.allIngredients = p.content));
  }

  load(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    this.recipeService.getById(id).subscribe((r) => {
      this.recipe = r;
      this.displayServings = r.servings || 1;
    });
  }

  adjustServings(delta: number): void {
    this.displayServings = Math.max(1, this.displayServings + delta);
  }

  private get servingsRatio(): number {
    if (!this.recipe?.servings || this.recipe.servings === 0) return 1;
    return this.displayServings / this.recipe.servings;
  }

  scaledQty(qty: number): string {
    const val = qty * this.servingsRatio;
    return val % 1 === 0 ? String(val) : val.toFixed(1);
  }

  scaledMacro(m: number | null | undefined): string {
    if (m == null) return '0';
    const val = m * this.servingsRatio;
    return val % 1 === 0 ? String(Math.round(val)) : val.toFixed(1);
  }

  scaledCalories(): number {
    if (!this.recipe?.totalCalories) return 0;
    return Math.round(this.recipe.totalCalories * this.servingsRatio);
  }

  private get totalMacroKcal(): number {
    if (!this.recipe) return 0;
    return (
      (this.recipe.protein ?? 0) * 4 +
      (this.recipe.carbs ?? 0) * 4 +
      (this.recipe.fat ?? 0) * 9 +
      (this.recipe.fiber ?? 0) * 2
    );
  }

  proteinDash(): number {
    const total = this.totalMacroKcal;
    if (total === 0 || !this.recipe?.protein) return 0;
    return ((this.recipe.protein * 4) / total) * 339;
  }

  carbsDash(): number {
    const total = this.totalMacroKcal;
    if (total === 0 || !this.recipe?.carbs) return 0;
    return ((this.recipe.carbs * 4) / total) * 339;
  }

  fatDash(): number {
    const total = this.totalMacroKcal;
    if (total === 0 || !this.recipe?.fat) return 0;
    return ((this.recipe.fat * 9) / total) * 339;
  }

  fiberDash(): number {
    const total = this.totalMacroKcal;
    if (total === 0 || !this.recipe?.fiber) return 0;
    return ((this.recipe.fiber * 2) / total) * 339;
  }

  toggleIngredient(id: number): void {
    if (this.checkedIngredients.has(id)) {
      this.checkedIngredients.delete(id);
    } else {
      this.checkedIngredients.add(id);
    }
  }

  onRate(stars: number): void {
    if (!this.recipe) return;
    this.ratingService.rate(this.recipe.id, stars).subscribe(() => this.load());
  }

  deleteError = '';

  delete(): void {
    if (!this.recipe || !confirm('Delete this recipe?')) return;
    this.deleteError = '';
    const id = this.recipe.id;
    this.recipeService.delete(id).subscribe({
      next: () => this.router.navigate(['/recipes']),
      error: (e: HttpErrorResponse) => {
        if (e.status === 409) {
          this.deleteError = 'This recipe is part of an active meal plan and cannot be deleted.';
        } else {
          this.deleteError = 'Failed to delete recipe.';
        }
      }
    });
  }

  addIngredient(): void {
    if (!this.recipe || this.addIngredientForm.invalid) return;
    const v = this.addIngredientForm.getRawValue();
    this.recipeService
      .addIngredient(this.recipe.id, {
        ingredientId: v.ingredientId!,
        quantity: v.quantity!,
        unit: v.unit!
      })
      .subscribe(() => {
        this.addIngredientForm.reset();
        this.showAddIngredient = false;
        this.load();
      });
  }

  removeIngredient(ingredientId: number): void {
    if (!this.recipe || !confirm('Remove ingredient?')) return;
    this.recipeService.removeIngredient(this.recipe.id, ingredientId).subscribe(() => this.load());
  }

  addStep(): void {
    if (!this.recipe || this.addStepForm.invalid) return;
    const stepNumber = this.recipe.steps.length + 1;
    const description = this.addStepForm.getRawValue().description!;
    this.recipeService.addStep(this.recipe.id, { stepNumber, description }).subscribe(() => {
      this.addStepForm.reset();
      this.showAddStep = false;
      this.load();
    });
  }

  removeStep(stepId: number): void {
    if (!this.recipe || !confirm('Remove step?')) return;
    this.recipeService.removeStep(this.recipe.id, stepId).subscribe(() => this.load());
  }
}
