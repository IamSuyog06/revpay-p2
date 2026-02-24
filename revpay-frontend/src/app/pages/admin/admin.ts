import { Component, OnInit, ChangeDetectionStrategy, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ToastrService } from 'ngx-toastr';
import { Router } from '@angular/router';
import { AdminService, AdminStats, AdminUser } from '../../core/services/admin.service';
import { AuthService } from '../../core/services/auth.service';
import { LoanApplication } from '../../models/loan.model';
import { environment } from '../../../environments/environment';

@Component({
  selector: 'app-admin',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './admin.html',
  styleUrl: './admin.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class AdminComponent implements OnInit {

  stats: AdminStats | null = null;
  loans: LoanApplication[] = [];
  users: AdminUser[] = [];
  activeTab: string = 'dashboard';
  activeFilter = 'ALL';
  isLoading = true;
  rejectReason = '';
  rejectingLoanId: number | null = null;

  filters = ['ALL', 'PENDING', 'APPROVED', 'REJECTED'];

  constructor(
    private adminService: AdminService,
    private authService: AuthService,
    private toastr: ToastrService,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    // redirect non-admins
    if (this.authService.getRole() !== 'ADMIN') {
      this.router.navigate(['/dashboard']);
      return;
    }
    this.loadStats();
    this.loadLoans();
  }

  loadStats(): void {
    this.adminService.getDashboardStats().subscribe({
      next: (stats) => {
        this.stats = stats;
        this.cdr.markForCheck();
      }
    });
  }

  loadLoans(): void {
    this.isLoading = true;
    const obs = this.activeFilter === 'ALL'
      ? this.adminService.getAllLoans()
      : this.adminService.getLoansByStatus(this.activeFilter);

    obs.subscribe({
      next: (loans) => {
        this.loans = loans;
        this.isLoading = false;
        this.cdr.markForCheck();
      },
      error: () => {
        this.isLoading = false;
        this.cdr.markForCheck();
      }
    });
  }

  loadUsers(): void {
    this.isLoading = true;
    this.adminService.getAllUsers().subscribe({
      next: (users) => {
        this.users = users;
        this.isLoading = false;
        this.cdr.markForCheck();
      }
    });
  }

  setTab(tab: any): void {
    this.activeTab = tab;
    if (tab === 'loans') this.loadLoans();
    if (tab === 'users') this.loadUsers();
    this.cdr.markForCheck();
  }

  setFilter(filter: string): void {
    this.activeFilter = filter;
    this.loadLoans();
    this.cdr.markForCheck();
  }

  onApprove(id: number): void {
    this.adminService.approveLoan(id).subscribe({
      next: () => {
        this.toastr.success('Loan approved successfully');
        this.loadLoans();
        this.loadStats();
        this.cdr.markForCheck();
      },
      error: (err) => this.toastr.error(err.error?.message || 'Failed to approve loan')
    });
  }

  openRejectModal(id: number): void {
    this.rejectingLoanId = id;
    this.rejectReason = '';
    this.cdr.markForCheck();
  }

  closeRejectModal(): void {
    this.rejectingLoanId = null;
    this.rejectReason = '';
    this.cdr.markForCheck();
  }

  onReject(): void {
    if (!this.rejectingLoanId) return;
    this.adminService.rejectLoan(this.rejectingLoanId, this.rejectReason).subscribe({
      next: () => {
        this.toastr.success('Loan rejected');
        this.closeRejectModal();
        this.loadLoans();
        this.loadStats();
        this.cdr.markForCheck();
      },
      error: (err) => this.toastr.error(err.error?.message || 'Failed to reject loan')
    });
  }

  getStatusClass(status: string): string {
    switch (status) {
      case 'PENDING': return 'badge-pending';
      case 'APPROVED': return 'badge-success';
      case 'REJECTED': return 'badge-danger';
      default: return '';
    }
  }

  getRoleClass(role: string): string {
    switch (role) {
      case 'ADMIN': return 'badge-purple';
      case 'BUSINESS': return 'badge-blue';
      case 'PERSONAL': return 'badge-gray';
      default: return '';
    }
  }

viewDocument(loanId: number): void {
  const token = localStorage.getItem('token');
  fetch(`${environment.apiUrl}/loans/${loanId}/download-document`, {
    headers: { Authorization: `Bearer ${token}` }
  })
  .then(res => {
    if (!res.ok) throw new Error('Failed to load document');
    return res.blob();
  })
  .then(blob => {
    const url = URL.createObjectURL(blob);
    window.open(url, '_blank');
  })
  .catch(() => this.toastr.error('Failed to load document'));
}

  logout(): void {
    this.authService.logout();
  }
}