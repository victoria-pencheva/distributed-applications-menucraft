import { Component, OnInit, inject } from '@angular/core';
import { NgIf, NgFor } from '@angular/common';
import { RouterLink } from '@angular/router';
import { forkJoin, map } from 'rxjs';
import { Category } from '../../core/models/category.model';
import { Recipe } from '../../core/models/recipe.model';
import { CategoryService } from '../../core/services/category.service';
import { RecipeService } from '../../core/services/recipe.service';
import { AuthService } from '../../core/services/auth.service';

const PALETTE = [
  '#3D2B1F',
  '#7A3520',
  '#2D3D25',
  '#6B4F1A',
  '#4D1F2B',
  '#2D3B3F',
  '#5C3A1E',
  '#3D4A25'
];

@Component({
  selector: 'app-category-list',
  standalone: true,
  imports: [NgIf, NgFor, RouterLink],
  templateUrl: './category-list.component.html'
})
export class CategoryListComponent implements OnInit {
  private categoryService = inject(CategoryService);
  private recipeService = inject(RecipeService);
  private auth = inject(AuthService);

  categories: Category[] = [];
  recipesByCategory = new Map<string, Recipe[]>();
  isAdmin = this.auth.isAdmin();

  ngOnInit(): void {
    forkJoin({
      cats: this.categoryService.getAll().pipe(map((p) => p.content)),
      recipes: this.recipeService.getAll(0, 200).pipe(map((p) => p.content))
    }).subscribe(({ cats, recipes }) => {
      this.categories = cats;
      const map = new Map<string, Recipe[]>();
      for (const r of recipes) {
        if (!r.categoryName) continue;
        if (!map.has(r.categoryName)) map.set(r.categoryName, []);
        map.get(r.categoryName)!.push(r);
      }
      this.recipesByCategory = map;
    });
  }

  recipesFor(cat: Category): Recipe[] {
    return this.recipesByCategory.get(cat.name) ?? [];
  }

  colorFor(index: number): string {
    return PALETTE[index % PALETTE.length];
  }

  totalTime(r: Recipe): number {
    return r.prepTime + r.cookTime;
  }

  delete(id: number): void {
    if (confirm('Delete category?')) {
      this.categoryService.delete(id).subscribe(() => {
        this.categories = this.categories.filter((c) => c.id !== id);
      });
    }
  }
}
