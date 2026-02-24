import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Invoice, CreateInvoiceRequest } from '../../models/invoice.model';

@Injectable({ providedIn: 'root' })
export class InvoiceService {

  private apiUrl = `${environment.apiUrl}/invoices`;

  constructor(private http: HttpClient) {}

  getAllInvoices(): Observable<Invoice[]> {
    return this.http.get<Invoice[]>(this.apiUrl);
  }

  getInvoicesByStatus(status: string): Observable<Invoice[]> {
    return this.http.get<Invoice[]>(`${this.apiUrl}/status/${status}`);
  }

  getInvoiceById(id: number): Observable<Invoice> {
    return this.http.get<Invoice>(`${this.apiUrl}/${id}`);
  }

  createInvoice(request: CreateInvoiceRequest): Observable<Invoice> {
    return this.http.post<Invoice>(this.apiUrl, request);
  }

  sendInvoice(id: number): Observable<Invoice> {
    return this.http.put<Invoice>(`${this.apiUrl}/${id}/send`, {});
  }

  markAsPaid(id: number): Observable<Invoice> {
    return this.http.put<Invoice>(`${this.apiUrl}/${id}/mark-paid`, {});
  }

  cancelInvoice(id: number): Observable<Invoice> {
    return this.http.put<Invoice>(`${this.apiUrl}/${id}/cancel`, {});
  }
}