import { Component, OnInit, inject } from '@angular/core';
import { NgIf, NgFor } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { Router } from '@angular/router';
import { forkJoin } from 'rxjs';
import { Category } from '../../core/models/category.model';
import { Recipe } from '../../core/models/recipe.model';
import { CategoryService } from '../../core/services/category.service';
import { RecipeService } from '../../core/services/recipe.service';
import { AuthService } from '../../core/services/auth.service';
import { RecipeCardComponent } from '../../recipes/recipe-card/recipe-card.component';
import { RecipePreviewComponent } from '../../recipes/recipe-preview/recipe-preview.component';

@Component({
  selector: 'app-category-detail',
  standalone: true,
  imports: [NgIf, NgFor, RouterLink, RecipeCardComponent, RecipePreviewComponent],
  templateUrl: './category-detail.component.html'
})
export class CategoryDetailComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private categoryService = inject(CategoryService);
  private recipeService = inject(RecipeService);
  private auth = inject(AuthService);

  category: Category | null = null;
  recipes: Recipe[] = [];
  selectedRecipe: Recipe | null = null;
  isAdmin = this.auth.isAdmin();

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    forkJoin({
      cat: this.categoryService.getById(id),
      recipes: this.recipeService.search({ categoryId: id, page: 0, size: 100 })
    }).subscribe(({ cat, recipes }) => {
      this.category = cat;
      this.recipes = recipes.content;
    });
  }

  openRecipe(r: Recipe): void {
    this.router.navigate(['/recipes', r.id]);
  }

  delete(): void {
    if (!this.category) return;
    if (confirm('Delete category?')) {
      this.categoryService
        .delete(this.category.id)
        .subscribe(() => this.router.navigate(['/categories']));
    }
  }
}
