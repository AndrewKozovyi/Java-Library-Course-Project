import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatDialogActions, MatDialogContent, MatDialogRef, MatDialogTitle } from '@angular/material/dialog';
import { BookService } from '../../services/book.service';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { MatInputModule } from '@angular/material/input';
import { MatDatepickerModule } from '@angular/material/datepicker';
import {MAT_DATE_FORMATS, provideNativeDateAdapter} from '@angular/material/core';
import {debounceTime, startWith, switchMap} from 'rxjs';

export const YEAR_ONLY_FORMAT = {
  parse: {
    dateInput: { year: 'numeric' },
  },
  display: {
    dateInput: { year: 'numeric' },
    monthYearLabel: { year: 'numeric' },
    dateA11yLabel: { year: 'numeric' },
    monthYearA11yLabel: { year: 'numeric' },
  },
};

@Component({
  selector: 'app-add-book',
  templateUrl: './add-book.component.html',
  providers: [
    provideNativeDateAdapter(),
    { provide: MAT_DATE_FORMATS, useValue: YEAR_ONLY_FORMAT }
  ],
  imports: [
    MatDialogTitle,
    MatDialogContent,
    MatDialogActions,
    ReactiveFormsModule,
    MatInputModule,
    MatAutocompleteModule,
    MatDatepickerModule
  ],
  styleUrls: ['../../dialog-styles.scss']
})
export class AddBookComponent implements OnInit {
  addForm: FormGroup;
  filteredAuthors: string[] = [];
  filteredTitles: string[] = [];
  maxDate: Date = new Date();

  constructor(
    private fb: FormBuilder,
    private bookService: BookService,
    public dialogRef: MatDialogRef<AddBookComponent>
  ) {
    this.addForm = this.fb.group({
      author: ['', [Validators.required, Validators.minLength(3)]],
      title: ['', [Validators.required, Validators.minLength(3)]],
      year: [new Date(), Validators.required],
      copies: [1, [Validators.required, Validators.min(1)]]
    });
  }

  ngOnInit(): void {
    this.addForm.get('author')?.valueChanges.pipe(
      startWith(''),
      debounceTime(300),
      switchMap(value => this.bookService.getAuthors(value || '', this.addForm.get('title')?.value || ''))
    ).subscribe(data => this.filteredAuthors = data || []);

    this.addForm.get('title')?.valueChanges.pipe(
      startWith(''),
      debounceTime(300),
      switchMap(value => this.bookService.getTitles(value || '', this.addForm.get('author')?.value || ''))
    ).subscribe(data => this.filteredTitles = data || []);
  }

  setYear(event: any, dp: any) {
    this.addForm.get('year')?.setValue(event);
    dp.close();
  }

  increment() {
    const val = this.addForm.get('copies')?.value || 0;
    this.addForm.get('copies')?.setValue(val + 1);
  }

  decrement() {
    const val = this.addForm.get('copies')?.value || 1;
    if (val > 1) this.addForm.get('copies')?.setValue(val - 1);
  }

  submit() {
    if (this.addForm.valid) {
      const payload = {
        ...this.addForm.value,
        year: this.addForm.value.year.getFullYear()
      };
      this.bookService.addBook(payload).subscribe(() => this.dialogRef.close(true));
    }
  }
}
