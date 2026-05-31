import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';
import { adminGuard } from './core/guards/admin.guard';

export const routes: Routes = [
  { path: '', redirectTo: '/recipes', pathMatch: 'full' },
  {
    path: 'login',
    loadComponent: () => import('./auth/login/login.component').then((m) => m.LoginComponent)
  },
  {
    path: 'register',
    loadComponent: () =>
      import('./auth/register/register.component').then((m) => m.RegisterComponent)
  },
  {
    path: 'recipes',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./recipes/recipe-list/recipe-list.component').then((m) => m.RecipeListComponent)
  },
  {
    path: 'recipes/new',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./recipes/recipe-form/recipe-form.component').then((m) => m.RecipeFormComponent)
  },
  {
    path: 'recipes/:id',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./recipes/recipe-detail/recipe-detail.component').then((m) => m.RecipeDetailComponent)
  },
  {
    path: 'recipes/:id/edit',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./recipes/recipe-form/recipe-form.component').then((m) => m.RecipeFormComponent)
  },
  {
    path: 'categories',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./categories/category-list/category-list.component').then(
        (m) => m.CategoryListComponent
      )
  },
  {
    path: 'categories/new',
    canActivate: [authGuard, adminGuard],
    loadComponent: () =>
      import('./categories/category-form/category-form.component').then(
        (m) => m.CategoryFormComponent
      )
  },
  {
    path: 'categories/:id/edit',
    canActivate: [authGuard, adminGuard],
    loadComponent: () =>
      import('./categories/category-form/category-form.component').then(
        (m) => m.CategoryFormComponent
      )
  },
  {
    path: 'categories/:id',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./categories/category-detail/category-detail.component').then(
        (m) => m.CategoryDetailComponent
      )
  },
  {
    path: 'ingredients',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./ingredients/ingredient-list/ingredient-list.component').then(
        (m) => m.IngredientListComponent
      )
  },
  {
    path: 'ingredients/new',
    canActivate: [authGuard, adminGuard],
    loadComponent: () =>
      import('./ingredients/ingredient-form/ingredient-form.component').then(
        (m) => m.IngredientFormComponent
      )
  },
  {
    path: 'ingredients/:id/edit',
    canActivate: [authGuard, adminGuard],
    loadComponent: () =>
      import('./ingredients/ingredient-form/ingredient-form.component').then(
        (m) => m.IngredientFormComponent
      )
  },
  {
    path: 'ingredients/:id',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./ingredients/ingredient-detail/ingredient-detail.component').then(
        (m) => m.IngredientDetailComponent
      )
  },
  {
    path: 'meal-plan',
    canActivate: [authGuard],
    loadComponent: () => import('./meal-plan/meal-plan.component').then((m) => m.MealPlanComponent)
  },
  {
    path: 'pantry',
    canActivate: [authGuard],
    loadComponent: () => import('./pantry/pantry.component').then((m) => m.PantryComponent)
  },
  {
    path: 'shopping-list',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./shopping-list/shopping-list.component').then((m) => m.ShoppingListComponent)
  },
  { path: '**', redirectTo: '/recipes' }
];
