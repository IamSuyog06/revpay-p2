export interface Transaction {
  id: number;
  senderName: string;
  senderEmail: string;
  receiverName: string;
  receiverEmail: string;
  amount: number;
  type: string;
  status: string;
  note: string;
  createdAt: string;
}

export interface TransactionFilter {
  type?: string;
  status?: string;
  startDate?: string;
  endDate?: string;
  minAmount?: number;
  maxAmount?: number;
}