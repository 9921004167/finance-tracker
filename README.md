# Ledger — Personal Finance Tracker

A full-stack personal finance tracker demonstrating modern Java + React development:
**Spring Boot 3 (Java 17) REST API** with JWT authentication and MySQL, paired with a
**React (Vite) frontend** with chart-based dashboards.

## Features

- **Authentication** — signup, login, JWT-based session management. Every user only ever
  sees their own transactions and budgets.
- **Transaction management** — add, edit, and delete income/expense entries with category,
  date, amount, and description. Filter by type, category, and date range with pagination.
- **Dashboard** — monthly income/expense/net-balance summary, spending-by-category pie chart,
  and a 6-month income vs. expense trend chart (Recharts).
- **Budget analytics** — set a monthly spending limit per category and see live progress on a
  circular "budget dial" gauge that turns from mint → amber → coral as you approach and exceed
  the limit, plus a dashboard alert banner when categories go over budget.
- **Data export** — download a monthly report as a formatted PDF (OpenPDF) or raw CSV
  (OpenCSV) for spreadsheets/bookkeeping.

## Tech stack

| Layer      | Technology                                                              |
|------------|---------------------------------------------------------------------------|
| Backend    | Java 17, Spring Boot 3, Spring Security, Spring Data JPA, Maven          |
| Auth       | JWT (jjwt), BCrypt password hashing                                      |
| Database   | MySQL 8                                                                   |
| Export     | OpenPDF (PDF), OpenCSV (CSV)                                             |
| Frontend   | React 19, Vite, React Router, Axios, Recharts, lucide-react              |
| Tooling    | Docker / docker-compose, Git                                             |

## Project structure

```
finance-tracker/
├── backend/                 Spring Boot REST API
│   ├── src/main/java/com/financetracker/
│   │   ├── config/          Security & CORS configuration
│   │   ├── security/        JWT filter, JWT utility, UserDetailsService
│   │   ├── model/            JPA entities (User, Category, Transaction, Budget)
│   │   ├── repository/      Spring Data JPA repositories
│   │   ├── dto/               Request/response DTOs
│   │   ├── service/          Business logic
│   │   ├── controller/       REST controllers
│   │   └── exception/        Global exception handling
│   ├── src/main/resources/application.yml
│   ├── pom.xml
│   └── Dockerfile
├── frontend/                 React (Vite) SPA
│   ├── src/
│   │   ├── api/                Axios API clients (one per resource)
│   │   ├── context/           AuthContext (JWT session state)
│   │   ├── components/        Navbar, forms, tables, charts, UI primitives
│   │   ├── pages/               Login, Signup, Dashboard, Transactions, Budgets, Reports
│   │   └── styles/              Design tokens (CSS variables)
│   ├── package.json
│   └── Dockerfile
└── docker-compose.yml         MySQL + backend + frontend, wired together
```

## Running with Docker Compose (recommended)

This starts MySQL, the backend, and the frontend together.

```bash
cd finance-tracker
docker compose up --build
```

- Frontend: http://localhost:8081
- Backend API: http://localhost:8080/api
- MySQL: localhost:3306 (user `root`, password `root`, db `finance_tracker`)

## Running locally without Docker

### 1. Database

Start a local MySQL 8 instance and create the database (or let Hibernate create it — the
default config uses `createDatabaseIfNotExist=true`):

```sql
CREATE DATABASE finance_tracker;
```

### 2. Backend

Requires JDK 17 and Maven 3.9+.

```bash
cd backend
# Configure DB credentials via env vars if they differ from the defaults (root/root)
export DB_USERNAME=root
export DB_PASSWORD=root
mvn spring-boot:run
```

The API starts on `http://localhost:8080`. Health check: `GET /api/health`.

**JWT secret**: `application.yml` ships with a development default (`JWT_SECRET`). For
anything beyond local testing, generate your own 64-byte secret and export it, e.g.:

```bash
export JWT_SECRET=$(openssl rand -hex 64)
```

### 3. Frontend

Requires Node 18+.

```bash
cd frontend
cp .env.example .env   # points VITE_API_BASE_URL at http://localhost:8080/api
npm install
npm run dev
```

The app starts on `http://localhost:5173`.

## API overview

All endpoints except `/api/auth/**` and `/api/health` require a `Authorization: Bearer <token>`
header, obtained from `/api/auth/login` or `/api/auth/signup`.

| Method | Endpoint                          | Description                              |
|--------|-------------------------------------|-------------------------------------------|
| POST   | `/api/auth/signup`                 | Create an account, returns a JWT          |
| POST   | `/api/auth/login`                  | Authenticate, returns a JWT               |
| GET    | `/api/categories`                  | List categories visible to the user       |
| POST   | `/api/categories`                  | Create a custom category                  |
| DELETE | `/api/categories/{id}`             | Delete a custom category                  |
| GET    | `/api/transactions`                | Search/paginate transactions              |
| POST   | `/api/transactions`                | Create a transaction                      |
| PUT    | `/api/transactions/{id}`           | Update a transaction                      |
| DELETE | `/api/transactions/{id}`           | Delete a transaction                      |
| GET    | `/api/budgets?month=&year=`        | List budgets (with spend/progress) for a month |
| POST   | `/api/budgets`                     | Set a monthly budget for a category       |
| PUT    | `/api/budgets/{id}`                | Update a budget                           |
| DELETE | `/api/budgets/{id}`                | Delete a budget                           |
| GET    | `/api/dashboard/summary?month=&year=` | Aggregated totals + chart data for the dashboard |
| GET    | `/api/reports/csv?month=&year=`    | Download a monthly CSV export             |
| GET    | `/api/reports/pdf?month=&year=`    | Download a monthly PDF report             |

## Notes on design decisions

- **Global vs. custom categories**: every new account is seeded with a starter set of
  income/expense categories (shared, `user_id IS NULL`) so the dashboard isn't empty on day
  one; users can also add their own custom categories, which are private to them.
- **Budget uniqueness**: one budget per (user, category, month, year) — enforced at the DB
  level with a unique constraint and checked in `BudgetService` before insert.
- **Stateless auth**: sessions are not stored server-side; the JWT itself carries the identity,
  validated on every request by `JwtAuthenticationFilter`.
- **Ownership checks**: every query in `TransactionRepository` / `BudgetRepository` /
  `CategoryRepository` is scoped by the authenticated user's id, so there is no path for one
  user to read or modify another user's data.

## Version control

This project is initialized as a git repository at the root (`finance-tracker/.git`) with
backend and frontend `.gitignore` files excluding build artifacts (`target/`, `node_modules/`,
`dist/`) and local env files.
