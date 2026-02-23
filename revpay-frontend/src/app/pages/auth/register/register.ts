import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { ToastrService } from 'ngx-toastr';
import { AuthService } from '../../../core/services/auth.service';
import { RegisterRequest } from '../../../models/user.model';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './register.html',
  styleUrls: ['./register.scss']
})
export class RegisterComponent {

  registerData: RegisterRequest = {
    fullName: '',
    email: '',
    phone: '',
    password: '',
    securityQuestion: '',
    securityAnswer: '',
    isBusiness: false,
    businessName: '',
    businessType: '',
    taxId: '',
    businessAddress: ''
  };

  confirmPassword = '';
  isLoading = false;
  showPassword = false;
  currentStep = 1;

  securityQuestions = [
    'What is your pet\'s name?',
    'What is your mother\'s maiden name?',
    'What city were you born in?',
    'What was the name of your first school?',
    'What is your favorite movie?'
  ];

  businessTypes = [
    'Retail',
    'Technology',
    'Healthcare',
    'Education',
    'Finance',
    'Food & Beverage',
    'Manufacturing',
    'Other'
  ];

  constructor(
    private authService: AuthService,
    private router: Router,
    private toastr: ToastrService
  ) {}

  // toggle between personal and business
  setAccountType(isBusiness: boolean): void {
    this.registerData.isBusiness = isBusiness;
  }

  nextStep(): void {
    if (!this.validateStep1()) return;
    this.currentStep = 2;
  }

  prevStep(): void {
    this.currentStep = 1;
  }

  validateStep1(): boolean {
    if (!this.registerData.fullName) {
      this.toastr.error('Full name is required');
      return false;
    }
    if (!this.registerData.email) {
      this.toastr.error('Email is required');
      return false;
    }
    if (!this.registerData.phone) {
      this.toastr.error('Phone number is required');
      return false;
    }
    if (!this.registerData.password || this.registerData.password.length < 6) {
      this.toastr.error('Password must be at least 6 characters');
      return false;
    }
    if (this.registerData.password !== this.confirmPassword) {
      this.toastr.error('Passwords do not match');
      return false;
    }
    return true;
  }

  validateStep2(): boolean {
    if (!this.registerData.securityQuestion) {
      this.toastr.error('Please select a security question');
      return false;
    }
    if (!this.registerData.securityAnswer) {
      this.toastr.error('Security answer is required');
      return false;
    }
    if (this.registerData.isBusiness) {
      if (!this.registerData.businessName) {
        this.toastr.error('Business name is required');
        return false;
      }
      if (!this.registerData.businessType) {
        this.toastr.error('Business type is required');
        return false;
      }
      if (!this.registerData.taxId) {
        this.toastr.error('Tax ID is required');
        return false;
      }
      if (!this.registerData.businessAddress) {
        this.toastr.error('Business address is required');
        return false;
      }
    }
    return true;
  }

  onRegister(): void {
    if (!this.validateStep2()) return;

    this.isLoading = true;

    this.authService.register(this.registerData).subscribe({
      next: (response) => {
        this.isLoading = false;
        this.toastr.success('Account created successfully!');
        this.router.navigate(['/dashboard']);
      },
      error: (error) => {
        this.isLoading = false;
        this.toastr.error(error.error?.message || 'Registration failed. Please try again.');
      }
    });
  }

  togglePassword(): void {
    this.showPassword = !this.showPassword;
  }
}