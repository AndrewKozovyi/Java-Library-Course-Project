import { Component, Inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogActions, MatDialogContent, MatDialogRef, MatDialogTitle } from '@angular/material/dialog';
import { BookService } from '../../services/book.service';
import { Book } from '../../models/book.model';
import { MatInputModule } from '@angular/material/input';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import {FormControl, ReactiveFormsModule, FormsModule, AbstractControl, Validators} from '@angular/forms';
import { startWith, map } from 'rxjs';

@Component({
  selector: 'app-write-off',
  templateUrl: './write-off-book.component.html',
  imports: [
    MatDialogTitle,
    MatDialogContent,
    MatDialogActions,
    MatInputModule,
    MatAutocompleteModule,
    ReactiveFormsModule,
    FormsModule
  ],
  styleUrls: ['../../dialog-styles.scss'],
})
export class WriteOffComponent implements OnInit {
  filteredBooks: Book[] = [];
  amount: number = 1;
  books: Book[];

  bookValidator = (control: AbstractControl) => {
    const val = control.value;
    return (typeof val === 'object' && val !== null && 'id' in val) ? null : { invalidBook: true };
  };

  bookControl = new FormControl<string | Book>('', [Validators.required, this.bookValidator]);

  constructor(
    @Inject(MAT_DIALOG_DATA) public data: any,
    private bookService: BookService,
    public dialogRef: MatDialogRef<WriteOffComponent>
  ) {
    this.books = data.books;
  }

  ngOnInit() {
    this.filteredBooks = this.books;
    this.bookControl.valueChanges.pipe(
      startWith(''),
      map(value => {
        const name = typeof value === 'string' ? value : (value ? `${value.author} ${value.title}` : '');
        return name ? this._filter(name as string) : this.books.slice();
      })
    ).subscribe(res => this.filteredBooks = res);
  }

  private _filter(name: string): Book[] {
    const filterValue = name.toLowerCase();
    return this.books.filter(option =>
      `${option.author} ${option.title}`.toLowerCase().includes(filterValue)
    );
  }

  displayFn(book: Book): string {
    return book && book.author ? `${book.author} — "${book.title}" (Доступно: ${book.copies})` : '';
  }

  get isBookSelected(): boolean {
    return typeof this.bookControl.value === 'object' && this.bookControl.value !== null && 'id' in this.bookControl.value;
  }

  increment() { this.amount++; }
  decrement() { if (this.amount > 1) this.amount--; }

  submit() {
    const selectedBook = this.bookControl.value as Book;
    if (selectedBook && selectedBook.id) {
      this.bookService.writeOffBook(selectedBook.id, this.amount).subscribe(() => this.dialogRef.close(true));
    }
  }
}
