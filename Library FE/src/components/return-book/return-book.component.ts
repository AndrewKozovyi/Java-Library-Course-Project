import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatDialogActions, MatDialogContent, MatDialogRef, MatDialogTitle } from '@angular/material/dialog';
import { BookService } from '../../services/book.service';
import {debounceTime, startWith, switchMap} from 'rxjs';
import { MatInputModule } from '@angular/material/input';
import { MatAutocompleteModule } from '@angular/material/autocomplete';

@Component({
  selector: 'app-return-book',
  templateUrl: './return-book.component.html',
  imports: [
    MatDialogTitle,
    MatDialogContent,
    MatDialogActions,
    ReactiveFormsModule,
    MatInputModule,
    MatAutocompleteModule
  ],
  styleUrls: ['../../dialog-styles.scss']
})
export class ReturnBookComponent implements OnInit {
  returnForm: FormGroup;
  filteredAuthors: string[] = [];
  filteredTitles: string[] = [];

  constructor(
    private fb: FormBuilder,
    private bookService: BookService,
    public dialogRef: MatDialogRef<ReturnBookComponent>
  ) {
    this.returnForm = this.fb.group({
      author: ['', [Validators.required, Validators.minLength(3)]],
      title: ['', [Validators.required, Validators.minLength(3)]],
      copies: [1, [Validators.required, Validators.min(1)]]
    });
  }

  ngOnInit(): void {
    this.returnForm.get('author')?.valueChanges.pipe(
      startWith(''),
      debounceTime(300),
      switchMap(value => this.bookService.getAuthors(value || '', this.returnForm.get('title')?.value || ''))
    ).subscribe(data => this.filteredAuthors = data || []);

    // Для назв
    this.returnForm.get('title')?.valueChanges.pipe(
      startWith(''),
      debounceTime(300),
      switchMap(value => this.bookService.getTitles(value || '', this.returnForm.get('author')?.value || ''))
    ).subscribe(data => this.filteredTitles = data || []);
  }

  increment() {
    const val = this.returnForm.get('copies')?.value || 0;
    this.returnForm.get('copies')?.setValue(val + 1);
  }

  decrement() {
    const val = this.returnForm.get('copies')?.value || 1;
    if (val > 1) this.returnForm.get('copies')?.setValue(val - 1);
  }

  submit() {
    if (this.returnForm.valid) {
      this.bookService.returnBook(this.returnForm.value).subscribe(() => this.dialogRef.close(true));
    }
  }
}
