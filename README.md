# Banking System with Microservices Architecture

A comprehensive banking system built with Java 17 + Spring Boot, implementing microservices architecture with JWT authentication, account management, and transaction processing.

## 🏗️ Architecture

This system uses a microservices architecture with the following components:

- **Eureka Server** (Port 8761) - Service Discovery
- **API Gateway** (Port 8080) - Entry point for all requests
- **Auth Service** (Port 8081) - User authentication and JWT management
- **Account Service** (Port 8082) - Account management
- **Transaction Service** (Port 8083) - Transaction processing

## 🚀 Features

### Authentication & Authorization
- User registration and login
- JWT token-based authentication
- BCrypt password encryption
- Role-based access control (USER, ADMIN)
- Secure API endpoints with JWT validation

### Account Management
- Create bank accounts for users
- View account details and balance
- Multiple accounts per user support
- Different account types (SAVINGS, CHECKING)

### Transaction Management
- Deposit money
- Withdraw money with balance validation
- Transfer money between accounts
- Atomic transactions using @Transactional
- Transaction types: DEPOSIT, WITHDRAW, TRANSFER

### Transaction History
- Store all transactions in database
- View transaction history by account
- Pagination and sorting support

## 📋 Prerequisites

- Java 17
- Maven 3.6+
- MongoDB (running on localhost:27017)

## 🛠️ Setup Instructions

### 1. Install MongoDB

Make sure MongoDB is running on localhost:27017. You can use Docker:

```bash
docker run -d -p 27017:27017 --name mongodb mongo:latest
```

### 2. Build All Services

From the root directory:

```bash
mvn clean install
```

### 3. Start Services (in order)

**Step 1: Start Eureka Server**
```bash
cd eureka-server
mvn spring-boot:run
```
Wait for it to start at http://localhost:8761

**Step 2: Start Auth Service**
```bash
cd auth-service
mvn spring-boot:run
```

**Step 3: Start Account Service**
```bash
cd account-service
mvn spring-boot:run
```

**Step 4: Start Transaction Service**
```bash
cd transaction-service
mvn spring-boot:run
```

**Step 5: Start API Gateway**
```bash
cd api-gateway
mvn spring-boot:run
```

### 4. Verify Services

Visit Eureka Dashboard at http://localhost:8761 to see all registered services.

## 🔌 API Endpoints

All requests go through API Gateway at http://localhost:8080

### Authentication (No JWT required)

**Register User**
```bash
POST /api/auth/register
Content-Type: application/json

{
  "username": "john_doe",
  "email": "john@example.com",
  "password": "password123",
  "roles": ["USER"]
}
```

**Login**
```bash
POST /api/auth/login
Content-Type: application/json

{
  "username": "john_doe",
  "password": "password123"
}
```

Response includes JWT token to use for subsequent requests.

### Account Management (JWT required)

**Create Account**
```bash
POST /api/accounts
Authorization: Bearer <JWT_TOKEN>
Content-Type: application/json

{
  "userId": "user_id_from_login",
  "accountType": "SAVINGS",
  "currency": "USD"
}
```

**Get User Accounts**
```bash
GET /api/accounts/user/{userId}
Authorization: Bearer <JWT_TOKEN>
```

**Get Account Balance**
```bash
GET /api/accounts/{accountId}/balance
Authorization: Bearer <JWT_TOKEN>
```

### Transactions (JWT required)

**Deposit Money**
```bash
POST /api/transactions/deposit
Authorization: Bearer <JWT_TOKEN>
Content-Type: application/json

{
  "accountId": "account_id",
  "amount": 1000.00,
  "description": "Initial deposit"
}
```

**Withdraw Money**
```bash
POST /api/transactions/withdraw
Authorization: Bearer <JWT_TOKEN>
Content-Type: application/json

{
  "accountId": "account_id",
  "amount": 500.00,
  "description": "ATM withdrawal"
}
```

**Transfer Money**
```bash
POST /api/transactions/transfer
Authorization: Bearer <JWT_TOKEN>
Content-Type: application/json

{
  "fromAccountId": "account_id_1",
  "toAccountId": "account_id_2",
  "amount": 250.00,
  "description": "Transfer to friend"
}
```

**Get Transaction History**
```bash
GET /api/transactions/account/{accountId}?page=0&size=10&sortBy=createdAt&direction=DESC
Authorization: Bearer <JWT_TOKEN>
```

## 🔒 Security Features

- JWT authentication with HS512 algorithm
- BCrypt password hashing
- CORS configuration
- Input validation using @Valid
- Global exception handling
- Protected endpoints via API Gateway
- XSS and SQL Injection protection

## 🗄️ Database

- **MongoDB** - Separate databases for each service:
  - `bank_auth` - User data
  - `bank_accounts` - Account data
  - `bank_transactions` - Transaction data

## 🛡️ Best Practices Implemented

- RESTful API design
- Proper HTTP methods and status codes
- DTOs for request/response
- Service layer separation
- Repository pattern
- Global exception handling
- Input validation
- Atomic transactions
- Microservices communication via REST
- Service discovery with Eureka
- API Gateway pattern
- Load balancing

## 📝 Notes

- Default JWT expiration: 24 hours (configurable)
- All monetary values use BigDecimal for precision
- Transactions are atomic using @Transactional
- Services communicate via Eureka service discovery
- API Gateway handles JWT validation for protected routes

## 🧪 Testing

You can test the APIs using tools like:
- Postman
- cURL
- Any REST client

Sample workflow:
1. Register a new user
2. Login to get JWT token
3. Create an account
4. Perform deposits, withdrawals, and transfers
5. View transaction history
