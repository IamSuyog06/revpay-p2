import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { PaymentMethod, AddPaymentMethodRequest } from '../../models/payment-method.model';

@Injectable({ providedIn: 'root' })
export class PaymentMethodService {

  private apiUrl = `${environment.apiUrl}/payment-methods`;

  constructor(private http: HttpClient) {}

  getAllPaymentMethods(): Observable<PaymentMethod[]> {
    return this.http.get<PaymentMethod[]>(this.apiUrl);
  }

  addPaymentMethod(request: AddPaymentMethodRequest): Observable<PaymentMethod> {
    return this.http.post<PaymentMethod>(this.apiUrl, request);
  }

  setDefault(id: number): Observable<PaymentMethod> {
    return this.http.put<PaymentMethod>(`${this.apiUrl}/${id}/set-default`, {});
  }

deletePaymentMethod(id: number): Observable<string> {
  return this.http.delete(
    `${this.apiUrl}/${id}`,
    { responseType: 'text' }
  );
}
}