import { Component, OnInit, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { NgIf } from '@angular/common';
import { CategoryService } from '../../core/services/category.service';
import { UploadService } from '../../core/services/upload.service';

@Component({
  selector: 'app-category-form',
  standalone: true,
  imports: [NgIf, RouterLink, ReactiveFormsModule],
  templateUrl: './category-form.component.html'
})
export class CategoryFormComponent implements OnInit {
  private fb = inject(FormBuilder);
  private route = inject(ActivatedRoute);
  private categoryService = inject(CategoryService);
  private uploadService = inject(UploadService);
  private router = inject(Router);

  editId: number | null = null;

  form = this.fb.group({
    name: ['', [Validators.required, Validators.maxLength(50)]],
    description: [''],
    imageUrl: ['']
  });
  error = '';
  uploading = false;
  previewUrl: string | null = null;

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.editId = Number(id);
      this.categoryService.getById(this.editId).subscribe((c) => {
        this.form.patchValue({ name: c.name, description: c.description, imageUrl: c.imageUrl });
      });
    }
  }

  clearImage(): void {
    this.previewUrl = null;
    this.form.patchValue({ imageUrl: '' });
  }

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (!input.files?.length) return;
    const file = input.files[0];
    const reader = new FileReader();
    reader.onload = () => (this.previewUrl = reader.result as string);
    reader.readAsDataURL(file);
    this.uploading = true;
    this.error = '';
    this.uploadService.uploadImage(file).subscribe({
      next: (res) => {
        this.form.patchValue({ imageUrl: res.url });
        this.uploading = false;
      },
      error: () => {
        this.error = 'Image upload failed';
        this.uploading = false;
        this.previewUrl = null;
      }
    });
  }

  submit(): void {
    if (this.form.invalid) return;
    const req = this.form.getRawValue() as any;
    const save$ = this.editId
      ? this.categoryService.update(this.editId, req)
      : this.categoryService.create(req);
    save$.subscribe({
      next: (c) => this.router.navigate(['/categories', c.id]),
      error: (e: any) => (this.error = e.error?.error || 'Save failed')
    });
  }
}
