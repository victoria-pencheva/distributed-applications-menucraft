import { Component, OnInit, inject } from '@angular/core';
import { NgIf, DatePipe } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { Router } from '@angular/router';
import { Ingredient } from '../../core/models/ingredient.model';
import { IngredientService } from '../../core/services/ingredient.service';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-ingredient-detail',
  standalone: true,
  imports: [NgIf, DatePipe, RouterLink],
  templateUrl: './ingredient-detail.component.html'
})
export class IngredientDetailComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private ingredientService = inject(IngredientService);
  private auth = inject(AuthService);

  ingredient: Ingredient | null = null;
  isAdmin = this.auth.isAdmin();

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    this.ingredientService.getById(id).subscribe((i) => (this.ingredient = i));
  }

  delete(): void {
    if (!this.ingredient) return;
    if (confirm('Delete ingredient?')) {
      this.ingredientService
        .delete(this.ingredient.id)
        .subscribe(() => this.router.navigate(['/ingredients']));
    }
  }
}
