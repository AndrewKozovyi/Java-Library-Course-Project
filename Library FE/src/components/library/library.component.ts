import { Component, OnInit } from '@angular/core';
import { BookService } from '../../services/book.service';
import { Book } from '../../models/book.model';
import { MatDialog } from '@angular/material/dialog';
import { AddBookComponent } from '../add-book/add-book.component';
import { ReturnBookComponent } from '../return-book/return-book.component';
import { WriteOffComponent } from '../write-off-book/write-off-book.component';
import { NgClass } from '@angular/common';

@Component({
  selector: 'app-library',
  templateUrl: './library.component.html',
  imports: [NgClass],
  styleUrls: ['./library.component.scss']
})
export class LibraryComponent implements OnInit {
  books: Book[] = [];
  toast: { message: string, type: 'success' | 'error' } | null = null;

  sortColumn: keyof Book = 'author';
  sortDirection: 'asc' | 'desc' = 'asc';

  constructor(private bookService: BookService, private dialog: MatDialog) {}

  ngOnInit(): void {
    this.loadBooks();
  }

  loadBooks(): void {
    this.bookService.getBooks().subscribe({
      next: (data) => {
        this.books = data;
        this.applySort();
      },
      error: () => this.showToast('Помилка завантаження бази даних', 'error')
    });
  }

  sortData(column: keyof Book): void {
    if (this.sortColumn === column) {
      this.sortDirection = this.sortDirection === 'asc' ? 'desc' : 'asc';
    } else {
      this.sortColumn = column;
      this.sortDirection = 'asc';
    }
    this.applySort();
  }

  applySort(): void {
    this.books.sort((a, b) => {
      let valA = a[this.sortColumn];
      let valB = b[this.sortColumn];

      if (typeof valA === 'string') valA = valA.toLowerCase();
      if (typeof valB === 'string') valB = valB.toLowerCase();

      if (valA < valB) return this.sortDirection === 'asc' ? -1 : 1;
      if (valA > valB) return this.sortDirection === 'asc' ? 1 : -1;
      return 0;
    });
  }

  showToast(message: string, type: 'success' | 'error'): void {
    this.toast = { message, type };
    setTimeout(() => this.toast = null, 4000);
  }

  openAddModal(): void {
    const dialogRef = this.dialog.open(AddBookComponent, { width: '600px', autoFocus: false });
    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.loadBooks();
        this.showToast('Книгу успішно додано!', 'success');
      }
    });
  }

  openReturnModal(): void {
    const dialogRef = this.dialog.open(ReturnBookComponent, { width: '600px', autoFocus: false });
    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.loadBooks();
        this.showToast('Книгу успішно повернуто!', 'success');
      }
    });
  }

  openWriteOffModal(): void {
    const dialogRef = this.dialog.open(WriteOffComponent, {
      width: '400px',
      autoFocus: false,
      data: { books: this.books }
    });
    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.loadBooks();
        this.showToast('Книгу успішно списано!', 'success');
      }
    });
  }
}
