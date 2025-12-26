# API Testing Guide

This document provides examples of how to test the Banking System APIs using curl commands.

## Prerequisites

- All services must be running (use `./start-services.sh`)
- API Gateway should be accessible at http://localhost:8080

## 1. User Registration

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john_doe",
    "email": "john@example.com",
    "password": "password123",
    "roles": ["USER"]
  }'
```

Expected Response:
```json
{
  "message": "User registered successfully"
}
```

## 2. User Login

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john_doe",
    "password": "password123"
  }'
```

Expected Response:
```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "type": "Bearer",
  "id": "user_id_here",
  "username": "john_doe",
  "email": "john@example.com",
  "roles": ["ROLE_USER"]
}
```

**Save the token for subsequent requests!**

## 3. Create Bank Account

```bash
# Replace YOUR_TOKEN and USER_ID with actual values
curl -X POST http://localhost:8080/api/accounts \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -d '{
    "userId": "USER_ID",
    "accountType": "SAVINGS",
    "currency": "USD"
  }'
```

Expected Response:
```json
{
  "id": "account_id_here",
  "userId": "user_id_here",
  "accountNumber": "1234567890",
  "accountType": "SAVINGS",
  "balance": 0,
  "currency": "USD",
  "active": true,
  "createdAt": "2025-12-26T07:00:00"
}
```

**Save the account ID for subsequent requests!**

## 4. Get User Accounts

```bash
curl -X GET http://localhost:8080/api/accounts/user/USER_ID \
  -H "Authorization: Bearer YOUR_TOKEN"
```

## 5. Get Account Balance

```bash
curl -X GET http://localhost:8080/api/accounts/ACCOUNT_ID/balance \
  -H "Authorization: Bearer YOUR_TOKEN"
```

## 6. Deposit Money

```bash
curl -X POST http://localhost:8080/api/transactions/deposit \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -d '{
    "accountId": "ACCOUNT_ID",
    "amount": 1000.00,
    "description": "Initial deposit"
  }'
```

Expected Response:
```json
{
  "id": "transaction_id",
  "accountId": "account_id",
  "type": "DEPOSIT",
  "amount": 1000.00,
  "currency": "USD",
  "description": "Initial deposit",
  "status": "COMPLETED",
  "createdAt": "2025-12-26T07:00:00"
}
```

## 7. Withdraw Money

```bash
curl -X POST http://localhost:8080/api/transactions/withdraw \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -d '{
    "accountId": "ACCOUNT_ID",
    "amount": 200.00,
    "description": "ATM withdrawal"
  }'
```

## 8. Transfer Money

First, create a second account, then transfer between them:

```bash
curl -X POST http://localhost:8080/api/transactions/transfer \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -d '{
    "fromAccountId": "FROM_ACCOUNT_ID",
    "toAccountId": "TO_ACCOUNT_ID",
    "amount": 250.00,
    "description": "Transfer to savings"
  }'
```

## 9. Get Transaction History

```bash
curl -X GET "http://localhost:8080/api/transactions/account/ACCOUNT_ID?page=0&size=10&sortBy=createdAt&direction=DESC" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

Expected Response:
```json
{
  "content": [
    {
      "id": "transaction_id",
      "accountId": "account_id",
      "type": "DEPOSIT",
      "amount": 1000.00,
      "currency": "USD",
      "description": "Initial deposit",
      "status": "COMPLETED",
      "createdAt": "2025-12-26T07:00:00"
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 10
  },
  "totalElements": 1,
  "totalPages": 1
}
```

## Complete Workflow Example

```bash
# 1. Register user
REGISTER_RESPONSE=$(curl -s -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username": "alice", "email": "alice@example.com", "password": "password123"}')

echo "Registered: $REGISTER_RESPONSE"

# 2. Login
LOGIN_RESPONSE=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "alice", "password": "password123"}')

TOKEN=$(echo $LOGIN_RESPONSE | jq -r '.token')
USER_ID=$(echo $LOGIN_RESPONSE | jq -r '.id')

echo "Logged in, token: $TOKEN"
echo "User ID: $USER_ID"

# 3. Create account
ACCOUNT_RESPONSE=$(curl -s -X POST http://localhost:8080/api/accounts \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d "{\"userId\": \"$USER_ID\", \"accountType\": \"SAVINGS\", \"currency\": \"USD\"}")

ACCOUNT_ID=$(echo $ACCOUNT_RESPONSE | jq -r '.id')
echo "Created account: $ACCOUNT_ID"

# 4. Deposit money
DEPOSIT_RESPONSE=$(curl -s -X POST http://localhost:8080/api/transactions/deposit \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d "{\"accountId\": \"$ACCOUNT_ID\", \"amount\": 1000.00, \"description\": \"Initial deposit\"}")

echo "Deposit: $DEPOSIT_RESPONSE"

# 5. Check balance
BALANCE=$(curl -s -X GET http://localhost:8080/api/accounts/$ACCOUNT_ID/balance \
  -H "Authorization: Bearer $TOKEN")

echo "Balance: $BALANCE"
```

## Error Handling

### Insufficient Balance
```bash
curl -X POST http://localhost:8080/api/transactions/withdraw \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -d '{
    "accountId": "ACCOUNT_ID",
    "amount": 999999.00,
    "description": "Too much"
  }'
```

Expected Response (400 Bad Request):
```json
{
  "message": "Insufficient balance"
}
```

### Invalid Token
Request without token or with invalid token will return 401 Unauthorized.

### Validation Errors
```bash
curl -X POST http://localhost:8080/api/transactions/deposit \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -d '{
    "accountId": "",
    "amount": -100.00
  }'
```

Expected Response (400 Bad Request):
```json
{
  "accountId": "Account ID is required",
  "amount": "Amount must be greater than 0"
}
```

## Notes

- Replace `YOUR_TOKEN`, `USER_ID`, `ACCOUNT_ID`, etc. with actual values from previous responses
- The token expires after 24 hours by default
- All monetary amounts are in USD by default
- Transaction history supports pagination with `page`, `size`, `sortBy`, and `direction` parameters
