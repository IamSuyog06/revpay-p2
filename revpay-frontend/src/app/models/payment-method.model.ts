export interface PaymentMethod {
  id: number;
  cardHolderName: string;
  lastFourDigits: string;
  expiryMonth: string;
  expiryYear: string;
  cardType: string;
  billingAddress: string;
  isDefault: boolean;
  createdAt: string;
}

export interface AddPaymentMethodRequest {
  cardHolderName: string;
  cardNumber: string;
  expiryMonth: string;
  expiryYear: string;
  cvv: string;
  cardType: string;
  billingAddress: string;
}