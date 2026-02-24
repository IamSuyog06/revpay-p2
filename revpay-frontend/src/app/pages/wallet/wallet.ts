import { Component, OnInit, ChangeDetectionStrategy, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ToastrService } from 'ngx-toastr';
import { NavbarComponent } from '../../shared/components/navbar/navbar';
import { WalletService } from '../../core/services/wallet.service';
import { PaymentMethodService } from '../../core/services/payment-method.service';
import { Wallet, MoneyRequestResponse } from '../../models/wallet.model';
import { PaymentMethod } from '../../models/payment-method.model';
import { RouterLink } from '@angular/router';
import { Transaction } from '../../models/transaction.model';

@Component({
  selector: 'app-wallet',
  standalone: true,
  imports: [CommonModule, FormsModule, NavbarComponent, RouterLink],
  templateUrl: './wallet.html',
  styleUrl: './wallet.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class WalletComponent implements OnInit {

  wallet: Wallet | null = null;
  paymentMethods: PaymentMethod[] = [];
  incomingRequests: MoneyRequestResponse[] = [];
  outgoingRequests: MoneyRequestResponse[] = [];

  // active tab
  activeTab: 'add' | 'send' | 'withdraw' | 'request' | 'incoming' | 'outgoing' = 'send';

  // form models
  addMoneyForm = { amount: null as any, paymentMethodId: null as any };
  sendMoneyForm = { recipientIdentifier: '', amount: null as any, note: '', transactionPin: '' };
  withdrawForm = { amount: null as any, bankAccountNumber: '', transactionPin: '' };
  requestMoneyForm = { recipientIdentifier: '', amount: null as any, purpose: '' };
  acceptPinMap: { [key: number]: string } = {};

  isLoading = false;

  constructor(
    private walletService: WalletService,
    private paymentMethodService: PaymentMethodService,
    private toastr: ToastrService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.loadWallet();
    this.loadPaymentMethods();
    this.loadRequests();
  }

  loadWallet(): void {
    this.walletService.getWallet().subscribe({
      next: (wallet) => {
        this.wallet = wallet;
        this.cdr.markForCheck();
      }
    });
  }

  loadPaymentMethods(): void {
    this.paymentMethodService.getAllPaymentMethods().subscribe({
      next: (methods) => {
        this.paymentMethods = methods;
        this.cdr.markForCheck();
      }
    });
  }

  loadRequests(): void {
    this.walletService.getIncomingRequests().subscribe({
      next: (requests) => {
        this.incomingRequests = requests;
        this.cdr.markForCheck();
      }
    });

    this.walletService.getOutgoingRequests().subscribe({
      next: (requests) => {
        this.outgoingRequests = requests;
        this.cdr.markForCheck();
      }
    });
  }

  setTab(tab: any): void {
    this.activeTab = tab;
    this.cdr.markForCheck();
  }

  onAddMoney(): void {
    if (!this.addMoneyForm.amount || !this.addMoneyForm.paymentMethodId) {
      this.toastr.error('Please fill all fields');
      return;
    }
    this.isLoading = true;
    this.walletService.addMoney(this.addMoneyForm).subscribe({
      next: () => {
        this.isLoading = false;
        this.toastr.success('Money added successfully');
        this.addMoneyForm = { amount: null, paymentMethodId: null };
        this.loadWallet();
        this.cdr.markForCheck();
      },
      error: (err) => {
        this.isLoading = false;
        this.toastr.error(err.error?.message || 'Failed to add money');
        this.cdr.markForCheck();
      }
    });
  }

  onSendMoney(): void {
    if (!this.sendMoneyForm.recipientIdentifier || !this.sendMoneyForm.amount || !this.sendMoneyForm.transactionPin) {
      this.toastr.error('Please fill all required fields');
      return;
    }
    this.isLoading = true;
    this.walletService.sendMoney(this.sendMoneyForm).subscribe({
      next: () => {
        this.isLoading = false;
        this.toastr.success('Money sent successfully');
        this.sendMoneyForm = { recipientIdentifier: '', amount: null, note: '', transactionPin: '' };
        this.loadWallet();
        this.cdr.markForCheck();
      },
      error: (err) => {
        this.isLoading = false;
        this.toastr.error(err.error?.message || 'Failed to send money');
        this.cdr.markForCheck();
      }
    });
  }

  onWithdraw(): void {
    if (!this.withdrawForm.amount || !this.withdrawForm.bankAccountNumber || !this.withdrawForm.transactionPin) {
      this.toastr.error('Please fill all fields');
      return;
    }
    this.isLoading = true;
    this.walletService.withdraw(this.withdrawForm).subscribe({
      next: () => {
        this.isLoading = false;
        this.toastr.success('Withdrawal successful');
        this.withdrawForm = { amount: null, bankAccountNumber: '', transactionPin: '' };
        this.loadWallet();
        this.cdr.markForCheck();
      },
      error: (err) => {
        this.isLoading = false;
        this.toastr.error(err.error?.message || 'Failed to withdraw');
        this.cdr.markForCheck();
      }
    });
  }

  onRequestMoney(): void {
    if (!this.requestMoneyForm.recipientIdentifier || !this.requestMoneyForm.amount) {
      this.toastr.error('Please fill all required fields');
      return;
    }
    this.isLoading = true;
    this.walletService.requestMoney(this.requestMoneyForm).subscribe({
      next: () => {
        this.isLoading = false;
        this.toastr.success('Money request sent');
        this.requestMoneyForm = { recipientIdentifier: '', amount: null, purpose: '' };
        this.loadRequests();
        this.cdr.markForCheck();
      },
      error: (err) => {
        this.isLoading = false;
        this.toastr.error(err.error?.message || 'Failed to send request');
        this.cdr.markForCheck();
      }
    });
  }

  onAcceptRequest(requestId: number): void {
    const pin = this.acceptPinMap[requestId];
    if (!pin) {
      this.toastr.error('Please enter your transaction PIN');
      return;
    }
    this.walletService.acceptRequest(requestId, pin).subscribe({
      next: () => {
        this.toastr.success('Request accepted');
        this.loadWallet();
        this.loadRequests();
        this.cdr.markForCheck();
      },
      error: (err) => {
        this.toastr.error(err.error?.message || 'Failed to accept request');
      }
    });
  }

  onDeclineRequest(requestId: number): void {
    this.walletService.declineRequest(requestId).subscribe({
      next: () => {
        this.toastr.success('Request declined');
        this.loadRequests();
        this.cdr.markForCheck();
      },
      error: (err) => {
        this.toastr.error(err.error?.message || 'Failed to decline');
      }
    });
  }

  onCancelRequest(requestId: number): void {
    this.walletService.cancelRequest(requestId).subscribe({
      next: () => {
        this.toastr.success('Request cancelled');
        this.loadRequests();
        this.cdr.markForCheck();
      },
      error: (err) => {
        this.toastr.error(err.error?.message || 'Failed to cancel');
      }
    });
  }

  getStatusClass(status: string): string {
    switch (status) {
      case 'PENDING': return 'badge-pending';
      case 'ACCEPTED': return 'badge-success';
      case 'DECLINED': return 'badge-danger';
      case 'CANCELLED': return 'badge-gray';
      default: return '';
    }
  }
}