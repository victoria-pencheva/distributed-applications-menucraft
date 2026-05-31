import { Component, inject } from '@angular/core';
import {
  AbstractControl,
  AsyncValidatorFn,
  FormBuilder,
  ReactiveFormsModule,
  ValidationErrors,
  Validators
} from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { NgIf } from '@angular/common';
import { Observable, debounceTime, first, map, switchMap } from 'rxjs';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    RouterLink,
    NgIf
  ],
  templateUrl: './register.component.html'
})
export class RegisterComponent {
  private fb = inject(FormBuilder);
  private auth = inject(AuthService);
  private router = inject(Router);

  private usernameTaken(): AsyncValidatorFn {
    return (ctrl: AbstractControl): Observable<ValidationErrors | null> =>
      ctrl.valueChanges.pipe(
        debounceTime(400),
        switchMap((v) => this.auth.checkUsername(v)),
        map((available) => (available ? null : { usernameTaken: true })),
        first()
      );
  }

  private emailTaken(): AsyncValidatorFn {
    return (ctrl: AbstractControl): Observable<ValidationErrors | null> =>
      ctrl.valueChanges.pipe(
        debounceTime(400),
        switchMap((v) => this.auth.checkEmail(v)),
        map((available) => (available ? null : { emailTaken: true })),
        first()
      );
  }

  form = this.fb.group({
    username: ['', [Validators.required, Validators.minLength(3)], [this.usernameTaken()]],
    email: ['', [Validators.required, Validators.email], [this.emailTaken()]],
    password: ['', [Validators.required, Validators.pattern(/^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&]).{8,}$/)]]
  });
  error = '';

  submit(): void {
    if (this.form.invalid) return;
    this.auth.register(this.form.value as any).subscribe({
      next: () => this.router.navigate(['/recipes']),
      error: (e: any) => (this.error = e.error?.error || 'Registration failed')
    });
  }
}
