import { Component, OnInit, ChangeDetectionStrategy, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ToastrService } from 'ngx-toastr';
import { NavbarComponent } from '../../shared/components/navbar/navbar';
import { InvoiceService } from '../../core/services/invoice.service';
import { Invoice, CreateInvoiceRequest, LineItemRequest } from '../../models/invoice.model';

@Component({
  selector: 'app-invoices',
  standalone: true,
  imports: [CommonModule, FormsModule, NavbarComponent],
  templateUrl: './invoices.html',
  styleUrl: './invoices.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class InvoicesComponent implements OnInit {

  invoices: Invoice[] = [];
  selectedInvoice: Invoice | null = null;
  showCreateForm = false;
  isLoading = true;
  activeFilter = 'ALL';

  newInvoice: CreateInvoiceRequest = {
    customerName: '',
    customerEmail: '',
    customerAddress: '',
    paymentTerms: 'Net 30',
    dueDate: '',
    lineItems: []
  };

  newLineItem: LineItemRequest = {
    description: '',
    quantity: 1,
    unitPrice: 0,
    tax: 0
  };

  filters = ['ALL', 'DRAFT', 'SENT', 'PAID', 'OVERDUE', 'CANCELLED'];

  constructor(
    private invoiceService: InvoiceService,
    private toastr: ToastrService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.loadInvoices();
  }

  loadInvoices(): void {
    this.isLoading = true;
    const obs = this.activeFilter === 'ALL'
      ? this.invoiceService.getAllInvoices()
      : this.invoiceService.getInvoicesByStatus(this.activeFilter);

    obs.subscribe({
      next: (invoices) => {
        this.invoices = invoices;
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
    this.selectedInvoice = null;
    this.loadInvoices();
    this.cdr.markForCheck();
  }

  selectInvoice(invoice: Invoice): void {
    this.selectedInvoice = invoice;
    this.showCreateForm = false;
    this.cdr.markForCheck();
  }

  toggleCreateForm(): void {
    this.showCreateForm = !this.showCreateForm;
    this.selectedInvoice = null;
    if (this.showCreateForm) {
      this.resetForm();
    }
    this.cdr.markForCheck();
  }

  resetForm(): void {
    this.newInvoice = {
      customerName: '',
      customerEmail: '',
      customerAddress: '',
      paymentTerms: 'Net 30',
      dueDate: '',
      lineItems: []
    };
    this.newLineItem = { description: '', quantity: null as any, unitPrice: null as any, tax: null as any };
  }

  addLineItem(): void {
    if (!this.newLineItem.description || !this.newLineItem.unitPrice) {
      this.toastr.error('Please fill in description and unit price');
      return;
    }
    this.newInvoice.lineItems.push({ ...this.newLineItem });
    this.newLineItem = { description: '', quantity: null as any, unitPrice: null as any, tax: null as any };
    this.cdr.markForCheck();
  }

  removeLineItem(index: number): void {
    this.newInvoice.lineItems.splice(index, 1);
    this.cdr.markForCheck();
  }

  getLineItemTotal(item: LineItemRequest): number {
    return item.quantity * item.unitPrice * (1 + item.tax / 100);
  }

  getInvoiceTotal(): number {
    return this.newInvoice.lineItems.reduce((sum, item) => sum + this.getLineItemTotal(item), 0);
  }

  onCreateInvoice(): void {
    if (!this.newInvoice.customerName || !this.newInvoice.customerEmail) {
      this.toastr.error('Customer name and email are required');
      return;
    }
    if (this.newInvoice.lineItems.length === 0) {
      this.toastr.error('Please add at least one line item');
      return;
    }

    this.invoiceService.createInvoice(this.newInvoice).subscribe({
      next: () => {
        this.toastr.success('Invoice created successfully');
        this.showCreateForm = false;
        this.loadInvoices();
        this.cdr.markForCheck();
      },
      error: (err) => this.toastr.error(err.error?.message || 'Failed to create invoice')
    });
  }

  onSendInvoice(id: number): void {
    this.invoiceService.sendInvoice(id).subscribe({
      next: (updated) => {
        this.toastr.success('Invoice sent');
        this.selectedInvoice = updated;
        this.loadInvoices();
        this.cdr.markForCheck();
      },
      error: (err) => this.toastr.error(err.error?.message || 'Failed to send invoice')
    });
  }

  onMarkAsPaid(id: number): void {
    this.invoiceService.markAsPaid(id).subscribe({
      next: (updated) => {
        this.toastr.success('Invoice marked as paid');
        this.selectedInvoice = updated;
        this.loadInvoices();
        this.cdr.markForCheck();
      },
      error: (err) => this.toastr.error(err.error?.message || 'Failed to update invoice')
    });
  }

  onCancelInvoice(id: number): void {
    this.invoiceService.cancelInvoice(id).subscribe({
      next: (updated) => {
        this.toastr.success('Invoice cancelled');
        this.selectedInvoice = updated;
        this.loadInvoices();
        this.cdr.markForCheck();
      },
      error: (err) => this.toastr.error(err.error?.message || 'Failed to cancel invoice')
    });
  }

  getStatusClass(status: string): string {
    switch (status) {
      case 'DRAFT': return 'badge-gray';
      case 'SENT': return 'badge-blue';
      case 'PAID': return 'badge-success';
      case 'OVERDUE': return 'badge-danger';
      case 'CANCELLED': return 'badge-dark';
      default: return '';
    }
  }
}