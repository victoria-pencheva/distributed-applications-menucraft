export interface RecipeIngredient {
  id: number;
  ingredientId: number;
  ingredientName: string;
  quantity: number;
  unit: string;
}
export interface RecipeStep {
  id: number;
  stepNumber: number;
  description: string;
}

export interface Recipe {
  id: number;
  name: string;
  description: string;
  prepTime: number;
  cookTime: number;
  servings: number;
  difficulty: string;
  imageUrl: string;
  createdAt: string;
  username: string;
  categoryId: number;
  categoryName: string;
  ingredients: RecipeIngredient[];
  steps: RecipeStep[];
  totalCalories: number;
  caloriesPerServing: number;
  cuisine: string;
  blurb: string;
  tags: string[];
  protein: number | null;
  carbs: number | null;
  fat: number | null;
  fiber: number | null;
  avgRating: number | null;
  ratingCount: number | null;
  myRating: number | null;
}

export interface RecipeRequest {
  name: string;
  description: string;
  prepTime: number;
  cookTime: number;
  servings: number;
  difficulty: string;
  imageUrl: string;
  categoryId: number | null;
  cuisine?: string;
  blurb?: string;
  protein?: number;
  carbs?: number;
  fat?: number;
  fiber?: number;
}

export interface RecipeSearchParams {
  name?: string;
  categoryId?: number;
  ingredientId?: number;
  difficulty?: string;
  minPrepTime?: number;
  maxPrepTime?: number;
  includeIngredientIds?: number[];
  excludeIngredientIds?: number[];
  cuisine?: string;
  tags?: string[];
  maxCalories?: number;
  minProtein?: number;
  maxCarbs?: number;
  maxFat?: number;
  sort?: string;
  page?: number;
  size?: number;
}
