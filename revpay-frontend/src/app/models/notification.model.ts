export interface Notification {
  id: number;
  message: string;
  type: string;
  isRead: boolean;
  createdAt: string;
}

export interface NotificationPreference {
  id: number;
  transactionAlerts: boolean;
  moneyRequestAlerts: boolean;
  cardChangeAlerts: boolean;
  lowBalanceAlerts: boolean;
  invoiceAlerts: boolean;
  loanAlerts: boolean;
}