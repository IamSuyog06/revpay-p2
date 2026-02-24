import { Component, OnInit, ChangeDetectionStrategy, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ToastrService } from 'ngx-toastr';
import { NavbarComponent } from '../../shared/components/navbar/navbar';
import { UserService } from '../../core/services/user.service';
import { User } from '../../models/user.model';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule, FormsModule, NavbarComponent],
  templateUrl: './profile.html',
  styleUrl: './profile.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProfileComponent implements OnInit {

  user: User | null = null;
  activeTab: 'profile' | 'password' | 'pin' = 'profile';

  profileForm = { fullName: '', phone: '' };
  passwordForm = { currentPassword: '', newPassword: '', confirmPassword: '' };
  pinForm = { pin: '', confirmPin: '', currentPin: '' };

  isLoading = false;

  constructor(
    private userService: UserService,
    private toastr: ToastrService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.loadProfile();
  }

  loadProfile(): void {
    this.userService.getProfile().subscribe({
      next: (user) => {
        this.user = user;
        this.profileForm.fullName = user.fullName;
        this.profileForm.phone = user.phone;
        this.cdr.markForCheck();
      }
    });
  }

  setTab(tab: any): void {
    this.activeTab = tab;
    this.cdr.markForCheck();
  }

  onUpdateProfile(): void {
    if (!this.profileForm.fullName || !this.profileForm.phone) {
      this.toastr.error('Please fill all fields');
      return;
    }
    this.isLoading = true;
    this.userService.updateProfile(this.profileForm).subscribe({
      next: (user) => {
        this.isLoading = false;
        this.user = user;
        this.toastr.success('Profile updated successfully');
        // update name in localStorage too
        localStorage.setItem('fullName', user.fullName);
        this.cdr.markForCheck();
      },
      error: (err) => {
        this.isLoading = false;
        this.toastr.error(err.error?.message || 'Failed to update profile');
        this.cdr.markForCheck();
      }
    });
  }

  onChangePassword(): void {
    if (!this.passwordForm.currentPassword || !this.passwordForm.newPassword) {
      this.toastr.error('Please fill all fields');
      return;
    }
    if (this.passwordForm.newPassword !== this.passwordForm.confirmPassword) {
      this.toastr.error('Passwords do not match');
      return;
    }
    if (this.passwordForm.newPassword.length < 6) {
      this.toastr.error('Password must be at least 6 characters');
      return;
    }
    this.isLoading = true;
    this.userService.changePassword({
      currentPassword: this.passwordForm.currentPassword,
      newPassword: this.passwordForm.newPassword
    }).subscribe({
      next: () => {
        this.isLoading = false;
        this.toastr.success('Password changed successfully');
        this.passwordForm = { currentPassword: '', newPassword: '', confirmPassword: '' };
        this.cdr.markForCheck();
      },
      error: (err) => {
        this.isLoading = false;
        this.toastr.error(err.error?.message || 'Failed to change password');
        this.cdr.markForCheck();
      }
    });
  }

  onSetPin(): void {
    if (!this.pinForm.pin) {
      this.toastr.error('Please enter a PIN');
      return;
    }
    if (this.pinForm.pin.length !== 4) {
      this.toastr.error('PIN must be exactly 4 digits');
      return;
    }
    if (this.pinForm.pin !== this.pinForm.confirmPin) {
      this.toastr.error('PINs do not match');
      return;
    }
    this.isLoading = true;
    const payload: any = { pin: this.pinForm.pin };
    if (this.user?.hasTransactionPin) {
      payload.currentPin = this.pinForm.currentPin;
    }
    this.userService.setTransactionPin(payload).subscribe({
      next: () => {
        this.isLoading = false;
        this.toastr.success('Transaction PIN set successfully');
        this.pinForm = { pin: '', confirmPin: '', currentPin: '' };
        this.loadProfile();
        this.cdr.markForCheck();
      },
      error: (err) => {
        this.isLoading = false;
        this.toastr.error(err.error?.message || 'Failed to set PIN');
        this.cdr.markForCheck();
      }
    });
  }
}