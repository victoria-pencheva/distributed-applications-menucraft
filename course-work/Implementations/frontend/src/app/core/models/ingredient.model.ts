export type UnitType = 'g' | 'kg' | 'pcs';

export interface Ingredient {
  id: number;
  name: string;
  description: string;
  defaultUnit: UnitType;
  calories?: number;
  protein?: number;
  carbs?: number;
  fat?: number;
  fiber?: number;
  category?: string;
  containsGluten: boolean;
  createdAt: string;
  updatedAt: string;
}
export interface IngredientRequest {
  name: string;
  description: string;
  defaultUnit: UnitType;
  calories: number | null;
  protein: number | null;
  carbs: number | null;
  fat: number | null;
  fiber: number | null;
}
