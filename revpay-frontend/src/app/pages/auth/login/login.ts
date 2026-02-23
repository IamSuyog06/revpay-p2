import { Component, ChangeDetectionStrategy, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { ToastrService } from 'ngx-toastr';
import { AuthService } from '../../../core/services/auth.service';
import { LoginRequest } from '../../../models/user.model';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './login.html',
  styleUrl: './login.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class LoginComponent {

  loginData: LoginRequest = {
    emailOrPhone: '',
    password: ''
  };

  isLoading = false;
  showPassword = false;

  constructor(
    private authService: AuthService,
    private router: Router,
    private toastr: ToastrService,
    private cdr: ChangeDetectorRef
  ) {
    if (this.authService.isLoggedIn()) {
      this.router.navigate(['/dashboard']);
    }
  }

  onLogin(): void {
    if (!this.loginData.emailOrPhone || !this.loginData.password) {
      this.toastr.error('Please fill in all fields');
      return;
    }

    this.isLoading = true;
    this.cdr.markForCheck();

    this.authService.login(this.loginData).subscribe({
      next: (response) => {
        this.isLoading = false;
        this.cdr.markForCheck();
        this.toastr.success(`Welcome back, ${response.fullName}!`);
        this.router.navigate(['/dashboard']);
      },
      error: (error) => {
        this.isLoading = false;
        this.cdr.markForCheck();
        this.toastr.error(error.error?.message || 'Login failed. Please try again.');
      }
    });
  }

  togglePassword(): void {
    this.showPassword = !this.showPassword;
    this.cdr.markForCheck();
  }
}