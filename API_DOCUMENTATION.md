# 💰 Finance Tracker API Documentation

> **Complete API Reference for Frontend Engineers**

## 📌 Table of Contents

- [Overview](#overview)
- [Base Configuration](#base-configuration)
- [Authentication](#authentication)
- [Expenses](#expenses)
- [Budget](#budget)
- [Savings Goals](#savings-goals)
- [Dashboard](#dashboard)
- [Split Bills](#split-bills)
- [User Profile](#user-profile)
- [Error Handling](#error-handling)
- [Frontend Integration Guide](#frontend-integration-guide)

---

## Overview

| Property | Value |
|----------|-------|
| **Base URL (Production)** | `https://springbootbackend-production-6c94.up.railway.app` |
| **Base URL (Local)** | `http://localhost:8080` |
| **API Version** | v1 |
| **Authentication** | JWT Bearer Token |
| **Content Type** | `application/json` |

---

## Base Configuration

### Required Headers

#### All Requests
```javascript
{
  "Content-Type": "application/json"
}
```

#### Authenticated Requests (after login)
```javascript
{
  "Content-Type": "application/json",
  "Authorization": "Bearer <your_jwt_token>"
}
```

### Standard API Response Format

All API responses follow this structure:

```typescript
interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T | null;
  timestamp: string; // ISO 8601 format
}
```

#### Success Response Example
```json
{
  "success": true,
  "message": "Operation successful",
  "data": { /* response data */ },
  "timestamp": "2026-01-26T11:40:02.377"
}
```

#### Error Response Example
```json
{
  "success": false,
  "message": "Error description",
  "data": null,
  "timestamp": "2026-01-26T11:40:02.377"
}
```

---

## Authentication

> ⚠️ **These endpoints do NOT require authentication token**

### Register New User

```
POST /api/auth/register
```

#### Request Body
```json
{
  "firstName": "John",
  "lastName": "Doe",
  "email": "john@example.com",
  "password": "password123"
}
```

#### Validation Rules
| Field | Type | Required | Constraints |
|-------|------|----------|-------------|
| `firstName` | string | ✅ Yes | Min: 2, Max: 50 characters |
| `lastName` | string | ✅ Yes | Min: 2, Max: 50 characters |
| `email` | string | ✅ Yes | Valid email format |
| `password` | string | ✅ Yes | Min: 6, Max: 100 characters |

#### Success Response (HTTP 201)
```json
{
  "success": true,
  "message": "User registered successfully",
  "data": {
    "token": "eyJhbGciOiJIUzM4NCJ9...",
    "tokenType": "Bearer",
    "user": {
      "id": "6977528a3538a63b06197b55",
      "email": "john@example.com",
      "firstName": "John",
      "lastName": "Doe"
    }
  },
  "timestamp": "2026-01-26T11:39:55.350"
}
```

#### Error Responses
| HTTP Code | Cause |
|-----------|-------|
| 400 | Validation failed (missing/invalid fields) |
| 409 | Email already exists |

---

### Login

```
POST /api/auth/login
```

#### Request Body
```json
{
  "email": "john@example.com",
  "password": "password123"
}
```

#### Validation Rules
| Field | Type | Required |
|-------|------|----------|
| `email` | string | ✅ Yes |
| `password` | string | ✅ Yes |

#### Success Response (HTTP 200)
```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "token": "eyJhbGciOiJIUzM4NCJ9...",
    "tokenType": "Bearer",
    "user": {
      "id": "6977528a3538a63b06197b55",
      "email": "john@example.com",
      "firstName": "John",
      "lastName": "Doe"
    }
  },
  "timestamp": "2026-01-26T11:40:02.377"
}
```

#### Error Responses
| HTTP Code | Cause |
|-----------|-------|
| 401 | Invalid email or password |

---

## Expenses

> 🔒 **All endpoints require authentication**

### TypeScript Interface
```typescript
interface Expense {
  id: string;
  amount: number;
  description: string;
  category: string;
  date: string; // YYYY-MM-DD
  notes?: string;
  createdAt: string;
  updatedAt: string;
}

interface ExpenseRequest {
  amount: number;
  description: string;
  category: string;
  date: string; // YYYY-MM-DD
  notes?: string;
}
```

---

### Get All Expenses (Paginated)

```
GET /api/expenses
```

#### Query Parameters
| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `page` | number | 0 | Page number (0-indexed) |
| `size` | number | 20 | Items per page |
| `sort` | string | `date,desc` | Sort field and direction |

#### Example Request
```
GET /api/expenses?page=0&size=10&sort=date,desc
```

#### Success Response (HTTP 200)
```json
{
  "success": true,
  "message": "Expenses retrieved successfully",
  "data": {
    "content": [
      {
        "id": "exp123",
        "amount": 150.50,
        "description": "Grocery shopping",
        "category": "Food",
        "date": "2026-01-26",
        "notes": "Weekly groceries",
        "createdAt": "2026-01-26T10:00:00",
        "updatedAt": "2026-01-26T10:00:00"
      }
    ],
    "pageable": {
      "pageNumber": 0,
      "pageSize": 10
    },
    "totalElements": 50,
    "totalPages": 5,
    "first": true,
    "last": false
  },
  "timestamp": "2026-01-26T11:40:02.377"
}
```

---

### Get Expense by ID

```
GET /api/expenses/{id}
```

#### Success Response (HTTP 200)
```json
{
  "success": true,
  "message": "Expense retrieved successfully",
  "data": {
    "id": "exp123",
    "amount": 150.50,
    "description": "Grocery shopping",
    "category": "Food",
    "date": "2026-01-26",
    "notes": "Weekly groceries",
    "createdAt": "2026-01-26T10:00:00",
    "updatedAt": "2026-01-26T10:00:00"
  },
  "timestamp": "2026-01-26T11:40:02.377"
}
```

---

### Create Expense

```
POST /api/expenses
```

#### Request Body
```json
{
  "amount": 150.50,
  "description": "Grocery shopping",
  "category": "Food",
  "date": "2026-01-26",
  "notes": "Weekly groceries"
}
```

#### Validation Rules
| Field | Type | Required | Constraints |
|-------|------|----------|-------------|
| `amount` | number | ✅ Yes | Must be > 0.01 |
| `description` | string | ✅ Yes | Cannot be blank |
| `category` | string | ✅ Yes | Cannot be blank |
| `date` | string | ✅ Yes | ISO date format (YYYY-MM-DD) |
| `notes` | string | ❌ No | Optional |

#### Success Response (HTTP 201)
```json
{
  "success": true,
  "message": "Expense created successfully",
  "data": {
    "id": "exp123",
    "amount": 150.50,
    "description": "Grocery shopping",
    "category": "Food",
    "date": "2026-01-26",
    "notes": "Weekly groceries",
    "createdAt": "2026-01-26T10:00:00",
    "updatedAt": "2026-01-26T10:00:00"
  },
  "timestamp": "2026-01-26T11:40:02.377"
}
```

---

### Update Expense

```
PUT /api/expenses/{id}
```

#### Request Body
Same as Create Expense

#### Success Response (HTTP 200)
Returns updated expense object

---

### Delete Expense

```
DELETE /api/expenses/{id}
```

#### Success Response (HTTP 200)
```json
{
  "success": true,
  "message": "Expense deleted successfully",
  "data": null,
  "timestamp": "2026-01-26T11:40:02.377"
}
```

---

### Get Monthly Stats

```
GET /api/expenses/stats/monthly
```

#### Query Parameters
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `month` | number | ✅ Yes | Month (1-12) |
| `year` | number | ✅ Yes | Year (e.g., 2026) |

#### Example Request
```
GET /api/expenses/stats/monthly?month=1&year=2026
```

#### Success Response (HTTP 200)
```json
{
  "success": true,
  "message": "Monthly stats retrieved",
  "data": {
    "month": 1,
    "year": 2026,
    "totalExpenses": 2500.00,
    "expenseCount": 25,
    "averageExpense": 100.00,
    "categoryBreakdown": {
      "Food": 800.00,
      "Transport": 400.00,
      "Entertainment": 300.00
    }
  },
  "timestamp": "2026-01-26T11:40:02.377"
}
```

---

### Get Categories

```
GET /api/expenses/categories
```

#### Success Response (HTTP 200)
```json
{
  "success": true,
  "message": "Categories retrieved",
  "data": ["Food", "Transport", "Entertainment", "Shopping", "Bills", "Healthcare", "Other"],
  "timestamp": "2026-01-26T11:40:02.377"
}
```

---

### Filter by Date Range

```
GET /api/expenses/filter
```

#### Query Parameters
| Parameter | Type | Required | Format |
|-----------|------|----------|--------|
| `startDate` | string | ✅ Yes | YYYY-MM-DD |
| `endDate` | string | ✅ Yes | YYYY-MM-DD |

#### Example Request
```
GET /api/expenses/filter?startDate=2026-01-01&endDate=2026-01-31
```

---

### Filter by Category

```
GET /api/expenses/category/{category}
```

#### Example Request
```
GET /api/expenses/category/Food
```

---

## Budget

> 🔒 **All endpoints require authentication**

### TypeScript Interface
```typescript
interface Budget {
  id: string;
  monthlyLimit: number;
  month: number;
  year: number;
  spent: number;
  remaining: number;
  percentUsed: number;
}

interface BudgetRequest {
  monthlyLimit: number;
  month: number;
  year: number;
}
```

---

### Get Current Month Budget

```
GET /api/budget
```

#### Success Response (HTTP 200)
```json
{
  "success": true,
  "message": "Budget retrieved",
  "data": {
    "id": "budget123",
    "monthlyLimit": 5000.00,
    "month": 1,
    "year": 2026,
    "spent": 2500.00,
    "remaining": 2500.00,
    "percentUsed": 50.0
  },
  "timestamp": "2026-01-26T11:40:02.377"
}
```

---

### Get Budget by Month/Year

```
GET /api/budget/{month}/{year}
```

#### Example Request
```
GET /api/budget/1/2026
```

---

### Create or Update Budget

```
POST /api/budget
```

#### Request Body
```json
{
  "monthlyLimit": 5000.00,
  "month": 1,
  "year": 2026
}
```

#### Validation Rules
| Field | Type | Required | Constraints |
|-------|------|----------|-------------|
| `monthlyLimit` | number | ✅ Yes | Must be > 0.01 |
| `month` | number | ✅ Yes | 1-12 |
| `year` | number | ✅ Yes | >= 2020 |

#### Success Response (HTTP 200/201)
Returns the created/updated budget object

---

## Savings Goals

> 🔒 **All endpoints require authentication**

### TypeScript Interface
```typescript
interface SavingsGoal {
  id: string;
  name: string;
  description?: string;
  targetAmount: number;
  currentAmount: number;
  targetDate?: string;
  icon?: string;
  color?: string;
  progress: number; // percentage
  createdAt: string;
  updatedAt: string;
}

interface SavingsGoalRequest {
  name: string;
  description?: string;
  targetAmount: number;
  targetDate?: string;
  icon?: string;
  color?: string;
}

interface DepositRequest {
  amount: number;
}
```

---

### Get All Savings Goals

```
GET /api/savings/goals
```

#### Success Response (HTTP 200)
```json
{
  "success": true,
  "message": "Goals retrieved",
  "data": [
    {
      "id": "goal123",
      "name": "Emergency Fund",
      "description": "6 months of expenses",
      "targetAmount": 10000.00,
      "currentAmount": 5000.00,
      "targetDate": "2026-12-31",
      "icon": "💰",
      "color": "#4CAF50",
      "progress": 50.0,
      "createdAt": "2026-01-01T00:00:00",
      "updatedAt": "2026-01-26T00:00:00"
    }
  ],
  "timestamp": "2026-01-26T11:40:02.377"
}
```

---

### Get Goal by ID

```
GET /api/savings/goals/{id}
```

---

### Create Savings Goal

```
POST /api/savings/goals
```

#### Request Body
```json
{
  "name": "Emergency Fund",
  "description": "6 months of expenses",
  "targetAmount": 10000.00,
  "targetDate": "2026-12-31",
  "icon": "💰",
  "color": "#4CAF50"
}
```

#### Validation Rules
| Field | Type | Required | Constraints |
|-------|------|----------|-------------|
| `name` | string | ✅ Yes | Cannot be blank |
| `description` | string | ❌ No | Optional |
| `targetAmount` | number | ✅ Yes | Must be > 0.01 |
| `targetDate` | string | ❌ No | ISO date (YYYY-MM-DD) |
| `icon` | string | ❌ No | Emoji or icon name |
| `color` | string | ❌ No | Hex color code |

---

### Update Savings Goal

```
PUT /api/savings/goals/{id}
```

#### Request Body
Same as Create

---

### Delete Savings Goal

```
DELETE /api/savings/goals/{id}
```

---

### Deposit to Goal

```
POST /api/savings/goals/{id}/deposit
```

#### Request Body
```json
{
  "amount": 500.00
}
```

#### Validation Rules
| Field | Type | Required | Constraints |
|-------|------|----------|-------------|
| `amount` | number | ✅ Yes | Must be > 0 |

#### Success Response (HTTP 200)
```json
{
  "success": true,
  "message": "Deposit successful",
  "data": {
    "id": "goal123",
    "name": "Emergency Fund",
    "currentAmount": 5500.00,
    "progress": 55.0
  },
  "timestamp": "2026-01-26T11:40:02.377"
}
```

---

### Get Overall Savings Progress

```
GET /api/savings/progress
```

#### Success Response (HTTP 200)
```json
{
  "success": true,
  "message": "Progress retrieved",
  "data": {
    "totalTargetAmount": 25000.00,
    "totalCurrentAmount": 12500.00,
    "overallProgress": 50.0,
    "goalsCount": 3,
    "completedGoals": 1
  },
  "timestamp": "2026-01-26T11:40:02.377"
}
```

---

## Dashboard

> 🔒 **All endpoints require authentication**

### Get Dashboard Stats

```
GET /api/dashboard/stats
```

#### Success Response (HTTP 200)
```json
{
  "success": true,
  "message": "Stats retrieved",
  "data": {
    "totalExpenses": 15000.00,
    "monthlyExpenses": 2500.00,
    "budgetUsed": 50.0,
    "budgetRemaining": 2500.00,
    "savingsProgress": 45.0,
    "expenseCount": 75
  },
  "timestamp": "2026-01-26T11:40:02.377"
}
```

---

### Get Dashboard Summary

```
GET /api/dashboard/summary
```

#### Success Response (HTTP 200)
```json
{
  "success": true,
  "message": "Summary retrieved",
  "data": {
    "currentMonthExpenses": 2500.00,
    "previousMonthExpenses": 2800.00,
    "monthOverMonthChange": -10.7,
    "topCategories": [
      { "category": "Food", "amount": 800.00, "percentage": 32.0 },
      { "category": "Transport", "amount": 400.00, "percentage": 16.0 }
    ],
    "recentExpenses": []
  },
  "timestamp": "2026-01-26T11:40:02.377"
}
```

---

### Get Spending Breakdown

```
GET /api/dashboard/spending-breakdown
```

#### Success Response (HTTP 200)
```json
{
  "success": true,
  "message": "Breakdown retrieved",
  "data": [
    { "category": "Food", "amount": 800.00, "percentage": 32.0, "color": "#FF6384" },
    { "category": "Transport", "amount": 400.00, "percentage": 16.0, "color": "#36A2EB" },
    { "category": "Entertainment", "amount": 300.00, "percentage": 12.0, "color": "#FFCE56" }
  ],
  "timestamp": "2026-01-26T11:40:02.377"
}
```

---

### Get Spending Trends

```
GET /api/dashboard/spending-trends
```

#### Query Parameters
| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `months` | number | 6 | Number of months to include |

#### Example Request
```
GET /api/dashboard/spending-trends?months=6
```

#### Success Response (HTTP 200)
```json
{
  "success": true,
  "message": "Trends retrieved",
  "data": [
    { "month": "Aug 2025", "amount": 2100.00 },
    { "month": "Sep 2025", "amount": 2300.00 },
    { "month": "Oct 2025", "amount": 2500.00 },
    { "month": "Nov 2025", "amount": 2200.00 },
    { "month": "Dec 2025", "amount": 2800.00 },
    { "month": "Jan 2026", "amount": 2500.00 }
  ],
  "timestamp": "2026-01-26T11:40:02.377"
}
```

---

## Split Bills

> 🔒 **All endpoints require authentication**

### TypeScript Interface
```typescript
interface Participant {
  id: string;
  name: string;
  email?: string;
  amountOwed: number;
  paid: boolean;
}

interface SplitBill {
  id: string;
  name: string;
  description?: string;
  totalAmount: number;
  category?: string;
  participants: Participant[];
  createdAt: string;
  updatedAt: string;
}

interface SplitBillRequest {
  name: string;
  description?: string;
  totalAmount: number;
  category?: string;
  participants: ParticipantRequest[];
}

interface ParticipantRequest {
  name: string;
  email?: string;
  amountOwed: number;
  paid?: boolean;
}
```

---

### Get All Split Bills

```
GET /api/split-bills
```

---

### Get Split Bill by ID

```
GET /api/split-bills/{id}
```

---

### Create Split Bill

```
POST /api/split-bills
```

#### Request Body
```json
{
  "name": "Dinner at Restaurant",
  "description": "Birthday celebration",
  "totalAmount": 200.00,
  "category": "Food",
  "participants": [
    {
      "name": "Alice",
      "email": "alice@example.com",
      "amountOwed": 50.00,
      "paid": false
    },
    {
      "name": "Bob",
      "email": "bob@example.com",
      "amountOwed": 50.00,
      "paid": true
    },
    {
      "name": "Charlie",
      "email": "charlie@example.com",
      "amountOwed": 50.00,
      "paid": false
    },
    {
      "name": "Me",
      "amountOwed": 50.00,
      "paid": true
    }
  ]
}
```

#### Validation Rules
| Field | Type | Required | Constraints |
|-------|------|----------|-------------|
| `name` | string | ✅ Yes | Cannot be blank |
| `description` | string | ❌ No | Optional |
| `totalAmount` | number | ✅ Yes | Must be > 0 |
| `category` | string | ❌ No | Optional |
| `participants` | array | ✅ Yes | At least 1 participant |

**Participant Validation:**
| Field | Type | Required | Constraints |
|-------|------|----------|-------------|
| `name` | string | ✅ Yes | Cannot be blank |
| `email` | string | ❌ No | Valid email if provided |
| `amountOwed` | number | ✅ Yes | Must be >= 0 |
| `paid` | boolean | ❌ No | Default: false |

---

### Update Split Bill

```
PUT /api/split-bills/{id}
```

---

### Delete Split Bill

```
DELETE /api/split-bills/{id}
```

---

### Add Participants to Bill

```
POST /api/split-bills/{id}/participants
```

#### Request Body
```json
[
  {
    "name": "David",
    "email": "david@example.com",
    "amountOwed": 25.00,
    "paid": false
  }
]
```

---

### Mark Participant as Paid

```
PUT /api/split-bills/{id}/participants/{participantId}/pay
```

#### Success Response (HTTP 200)
```json
{
  "success": true,
  "message": "Participant marked as paid",
  "data": {
    "participantId": "part123",
    "name": "Alice",
    "paid": true
  },
  "timestamp": "2026-01-26T11:40:02.377"
}
```

---

### Get Split Bills Summary

```
GET /api/split-bills/summary
```

#### Success Response (HTTP 200)
```json
{
  "success": true,
  "message": "Summary retrieved",
  "data": {
    "totalBills": 5,
    "totalAmount": 1500.00,
    "amountOwedToYou": 450.00,
    "amountYouOwe": 200.00,
    "settledBills": 2,
    "pendingBills": 3
  },
  "timestamp": "2026-01-26T11:40:02.377"
}
```

---

## User Profile

> 🔒 **All endpoints require authentication**

### TypeScript Interface
```typescript
interface UserProfile {
  id: string;
  email: string;
  firstName: string;
  lastName: string;
  createdAt: string;
}

interface UpdateProfileRequest {
  firstName: string;
  lastName: string;
}
```

---

### Get User Profile

```
GET /api/users/profile
```

#### Success Response (HTTP 200)
```json
{
  "success": true,
  "message": "Profile retrieved",
  "data": {
    "id": "user123",
    "email": "john@example.com",
    "firstName": "John",
    "lastName": "Doe",
    "createdAt": "2026-01-01T00:00:00"
  },
  "timestamp": "2026-01-26T11:40:02.377"
}
```

---

### Update User Profile

```
PUT /api/users/profile
```

#### Request Body
```json
{
  "firstName": "John",
  "lastName": "Smith"
}
```

#### Validation Rules
| Field | Type | Required | Constraints |
|-------|------|----------|-------------|
| `firstName` | string | ✅ Yes | Min: 2, Max: 50 characters |
| `lastName` | string | ✅ Yes | Min: 2, Max: 50 characters |

---

## Error Handling

### HTTP Status Codes

| Code | Meaning | When It Happens |
|------|---------|-----------------|
| 200 | OK | Successful GET, PUT, DELETE |
| 201 | Created | Successful POST (resource created) |
| 400 | Bad Request | Validation failed, malformed JSON |
| 401 | Unauthorized | Missing/invalid/expired token |
| 404 | Not Found | Resource doesn't exist |
| 409 | Conflict | Duplicate resource (e.g., email exists) |
| 500 | Server Error | Internal server error |

### Validation Error Response (400)

```json
{
  "success": false,
  "message": "Validation failed",
  "data": {
    "errors": [
      { "field": "email", "message": "Invalid email format" },
      { "field": "password", "message": "Password must be between 6 and 100 characters" }
    ]
  },
  "timestamp": "2026-01-26T11:40:02.377"
}
```

### Authentication Error Response (401)

```json
{
  "success": false,
  "message": "Unauthorized: Full authentication is required",
  "data": null,
  "timestamp": "2026-01-26T11:40:02.377"
}
```

---

## Frontend Integration Guide

### 1. API Client Setup (Axios Example)

```javascript
// api/client.js
import axios from 'axios';

const API_BASE_URL = process.env.REACT_APP_API_URL || 'https://springbootbackend-production-6c94.up.railway.app';

const apiClient = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor - Add auth token
apiClient.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// Response interceptor - Handle errors
apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    const { response } = error;
    
    if (response?.status === 401) {
      // Check if it's an auth endpoint
      const isAuthEndpoint = error.config.url.includes('/api/auth/');
      
      if (!isAuthEndpoint) {
        // Token expired - redirect to login
        localStorage.removeItem('token');
        localStorage.removeItem('user');
        window.location.href = '/login';
      }
    }
    
    // Extract error message
    const message = response?.data?.message || 'An error occurred';
    return Promise.reject(new Error(message));
  }
);

export default apiClient;
```

### 2. Auth Service Example

```javascript
// api/authService.js
import apiClient from './client';

export const authService = {
  async register(userData) {
    // userData must have: firstName, lastName, email, password
    const response = await apiClient.post('/api/auth/register', userData);
    
    if (response.data.success) {
      const { token, user } = response.data.data;
      localStorage.setItem('token', token);
      localStorage.setItem('user', JSON.stringify(user));
    }
    
    return response.data;
  },

  async login(credentials) {
    // credentials must have: email, password
    const response = await apiClient.post('/api/auth/login', credentials);
    
    if (response.data.success) {
      const { token, user } = response.data.data;
      localStorage.setItem('token', token);
      localStorage.setItem('user', JSON.stringify(user));
    }
    
    return response.data;
  },

  logout() {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    window.location.href = '/login';
  },

  isAuthenticated() {
    return !!localStorage.getItem('token');
  },

  getCurrentUser() {
    const user = localStorage.getItem('user');
    return user ? JSON.parse(user) : null;
  }
};
```

### 3. Expense Service Example

```javascript
// api/expenseService.js
import apiClient from './client';

export const expenseService = {
  async getAll(page = 0, size = 20, sort = 'date,desc') {
    const response = await apiClient.get('/api/expenses', {
      params: { page, size, sort }
    });
    return response.data;
  },

  async getById(id) {
    const response = await apiClient.get(`/api/expenses/${id}`);
    return response.data;
  },

  async create(expense) {
    // expense: { amount, description, category, date, notes? }
    const response = await apiClient.post('/api/expenses', expense);
    return response.data;
  },

  async update(id, expense) {
    const response = await apiClient.put(`/api/expenses/${id}`, expense);
    return response.data;
  },

  async delete(id) {
    const response = await apiClient.delete(`/api/expenses/${id}`);
    return response.data;
  },

  async getMonthlyStats(month, year) {
    const response = await apiClient.get('/api/expenses/stats/monthly', {
      params: { month, year }
    });
    return response.data;
  },

  async getCategories() {
    const response = await apiClient.get('/api/expenses/categories');
    return response.data;
  },

  async filterByDateRange(startDate, endDate) {
    const response = await apiClient.get('/api/expenses/filter', {
      params: { startDate, endDate }
    });
    return response.data;
  }
};
```

### 4. React Hook Example

```javascript
// hooks/useExpenses.js
import { useState, useEffect, useCallback } from 'react';
import { expenseService } from '../api/expenseService';

export function useExpenses(page = 0, size = 20) {
  const [expenses, setExpenses] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [pagination, setPagination] = useState({});

  const fetchExpenses = useCallback(async () => {
    try {
      setLoading(true);
      setError(null);
      const response = await expenseService.getAll(page, size);
      
      if (response.success) {
        setExpenses(response.data.content);
        setPagination({
          totalElements: response.data.totalElements,
          totalPages: response.data.totalPages,
          currentPage: response.data.pageable.pageNumber,
        });
      }
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  }, [page, size]);

  useEffect(() => {
    fetchExpenses();
  }, [fetchExpenses]);

  const addExpense = async (expense) => {
    const response = await expenseService.create(expense);
    if (response.success) {
      fetchExpenses(); // Refresh list
    }
    return response;
  };

  const deleteExpense = async (id) => {
    const response = await expenseService.delete(id);
    if (response.success) {
      fetchExpenses(); // Refresh list
    }
    return response;
  };

  return {
    expenses,
    loading,
    error,
    pagination,
    refresh: fetchExpenses,
    addExpense,
    deleteExpense,
  };
}
```

### 5. Form Example (Register)

```jsx
// components/RegisterForm.jsx
import { useState } from 'react';
import { authService } from '../api/authService';

export function RegisterForm() {
  const [formData, setFormData] = useState({
    firstName: '',
    lastName: '',
    email: '',
    password: '',
  });
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      const response = await authService.register(formData);
      if (response.success) {
        // Redirect to dashboard
        window.location.href = '/dashboard';
      }
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <form onSubmit={handleSubmit}>
      {error && <div className="error">{error}</div>}
      
      <input
        type="text"
        placeholder="First Name"
        value={formData.firstName}
        onChange={(e) => setFormData({ ...formData, firstName: e.target.value })}
        required
        minLength={2}
        maxLength={50}
      />
      
      <input
        type="text"
        placeholder="Last Name"
        value={formData.lastName}
        onChange={(e) => setFormData({ ...formData, lastName: e.target.value })}
        required
        minLength={2}
        maxLength={50}
      />
      
      <input
        type="email"
        placeholder="Email"
        value={formData.email}
        onChange={(e) => setFormData({ ...formData, email: e.target.value })}
        required
      />
      
      <input
        type="password"
        placeholder="Password"
        value={formData.password}
        onChange={(e) => setFormData({ ...formData, password: e.target.value })}
        required
        minLength={6}
        maxLength={100}
      />
      
      <button type="submit" disabled={loading}>
        {loading ? 'Registering...' : 'Register'}
      </button>
    </form>
  );
}
```

---

## Common Issues & Solutions

### Issue: "Validation failed" on Register
**Cause:** Missing `firstName` or `lastName` fields  
**Solution:** Ensure you send `firstName` and `lastName` (NOT just `name`)

### Issue: 401 on Login
**Cause:** User doesn't exist or wrong password  
**Solution:** Register first, then use same credentials to login

### Issue: 401 on other endpoints
**Cause:** Token missing, invalid, or expired  
**Solution:** Check `Authorization` header format: `Bearer <token>`

### Issue: CORS Error
**Cause:** Frontend origin not allowed  
**Solution:** Backend already allows common origins. Contact backend if needed.

### Issue: Date format errors
**Cause:** Wrong date format  
**Solution:** Always use ISO format: `YYYY-MM-DD` (e.g., `2026-01-26`)

---

## Test Credentials

For quick testing:
```
Email: testuser123@example.com
Password: test123456
```

---

## Environment Variables

```env
# .env (React)
REACT_APP_API_URL=https://springbootbackend-production-6c94.up.railway.app

# .env (Vite)
VITE_API_URL=https://springbootbackend-production-6c94.up.railway.app
```

---

## Support

If you encounter issues not covered here, check:
1. Network tab in browser DevTools for request/response details
2. Ensure JSON is properly formatted
3. Verify all required fields are present
4. Check that numbers are sent as numbers (not strings)
