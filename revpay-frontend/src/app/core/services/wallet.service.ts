import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import {
  Wallet,
  SendMoneyRequest,
  AddMoneyRequest,
  WithdrawRequest,
  MoneyRequestDto,
  MoneyRequestResponse
} from '../../models/wallet.model';
import { Transaction } from '../../models/transaction.model';

@Injectable({ providedIn: 'root' })
export class WalletService {

  private apiUrl = `${environment.apiUrl}/wallet`;

  constructor(private http: HttpClient) {}

  getWallet(): Observable<Wallet> {
    return this.http.get<Wallet>(this.apiUrl);
  }

  addMoney(request: AddMoneyRequest): Observable<Transaction> {
    return this.http.post<Transaction>(`${this.apiUrl}/add-money`, request);
  }

  withdraw(request: WithdrawRequest): Observable<Transaction> {
    return this.http.post<Transaction>(`${this.apiUrl}/withdraw`, request);
  }

  sendMoney(request: SendMoneyRequest): Observable<Transaction> {
    return this.http.post<Transaction>(`${this.apiUrl}/send-money`, request);
  }

  requestMoney(request: MoneyRequestDto): Observable<MoneyRequestResponse> {
    return this.http.post<MoneyRequestResponse>(`${this.apiUrl}/request-money`, request);
  }

  getIncomingRequests(): Observable<MoneyRequestResponse[]> {
    return this.http.get<MoneyRequestResponse[]>(`${this.apiUrl}/requests/incoming`);
  }

  getOutgoingRequests(): Observable<MoneyRequestResponse[]> {
    return this.http.get<MoneyRequestResponse[]>(`${this.apiUrl}/requests/outgoing`);
  }

  acceptRequest(requestId: number, transactionPin: string): Observable<Transaction> {
    return this.http.put<Transaction>(
      `${this.apiUrl}/requests/${requestId}/accept?transactionPin=${transactionPin}`, {}
    );
  }

declineRequest(requestId: number): Observable<string> {
  return this.http.put(
    `${this.apiUrl}/requests/${requestId}/decline`,
    {},
    { responseType: 'text' }
  );
}

cancelRequest(requestId: number): Observable<string> {
  return this.http.put(
    `${this.apiUrl}/requests/${requestId}/cancel`,
    {},
    { responseType: 'text' }
  );
}
}