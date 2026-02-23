import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Transaction, TransactionFilter } from '../../models/transaction.model';

@Injectable({ providedIn: 'root' })
export class TransactionService {

  private apiUrl = `${environment.apiUrl}/transactions`;

  constructor(private http: HttpClient) {}

  getAllTransactions(): Observable<Transaction[]> {
    return this.http.get<Transaction[]>(this.apiUrl);
  }

  getTransactionById(id: number): Observable<Transaction> {
    return this.http.get<Transaction>(`${this.apiUrl}/${id}`);
  }

  filterTransactions(filter: TransactionFilter): Observable<Transaction[]> {
    return this.http.post<Transaction[]>(`${this.apiUrl}/filter`, filter);
  }

  searchTransactions(keyword: string): Observable<Transaction[]> {
    return this.http.get<Transaction[]>(`${this.apiUrl}/search?keyword=${keyword}`);
  }

  getExportUrl(): string {
    return `${this.apiUrl}/export/csv`;
  }
}