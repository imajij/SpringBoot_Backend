# Mock Data for Postman API Testing

## 1. Register User

**POST** `/api/auth/register`

```json
{
  "email": "testuser1@example.com",
  "password": "Test@1234",
  "firstName": "Test",
  "lastName": "User"
}
```

---

## 2. Login User

**POST** `/api/auth/login`

```json
{
  "email": "testuser1@example.com",
  "password": "Test@1234"
}
```

---

## 3. Create Expense (with Bill Photo)

**POST** `/api/expenses` (multipart/form-data)

- Key: `expense` (type: text)
- Value:

```json
{
  "amount": 150.75,
  "description": "Dinner at restaurant",
  "category": "Food & Dining",
  "date": "2026-01-30",
  "notes": "Birthday dinner"
}
```

- Key: `billPhoto` (type: file)
- Value: (attach a sample image file, e.g., `bill.jpg`)

---

## 4. Create Budget

**POST** `/api/budget`

```json
{
  "month": 1,
  "year": 2026,
  "monthlyLimit": 5000.00
}
```

---

## 5. Create Savings Goal

**POST** `/api/savings/goals`

```json
{
  "name": "Vacation Fund",
  "targetAmount": 10000,
  "currentAmount": 0,
  "deadline": "2026-06-30"
}
```

---

## 6. Create Split Bill

**POST** `/api/split-bills`

```json
{
  "name": "Trip to Goa",
  "totalAmount": 12000,
  "participants": [
    { "name": "Ajay", "email": "ajay@example.com" },
    { "name": "Vijay", "email": "vijay@example.com" }
  ]
}
```

---

## 7. Update User Profile

**PUT** `/api/users/profile`

```json
{
  "firstName": "Test",
  "lastName": "User",
  "email": "testuser1@example.com"
}
```

---

## 8. Deposit to Savings Goal

**POST** `/api/savings/goals/{id}/deposit`

```json
{
  "amount": 2000
}
```

---

## 9. Filter Expenses by Date Range

**GET** `/api/expenses/filter?startDate=2026-01-01&endDate=2026-01-31`

No body required.

---

## 10. Get Dashboard Stats

**GET** `/api/dashboard/stats`

No body required.
