import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Book } from '../models/book.model';

@Injectable({
  providedIn: 'root'
})
export class BookService {
  private apiUrl = 'http://localhost:8080/api/books';

  constructor(private http: HttpClient) {}

  getBooks(): Observable<Book[]> {
    return this.http.get<Book[]>(this.apiUrl);
  }

  addBook(bookData: any): Observable<any> {
    return this.http.post(this.apiUrl, bookData, { responseType: 'text' });
  }

  returnBook(data: any): Observable<any> {
    return this.http.put(`${this.apiUrl}/return`, data, { responseType: 'text' });
  }

  writeOffBook(id: number, amount: number): Observable<any> {
    return this.http.post(`${this.apiUrl}/writeoff`, { id, amount }, { responseType: 'text' });
  }

  getAuthors(prefix: string, title: string = ''): Observable<string[]> {
    return this.http.get<string[]>(`${this.apiUrl}/authors`, { params: { prefix, title } });
  }

  getTitles(prefix: string, author: string = ''): Observable<string[]> {
    return this.http.get<string[]>(`${this.apiUrl}/titles`, { params: { prefix, author } });
  }
}
