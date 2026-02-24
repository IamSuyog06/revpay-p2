import { Component, OnInit, ChangeDetectionStrategy, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { NavbarComponent } from '../../shared/components/navbar/navbar';
import { WalletService } from '../../core/services/wallet.service';
import { TransactionService } from '../../core/services/transaction.service';
import { AuthService } from '../../core/services/auth.service';
import { Wallet, MoneyRequestResponse } from '../../models/wallet.model';
import { Transaction } from '../../models/transaction.model';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink, NavbarComponent],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class DashboardComponent implements OnInit {

  wallet: Wallet | null = null;
  recentTransactions: Transaction[] = [];
  incomingRequests: MoneyRequestResponse[] = [];
  fullName = '';
  isLoading = true;

  constructor(
    private walletService: WalletService,
    private transactionService: TransactionService,
    private authService: AuthService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.fullName = this.authService.getFullName();
    this.loadDashboardData();
  }

  loadDashboardData(): void {
    // load wallet
    this.walletService.getWallet().subscribe({
      next: (wallet) => {
        this.wallet = wallet;
        this.cdr.markForCheck();
      }
    });

    // load recent transactions - show only last 5
    this.transactionService.getAllTransactions().subscribe({
      next: (transactions) => {
        this.recentTransactions = transactions.slice(0, 5);
        this.isLoading = false;
        this.cdr.markForCheck();
      }
    });

    // load pending incoming requests
    this.walletService.getIncomingRequests().subscribe({
      next: (requests) => {
        this.incomingRequests = requests.filter(r => r.status === 'PENDING');
        this.cdr.markForCheck();
      }
    });
  }

  getTransactionColor(type: string): string {
    switch (type) {
      case 'RECEIVED': return 'green';
      case 'ADDED_FUNDS': return 'green';
      case 'SENT': return 'red';
      case 'WITHDRAWAL': return 'red';
      default: return 'gray';
    }
  }

  getTransactionSign(type: string): string {
    return (type === 'RECEIVED' || type === 'ADDED_FUNDS') ? '+' : '-';
  }

  isBusinessUser(): boolean {
    return this.authService.isBusinessUser();
  }
}