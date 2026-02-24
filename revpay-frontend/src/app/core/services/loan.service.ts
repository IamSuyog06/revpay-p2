import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { LoanApplication, LoanApplicationRequest, LoanRepaymentRequest } from '../../models/loan.model';

@Injectable({ providedIn: 'root' })
export class LoanService {

  private apiUrl = `${environment.apiUrl}/loans`;

  constructor(private http: HttpClient) {}

  getAllLoans(): Observable<LoanApplication[]> {
    return this.http.get<LoanApplication[]>(this.apiUrl);
  }

  getLoansByStatus(status: string): Observable<LoanApplication[]> {
    return this.http.get<LoanApplication[]>(`${this.apiUrl}/status/${status}`);
  }

  getLoanById(id: number): Observable<LoanApplication> {
    return this.http.get<LoanApplication>(`${this.apiUrl}/${id}`);
  }

  applyForLoan(request: LoanApplicationRequest): Observable<LoanApplication> {
    return this.http.post<LoanApplication>(`${this.apiUrl}/apply`, request);
  }

  makeRepayment(id: number, request: LoanRepaymentRequest): Observable<any> {
    return this.http.post(`${this.apiUrl}/${id}/repay`, request);
  }

  uploadDocument(loanId: number, file: File): Observable<string> {
  const formData = new FormData();
  formData.append('file', file);
  return this.http.post(
    `${this.apiUrl}/${loanId}/upload-document`,
    formData,
    { responseType: 'text' }
  );
}
}