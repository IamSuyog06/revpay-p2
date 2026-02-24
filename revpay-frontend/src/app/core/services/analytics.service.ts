import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface AnalyticsDashboard {
  totalReceived: number;
  totalSent: number;
  currentBalance: number;
  pendingRequestsCount: number;
  pendingRequestsAmount: number;
  revenueReport: { daily: number; weekly: number; monthly: number };
  invoiceSummary: {
    totalInvoices: number;
    paidInvoices: number;
    unpaidInvoices: number;
    overdueInvoices: number;
    totalPaidAmount: number;
    totalOutstandingAmount: number;
  };
  topCustomers: {
    customerName: string;
    customerEmail: string;
    totalTransactionVolume: number;
    transactionCount: number;
  }[];
}

@Injectable({ providedIn: 'root' })
export class AnalyticsService {

  private apiUrl = `${environment.apiUrl}/analytics`;

  constructor(private http: HttpClient) {}

  getDashboard(): Observable<AnalyticsDashboard> {
    return this.http.get<AnalyticsDashboard>(`${this.apiUrl}/dashboard`);
  }
}