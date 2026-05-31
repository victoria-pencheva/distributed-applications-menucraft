import { Component, Input, Output, EventEmitter, ChangeDetectionStrategy } from '@angular/core';
import { NgFor } from '@angular/common';

@Component({
  selector: 'app-star-rating',
  standalone: true,
  imports: [NgFor],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <div class="sr-wrap">
      <button
        *ngFor="let s of stars"
        class="sr-star"
        [class.sr-filled]="s <= (hovered ?? current ?? 0)"
        [class.sr-mine]="!hovered && s <= (current ?? 0)"
        (mouseenter)="hovered = s"
        (mouseleave)="hovered = null"
        (click)="onRate(s)"
        [attr.aria-label]="s + ' stars'"
      >
        ★
      </button>
    </div>
  `,
  styles: [
    `
      .sr-wrap {
        display: flex;
        gap: 2px;
      }
      .sr-star {
        background: none;
        border: none;
        cursor: pointer;
        font-size: 22px;
        color: var(--ink-4, #ccc);
        padding: 0 2px;
        line-height: 1;
        transition:
          color 0.1s,
          transform 0.1s;
      }
      .sr-star:hover {
        transform: scale(1.15);
      }
      .sr-filled {
        color: var(--amber, #d89b3c);
      }
      .sr-mine {
        color: var(--amber, #d89b3c);
      }
    `
  ]
})
export class StarRatingComponent {
  @Input() current: number | null = null;
  @Output() rated = new EventEmitter<number>();

  readonly stars = [1, 2, 3, 4, 5];
  hovered: number | null = null;

  onRate(stars: number): void {
    this.rated.emit(stars);
  }
}
