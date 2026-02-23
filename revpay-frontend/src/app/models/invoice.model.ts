export interface InvoiceLineItem {
  id: number;
  description: string;
  quantity: number;
  unitPrice: number;
  tax: number;
  totalPrice: number;
}

export interface Invoice {
  id: number;
  businessUserName: string;
  businessUserEmail: string;
  customerName: string;
  customerEmail: string;
  customerAddress: string;
  totalAmount: number;
  paymentTerms: string;
  dueDate: string;
  status: string;
  lineItems: InvoiceLineItem[];
  createdAt: string;
}

export interface CreateInvoiceRequest {
  customerName: string;
  customerEmail: string;
  customerAddress?: string;
  paymentTerms?: string;
  dueDate?: string;
  lineItems: LineItemRequest[];
}

export interface LineItemRequest {
  description: string;
  quantity: number;
  unitPrice: number;
  tax: number;
}