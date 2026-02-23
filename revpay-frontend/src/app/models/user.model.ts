export interface User {
  id: number;
  fullName: string;
  email: string;
  phone: string;
  role: string;
  isVerified: boolean;
  isActive: boolean;
  hasTransactionPin: boolean;
  createdAt: string;
}

export interface AuthResponse {
  token: string;
  role: string;
  fullName: string;
  email: string;
}

export interface RegisterRequest {
  fullName: string;
  email: string;
  phone: string;
  password: string;
  securityQuestion: string;
  securityAnswer: string;
  isBusiness: boolean;
  businessName?: string;
  businessType?: string;
  taxId?: string;
  businessAddress?: string;
}

export interface LoginRequest {
  emailOrPhone: string;
  password: string;
}