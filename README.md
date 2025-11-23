# OASIS Home Health Care - Backend (OASIS-operation)

Multi-tenant OASIS Home Health Care Management System - Backend API

## Technology Stack

- **Java 17**
- **Spring Boot 3.2.0**
- **Spring Security** with JWT Authentication
- **Spring Data JPA** with Hibernate
- **PostgreSQL** (Production) / H2 (Development)
- **Maven** for dependency management
- **Swagger/OpenAPI** for API documentation

## Project Structure

```
OASIS-operation/
├── src/main/java/com/oasis/homehealth/
│   ├── OasisApplication.java              # Main application class
│   ├── common/
│   │   ├── entity/BaseEntity.java         # Base entity with audit fields
│   │   └── exception/                     # Global exception handling
│   ├── config/                            # Configuration classes
│   ├── controller/                        # REST Controllers
│   ├── dto/                               # Data Transfer Objects
│   ├── entity/                            # JPA Entities
│   ├── repository/                        # JPA Repositories
│   ├── security/                          # Security configuration & JWT
│   └── service/                           # Business logic services
└── src/main/resources/
    ├── application.yml                    # Main configuration
    └── application-dev.yml                # Development profile
```

## Phase 0 - Foundation (COMPLETED)

### Entities Implemented
- ✅ **User** - User management with authentication
- ✅ **Role** - Role-based access control
- ✅ **Permission** - Granular permissions
- ✅ **Organization** - Multi-tenant organization management
- ✅ User-Role mapping (Many-to-Many)
- ✅ User-Organization mapping (Many-to-Many)
- ✅ Role-Permission mapping (Many-to-Many)

### Features Implemented
- ✅ JWT-based authentication
- ✅ Login/Logout APIs
- ✅ Organization selection after login
- ✅ Permission-based authorization
- ✅ Audit trail (created_by, updated_by, timestamps)
- ✅ Global exception handling
- ✅ CORS configuration
- ✅ Swagger API documentation

## Pre-configured Roles

1. **SYSTEM_ADMIN** - Full system access
2. **ORG_ADMIN** - Organization administrator
3. **CLINICAL_MANAGER** - Clinical operations manager
4. **INTAKE_COORDINATOR** - Patient intake and referrals
5. **RN** - Registered Nurse
6. **PT** - Physical Therapist
7. **OT** - Occupational Therapist
8. **HHA** - Home Health Aide
9. **BILLING_SPECIALIST** - Billing and claims
10. **SCHEDULER** - Visit scheduling

## Default Users

| Username | Password | Role | Organization |
|----------|----------|------|--------------|
| admin | Admin@123 | SYSTEM_ADMIN | All |
| intake.coordinator | Intake@123 | INTAKE_COORDINATOR | OASIS-NYC-01 |
| rn.johnson | RN@123 | RN | OASIS-NYC-01 |

## Getting Started

### Prerequisites
- Java 17 or higher
- Maven 3.6+
- PostgreSQL 12+ (for production) or use H2 (for development)

### Development Setup (H2 Database)

1. **Navigate to project directory:**
   ```bash
   cd OASIS-operation
   ```

2. **Run with development profile:**
   ```bash
   mvn spring-boot:run -Dspring-boot.run.profiles=dev
   ```

3. **Access H2 Console:**
   - URL: http://localhost:8080/api/h2-console
   - JDBC URL: jdbc:h2:mem:oasis_db
   - Username: sa
   - Password: (leave empty)

### Production Setup (PostgreSQL)

1. **Create PostgreSQL database:**
   ```sql
   CREATE DATABASE oasis_db;
   CREATE USER postgres WITH PASSWORD 'postgres';
   GRANT ALL PRIVILEGES ON DATABASE oasis_db TO postgres;
   ```

2. **Update application.yml** with your database credentials

3. **Run application:**
   ```bash
   mvn spring-boot:run
   ```

### Build for Production

```bash
mvn clean package
java -jar target/oasis-operation-1.0.0.jar
```

## API Documentation

