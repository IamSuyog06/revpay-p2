export interface LoanApplication {
  id: number;
  loanAmount: number;
  purpose: string;
  tenure: number;
  interestRate: number;
  emiAmount: number;
  financialInfo: string;
  status: string;
  totalAmountPaid: number;
  remainingAmount: number;
  repayments: LoanRepayment[];
  createdAt: string;
}

export interface LoanRepayment {
  id: number;
  loanApplicationId: number;
  amountPaid: number;
  paidAt: string;
}

export interface LoanApplicationRequest {
  loanAmount: number;
  purpose: string;
  tenure: number;
  financialInfo: string;
}

export interface LoanRepaymentRequest {
  amount: number;
  transactionPin: string;
}