# Finance Tracker Backend

A comprehensive Finance Tracker REST API built with Spring Boot 3.x, Java 21, and MongoDB.

## Tech Stack

- **Java 21** - Latest LTS version with modern features
- **Spring Boot 3.2.2** - Production-ready framework
- **Spring Security** - JWT-based authentication
- **Spring Data MongoDB** - Database integration
- **Maven** - Build and dependency management
- **Lombok** - Reduce boilerplate code
- **MapStruct** - Object mapping
- **JJWT** - JWT token handling

## Project Structure

```
src/main/java/com/financetracker/
├── config/          # Configuration classes
├── controller/      # REST controllers
├── dto/             # Data Transfer Objects
├── entity/          # MongoDB documents
├── exception/       # Custom exceptions & global handler
├── mapper/          # MapStruct mappers
├── repository/      # MongoDB repositories
├── security/        # JWT & Security components
├── service/         # Service interfaces
├── service/impl/    # Service implementations
└── util/            # Utility classes
```

## API Endpoints

### Authentication
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/auth/register` | Register new user |
| POST | `/api/auth/login` | Login user |

### User Profile
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/users/profile` | Get current user profile |
| PUT | `/api/users/profile` | Update profile |

### Expenses
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/expenses` | Get all expenses (paginated) |
| GET | `/api/expenses/{id}` | Get expense by ID |
| POST | `/api/expenses` | Create new expense |
| PUT | `/api/expenses/{id}` | Update expense |
| DELETE | `/api/expenses/{id}` | Delete expense |
| GET | `/api/expenses/stats/monthly` | Monthly statistics |
| GET | `/api/expenses/categories` | List categories |

### Savings Goals
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/savings/goals` | Get all savings goals |
| GET | `/api/savings/goals/{id}` | Get goal by ID |
| POST | `/api/savings/goals` | Create savings goal |
| PUT | `/api/savings/goals/{id}` | Update goal |
| DELETE | `/api/savings/goals/{id}` | Delete goal |
| POST | `/api/savings/goals/{id}/deposit` | Add money to goal |
| GET | `/api/savings/progress` | Overall progress |

### Split Bills
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/split-bills` | Get all split bills |
| GET | `/api/split-bills/{id}` | Get bill details |
| POST | `/api/split-bills` | Create split bill |
| PUT | `/api/split-bills/{id}` | Update bill |
| DELETE | `/api/split-bills/{id}` | Delete bill |
| POST | `/api/split-bills/{id}/participants` | Add participants |
| PUT | `/api/split-bills/{id}/participants/{pid}/pay` | Mark as paid |
| GET | `/api/split-bills/summary` | Balance summary |

### Budget
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/budget` | Get current budget |
| POST | `/api/budget` | Create/update budget |

### Dashboard
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/dashboard/stats` | Dashboard statistics |
| GET | `/api/dashboard/spending-breakdown` | By category |
| GET | `/api/dashboard/spending-trends` | Monthly trends |

## Getting Started

### Prerequisites

- Java 21+
- Maven 3.8+
- MongoDB 6.0+

### Configuration

Update `src/main/resources/application.yml`:

```yaml
spring:
  data:
    mongodb:
      uri: mongodb://localhost:27017/finance_tracker

jwt:
  secret: your-base64-encoded-secret-key
  expiration: 86400000  # 24 hours
```

### Running the Application

```bash
# Build the project
mvn clean install

# Run the application
mvn spring-boot:run
```

The API will be available at `http://localhost:8080`

### Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `MONGODB_URI` | MongoDB connection string | `mongodb://localhost:27017/finance_tracker` |
| `JWT_SECRET` | Base64 encoded JWT secret | (development key) |

## API Response Format

All endpoints return responses in a standard format:

```json
{
  "success": true,
  "message": "Success",
  "data": { ... },
  "timestamp": "2026-01-26T10:30:00"
}
```

## Authentication

Include the JWT token in the Authorization header:

```
Authorization: Bearer <your-jwt-token>
```

## Default Expense Categories

- Food & Dining
- Transportation
- Shopping
- Entertainment
- Bills & Utilities
- Healthcare
- Education
- Travel
- Other

## License

MIT License
