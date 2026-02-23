import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { User } from '../../models/user.model';

@Injectable({ providedIn: 'root' })
export class UserService {

  private apiUrl = `${environment.apiUrl}/user`;

  constructor(private http: HttpClient) {}

  getProfile(): Observable<User> {
    return this.http.get<User>(`${this.apiUrl}/profile`);
  }

  updateProfile(data: { fullName: string; phone: string }): Observable<User> {
    return this.http.put<User>(`${this.apiUrl}/profile`, data);
  }

  changePassword(data: { currentPassword: string; newPassword: string }): Observable<string> {
    return this.http.put<string>(`${this.apiUrl}/change-password`, data);
  }

  setTransactionPin(data: { pin: string; currentPin?: string }): Observable<string> {
    return this.http.put<string>(`${this.apiUrl}/transaction-pin`, data);
  }
}