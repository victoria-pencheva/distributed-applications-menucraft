export type MealType = 'BREAKFAST' | 'LUNCH' | 'DINNER';

export interface MealPlanEntry {
  id: number;
  recipeId: number;
  recipeName: string;
  recipeImageUrl: string | null;
  recipePrepTime: number;
  recipeCookTime: number;
  date: string;
  mealType: MealType;
  servings: number;
  calories: number | null;
  protein: number | null;
  carbs: number | null;
  fat: number | null;
}
