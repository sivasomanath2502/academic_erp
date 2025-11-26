# Academic ERP - Complete Project Workflow & Architecture Guide

## 📋 Table of Contents
1. [Project Overview](#project-overview)
2. [System Architecture](#system-architecture)
3. [Authentication Flow](#authentication-flow)
4. [Student Admission Workflow](#student-admission-workflow)
5. [Data Flow Diagrams](#data-flow-diagrams)
6. [Folder Structure](#folder-structure)
7. [Key Components Explained](#key-components-explained)
8. [API Endpoints](#api-endpoints)
9. [Database Schema](#database-schema)

---

## 🎯 Project Overview

**Academic ERP** is a student admission management system with:
- **Frontend**: React + TypeScript + Vite + Tailwind CSS
- **Backend**: Spring Boot + MySQL + Spring Security
- **Authentication**: Google OAuth 2.0 (Server-side flow)
- **Purpose**: Allow authorized users (emails starting with "erphead") to admit students and view student records

---

## 🏗️ System Architecture

```
┌─────────────────┐         ┌─────────────────┐         ┌─────────────────┐
│   React Frontend│         │  Spring Boot    │         │   MySQL Database│
│   (Port 5173)   │◄───────►│   (Port 8080)   │◄───────►│   (Port 3306)   │
│                 │         │                 │         │                 │
│  - WelcomePage │         │  - Controllers  │         │  - students     │
│  - AddStudent  │         │  - Services    │         │  - domains     │
│  - ViewStudents│         │  - Repositories│         │  - specialisations│
│  - AuthContext │         │  - Security     │         │  - placements   │
└─────────────────┘         └─────────────────┘         └─────────────────┘
         │                           │
         │                           │
         └───────────────────────────┘
              Google OAuth 2.0
         (accounts.google.com)
```

### Technology Stack

**Frontend:**
- React 19
- TypeScript
- Vite (Build tool)
- Tailwind CSS (Styling)
- React Router (Routing)
- Axios (HTTP client)

**Backend:**
- Spring Boot 3.x
- Spring Security (Authentication)
- Spring Data JPA (Database access)
- MySQL (Database)
- Maven (Build tool)

---

## 🔐 Authentication Flow

### Complete OAuth 2.0 Flow

```
┌──────────┐     1. Click "Sign in"      ┌──────────┐
│  User    │─────────────────────────────►│ Frontend │
│          │                              │          │
└──────────┘                              └────┬─────┘
                                               │
                                               │ 2. Redirect to /login
                                               ▼
                                        ┌──────────────┐
                                        │   Backend    │
                                        │ /login       │
                                        └──────┬───────┘
                                               │
                                               │ 3. Redirect to Google
                                               ▼
                                        ┌──────────────┐
                                        │   Google     │
                                        │ OAuth Server │
                                        └──────┬───────┘
                                               │
                                               │ 4. User authenticates
                                               │    & grants permission
                                               ▼
                                        ┌──────────────┐
                                        │   Google     │
                                        │ Redirects to │
                                        │ /oauth2/     │
                                        │ callback?code│
                                        └──────┬───────┘
                                               │
                                               │ 5. Authorization code
                                               ▼
                                        ┌──────────────┐
                                        │   Backend    │
                                        │ /oauth2/     │
                                        │ callback     │
                                        └──────┬───────┘
                                               │
                                               │ 6. Exchange code for tokens
                                               ▼
                                        ┌──────────────┐
                                        │ TokenService │
                                        │ - Exchange   │
                                        │ - Validate   │
                                        └──────┬───────┘
                                               │
                                               │ 7. Store tokens
                                               │    - ID token → Cookie
                                               │    - Access/Refresh → Session
                                               ▼
                                        ┌──────────────┐
                                        │   Backend    │
                                        │ Sets cookie │
                                        │ Redirects to│
                                        │ frontend    │
                                        └──────┬───────┘
                                               │
                                               │ 8. Redirect based on email
                                               ▼
                    ┌──────────────────────────┴──────────────────────────┐
                    │                                                      │
                    ▼                                                      ▼
        ┌──────────────────┐                              ┌──────────────────┐
        │ erphead* email  │                              │ Other emails     │
        │ → /add-student  │                              │ → /access-denied │
        └──────────────────┘                              └──────────────────┘
```

### Step-by-Step Authentication Process

#### 1. **User Initiates Login** (`WelcomePage.tsx`)
```typescript
// User clicks "Sign in with Google"
handleLogin() {
  window.location.href = `${API_BASE_URL}/login`
}
```

#### 2. **Backend Redirects to Google** (`OAuthController.java`)
```java
@GetMapping("/login")
public RedirectView login() {
    // Build Google OAuth URL
    String authUrl = "https://accounts.google.com/o/oauth2/v2/auth?...";
    return new RedirectView(authUrl);
}
```

#### 3. **Google Authentication**
- User logs in with Google account
- Google shows consent screen
- User grants permissions

#### 4. **Google Redirects Back** (`/oauth2/callback`)
```java
@GetMapping("/oauth2/callback")
public RedirectView callback(@RequestParam String code) {
    // 1. Exchange authorization code for tokens
    TokenExchangeResponse tokens = tokenService.exchangeCode(code);
    
    // 2. Validate ID token
    TokenInfoResponse userInfo = tokenService.validateIdToken(tokens.getIdToken());
    
    // 3. Store tokens
    // - ID token → HTTP-only cookie
    // - Access/Refresh → Session
    
    // 4. Check authorization
    if (userInfo.getEmail().startsWith("erphead")) {
        return new RedirectView("http://localhost:5173/add-student");
    } else {
        return new RedirectView("http://localhost:5173/access-denied");
    }
}
```

#### 5. **Token Validation on Each Request** (`JwtAuthenticationFilter.java`)
```java
// Runs on every API request
protected void doFilterInternal(...) {
    // 1. Extract ID token from cookie
    String idToken = extractIdTokenFromCookie(request);
    
    // 2. Validate token with Google
    TokenInfoResponse userInfo = tokenService.validateIdToken(idToken);
    
    // 3. Check if email starts with "erphead"
    if (userInfo.getEmail().startsWith("erphead")) {
        // Set authentication in Spring Security context
        SecurityContextHolder.getContext().setAuthentication(...);
    }
}
```

#### 6. **Frontend Session Management** (`AuthContext.tsx`)
```typescript
// On app load, check if user is authenticated
useEffect(() => {
  refreshUser() // Calls GET /api/auth/me
}, [])

// refreshUser() fetches user info from backend
const refreshUser = async () => {
  const { data } = await apiClient.get('/api/auth/me')
  setUser(data)
}
```

---

## 📝 Student Admission Workflow

### Complete Admission Process

```
┌──────────┐
│   User   │
│ (erphead)│
└────┬─────┘
     │
     │ 1. Navigate to /add-student
     ▼
┌─────────────────────────────────┐
│      AddStudentPage.tsx         │
│                                 │
│  ┌──────────────────────────┐  │
│  │  Load Domains            │  │
│  │  GET /api/domains        │  │
│  └──────────────────────────┘  │
│                                 │
│  ┌──────────────────────────┐  │
│  │  Fill Form:               │  │
│  │  - First Name             │  │
│  │  - Last Name              │  │
│  │  - Email                  │  │
│  │  - Domain (dropdown)      │  │
│  │  - Join Year              │  │
│  │  - Photo (upload)         │  │
│  └──────────────────────────┘  │
│                                 │
│  ┌──────────────────────────┐  │
│  │  Upload Photo            │  │
│  │  POST /api/uploads/photo │  │
│  │  → Store on filesystem   │  │
│  │  → Return path           │  │
│  └──────────────────────────┘  │
│                                 │
│  ┌──────────────────────────┐  │
│  │  Submit Form             │  │
│  │  POST /api/students/admit│  │
│  └──────────────────────────┘  │
     │
     ▼
┌─────────────────────────────────┐
│   AdmissionController.java     │
│   POST /api/students/admit     │
└────────────┬──────────────────┘
             │
             ▼
┌─────────────────────────────────┐
│   AdmissionServiceImpl.java     │
│                                 │
│  1. Validate request            │
│  2. Find domain                 │
│  3. Generate roll number        │
│  4. Store photo path            │
│  5. Save student to DB          │
│  6. Return response             │
└────────────┬──────────────────┘
             │
             ▼
┌─────────────────────────────────┐
│      RollNumberGenerator        │
│                                 │
│  Format: XXYYYYDDD              │
│  - XX: Degree prefix            │
│    (MT=M.Tech, BT=B.Tech, MS)  │
│  - YYYY: Join year              │
│  - DDD: Department sequence     │
│    (CSE: 001-200)               │
│    (ECE: 501-600)               │
│    (AIDS: 701-800)              │
└────────────┬──────────────────┘
             │
             ▼
┌─────────────────────────────────┐
│      StudentRepository          │
│      .save(student)            │
└────────────┬──────────────────┘
             │
             ▼
┌─────────────────────────────────┐
│      MySQL Database             │
│      students table             │
└─────────────────────────────────┘
```

### Detailed Steps

#### 1. **Page Load** (`AddStudentPage.tsx`)
```typescript
useEffect(() => {
  loadDomains() // Fetch available domains
}, [])

const loadDomains = async () => {
  const { data } = await apiClient.get('/api/domains')
  setDomains(data)
}
```

#### 2. **Photo Upload** (`AddStudentPage.tsx`)
```typescript
const handlePhotoUpload = async (file: File) => {
  const formData = new FormData()
  formData.append('file', file)
  
  // Upload to backend
  const { data } = await apiClient.post('/api/uploads/photo', formData)
  
  // Store path in form state
  setForm(prev => ({ ...prev, photographPath: data.path }))
}
```

**Backend Photo Handling:**
```java
@PostMapping("/api/uploads/photo")
public PhotoUploadResponse uploadPhoto(@RequestParam MultipartFile file) {
    // 1. Validate file type and size
    // 2. Generate unique filename (UUID)
    // 3. Store on filesystem: uploads/photos/{uuid}.{ext}
    // 4. Return path: /uploads/photos/{uuid}.{ext}
    return new PhotoUploadResponse(path, filename, size);
}
```

#### 3. **Form Submission** (`AddStudentPage.tsx`)
```typescript
const handleSubmit = async (event: FormEvent) => {
  event.preventDefault()
  
  const payload = {
    firstName: form.firstName.trim(),
    lastName: form.lastName.trim(),
    email: form.email.trim(),
    domainId: Number(form.domainId),
    joinYear: Number(form.joinYear),
    photographPath: form.photographPath
  }
  
  const { data } = await apiClient.post('/api/students/admit', payload)
  // Show success message with roll number
}
```

#### 4. **Backend Processing** (`AdmissionServiceImpl.java`)
```java
public StudentResponseDto admitStudent(StudentAdmissionRequestDto request) {
    // 1. Find domain
    Domain domain = domainRepository.findById(request.getDomainId())
        .orElseThrow(() -> new RuntimeException("Domain not found"));
    
    // 2. Generate roll number
    String rollNumber = rollNumberGenerator.generate(
        domain.getDegree(),
        request.getJoinYear(),
        domain.getSpecialisation()
    );
    
    // 3. Create student entity
    Student student = Student.builder()
        .firstName(request.getFirstName())
        .lastName(request.getLastName())
        .email(request.getEmail())
        .domain(domain)
        .joinYear(request.getJoinYear())
        .photographPath(request.getPhotographPath())
        .rollNumber(rollNumber)
        .build();
    
    // 4. Save to database
    studentRepository.save(student);
    
    // 5. Return response
    return StudentResponseDto.builder()
        .rollNumber(rollNumber)
        .firstName(student.getFirstName())
        // ... other fields
        .build();
}
```

#### 5. **Roll Number Generation** (`RollNumberGenerator.java`)
```java
public String generate(String degree, Integer joinYear, Specialisation spec) {
    // 1. Get degree prefix (MT, BT, MS)
    String degreePrefix = getDegreePrefix(degree);
    
    // 2. Get year (last 4 digits)
    String year = String.valueOf(joinYear);
    
    // 3. Get department range
    DepartmentRange range = getDepartmentRange(spec);
    
    // 4. Find next available sequence number
    Integer nextSeq = findNextSequence(joinYear, spec);
    
    // 5. Format: XXYYYYDDD
    return String.format("%s%s%03d", degreePrefix, year, nextSeq);
}
```

---

## 📊 Data Flow Diagrams

### Request Flow for Protected Routes

```
Frontend Request
    │
    ▼
ProtectedRoute Component
    │
    ├─► Check: user exists?
    │   └─► No → Redirect to /
    │
    ├─► Check: isAuthorized? (email starts with "erphead")
    │   └─► No → Redirect to /access-denied
    │
    └─► Yes → Render protected component
            │
            ▼
        API Request (with cookie)
            │
            ▼
        JwtAuthenticationFilter
            │
            ├─► Extract ID token from cookie
            ├─► Validate with Google
            ├─► Check email prefix
            └─► Set authentication in SecurityContext
                    │
                    ▼
                Controller
                    │
                    ▼
                Service
                    │
                    ▼
                Repository
                    │
                    ▼
                Database
```

### Photo Upload Flow

```
User selects photo
    │
    ▼
Frontend: FormData
    │
    ▼
POST /api/uploads/photo
    │
    ▼
PhotoUploadController
    │
    ▼
PhotoStorageService
    │
    ├─► Validate file type (JPEG, PNG, GIF, WEBP)
    ├─► Validate file size (< 5MB)
    ├─► Generate UUID filename
    ├─► Store on filesystem: uploads/photos/{uuid}.{ext}
    └─► Return path: /uploads/photos/{uuid}.{ext}
            │
            ▼
        Frontend stores path in form
            │
            ▼
        Submit form with path (not file)
            │
            ▼
        Backend saves path to database (VARCHAR, not BLOB)
```

---

## 📁 Folder Structure

### Frontend Structure
```
frontend/academic-erp-frotnend/
├── src/
│   ├── assets/              # Static assets (images, icons)
│   ├── components/          # Reusable React components
│   │   ├── AppLayout.tsx    # Main layout with header/nav
│   │   └── ProtectedRoute.tsx # Route guard
│   ├── context/             # React Context providers
│   │   └── AuthContext.tsx  # Authentication state management
│   ├── models/              # TypeScript interfaces/types
│   │   └── index.ts         # Domain, Student, UserProfile, etc.
│   ├── pages/               # Page components
│   │   ├── WelcomePage.tsx      # Login page
│   │   ├── AccessDeniedPage.tsx # Unauthorized access
│   │   ├── AddStudentPage.tsx   # Student admission form
│   │   └── ViewStudentsPage.tsx # Student list/view
│   ├── utils/               # Utility functions
│   │   ├── api.ts           # Axios client & endpoints
│   │   └── useAuth.ts       # Auth hook wrapper
│   ├── App.tsx              # Main app component (routing)
│   ├── main.tsx             # Entry point
│   └── index.css            # Global styles (Tailwind)
├── public/                  # Public static files
├── package.json             # Dependencies
├── vite.config.ts           # Vite configuration
├── tailwind.config.js       # Tailwind CSS config
└── tsconfig.json            # TypeScript config
```

### Backend Structure
```
backend/
├── src/main/java/com/academic/erp/backend/
│   ├── Application.java              # Spring Boot entry point
│   ├── config/                      # Configuration classes
│   │   ├── SecurityConfig.java      # Spring Security setup
│   │   └── WebConfig.java           # Static resource serving
│   ├── controller/                  # REST controllers
│   │   ├── OAuthController.java     # OAuth login/callback/logout
│   │   ├── AdmissionController.java # Student admission
│   │   ├── DomainController.java    # Domain listing
│   │   ├── StudentQueryController.java # Student queries
│   │   └── PhotoUploadController.java  # Photo uploads
│   ├── dto/                         # Data Transfer Objects
│   │   ├── StudentAdmissionRequestDto.java
│   │   ├── StudentResponseDto.java
│   │   ├── TokenExchangeResponse.java
│   │   └── TokenInfoResponse.java
│   ├── entity/                      # JPA entities (database models)
│   │   ├── Student.java
│   │   ├── Domain.java
│   │   ├── Specialisation.java
│   │   ├── Placement.java
│   │   └── Organisation.java
│   ├── repository/                  # Spring Data JPA repositories
│   │   ├── StudentRepository.java
│   │   ├── DomainRepository.java
│   │   └── ...
│   ├── service/                     # Business logic
│   │   ├── AdmissionService.java
│   │   ├── AdmissionServiceImpl.java
│   │   ├── TokenService.java        # OAuth token handling
│   │   ├── PhotoStorageService.java # File storage
│   │   └── RollNumberGenerator.java # Roll number logic
│   ├── filter/                      # Spring Security filters
│   │   └── JwtAuthenticationFilter.java # Token validation
│   └── exception/                   # Exception handling
│       └── GlobalExceptionHandler.java
└── src/main/resources/
    └── application.properties       # Configuration
```

---

## 🔧 Key Components Explained

### Frontend Components

#### 1. **AuthContext** (`context/AuthContext.tsx`)
**Purpose**: Manages authentication state globally

**Key Functions:**
- `refreshUser()`: Fetches current user from `/api/auth/me`
- `login()`: Sets user in state
- `logout()`: Clears user and calls `/signout`
- `isAuthorized`: Checks if email starts with "erphead"

**Usage:**
```typescript
const { user, isAuthorized, logout } = useAuth()
```

#### 2. **ProtectedRoute** (`components/ProtectedRoute.tsx`)
**Purpose**: Guards routes that require authentication

**Logic:**
1. Check if `isLoading` → Show loading spinner
2. Check if `user` exists → Redirect to `/` if not
3. Check if `isAuthorized` → Redirect to `/access-denied` if not
4. Otherwise → Render protected content

#### 3. **AppLayout** (`components/AppLayout.tsx`)
**Purpose**: Provides consistent layout for authenticated pages

**Features:**
- Header with app title
- Navigation links (Add Student, View Students)
- User profile display
- Logout button

### Backend Components

#### 1. **JwtAuthenticationFilter** (`filter/JwtAuthenticationFilter.java`)
**Purpose**: Validates ID token on every request

**Process:**
1. Extract `id_token` from HTTP-only cookie
2. Validate token with Google's tokeninfo endpoint
3. Check if email starts with "erphead"
4. Set authentication in Spring Security context

#### 2. **TokenService** (`service/TokenService.java`)
**Purpose**: Handles OAuth token operations

**Methods:**
- `exchangeCode()`: Exchanges authorization code for tokens
- `validateIdToken()`: Validates ID token with Google
- `validateAccessToken()`: Validates access token

#### 3. **RollNumberGenerator** (`service/RollNumberGenerator.java`)
**Purpose**: Generates unique roll numbers

**Format**: `XXYYYYDDD`
- **XX**: Degree prefix (MT, BT, MS)
- **YYYY**: Join year
- **DDD**: Department sequence (001-200 for CSE, 501-600 for ECE, 701-800 for AIDS)

**Uniqueness**: Ensures no duplicate roll numbers per year+department

---

## 🌐 API Endpoints

### Public Endpoints (No Authentication)
- `GET /login` - Redirects to Google OAuth
- `GET /oauth2/callback` - OAuth callback handler
- `POST /signout` - Logout
- `GET /api/auth/me` - Get current user info
- `GET /api/health` - Health check

### Protected Endpoints (Require Authentication)
- `GET /api/domains` - List all domains
- `POST /api/students/admit` - Admit new student
- `GET /api/students` - List all students
- `POST /api/uploads/photo` - Upload student photo

---

## 🗄️ Database Schema

### Key Tables

#### `students`
```sql
- student_id (PK, AUTO_INCREMENT)
- roll_number (UNIQUE, VARCHAR(50))
- first_name (VARCHAR(120))
- last_name (VARCHAR(120))
- email (UNIQUE, VARCHAR(255))
- photograph_path (VARCHAR(512)) -- File path, NOT BLOB
- domain_id (FK → domains)
- specialisation_id (FK → specialisations)
- placement_id (FK → placements)
- join_year (INTEGER)
- seq_no (INTEGER) -- Sequence number for roll number
- total_credits (INTEGER)
- cgpa (DOUBLE)
- created_at (TIMESTAMP)
```

#### `domains`
```sql
- domain_id (PK)
- program (VARCHAR) -- e.g., "M.Tech CSE", "B.Tech ECE"
- capacity (INTEGER)
- degree (VARCHAR) -- "M.Tech", "B.Tech", "MS"
```

#### `specialisations`
```sql
- specialisation_id (PK)
- name (VARCHAR) -- e.g., "CSE", "ECE", "AIDS"
```

**Note**: Photos are stored on the **filesystem** (`uploads/photos/`), not as BLOBs in the database. Only the file path is stored in the database.

---

## 🔄 Complete User Journey

### Scenario: Admin Admits a Student

1. **User visits** `http://localhost:5173`
2. **Clicks "Sign in with Google"**
3. **Redirected to Google** → Authenticates
4. **Google redirects back** → Backend validates & sets cookie
5. **Redirected to** `/add-student` (if email starts with "erphead")
6. **Page loads** → Fetches domains from API
7. **User fills form**:
   - First Name: "John"
   - Last Name: "Doe"
   - Email: "john@example.com"
   - Domain: "M.Tech CSE"
   - Join Year: "2025"
   - Photo: Uploads image
8. **Photo uploads** → Stored on filesystem, path returned
9. **User clicks "Admit Student"**
10. **Backend processes**:
    - Validates data
    - Finds domain
    - Generates roll number (e.g., "MT2025001")
    - Saves student to database
11. **Success message** shown with roll number
12. **User can view** student in `/students` page

---

## 🛡️ Security Features

1. **HTTP-only Cookies**: ID tokens stored in HTTP-only cookies (not accessible via JavaScript)
2. **Server-side Validation**: All tokens validated with Google on server
3. **Email-based Authorization**: Only emails starting with "erphead" can access protected routes
4. **CORS Configuration**: Only allows requests from `http://localhost:5173`
5. **Session Management**: Access/refresh tokens stored in server session
6. **File Upload Validation**: File type and size validation before storage

---

## 📝 Important Notes

1. **Photo Storage**: Photos are stored on filesystem, NOT in database as BLOBs
2. **Roll Number Uniqueness**: Ensured per year and department combination
3. **Token Expiration**: ID tokens expire after 1 hour (cookie maxAge)
4. **Development vs Production**: 
   - Currently configured for `localhost:5173` (frontend) and `localhost:8080` (backend)
   - For production, update CORS origins and redirect URIs
5. **Database**: Uses MySQL with JPA/Hibernate for ORM

---

## 🚀 Running the Project

### Backend
```bash
cd backend
mvn spring-boot:run
# Runs on http://localhost:8080
```

### Frontend
```bash
cd frontend/academic-erp-frotnend
npm install
npm run dev
# Runs on http://localhost:5173
```

### Database
- MySQL should be running on `localhost:3306`
- Database name: `erp_admission`
- Auto-created if it doesn't exist

---

This guide covers the complete workflow and architecture of the Academic ERP system. Each component works together to provide a secure, efficient student admission management system.

