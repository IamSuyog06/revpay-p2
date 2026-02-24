import { Component, OnInit, ChangeDetectionStrategy, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ToastrService } from 'ngx-toastr';
import { NavbarComponent } from '../../shared/components/navbar/navbar';
import { PaymentMethodService } from '../../core/services/payment-method.service';
import { PaymentMethod, AddPaymentMethodRequest } from '../../models/payment-method.model';

@Component({
  selector: 'app-payment-methods',
  standalone: true,
  imports: [CommonModule, FormsModule, NavbarComponent],
  templateUrl: './payment-methods.html',
  styleUrl: './payment-methods.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class PaymentMethodsComponent implements OnInit {

  paymentMethods: PaymentMethod[] = [];
  showAddForm = false;
  isLoading = false;

  newCard: AddPaymentMethodRequest = {
    cardHolderName: '',
    cardNumber: '',
    expiryMonth: '',
    expiryYear: '',
    cvv: '',
    cardType: 'CREDIT',
    billingAddress: ''
  };

  constructor(
    private paymentMethodService: PaymentMethodService,
    private toastr: ToastrService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.loadPaymentMethods();
  }

  loadPaymentMethods(): void {
    this.paymentMethodService.getAllPaymentMethods().subscribe({
      next: (methods) => {
        this.paymentMethods = methods;
        this.cdr.markForCheck();
      }
    });
  }

  toggleAddForm(): void {
    this.showAddForm = !this.showAddForm;
    this.cdr.markForCheck();
  }

  onAddCard(): void {
    if (!this.newCard.cardHolderName || !this.newCard.cardNumber ||
        !this.newCard.expiryMonth || !this.newCard.expiryYear || !this.newCard.cvv) {
      this.toastr.error('Please fill all required fields');
      return;
    }

    if (this.newCard.cardNumber.length < 16) {
      this.toastr.error('Card number must be 16 digits');
      return;
    }

    this.isLoading = true;
    this.paymentMethodService.addPaymentMethod(this.newCard).subscribe({
      next: () => {
        this.isLoading = false;
        this.toastr.success('Card added successfully');
        this.showAddForm = false;
        this.newCard = {
          cardHolderName: '',
          cardNumber: '',
          expiryMonth: '',
          expiryYear: '',
          cvv: '',
          cardType: 'CREDIT',
          billingAddress: ''
        };
        this.loadPaymentMethods();
        this.cdr.markForCheck();
      },
      error: (err) => {
        this.isLoading = false;
        this.toastr.error(err.error?.message || 'Failed to add card');
        this.cdr.markForCheck();
      }
    });
  }

  onSetDefault(id: number): void {
    this.paymentMethodService.setDefault(id).subscribe({
      next: () => {
        this.toastr.success('Default card updated');
        this.loadPaymentMethods();
      },
      error: () => this.toastr.error('Failed to update default card')
    });
  }

  onDeleteCard(id: number): void {
    if (!confirm('Are you sure you want to delete this card?')) return;
    this.paymentMethodService.deletePaymentMethod(id).subscribe({
      next: () => {
        this.toastr.success('Card deleted');
        this.loadPaymentMethods();
      },
      error: () => this.toastr.error('Failed to delete card')
    });
  }

  getCardIcon(type: string): string {
    return type === 'CREDIT' ? '💳' : '🏦';
  }
}