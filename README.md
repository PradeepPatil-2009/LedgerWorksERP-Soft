# LedgerWorks ERP

LedgerWorks ERP is a full-stack enterprise resource planning application built with a
**React** front end ([`ledgerworks-ui`](ledgerworks-ui)), a **Spring Boot** back end
([`ledgerworks-backEnd`](ledgerworks-backEnd)), and a **MySQL** database.

## Overview

The system covers the core operational and financial flows of a manufacturing/trading
business through the following modules: **Masters** (customers, vendors, item masters,
ledger accounts), **Inventory** (stock levels and stock ledger), **Purchase** (vendor
purchases), **Sales** (invoices and invoice payments), **Production** (bills of materials,
production runs, material issues and production costing), **Delivery Challan**,
**Accounting** (journal entries, ledger transactions, trial balance, balance sheet,
profit & loss, cash flow), **GST** (GST reports and exports), **Reports** (aging,
outstanding, ledger statements, dashboards), and **Backup** (data backup, download and
restore). Authentication is JWT-based with role-based authorization.

## Prerequisites

- **Java 17** (JDK)
- **Maven 3.9+**
- **Node.js 18+** and **npm**
- **MySQL 8 or 9** running locally

## Running the Backend

The backend is a Spring Boot 3.2.5 / Java 17 Maven project.

```bash
cd ledgerworks-backEnd
mvn spring-boot:run
```

- The database is **auto-created** on first run (the JDBC URL uses
  `createDatabaseIfNotExist=true` and Hibernate `ddl-auto=update`).
- The default datasource connects as user **`root`** with an **empty password**.
- Override the defaults via environment variables:

  | Variable        | Default                                                                 |
  | --------------- | ----------------------------------------------------------------------- |
  | `DB_URL`        | `jdbc:mysql://localhost:3306/ledgerworks?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC` |
  | `DB_USERNAME`   | `root`                                                                   |
  | `DB_PASSWORD`   | *(empty)*                                                                |
  | `JWT_SECRET`    | built-in development secret                                              |
  | `SERVER_PORT`   | `8080`                                                                   |

- Backend runs at **http://localhost:8080**
- Swagger UI: **http://localhost:8080/swagger-ui/index.html**

Example with overrides:

```bash
DB_USERNAME=root DB_PASSWORD=secret JWT_SECRET=change-me SERVER_PORT=8080 mvn spring-boot:run
```

## Running the Frontend

The frontend is a Create React App project (React 19, react-router-dom 7).

```bash
cd ledgerworks-ui
npm install
npm start
```

- App runs at **http://localhost:3000**
- By default the UI talks to the backend at `http://localhost:8080`. Override the API base
  URL with the `REACT_APP_API_URL` environment variable:

  ```bash
  REACT_APP_API_URL=http://localhost:8080 npm start
  ```

## Default Login & Roles

A default administrator account is seeded automatically on startup:

- **Username:** `admin`
- **Password:** `admin123`
- **Role:** `ADMIN`

There are four roles:

| Role         | Description                                                          |
| ------------ | ------------------------------------------------------------------- |
| `ADMIN`      | Full access, including user management and backup restore/delete.    |
| `ACCOUNTANT` | Accounting and financial operations.                                |
| `USER`       | Standard operational access.                                        |
| `VIEWER`     | Read-only access.                                                   |

The **Users** management page and the **backup restore/delete** operations are
**ADMIN-only**.

## Running Tests

**Backend** (uses an in-memory H2 database — no MySQL required):

```bash
cd ledgerworks-backEnd
mvn test
```

**Frontend:**

```bash
cd ledgerworks-ui
npm test
```

## What changed on the `develop` branch

- Restored the real Spring Boot backend (the `main` branch shipped an empty skeleton)
  from an archived zip.
- Replaced the hardcoded `admin`/`admin123` dummy login with real **JWT authentication**
  and **role-based authorization**.
- Added **global JWT attachment** so the token is automatically sent on all axios and
  fetch requests.
- Introduced a **cross-platform, config-driven `BackupService`**.
- Added **SLF4J logging** across the backend.
- Added UI **toast notifications** and a **responsive layout**.
- Added **JUnit tests (H2)** for the backend and **frontend tests**.
