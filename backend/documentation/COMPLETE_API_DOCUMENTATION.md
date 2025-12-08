# Personal Investment Portfolio Tracker - Complete API Documentation

**Version:** 1.0
**Last Updated:** December 2025
**Total Endpoints:** 30

---

## Table of Contents

1. [Authentication Endpoints (7)](#authentication-endpoints)
2. [Portfolio Management Endpoints (3)](#portfolio-management-endpoints)
3. [Watchlist Endpoints (4)](#watchlist-endpoints)
4. [Transaction Endpoints (5)](#transaction-endpoints)
5. [Dashboard & Analytics Endpoints (6)](#dashboard--analytics-endpoints)
6. [Earnings Calendar Endpoints (2)](#earnings-calendar-endpoints)
7. [Price Alerts Endpoints (3)](#price-alerts-endpoints)
8. [Password Reset Endpoints (3)](#password-reset-endpoints)
9. [Error Handling](#error-handling)
10. [Authentication](#authentication)

---

## Authentication Endpoints

### 1. User Registration
**Endpoint:** `POST /api/auth/register`
**Authentication:** None
**Description:** Create a new user account

**Request Body:**
```json
{
  "username": "john_doe",
  "email": "john@example.com",
  "fullName": "John Doe",
  "password": "SecurePass123!"
}
```

**Response (201 Created):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "userId": "550e8400-e29b-41d4-a716-446655440000",
  "email": "john@example.com",
  "expiresAt": "2025-12-08T12:00:00"
}
```

**Error Responses:**
- `400 Bad Request` - Username or email already exists, or invalid password format
- `400 Bad Request` - Missing required fields

---

### 2. User Login
**Endpoint:** `POST /api/auth/login`
**Authentication:** None
**Description:** Authenticate user and receive JWT token

**Request Body:**
```json
{
  "email": "john@example.com",
  "password": "SecurePass123!"
}
```

**Response (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "username": "john_doe",
  "fullName": "John Doe",
  "email": "john@example.com",
  "roles": ["ROLE_USER"]
}
```

**Error Responses:**
- `400 Bad Request` - User not found
- `401 Unauthorized` - Invalid password

---

### 3. Get Current User Profile
**Endpoint:** `GET /api/auth/me`
**Authentication:** Required (Bearer Token)
**Description:** Retrieve authenticated user's profile information

**Headers:**
```
Authorization: Bearer {token}
```

**Response (200 OK):**
```json
{
  "userId": "550e8400-e29b-41d4-a716-446655440000",
  "username": "john_doe",
  "email": "john@example.com",
  "createdAt": "2025-12-01T10:00:00"
}
```

**Error Responses:**
- `401 Unauthorized` - Invalid or missing token
- `404 Not Found` - User not found

---

### 4. Refresh JWT Token
**Endpoint:** `POST /api/auth/refresh`
**Authentication:** None
**Description:** Generate a new access token using refresh token

**Request Body:**
```json
{
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**Response (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "userId": "550e8400-e29b-41d4-a716-446655440000",
  "email": "john@example.com",
  "expiresAt": "2025-12-09T12:00:00"
}
```

**Error Responses:**
- `400 Bad Request` - Invalid or expired refresh token

---

### 5. Verify Token Validity
**Endpoint:** `GET /api/auth/verify`
**Authentication:** None
**Description:** Check if a token is valid and not expired

**Headers:**
```
Authorization: Bearer {token}
```

**Response (200 OK):**
```json
{
  "valid": true
}
```

**Response (200 OK - Invalid Token):**
```json
{
  "valid": false
}
```

---

### 6. Logout
**Endpoint:** `POST /api/auth/logout`
**Authentication:** Required (Bearer Token)
**Description:** Logout user (client-side token discard recommended)

**Headers:**
```
Authorization: Bearer {token}
```

**Response (200 OK):**
```json
{
  "message": "Logout successful"
}
```

**Error Responses:**
- `401 Unauthorized` - Invalid or missing token

---

### 7. Change Password (Authenticated)
**Endpoint:** `POST /api/auth/change-password`
**Authentication:** Required (Bearer Token)
**Description:** Change password for authenticated user

**Headers:**
```
Authorization: Bearer {token}
```

**Request Body:**
```json
{
  "currentPassword": "OldPass123!",
  "newPassword": "NewPass456!"
}
```

**Response (200 OK):**
```json
{
  "message": "Password changed successfully"
}
```

**Error Responses:**
- `400 Bad Request` - Current password is incorrect
- `400 Bad Request` - New password doesn't meet requirements (min 8 chars)
- `401 Unauthorized` - Invalid or missing token

---

## Password Reset Endpoints

### 8. Request Password Reset
**Endpoint:** `POST /api/auth/forgot-password`
**Authentication:** None
**Description:** Request password reset token for account recovery

**Request Body:**
```json
{
  "email": "john@example.com"
}
```

**Response (200 OK):**
```json
{
  "message": "Password reset token generated. Use this token to reset your password.",
  "resetToken": "550e8400-e29b-41d4-a716-446655440000",
  "email": "john@example.com"
}
```

**Note:** In production, this token should be sent via email instead of returned in response.

**Error Responses:**
- `404 Not Found` - User not found with given email

---

### 9. Reset Password with Token
**Endpoint:** `POST /api/auth/reset-password`
**Authentication:** None
**Description:** Reset password using valid reset token

**Request Body:**
```json
{
  "resetToken": "550e8400-e29b-41d4-a716-446655440000",
  "newPassword": "NewPass456!"
}
```

**Response (200 OK):**
```json
{
  "message": "Password reset successfully"
}
```

**Error Responses:**
- `400 Bad Request` - Invalid or expired reset token
- `400 Bad Request` - New password doesn't meet requirements (min 8 chars)

---

### 10. Verify Reset Token
**Endpoint:** `GET /api/auth/verify-reset-token/{token}`
**Authentication:** None
**Description:** Check if a password reset token is valid

**Path Parameters:**
- `token` (string) - The reset token to verify

**Response (200 OK):**
```json
{
  "valid": true
}
```

**Response (200 OK - Invalid Token):**
```json
{
  "valid": false
}
```

---

## Portfolio Management Endpoints

### 11. Get Portfolio Overview
**Endpoint:** `GET /api/portfolio/overview/account/{accountId}`
**Authentication:** Required (Bearer Token)
**Description:** Get portfolio overview for a specific account

**Path Parameters:**
- `accountId` (UUID) - The account ID

**Headers:**
```
Authorization: Bearer {token}
```

**Response (200 OK):**
```json
{
  "accountId": "550e8400-e29b-41d4-a716-446655440000",
  "accountName": "My Primary Account",
  "totalPortfolioValue": 125000.00,
  "totalCostBasis": 100000.00,
  "cashBalance": 5000.00,
  "holdingCount": 15,
  "lastUpdated": "2025-12-07T12:00:00"
}
```

**Error Responses:**
- `404 Not Found` - Account not found
- `401 Unauthorized` - Invalid or missing token

---

### 12. Get Portfolio Summary
**Endpoint:** `GET /api/portfolio/summary/account/{accountId}`
**Authentication:** Required (Bearer Token)
**Description:** Get account summary with key metrics

**Path Parameters:**
- `accountId` (UUID) - The account ID

**Response (200 OK):**
```json
{
  "accountId": "550e8400-e29b-41d4-a716-446655440000",
  "accountName": "My Primary Account",
  "totalValue": 125000.00,
  "totalCostBasis": 100000.00,
  "unrealizedGain": 25000.00,
  "realizedGain": 5000.00,
  "cashBalance": 5000.00,
  "holdingCount": 15
}
```

**Error Responses:**
- `404 Not Found` - Account not found
- `401 Unauthorized` - Invalid or missing token

---

### 13. Get Allocation Breakdown
**Endpoint:** `GET /api/portfolio/allocations/account/{accountId}`
**Authentication:** Required (Bearer Token)
**Description:** Get asset allocation breakdown by sector/industry

**Path Parameters:**
- `accountId` (UUID) - The account ID

**Response (200 OK):**
```json
[
  {
    "sector": "Technology",
    "value": 45000.00,
    "percentage": 36.0,
    "holdingCount": 5
  },
  {
    "sector": "Healthcare",
    "value": 30000.00,
    "percentage": 24.0,
    "holdingCount": 3
  },
  {
    "sector": "Finance",
    "value": 25000.00,
    "percentage": 20.0,
    "holdingCount": 3
  },
  {
    "sector": "Other",
    "value": 25000.00,
    "percentage": 20.0,
    "holdingCount": 4
  }
]
```

**Error Responses:**
- `404 Not Found` - Account not found
- `401 Unauthorized` - Invalid or missing token

---

## Watchlist Endpoints

### 14. Get User's Watchlist
**Endpoint:** `GET /api/watchlist`
**Authentication:** Required (Bearer Token)
**Description:** Retrieve all stocks in user's watchlist with real-time pricing

**Headers:**
```
Authorization: Bearer {token}
```

**Response (200 OK):**
```json
[
  {
    "watchlistId": "550e8400-e29b-41d4-a716-446655440000",
    "userId": "660e8400-e29b-41d4-a716-446655440001",
    "stockId": "770e8400-e29b-41d4-a716-446655440002",
    "stockCode": "AAPL",
    "companyName": "Apple Inc.",
    "currentPrice": 189.50,
    "priceChange": 2.50,
    "priceChangePercent": 1.33,
    "addedAt": "2025-12-01T10:00:00"
  },
  {
    "watchlistId": "550e8400-e29b-41d4-a716-446655440003",
    "userId": "660e8400-e29b-41d4-a716-446655440001",
    "stockId": "770e8400-e29b-41d4-a716-446655440004",
    "stockCode": "GOOGL",
    "companyName": "Alphabet Inc.",
    "currentPrice": 140.25,
    "priceChange": -1.75,
    "priceChangePercent": -1.23,
    "addedAt": "2025-12-02T14:30:00"
  }
]
```

**Error Responses:**
- `401 Unauthorized` - Invalid or missing token

---

### 15. Add Stock to Watchlist
**Endpoint:** `POST /api/watchlist`
**Authentication:** Required (Bearer Token)
**Description:** Add a stock to user's watchlist

**Headers:**
```
Authorization: Bearer {token}
```

**Request Body:**
```json
{
  "stockId": "770e8400-e29b-41d4-a716-446655440002"
}
```

**Response (200 OK):**
```json
{
  "watchlistId": "550e8400-e29b-41d4-a716-446655440000",
  "userId": "660e8400-e29b-41d4-a716-446655440001",
  "stockId": "770e8400-e29b-41d4-a716-446655440002",
  "stockCode": "AAPL",
  "companyName": "Apple Inc.",
  "currentPrice": 189.50,
  "priceChange": 2.50,
  "priceChangePercent": 1.33,
  "addedAt": "2025-12-07T12:00:00"
}
```

**Error Responses:**
- `400 Bad Request` - Stock already in watchlist
- `400 Bad Request` - Stock ID is required
- `404 Not Found` - Stock not found
- `401 Unauthorized` - Invalid or missing token

---

### 16. Remove Stock from Watchlist
**Endpoint:** `DELETE /api/watchlist/{stockId}`
**Authentication:** Required (Bearer Token)
**Description:** Remove a stock from user's watchlist

**Path Parameters:**
- `stockId` (UUID) - The stock ID to remove

**Headers:**
```
Authorization: Bearer {token}
```

**Response (204 No Content)**

**Error Responses:**
- `404 Not Found` - Stock not in watchlist
- `401 Unauthorized` - Invalid or missing token

---

### 17. Check if Stock in Watchlist
**Endpoint:** `GET /api/watchlist/check/{stockId}`
**Authentication:** Required (Bearer Token)
**Description:** Check if a specific stock is in user's watchlist

**Path Parameters:**
- `stockId` (UUID) - The stock ID to check

**Headers:**
```
Authorization: Bearer {token}
```

**Response (200 OK):**
```json
{
  "inWatchlist": true
}
```

**Error Responses:**
- `401 Unauthorized` - Invalid or missing token

---

## Transaction Endpoints

### 18. Get All Transactions
**Endpoint:** `GET /api/transactions`
**Authentication:** None
**Description:** Retrieve all transactions (system-wide)

**Response (200 OK):**
```json
[
  {
    "transactionId": "550e8400-e29b-41d4-a716-446655440000",
    "stockId": "770e8400-e29b-41d4-a716-446655440002",
    "accountId": "880e8400-e29b-41d4-a716-446655440005",
    "shareQuantity": 10,
    "pricePerShare": 150.00,
    "transactionType": "BUY",
    "transactionDate": "2025-12-01T10:00:00"
  }
]
```

---

### 19. Get Transaction by ID
**Endpoint:** `GET /api/transactions/{id}`
**Authentication:** None
**Description:** Retrieve a specific transaction by ID

**Path Parameters:**
- `id` (UUID) - The transaction ID

**Response (200 OK):**
```json
{
  "transactionId": "550e8400-e29b-41d4-a716-446655440000",
  "stockId": "770e8400-e29b-41d4-a716-446655440002",
  "accountId": "880e8400-e29b-41d4-a716-446655440005",
  "shareQuantity": 10,
  "pricePerShare": 150.00,
  "transactionType": "BUY",
  "transactionDate": "2025-12-01T10:00:00"
}
```

**Error Responses:**
- `404 Not Found` - Transaction not found

---

### 20. Create Transaction
**Endpoint:** `POST /api/transactions`
**Authentication:** None
**Description:** Create a new buy or sell transaction

**Request Body:**
```json
{
  "accountId": "880e8400-e29b-41d4-a716-446655440005",
  "stockId": "770e8400-e29b-41d4-a716-446655440002",
  "transactionType": "BUY",
  "shareQuantity": 10,
  "pricePerShare": 150.00,
  "commission": 10.00
}
```

**Response (200 OK):**
```json
{
  "transactionId": "550e8400-e29b-41d4-a716-446655440000",
  "stockId": "770e8400-e29b-41d4-a716-446655440002",
  "accountId": "880e8400-e29b-41d4-a716-446655440005",
  "shareQuantity": 10,
  "pricePerShare": 150.00,
  "transactionType": "BUY",
  "transactionDate": "2025-12-07T12:00:00"
}
```

**Error Responses:**
- `400 Bad Request` - Invalid transaction type
- `400 Bad Request` - Insufficient balance for BUY transaction
- `400 Bad Request` - Insufficient shares for SELL transaction

---

### 21. Delete Transaction
**Endpoint:** `DELETE /api/transactions/{id}`
**Authentication:** None
**Description:** Delete a transaction record

**Path Parameters:**
- `id` (UUID) - The transaction ID

**Response (204 No Content)**

**Error Responses:**
- `404 Not Found` - Transaction not found

---

### 22. Get Account Transactions
**Endpoint:** `GET /api/transactions/account/{accountId}`
**Authentication:** None
**Description:** Get all transactions for a specific account

**Path Parameters:**
- `accountId` (UUID) - The account ID

**Response (200 OK):**
```json
[
  {
    "transactionId": "550e8400-e29b-41d4-a716-446655440000",
    "stockId": "770e8400-e29b-41d4-a716-446655440002",
    "accountId": "880e8400-e29b-41d4-a716-446655440005",
    "shareQuantity": 10,
    "pricePerShare": 150.00,
    "transactionType": "BUY",
    "transactionDate": "2025-12-01T10:00:00"
  },
  {
    "transactionId": "550e8400-e29b-41d4-a716-446655440001",
    "stockId": "770e8400-e29b-41d4-a716-446655440002",
    "accountId": "880e8400-e29b-41d4-a716-446655440005",
    "shareQuantity": 5,
    "pricePerShare": 160.00,
    "transactionType": "SELL",
    "transactionDate": "2025-12-05T14:30:00"
  }
]
```

---

## Dashboard & Analytics Endpoints

### 23. Get Dashboard for Account
**Endpoint:** `GET /dashboard/account/{accountId}`
**Authentication:** None (in production, should require auth)
**Description:** Get comprehensive dashboard data for a specific account

**Path Parameters:**
- `accountId` (UUID) - The account ID

**Response (200 OK):**
```json
{
  "portfolioOverview": {
    "accountId": "880e8400-e29b-41d4-a716-446655440005",
    "accountName": "My Primary Account",
    "totalPortfolioValue": 125000.00,
    "totalCostBasis": 100000.00,
    "cashBalance": 5000.00,
    "holdingCount": 15,
    "lastUpdated": "2025-12-07T12:00:00"
  },
  "portfolioPerformance": {
    "userId": "660e8400-e29b-41d4-a716-446655440001",
    "accountId": "880e8400-e29b-41d4-a716-446655440005",
    "totalPortfolioValue": 125000.00,
    "totalCostBasis": 100000.00,
    "totalRealizedGain": 5000.00,
    "totalUnrealizedGain": 25000.00,
    "totalDividends": 2500.00,
    "cashBalance": 5000.00,
    "roiPercentage": 30.25,
    "dailyGain": 450.00,
    "monthlyGain": 3200.00
  },
  "allocations": [
    {
      "sector": "Technology",
      "value": 45000.00,
      "percentage": 36.0,
      "holdingCount": 5
    }
  ],
  "recentTransactions": [
    {
      "transactionId": "550e8400-e29b-41d4-a716-446655440000",
      "stockId": "770e8400-e29b-41d4-a716-446655440002",
      "accountId": "880e8400-e29b-41d4-a716-446655440005",
      "shareQuantity": 10,
      "pricePerShare": 150.00,
      "transactionType": "BUY",
      "transactionDate": "2025-12-07T10:00:00"
    }
  ]
}
```

---

### 24. Get Dashboard for User
**Endpoint:** `GET /dashboard/user/{userId}`
**Authentication:** None (in production, should require auth)
**Description:** Get aggregated dashboard data across all user accounts

**Path Parameters:**
- `userId` (UUID) - The user ID

**Response (200 OK):**
```json
{
  "portfolioOverview": {
    "totalPortfolioValue": 250000.00,
    "totalCostBasis": 200000.00,
    "cashBalance": 10000.00,
    "holdingCount": 30,
    "accountCount": 2,
    "lastUpdated": "2025-12-07T12:00:00"
  },
  "portfolioPerformance": {
    "totalPortfolioValue": 250000.00,
    "totalCostBasis": 200000.00,
    "totalRealizedGain": 10000.00,
    "totalUnrealizedGain": 50000.00,
    "totalDividends": 5000.00,
    "cashBalance": 10000.00,
    "roiPercentage": 30.00,
    "dailyGain": 900.00,
    "monthlyGain": 6400.00
  },
  "allocations": [
    {
      "sector": "Technology",
      "value": 90000.00,
      "percentage": 36.0,
      "holdingCount": 10
    }
  ],
  "recentTransactions": [
    {
      "transactionId": "550e8400-e29b-41d4-a716-446655440000",
      "stockId": "770e8400-e29b-41d4-a716-446655440002",
      "accountId": "880e8400-e29b-41d4-a716-446655440005",
      "shareQuantity": 10,
      "pricePerShare": 150.00,
      "transactionType": "BUY",
      "transactionDate": "2025-12-07T10:00:00"
    }
  ]
}
```

---

### 25. Get Portfolio Performance (Account)
**Endpoint:** `GET /portfolio/performance/account/{accountId}`
**Authentication:** None
**Description:** Get detailed performance metrics for account

**Path Parameters:**
- `accountId` (UUID) - The account ID

**Response (200 OK):**
```json
{
  "userId": "660e8400-e29b-41d4-a716-446655440001",
  "accountId": "880e8400-e29b-41d4-a716-446655440005",
  "totalPortfolioValue": 125000.00,
  "totalCostBasis": 100000.00,
  "totalRealizedGain": 5000.00,
  "totalUnrealizedGain": 25000.00,
  "totalDividends": 2500.00,
  "cashBalance": 5000.00,
  "roiPercentage": 30.25,
  "dailyGain": 450.00,
  "monthlyGain": 3200.00
}
```

---

### 26. Get Portfolio Performance (User)
**Endpoint:** `GET /portfolio/performance/user/{userId}`
**Authentication:** None
**Description:** Get aggregated performance metrics across all user accounts

**Path Parameters:**
- `userId` (UUID) - The user ID

**Response (200 OK):**
```json
{
  "totalPortfolioValue": 250000.00,
  "totalCostBasis": 200000.00,
  "totalRealizedGain": 10000.00,
  "totalUnrealizedGain": 50000.00,
  "totalDividends": 5000.00,
  "cashBalance": 10000.00,
  "roiPercentage": 30.00,
  "dailyGain": 900.00,
  "monthlyGain": 6400.00
}
```

---

### 27. Get Allocations for Account
**Endpoint:** `GET /portfolio/allocations/account/{accountId}`
**Authentication:** None
**Description:** Get detailed allocation breakdown for account (covered in endpoint 13)

---

### 28. Get Allocations for User
**Endpoint:** `GET /portfolio/allocations/user/{userId}`
**Authentication:** None
**Description:** Get aggregated allocation breakdown across all user accounts

**Response (200 OK):**
```json
[
  {
    "sector": "Technology",
    "value": 90000.00,
    "percentage": 36.0,
    "holdingCount": 10
  },
  {
    "sector": "Healthcare",
    "value": 60000.00,
    "percentage": 24.0,
    "holdingCount": 6
  },
  {
    "sector": "Finance",
    "value": 50000.00,
    "percentage": 20.0,
    "holdingCount": 6
  },
  {
    "sector": "Other",
    "value": 50000.00,
    "percentage": 20.0,
    "holdingCount": 8
  }
]
```

---

## Earnings Calendar Endpoints

### 29. Get Upcoming Earnings
**Endpoint:** `GET /api/earnings/upcoming`
**Authentication:** None
**Description:** Get earnings for next 90 days

**Response (200 OK):**
```json
[
  {
    "earningId": "550e8400-e29b-41d4-a716-446655440000",
    "stockId": "770e8400-e29b-41d4-a716-446655440002",
    "stockCode": "AAPL",
    "companyName": "Apple Inc.",
    "earningsDate": "2025-12-15",
    "estimatedEPS": 2.45,
    "actualEPS": null,
    "reportTime": "after-close"
  },
  {
    "earningId": "550e8400-e29b-41d4-a716-446655440001",
    "stockId": "770e8400-e29b-41d4-a716-446655440003",
    "stockCode": "GOOGL",
    "companyName": "Alphabet Inc.",
    "earningsDate": "2025-12-18",
    "estimatedEPS": 1.85,
    "actualEPS": null,
    "reportTime": "before-open"
  }
]
```

---

### 30. Get Earnings by Stock
**Endpoint:** `GET /api/earnings/by-stock/{stockId}`
**Authentication:** None
**Description:** Get all earnings records for a specific stock

**Path Parameters:**
- `stockId` (UUID) - The stock ID

**Response (200 OK):**
```json
[
  {
    "earningId": "550e8400-e29b-41d4-a716-446655440000",
    "stockId": "770e8400-e29b-41d4-a716-446655440002",
    "stockCode": "AAPL",
    "companyName": "Apple Inc.",
    "earningsDate": "2025-12-15",
    "estimatedEPS": 2.45,
    "actualEPS": 2.52,
    "reportTime": "after-close"
  },
  {
    "earningId": "550e8400-e29b-41d4-a716-446655440001",
    "stockId": "770e8400-e29b-41d4-a716-446655440002",
    "stockCode": "AAPL",
    "companyName": "Apple Inc.",
    "earningsDate": "2025-09-15",
    "estimatedEPS": 2.10,
    "actualEPS": 2.18,
    "reportTime": "after-close"
  }
]
```

---

## Price Alerts Endpoints

### 31. Get Price Alerts
**Endpoint:** `GET /api/price-alerts`
**Authentication:** Required (Bearer Token)
**Description:** Get all price alerts for authenticated user

**Headers:**
```
Authorization: Bearer {token}
```

**Query Parameters:**
- `active` (boolean, optional) - Filter by active status (true/false)

**Example:** `GET /api/price-alerts?active=true`

**Response (200 OK):**
```json
[
  {
    "alertId": "550e8400-e29b-41d4-a716-446655440000",
    "userId": "660e8400-e29b-41d4-a716-446655440001",
    "stockId": "770e8400-e29b-41d4-a716-446655440002",
    "stockCode": "AAPL",
    "companyName": "Apple Inc.",
    "type": "ABOVE",
    "targetPrice": 200.00,
    "isActive": true,
    "createdAt": "2025-12-01T10:00:00",
    "triggeredAt": null
  },
  {
    "alertId": "550e8400-e29b-41d4-a716-446655440001",
    "userId": "660e8400-e29b-41d4-a716-446655440001",
    "stockId": "770e8400-e29b-41d4-a716-446655440003",
    "stockCode": "GOOGL",
    "companyName": "Alphabet Inc.",
    "type": "BELOW",
    "targetPrice": 130.00,
    "isActive": true,
    "createdAt": "2025-12-02T14:30:00",
    "triggeredAt": null
  }
]
```

**Error Responses:**
- `401 Unauthorized` - Invalid or missing token

---

### 32. Create Price Alert
**Endpoint:** `POST /api/price-alerts`
**Authentication:** Required (Bearer Token)
**Description:** Create a new price alert for authenticated user

**Headers:**
```
Authorization: Bearer {token}
```

**Request Body:**
```json
{
  "stockId": "770e8400-e29b-41d4-a716-446655440002",
  "type": "ABOVE",
  "targetPrice": 200.00
}
```

**Response (200 OK):**
```json
{
  "alertId": "550e8400-e29b-41d4-a716-446655440000",
  "userId": "660e8400-e29b-41d4-a716-446655440001",
  "stockId": "770e8400-e29b-41d4-a716-446655440002",
  "stockCode": "AAPL",
  "companyName": "Apple Inc.",
  "type": "ABOVE",
  "targetPrice": 200.00,
  "isActive": true,
  "createdAt": "2025-12-07T12:00:00",
  "triggeredAt": null
}
```

**Error Responses:**
- `400 Bad Request` - Stock not found
- `400 Bad Request` - Alert type must be ABOVE or BELOW
- `400 Bad Request` - Target price must be positive
- `401 Unauthorized` - Invalid or missing token

---

### 33. Delete Price Alert
**Endpoint:** `DELETE /api/price-alerts/{alertId}`
**Authentication:** Required (Bearer Token)
**Description:** Delete a price alert

**Path Parameters:**
- `alertId` (UUID) - The alert ID to delete

**Headers:**
```
Authorization: Bearer {token}
```

**Response (204 No Content)**

**Error Responses:**
- `403 Forbidden` - Alert does not belong to authenticated user
- `404 Not Found` - Alert not found
- `401 Unauthorized` - Invalid or missing token

---

## Error Handling

All endpoints follow consistent error handling patterns:

### Standard Error Response Format
```json
{
  "error": "Error message describing what went wrong",
  "timestamp": "2025-12-07T12:00:00",
  "status": 400
}
```

### Common HTTP Status Codes

| Status | Meaning | Common Causes |
|--------|---------|---------------|
| 200 | OK | Request successful |
| 201 | Created | Resource created successfully |
| 204 | No Content | Deletion successful |
| 400 | Bad Request | Invalid input, validation failure, missing fields |
| 401 | Unauthorized | Missing/invalid authentication token |
| 403 | Forbidden | Authenticated but not authorized for resource |
| 404 | Not Found | Resource doesn't exist |
| 409 | Conflict | Resource already exists (duplicate) |
| 500 | Server Error | Internal server error |

---

## Authentication

### JWT Token Format

All protected endpoints require a Bearer token in the Authorization header:

```
Authorization: Bearer {jwt_token}
```

### Token Expiration

- **Access Token:** 24 hours
- **Refresh Token:** 7 days (default)

### Token Payload

```json
{
  "sub": "550e8400-e29b-41d4-a716-446655440000",
  "iss": "personal-investment-tracker",
  "iat": 1701949200,
  "exp": 1702035600,
  "email": "john@example.com"
}
```

---

## Implementation Examples

### React TypeScript Example - Get Watchlist
```typescript
import axios from 'axios';

const getWatchlist = async (token: string) => {
  try {
    const response = await axios.get('/api/watchlist', {
      headers: {
        Authorization: `Bearer ${token}`,
        'Content-Type': 'application/json'
      }
    });
    return response.data;
  } catch (error) {
    console.error('Error fetching watchlist:', error);
    throw error;
  }
};
```

### React Native Example - Create Price Alert
```typescript
import axios from 'axios';

const createPriceAlert = async (
  token: string,
  stockId: string,
  type: 'ABOVE' | 'BELOW',
  targetPrice: number
) => {
  try {
    const response = await axios.post('/api/price-alerts',
      { stockId, type, targetPrice },
      {
        headers: {
          Authorization: `Bearer ${token}`,
          'Content-Type': 'application/json'
        }
      }
    );
    return response.data;
  } catch (error) {
    console.error('Error creating price alert:', error);
    throw error;
  }
};
```

### Login Flow Example
```typescript
const login = async (email: string, password: string) => {
  try {
    const response = await axios.post('/api/auth/login', {
      email,
      password
    });

    const { token } = response.data;
    // Store token in local storage or secure storage
    localStorage.setItem('authToken', token);

    return response.data;
  } catch (error) {
    console.error('Login failed:', error);
    throw error;
  }
};
```

---

## Base URL

```
https://api.example.com
```

For local development:
```
http://localhost:8080
```

---

## Rate Limiting

Currently, no rate limiting is implemented. This may be added in future versions.

---

## API Version History

| Version | Release Date | Changes |
|---------|--------------|---------|
| 1.0 | December 2025 | Initial release with 30 endpoints |

---

## Support

For API support, contact: support@example.com

---

**End of API Documentation**