Once the application is running, access Swagger UI at:
- **Swagger UI:** http://localhost:8080/api/swagger-ui.html
- **API Docs:** http://localhost:8080/api/api-docs

## API Endpoints

### Authentication APIs

#### 1. Login
```http
POST /api/auth/login
Content-Type: application/json

{
  "usernameOrEmail": "admin",
  "password": "Admin@123"
}
```

**Response:**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tokenType": "Bearer",
  "userId": 1,
  "username": "admin",
  "email": "admin@oasishomehealth.com",
  "fullName": "System Administrator",
  "roles": [...],
  "organizations": [...],
  "requiresOrganizationSelection": true
}
```

#### 2. Select Organization
```http
POST /api/auth/select-organization
Authorization: Bearer {accessToken}
Content-Type: application/json

{
  "organizationId": 1
}
```

**Response:**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tokenType": "Bearer",
  "selectedOrganization": {...},
  "permissions": [...],
  "message": "Organization selected successfully"
}
```

#### 3. Get Current User
```http
GET /api/auth/me
Authorization: Bearer {accessToken}
```

#### 4. Logout
```http
POST /api/auth/logout
Authorization: Bearer {accessToken}
```

## Authentication Flow

1. **User logs in** → Receives JWT token with user info and available organizations
2. **User selects organization** → Receives new JWT token with organization context and permissions
3. **User makes API calls** → Uses organization-scoped token
4. **Token expires** → User must re-authenticate

## Security Features

- **Password Encryption:** BCrypt with strength 10
- **JWT Tokens:** 
  - Access Token: 24 hours expiry
  - Refresh Token: 7 days expiry
- **CORS:** Configured for frontend origins
- **CSRF:** Disabled for stateless API
- **Session Management:** Stateless (no server-side sessions)

## Database Schema

### Core Tables
- `users` - User accounts
- `roles` - System roles
- `permissions` - Granular permissions
- `organizations` - Multi-tenant organizations
- `user_roles` - User-Role mapping
- `user_organizations` - User-Organization mapping
- `role_permissions` - Role-Permission mapping

### Audit Fields (All tables)
- `created_at` - Record creation timestamp
- `updated_at` - Record update timestamp
- `created_by` - User who created the record
- `updated_by` - User who last updated the record
- `is_active` - Soft delete flag
- `is_deleted` - Hard delete flag

## Configuration

### JWT Configuration (application.yml)
```yaml
jwt:
  secret: your-secret-key-here  # Change in production!
  expiration: 86400000          # 24 hours
  refresh-expiration: 604800000 # 7 days
```

### CORS Configuration
```yaml
app:
  cors:
    allowed-origins: http://localhost:3000,http://localhost:5173
    allowed-methods: GET,POST,PUT,DELETE,PATCH,OPTIONS
    allowed-headers: "*"
    allow-credentials: true
```

## Next Phases

### Phase 1 - Patient Management (Coming Next)
- Patient entity and CRUD operations
- Insurance information
- Referral document management
- Episode management

### Phase 2 - Clinical Documentation
- OASIS SOC (Start of Care)
- Plan of Care generation
- Visit notes

### Phase 3 - Scheduling & Tasks
- Task/Visit generation
- Calendar management
- Clinician assignment

## Development Guidelines

1. **Code Style:** Follow Java naming conventions
2. **DTOs:** Always use DTOs for API requests/responses
3. **Validation:** Use Bean Validation annotations
4. **Exception Handling:** Use custom exceptions with GlobalExceptionHandler
5. **Transactions:** Use @Transactional for multi-step operations
6. **Security:** Use @PreAuthorize for method-level security

## Testing

```bash
# Run all tests
mvn test

# Run specific test
mvn test -Dtest=AuthServiceTest
```

## Troubleshooting

### Issue: Port 8080 already in use
**Solution:** Change port in application.yml or stop the process using port 8080

### Issue: Database connection failed
**Solution:** Verify PostgreSQL is running and credentials are correct

### Issue: JWT token invalid
**Solution:** Ensure JWT secret is at least 256 bits (32 characters)

## Support

For issues and questions, please contact the development team.

## License

Proprietary - OASIS Home Health Care System

