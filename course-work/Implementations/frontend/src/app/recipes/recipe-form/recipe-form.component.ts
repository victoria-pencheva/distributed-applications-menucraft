import { Component, OnInit, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { NgFor, NgIf, DecimalPipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { forkJoin, map, of, switchMap, Observable } from 'rxjs';
import { Category } from '../../core/models/category.model';
import { Ingredient } from '../../core/models/ingredient.model';
import { RecipeService } from '../../core/services/recipe.service';
import { CategoryService } from '../../core/services/category.service';
import { IngredientService } from '../../core/services/ingredient.service';
import { UploadService } from '../../core/services/upload.service';
import { AuthService } from '../../core/services/auth.service';

interface DraftIngredient {
  ingredientId: number;
  ingredientName: string;
  quantity: number;
  unit: string;
  caloriesPer100g: number;
}

interface DraftStep {
  id?: number;
  description: string;
}

@Component({
  selector: 'app-recipe-form',
  standalone: true,
  imports: [NgIf, NgFor, RouterLink, ReactiveFormsModule, FormsModule, DecimalPipe],
  templateUrl: './recipe-form.component.html'
})
export class RecipeFormComponent implements OnInit {
  private fb = inject(FormBuilder);
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private recipeService = inject(RecipeService);
  private categoryService = inject(CategoryService);
  private ingredientService = inject(IngredientService);
  private uploadService = inject(UploadService);
  private authService = inject(AuthService);

  form = this.fb.group({
    name: ['', [Validators.required, Validators.maxLength(100)]],
    blurb: ['', Validators.maxLength(180)],
    description: [''],
    prepTime: [0, [Validators.required, Validators.min(0)]],
    cookTime: [0, [Validators.required, Validators.min(0)]],
    servings: [4, [Validators.required, Validators.min(1)]],
    difficulty: ['EASY', Validators.required],
    imageUrl: [''],
    categoryId: [null as number | null]
  });

  categories: Category[] = [];
  allIngredients: Ingredient[] = [];
  editId: number | null = null;
  error = '';
  uploading = false;
  previewUrl: string | null = null;
  pendingImageFile: File | null = null;

  ingredientSearch = '';
  filteredIngredients: Ingredient[] = [];
  selectedIngredient: Ingredient | null = null;
  ingredientQty: number = 100;

  draftIngredients: DraftIngredient[] = [];
  draftSteps: DraftStep[] = [];

  get totalTime(): number {
    return +(this.form.get('prepTime')?.value ?? 0) + +(this.form.get('cookTime')?.value ?? 0);
  }

  get previewCalories(): number {
    const total = this.draftIngredients.reduce((s, d) => {
      let cal: number;
      if (d.unit === 'kg') cal = d.caloriesPer100g * d.quantity * 10;
      else if (d.unit === 'pcs') cal = d.caloriesPer100g * d.quantity;
      else cal = (d.caloriesPer100g * d.quantity) / 100;
      return s + cal;
    }, 0);
    const srv = +(this.form.get('servings')?.value ?? 1);
    return Math.round(srv > 0 ? total / srv : total);
  }

  get categoryName(): string {
    const id = this.form.get('categoryId')?.value;
    return this.categories.find((c) => c.id === id)?.name ?? '';
  }

  ngOnInit(): void {
    forkJoin({
      cats: this.categoryService.getAll().pipe(map((p) => p.content)),
      ings: this.ingredientService.getAll(0, 500).pipe(map((p) => p.content))
    }).subscribe(({ cats, ings }) => {
      this.categories = cats;
      this.allIngredients = ings;
      const id = this.route.snapshot.paramMap.get('id');
      if (id) {
        this.editId = Number(id);
        this.recipeService.getById(this.editId).subscribe((r) => {
          const me = this.authService.currentUser;
          if (me?.username !== r.username && !this.authService.isAdmin()) {
            this.router.navigate(['/recipes', this.editId]);
            return;
          }
          this.form.patchValue({
            name: r.name,
            blurb: r.blurb ?? '',
            description: r.description,
            prepTime: r.prepTime,
            cookTime: r.cookTime,
            servings: r.servings,
            difficulty: r.difficulty,
            imageUrl: r.imageUrl,
            categoryId: r.categoryId
          });
          this.draftIngredients = r.ingredients.map((i) => ({
            ingredientId: i.ingredientId,
            ingredientName: i.ingredientName,
            quantity: i.quantity,
            unit: i.unit,
            caloriesPer100g: this.allIngredients.find((a) => a.id === i.ingredientId)?.calories ?? 0
          }));
          const seen = new Set<string>();
          this.draftSteps = r.steps
            .filter((s) => !seen.has(s.description) && !!seen.add(s.description))
            .map((s) => ({ description: s.description }));
        });
      }
    });
  }

  setDifficulty(d: string): void {
    this.form.patchValue({ difficulty: d });
  }

  searchIngredients(): void {
    const q = this.ingredientSearch.toLowerCase().trim();
    this.selectedIngredient = null;
    this.filteredIngredients =
      q.length >= 1
        ? this.allIngredients.filter((i) => i.name.toLowerCase().includes(q)).slice(0, 8)
        : [];
  }

  selectIngredient(ing: Ingredient): void {
    this.selectedIngredient = ing;
    this.ingredientSearch = ing.name;
    this.filteredIngredients = [];
  }

  addIngredient(): void {
    if (!this.selectedIngredient || this.ingredientQty <= 0) return;

    const idx = this.draftIngredients.findIndex(
      (d) => d.ingredientId === this.selectedIngredient!.id
    );
    const entry: DraftIngredient = {
      ingredientId: this.selectedIngredient.id,
      ingredientName: this.selectedIngredient.name,
      quantity: this.ingredientQty,
      unit: this.selectedIngredient.defaultUnit,
      caloriesPer100g: this.selectedIngredient.calories ?? 0
    };
    if (idx >= 0) this.draftIngredients[idx] = entry;
    else this.draftIngredients.push(entry);
    this.selectedIngredient = null;
    this.ingredientSearch = '';
    this.ingredientQty = 100;
  }

  removeIngredient(i: number): void {
    this.draftIngredients.splice(i, 1);
  }

  addStep(): void {
    this.draftSteps.push({ description: '' });
  }
  removeStep(i: number): void {
    this.draftSteps.splice(i, 1);
  }

  clearImage(): void {
    this.previewUrl = null;
    this.pendingImageFile = null;
    this.form.patchValue({ imageUrl: '' });
  }

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (!input.files?.length) return;
    const file = input.files[0];
    this.pendingImageFile = file;
    const reader = new FileReader();
    reader.onload = () => (this.previewUrl = reader.result as string);
    reader.readAsDataURL(file);
  }

  submit(): void {
    if (this.form.invalid) return;
    this.uploading = true;
    this.error = '';

    const getImageUrl$: Observable<string> = this.pendingImageFile
      ? this.uploadService.uploadImage(this.pendingImageFile).pipe(map((res) => res.url))
      : of(this.form.value.imageUrl ?? '');

    getImageUrl$
      .pipe(
        switchMap((imageUrl) => {
          this.form.patchValue({ imageUrl });
          this.pendingImageFile = null;
          const v = this.form.value;
          const ingredients = this.draftIngredients.map((d) => ({
            ingredientId: d.ingredientId,
            quantity: d.quantity,
            unit: d.unit
          }));
          const steps = this.draftSteps
            .filter((s) => s.description.trim())
            .map((s, i) => ({ stepNumber: i + 1, description: s.description }));

          if (this.editId) {
            return this.recipeService.updateFull(this.editId, {
              name: v.name,
              description: v.description,
              blurb: v.blurb || null,
              prepTime: v.prepTime,
              cookTime: v.cookTime,
              servings: v.servings,
              difficulty: v.difficulty,
              imageUrl,
              categoryId: v.categoryId,
              ingredients,
              steps
            });
          } else {
            const req: any = {
              name: v.name,
              description: v.description,
              blurb: v.blurb || null,
              prepTime: v.prepTime,
              cookTime: v.cookTime,
              servings: v.servings,
              difficulty: v.difficulty,
              imageUrl,
              categoryId: v.categoryId
            };
            return this.recipeService.create(req).pipe(
              switchMap((recipe) =>
                ingredients.length === 0 && steps.length === 0
                  ? of(recipe)
                  : this.recipeService.updateFull(recipe.id, {
                      ...req,
                      imageUrl,
                      ingredients,
                      steps
                    })
              )
            );
          }
        })
      )
      .subscribe({
        next: (r: any) => {
          this.uploading = false;
          this.router.navigate(['/recipes', r.id]);
        },
        error: (e: any) => {
          this.uploading = false;
          this.error = e.error?.error || 'Save failed';
        }
      });
  }
}
