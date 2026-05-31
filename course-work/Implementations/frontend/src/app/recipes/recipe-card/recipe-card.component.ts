import { Component, Input, Output, EventEmitter, ChangeDetectionStrategy } from '@angular/core';
import { NgIf, NgFor, DecimalPipe } from '@angular/common';
import { Recipe } from '../../core/models/recipe.model';

@Component({
  selector: 'app-recipe-card',
  standalone: true,
  imports: [NgIf, NgFor, DecimalPipe],
  templateUrl: './recipe-card.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class RecipeCardComponent {
  @Input() recipe!: Recipe;
  @Input() large = false;
  @Output() clicked = new EventEmitter<Recipe>();

  get totalTime(): number {
    return (this.recipe.prepTime ?? 0) + (this.recipe.cookTime ?? 0);
  }

  placeholderColor(): string {
    const colors = ['#A5B384', '#D89B3C', '#8B4A2B', '#6B7D4A'];
    let hash = 0;
    for (const ch of this.recipe.name) hash = (hash * 31 + ch.charCodeAt(0)) & 0xffff;
    return colors[hash % colors.length];
  }
}
