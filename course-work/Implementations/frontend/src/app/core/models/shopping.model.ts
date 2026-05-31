export interface ShoppingItem {
  id: number;
  ingredientId: number | null;
  ingredientName: string | null;
  name: string | null;
  quantity: number | null;
  unit: string | null;
  checked: boolean;
  category?: string | null;
}
