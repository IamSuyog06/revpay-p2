
## Overview

RevPay is a simulated digital payment platform where users can:
- Send and receive money via wallet
- Manage linked debit/credit cards
- Track all transactions with filters and CSV export
- Raise and respond to money requests
- Receive in-app notifications for every action

Business users additionally get:
- Invoice creation and management
- Loan applications with document upload
- Business analytics dashboard

Admins can:
- Approve or reject loan applications
- View all users across the platform

---

## Features

### Personal User
| Feature | Description |
|---|---|
| Register / Login | JWT-based authentication with BCrypt password hashing |
| Wallet | View balance, add money via card, withdraw to bank |
| Send Money | Transfer funds to any user by email, phone, or ID |
| Request Money | Ask another user to pay you; they can accept or decline |
| Transaction History | View all transactions with filters by type, date, amount |
| Export CSV | Download filtered transaction history as CSV |
| Payment Methods | Add, delete, set default debit/credit card |
| Notifications | In-app alerts for every transaction, request, card change |
| Profile | Update info, change password, set/change transaction PIN |

### Business User
Everything above, plus:

| Feature | Description |
|---|---|
| Invoices | Create invoices with line items, send to customers, mark paid |
| Loans | Apply for business loans, upload supporting documents, repay EMI |
| Analytics | Revenue summary, top customers, outstanding invoices |

### Admin
| Feature | Description |
|---|---|
| Dashboard | Stats: total users, pending/approved/rejected loans |
| Loan Management | View all applications, approve or reject with reason |
