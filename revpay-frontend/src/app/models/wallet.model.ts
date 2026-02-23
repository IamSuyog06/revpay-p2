export interface Wallet {
  id: number;
  balance: number;
  ownerName: string;
  ownerEmail: string;
}

export interface SendMoneyRequest {
  recipientIdentifier: string;
  amount: number;
  note?: string;
  transactionPin: string;
}

export interface AddMoneyRequest {
  amount: number;
  paymentMethodId: number;
}

export interface WithdrawRequest {
  amount: number;
  bankAccountNumber: string;
  transactionPin: string;
}

export interface MoneyRequestDto {
  recipientIdentifier: string;
  amount: number;
  purpose?: string;
}

export interface MoneyRequestResponse {
  id: number;
  requesterName: string;
  requesterEmail: string;
  recipientName: string;
  recipientEmail: string;
  amount: number;
  purpose: string;
  status: string;
  createdAt: string;
}