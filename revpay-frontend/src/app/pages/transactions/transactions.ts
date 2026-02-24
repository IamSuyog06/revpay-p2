import { Component, OnInit, ChangeDetectionStrategy, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ToastrService } from 'ngx-toastr';
import { NavbarComponent } from '../../shared/components/navbar/navbar';
import { TransactionService } from '../../core/services/transaction.service';
import { Transaction, TransactionFilter } from '../../models/transaction.model';
import { environment } from '../../../environments/environment';

@Component({
  selector: 'app-transactions',
  standalone: true,
  imports: [CommonModule, FormsModule, NavbarComponent],
  templateUrl: './transactions.html',
  styleUrl: './transactions.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class TransactionsComponent implements OnInit {

  transactions: Transaction[] = [];
  isLoading = true;
  searchKeyword = '';

  filter: TransactionFilter = {
    type: '',
    status: '',
    startDate: '',
    endDate: '',
    minAmount: undefined,
    maxAmount: undefined
  };

  showFilters = false;

  constructor(
    private transactionService: TransactionService,
    private toastr: ToastrService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.loadTransactions();
  }

  loadTransactions(): void {
    this.isLoading = true;
    this.transactionService.getAllTransactions().subscribe({
      next: (transactions) => {
        this.transactions = transactions;
        this.isLoading = false;
        this.cdr.markForCheck();
      },
      error: () => {
        this.isLoading = false;
        this.toastr.error('Failed to load transactions');
        this.cdr.markForCheck();
      }
    });
  }

  onSearch(): void {
    if (!this.searchKeyword.trim()) {
      this.loadTransactions();
      return;
    }
    this.isLoading = true;
    this.transactionService.searchTransactions(this.searchKeyword).subscribe({
      next: (transactions) => {
        this.transactions = transactions;
        this.isLoading = false;
        this.cdr.markForCheck();
      },
      error: () => {
        this.isLoading = false;
        this.toastr.error('Search failed');
        this.cdr.markForCheck();
      }
    });
  }

  onFilter(): void {
    // build filter object - remove empty values
    const filterPayload: TransactionFilter = {};
    if (this.filter.type) filterPayload.type = this.filter.type;
    if (this.filter.status) filterPayload.status = this.filter.status;
    if (this.filter.startDate) filterPayload.startDate = this.filter.startDate;
    if (this.filter.endDate) filterPayload.endDate = this.filter.endDate;
    if (this.filter.minAmount) filterPayload.minAmount = this.filter.minAmount;
    if (this.filter.maxAmount) filterPayload.maxAmount = this.filter.maxAmount;

    this.isLoading = true;
    this.transactionService.filterTransactions(filterPayload).subscribe({
      next: (transactions) => {
        this.transactions = transactions;
        this.isLoading = false;
        this.cdr.markForCheck();
      },
      error: () => {
        this.isLoading = false;
        this.toastr.error('Filter failed');
        this.cdr.markForCheck();
      }
    });
  }

  onClearFilters(): void {
    this.filter = {
      type: '',
      status: '',
      startDate: '',
      endDate: '',
      minAmount: undefined,
      maxAmount: undefined
    };
    this.searchKeyword = '';
    this.loadTransactions();
  }

  onExportCsv(): void {
    const token = localStorage.getItem('token');
    // open export URL in new tab with token
    const url = `${environment.apiUrl}/transactions/export/csv`;
    fetch(url, {
      headers: { Authorization: `Bearer ${token}` }
    })
    .then(res => res.blob())
    .then(blob => {
      const link = document.createElement('a');
      link.href = URL.createObjectURL(blob);
      link.download = 'transactions.csv';
      link.click();
      this.toastr.success('CSV downloaded');
    })
    .catch(() => this.toastr.error('Export failed'));
  }

  toggleFilters(): void {
    this.showFilters = !this.showFilters;
    this.cdr.markForCheck();
  }

  getAmountColor(type: string): string {
    return (type === 'RECEIVED' || type === 'ADDED_FUNDS') ? 'green' : 'red';
  }

  getAmountSign(type: string): string {
    return (type === 'RECEIVED' || type === 'ADDED_FUNDS') ? '+' : '-';
  }

  getTypeIcon(type: string): string {
    switch (type) {
      case 'SENT': return '↑';
      case 'RECEIVED': return '↓';
      case 'ADDED_FUNDS': return '+';
      case 'WITHDRAWAL': return '↓';
      default: return '•';
    }
  }
}