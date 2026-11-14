# Startup Idea Validation Platform 🚀

A full-stack, enterprise-grade AI-powered Startup Idea Validation Platform built with **Java Spring Boot**, **MySQL**, and modern **HTML/CSS/JavaScript**.

Founders can submit startup ideas and receive structured AI validation containing:
- **Market Analysis & Feasibility**
- **Competitor Analysis & Market Share**
- **SWOT Analysis** (Strengths, Weaknesses, Opportunities, Threats)
- **AI Improvement Recommendations & Business Model Canvas (BMC)**
- **Stage-Aware Funding Roadmaps & Viability Scoring**
- **Saved Analysis Reports** and **User Dashboard**

---

## 🛠️ Technology Stack

| Layer | Technology |
|---|---|
| **Backend** | Java 21, Spring Boot 3.2.3, Spring Security 6, JWT (JJWT 0.11.5), Spring Data JPA |
| **Database** | MySQL 8.0 (InnoDB, UTF-8 MB4) |
| **Build Tool** | Apache Maven |
| **AI Integration** | OpenAI API (`gpt-4o-mini` / `gpt-4o`) with dynamic domain rules fallback |
| **Frontend** | HTML5, CSS3, Vanilla JavaScript (Fetch API, Responsive Dashboard) |
| **Security** | BCrypt Password Hashing, Stateless JWT Bearer Authentication, Role-based Access Control |

---

## 🏛️ Layered Backend Architecture

The backend follows clean layered architecture principles:
```
src/main/java/com/startup/
├── config/                  # SecurityConfig, OpenAiConfig
├── controller/              # AuthController, StartupIdeaController, AnalysisController, UserController, AdminController
├── dto/                     # Request and Response DTOs (Auth, Ideas, Analysis, Admin)
├── entity/                  # JPA Entities: User, StartupIdea, AnalysisReport, Role
├── exception/               # GlobalExceptionHandler, ResourceNotFoundException, APIException, etc.
├── repository/             # UserRepository, StartupIdeaRepository, AnalysisReportRepository
├── security/                # JwtTokenProvider, JwtAuthenticationFilter, CustomUserDetailsService
├── service/                 # UserService, StartupIdeaService, AnalysisService, OpenAiService, AdminService
└── StartupIdeaApplication.java # Spring Boot Main Application & .env loader
```

---

## 🗄️ MySQL Database Schema

The database strictly uses **MySQL** with three tables adhering to primary/foreign key constraints and indexing:

1. **`users`**
   - `id` (BIGINT, PK, AUTO_INCREMENT)
   - `username` (VARCHAR 50, UNIQUE)
   - `email` (VARCHAR 100, UNIQUE)
   - `password` (VARCHAR 255, BCrypt hashed)
   - `full_name` (VARCHAR 100)
   - `role` (VARCHAR 20, ROLE_USER / ROLE_ADMIN)
   - `created_at`, `updated_at` (TIMESTAMP)

2. **`startup_ideas`**
   - `id` (BIGINT, PK, AUTO_INCREMENT)
   - `user_id` (BIGINT, FK -> users.id, ON DELETE CASCADE)
   - `title` (VARCHAR 150)
   - `description` (TEXT)
   - `industry` (VARCHAR 100)
   - `target_audience` (VARCHAR 255)
   - `stage` (VARCHAR 50)
   - `created_at`, `updated_at` (TIMESTAMP)

3. **`analysis_reports`**
   - `id` (BIGINT, PK, AUTO_INCREMENT)
   - `idea_id` (BIGINT, UNIQUE, FK -> startup_ideas.id, ON DELETE CASCADE)
   - `market_analysis` (MEDIUMTEXT)
   - `competitor_analysis` (MEDIUMTEXT)
   - `swot_strengths`, `swot_weaknesses`, `swot_opportunities`, `swot_threats` (MEDIUMTEXT)
   - `improvement_suggestions` (MEDIUMTEXT)
   - `viability_score` (INT)
   - `created_at`, `updated_at` (TIMESTAMP)

---

## 🔐 Credentials & Environment Setup

Database credentials and the OpenAI API key are **never committed to Git**. They are managed via environment variables or a gitignored `.env` file:

