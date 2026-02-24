import { Component, OnInit, ChangeDetectionStrategy, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ToastrService } from 'ngx-toastr';
import { NavbarComponent } from '../../shared/components/navbar/navbar';
import { LoanService } from '../../core/services/loan.service';
import { LoanApplication } from '../../models/loan.model';

@Component({
  selector: 'app-loans',
  standalone: true,
  imports: [CommonModule, FormsModule, NavbarComponent],
  templateUrl: './loans.html',
  styleUrl: './loans.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class LoansComponent implements OnInit {

  loans: LoanApplication[] = [];
  selectedLoan: LoanApplication | null = null;
  showApplyForm = false;
  isLoading = true;
  activeFilter = 'ALL';

  applyForm = {
    loanAmount: null as any,
    purpose: '',
    tenure: 12,
    financialInfo: ''
  };

  repayForm = {
    amount: null as any,
    transactionPin: ''
  };

  showRepayForm = false;
  emiPreview: number | null = null;

  filters = ['ALL', 'PENDING', 'APPROVED', 'REJECTED'];

  constructor(
    private loanService: LoanService,
    private toastr: ToastrService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.loadLoans();
  }

  loadLoans(): void {
    this.isLoading = true;
    const obs = this.activeFilter === 'ALL'
      ? this.loanService.getAllLoans()
      : this.loanService.getLoansByStatus(this.activeFilter);

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

  setFilter(filter: string): void {
    this.activeFilter = filter;
    this.selectedLoan = null;
    this.loadLoans();
    this.cdr.markForCheck();
  }

  selectLoan(loan: LoanApplication): void {
    this.selectedLoan = loan;
    this.showApplyForm = false;
    this.showRepayForm = false;
    this.cdr.markForCheck();
  }

  toggleApplyForm(): void {
    this.showApplyForm = !this.showApplyForm;
    this.selectedLoan = null;
    this.emiPreview = null;
    this.cdr.markForCheck();
  }

  // calculate EMI preview as user types - same formula as backend
  calculateEmiPreview(): void {
    if (!this.applyForm.loanAmount || !this.applyForm.tenure) {
      this.emiPreview = null;
      return;
    }
    const P = this.applyForm.loanAmount;
    const r = 10 / 12 / 100; // 10% annual / 12 months / 100
    const n = this.applyForm.tenure;
    this.emiPreview = (P * r * Math.pow(1 + r, n)) / (Math.pow(1 + r, n) - 1);
    this.cdr.markForCheck();
  }

  onApplyLoan(): void {
    if (!this.applyForm.loanAmount || !this.applyForm.purpose || !this.applyForm.tenure) {
      this.toastr.error('Please fill all required fields');
      return;
    }

    this.loanService.applyForLoan(this.applyForm).subscribe({
      next: () => {
        this.toastr.success('Loan application submitted');
        this.showApplyForm = false;
        this.applyForm = { loanAmount: null, purpose: '', tenure: 12, financialInfo: '' };
        this.emiPreview = null;
        this.loadLoans();
        this.cdr.markForCheck();
      },
      error: (err) => this.toastr.error(err.error?.message || 'Failed to apply for loan')
    });
  }

  onMakeRepayment(): void {
    if (!this.selectedLoan || !this.repayForm.amount || !this.repayForm.transactionPin) {
      this.toastr.error('Please fill all fields');
      return;
    }

    this.loanService.makeRepayment(this.selectedLoan.id, this.repayForm).subscribe({
      next: () => {
        this.toastr.success('Repayment successful');
        this.repayForm = { amount: null, transactionPin: '' };
        this.showRepayForm = false;
        // reload loan details
        this.loanService.getLoanById(this.selectedLoan!.id).subscribe(loan => {
          this.selectedLoan = loan;
          this.loadLoans();
          this.cdr.markForCheck();
        });
      },
      error: (err) => this.toastr.error(err.error?.message || 'Repayment failed')
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

  getProgressPercent(loan: LoanApplication): number {
    if (!loan.loanAmount) return 0;
    return Math.min(100, (loan.totalAmountPaid / loan.loanAmount) * 100);
  }

  selectedFile: File | null = null;
uploadingLoanId: number | null = null;

onFileSelected(event: any): void {
  this.selectedFile = event.target.files[0] || null;
  this.cdr.markForCheck();
}

onUploadDocument(loanId: number): void {
  if (!this.selectedFile) {
    this.toastr.error('Please select a file first');
    return;
  }
  this.loanService.uploadDocument(loanId, this.selectedFile).subscribe({
    next: () => {
      this.toastr.success('Document uploaded successfully');
      this.selectedFile = null;
      this.uploadingLoanId = null;
      // reload loan to show updated documentPath
      this.loanService.getLoanById(this.selectedLoan!.id).subscribe(loan => {
        this.selectedLoan = loan;
        this.cdr.markForCheck();
      });
      this.cdr.markForCheck();
    },
    error: (err) => this.toastr.error(err.error?.message || 'Upload failed')
  });
}
}