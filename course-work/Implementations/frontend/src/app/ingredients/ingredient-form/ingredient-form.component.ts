import { Component, OnInit, inject } from '@angular/core';
import {
  FormBuilder,
  ReactiveFormsModule,
  Validators,
  AbstractControl,
  ValidationErrors
} from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { NgIf, NgFor } from '@angular/common';
import { IngredientService } from '../../core/services/ingredient.service';

function caloriesMatchMacros(control: AbstractControl): ValidationErrors | null {
  const p = +(control.get('protein')?.value ?? 0);
  const c = +(control.get('carbs')?.value ?? 0);
  const f = +(control.get('fat')?.value ?? 0);
  const fi = +(control.get('fiber')?.value ?? 0);
  const entered = +(control.get('calories')?.value ?? 0);
  if (p === 0 && c === 0 && f === 0 && fi === 0) return null;
  if (entered === 0) return null;
  const calc = p * 4 + c * 4 + f * 9 + fi * 2;
  const diff = Math.abs(entered - calc) / calc;
  return diff > 0.1 ? { calorieMismatch: { calc: Math.round(calc), entered } } : null;
}

@Component({
  selector: 'app-ingredient-form',
  standalone: true,
  imports: [NgIf, NgFor, RouterLink, ReactiveFormsModule],
  templateUrl: './ingredient-form.component.html'
})
export class IngredientFormComponent implements OnInit {
  private fb = inject(FormBuilder);
  private route = inject(ActivatedRoute);
  private ingredientService = inject(IngredientService);
  private router = inject(Router);

  readonly categories = [
    'MEAT',
    'POULTRY',
    'SEAFOOD',
    'DAIRY',
    'EGGS',
    'PRODUCE',
    'LEGUMES',
    'GRAINS',
    'SPICES',
    'OILS',
    'OTHER'
  ];

  editId: number | null = null;

  form = this.fb.group(
    {
      name: ['', [Validators.required, Validators.maxLength(100)]],
      description: [''],
      defaultUnit: ['g', Validators.required],
      category: [null as string | null],
      containsGluten: [false],
      calories: [null as number | null],
      protein: [null as number | null],
      carbs: [null as number | null],
      fat: [null as number | null],
      fiber: [null as number | null]
    },
    { validators: caloriesMatchMacros }
  );
  error = '';

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.editId = Number(id);
      this.ingredientService.getById(this.editId).subscribe((i) => {
        this.form.patchValue({
          name: i.name,
          description: i.description,
          defaultUnit: i.defaultUnit,
          category: i.category ?? null,
          containsGluten: i.containsGluten,
          calories: i.calories ?? null,
          protein: i.protein ?? null,
          carbs: i.carbs ?? null,
          fat: i.fat ?? null,
          fiber: i.fiber ?? null
        });
      });
    }
  }

  get calculatedCalories(): number | null {
    const v = this.form.value;
    const p = v.protein ?? 0,
      c = v.carbs ?? 0,
      f = v.fat ?? 0,
      fi = v.fiber ?? 0;
    if (p === 0 && c === 0 && f === 0 && fi === 0) return null;
    return Math.round(p * 4 + c * 4 + f * 9 + fi * 2);
  }

  get caloriesWarning(): string | null {
    const err = this.form.errors?.['calorieMismatch'];
    if (!err) return null;
    return `Macros suggest ≈ ${err.calc} kcal (entered ${err.entered}). Fix to save.`;
  }

  setUnit(u: string): void {
    this.form.patchValue({ defaultUnit: u });
  }
  toggleGluten(): void {
    this.form.patchValue({ containsGluten: !this.form.get('containsGluten')?.value });
  }

  submit(): void {
    if (this.form.invalid) return;
    const req = this.form.getRawValue() as any;
    const save$ = this.editId
      ? this.ingredientService.update(this.editId, req)
      : this.ingredientService.create(req);
    save$.subscribe({
      next: (i) => this.router.navigate(['/ingredients', i.id]),
      error: (e: any) => (this.error = e.error?.error || 'Save failed')
    });
  }
}