Create a `.env` file in the project root:
```env
DB_URL=jdbc:mysql://localhost:3306/startup_idea_db?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
DB_USERNAME=root
DB_PASSWORD=YourSecurePassword
JWT_SECRET=404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970
OPENAI_API_KEY=sk-your-openai-api-key-here
OPENAI_MODEL=gpt-4o-mini
```

---

## 📡 REST API Documentation

### 1. Authentication (`/api/auth`)
| Method | Endpoint | Description | Auth Required |
|---|---|---|---|
| `POST` | `/api/auth/register` | Register new user account | Public |
| `POST` | `/api/auth/login` | Authenticate and obtain JWT Bearer Token | Public |
| `GET` | `/api/auth/me` | Fetch currently authenticated user profile | Bearer Token |

### 2. User Profile (`/api/users`)
| Method | Endpoint | Description | Auth Required |
|---|---|---|---|
| `GET` | `/api/users/profile` | Retrieve user profile details | Bearer Token |
| `PUT` | `/api/users/profile` | Update full name and password | Bearer Token |
| `GET` | `/api/users/health` | Service health status check | Public |

### 3. Startup Ideas (`/api/ideas`)
| Method | Endpoint | Description | Auth Required |
|---|---|---|---|
| `POST` | `/api/ideas` | Submit a new startup idea | Bearer Token |
| `GET` | `/api/ideas` | List current user's ideas (`?search=`, `?industry=`) | Bearer Token |
| `GET` | `/api/ideas/{id}` | Get idea details by ID | Bearer Token |
| `PUT` | `/api/ideas/{id}` | Update an existing idea | Bearer Token |
| `DELETE`| `/api/ideas/{id}` | Delete an idea and its analysis report | Bearer Token |

### 4. AI Validation Analysis & Reports (`/api`)
| Method | Endpoint | Description | Auth Required |
|---|---|---|---|
| `POST` | `/api/ideas/{id}/analyze` | Trigger AI validation scan and save report | Bearer Token |
| `GET` | `/api/ideas/{id}/analysis` | Retrieve aggregated validation analysis | Bearer Token |
| `GET` | `/api/ideas/{id}/report` | Retrieve saved report from `analysis_reports` | Bearer Token |
| `GET` | `/api/reports` | Retrieve all saved reports for user | Bearer Token |

### 5. Administration (`/api/admin`)
| Method | Endpoint | Description | Auth Required |
|---|---|---|---|
| `GET` | `/api/admin/stats` | Platform statistics (total users, ideas, validations) | ADMIN Role |
| `GET` | `/api/admin/users` | List all platform users | ADMIN Role |
| `GET` | `/api/admin/ideas` | List all ideas across the platform | ADMIN Role |
| `DELETE`| `/api/admin/ideas/{id}` | Delete any idea (Admin action) | ADMIN Role |
| `DELETE`| `/api/admin/users/{id}` | Delete a user (Admin action) | ADMIN Role |

---

## 🚀 How to Run

### Step 1: Ensure MySQL is Running
Make sure MySQL 8.0 is running and the database `startup_idea_db` is created (or let Spring auto-create it via the connection string).
```sql
CREATE DATABASE IF NOT EXISTS startup_idea_db;
```

### Step 2: Run Spring Boot
Run using Maven:
```powershell
mvn spring-boot:run
```
Or execute the automated runner script:
```powershell
.\run_project.ps1
```

### Step 3: Access the Web Application
Open your browser and navigate to:
```
http://localhost:8080
```
- **Landing Page**: `http://localhost:8080/index.html`
- **Register**: `http://localhost:8080/register.html`
- **Login**: `http://localhost:8080/login.html`
- **Dashboard**: `http://localhost:8080/dashboard.html`
- **Submit Idea**: `http://localhost:8080/submit-idea.html`
- **AI Analysis**: `http://localhost:8080/analysis.html?id=1`
- **Admin Panel**: `http://localhost:8080/admin.html`

---

## 🧪 Testing

Run the automated integration test suite:
```powershell
powershell -ExecutionPolicy Bypass -File .\test_e2e_api.ps1
```
Tests all 7 phases:
1. Application startup & HikariCP database connectivity
2. Static frontend serving
3. Registration API & BCrypt hashing
4. Login API & JWT token generation
5. Startup idea submission & persistence
6. AI validation scan & report generation
7. Saved report retrieval from MySQL `analysis_reports`
