import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { LoanApplication } from '../../models/loan.model';

export interface AdminStats {
  totalUsers: number;
  businessUsers: number;
  personalUsers: number;
  totalLoans: number;
  pendingLoans: number;
  approvedLoans: number;
  rejectedLoans: number;
}

export interface AdminUser {
  id: number;
  fullName: string;
  email: string;
  phone: string;
  role: string;
  isVerified: boolean;
  isActive: boolean;
  hasTransactionPin: boolean;
  createdAt: string;
}

@Injectable({ providedIn: 'root' })
export class AdminService {

  private apiUrl = `${environment.apiUrl}/admin`;

  constructor(private http: HttpClient) {}

  getDashboardStats(): Observable<AdminStats> {
    return this.http.get<AdminStats>(`${this.apiUrl}/dashboard`);
  }

  getAllLoans(): Observable<LoanApplication[]> {
    return this.http.get<LoanApplication[]>(`${this.apiUrl}/loans`);
  }

  getLoansByStatus(status: string): Observable<LoanApplication[]> {
    return this.http.get<LoanApplication[]>(`${this.apiUrl}/loans/status/${status}`);
  }

  approveLoan(id: number): Observable<LoanApplication> {
    return this.http.put<LoanApplication>(`${this.apiUrl}/loans/${id}/approve`, {});
  }

  rejectLoan(id: number, reason: string): Observable<LoanApplication> {
    return this.http.put<LoanApplication>(`${this.apiUrl}/loans/${id}/reject`, { reason });
  }

  getAllUsers(): Observable<AdminUser[]> {
    return this.http.get<AdminUser[]>(`${this.apiUrl}/users`);
  }

}