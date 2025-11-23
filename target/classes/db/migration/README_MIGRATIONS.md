# Database Migration Scripts

This directory contains SQL migration scripts for database schema changes.

## Referrals Table Migration

### V1__create_referrals_table.sql

This migration script creates the `referrals` table with all required columns for the referral management system.

#### What it does:

1. **Creates the `referrals` table** with:
   - Base entity fields (id, created_at, updated_at, created_by, updated_by, is_active, is_deleted)
   - Organization reference for multi-tenancy
   - Referral information (number, date, source details)
   - Patient demographics (name, DOB, gender, SSN, etc.)
   - Patient contact information (phone, email, address)
   - Emergency contact information
   - Insurance/payer information (primary, secondary, Medicare, Medicaid)
   - Physician information (primary and referring physicians)
   - Diagnosis information (primary and up to 3 secondary diagnoses with ICD-10 codes)
   - Service information (requested service, start date, frequency, duration)
   - Clinical information (allergies, medications, medical history, etc.)
   - Status and notes
   - Link to patient record (if admitted)

2. **Creates indexes** for:
   - `referral_number` - for quick lookup by referral number
   - `organization_id` - for organization-based queries
   - `status` - for filtering by status
   - `referral_date` - for date-based queries
   - `patient_id` - for linking to patient records

3. **Adds foreign key constraints**:
   - Links to `organizations` table for multi-tenancy
   - Links to `patients` table (optional, if referral is admitted)

#### Running the Migration

**For PostgreSQL (Production):**

If you're using Flyway or Liquibase, place this script in the appropriate migration directory and it will run automatically.

If running manually:
```sql
psql -U postgres -d oasis_db -f V1__create_referrals_table.sql
```

**For H2 (Development):**

The H2 database uses `ddl-auto: create-drop`, so the table will be created automatically by JPA/Hibernate when the application starts. However, you can still run this script manually if needed.

**Note:** Since the project currently uses JPA with `ddl-auto: update` for PostgreSQL, the table will be created automatically by Hibernate. This migration script is provided for:
- Documentation purposes
- Manual database setup
- Future migration to Flyway/Liquibase
- Production deployments where manual control is preferred

#### Table Structure

The table follows the same pattern as other entities in the system:
- Inherits from `BaseEntity` (id, timestamps, audit fields)
- Multi-tenant (organization_id foreign key)
- Soft delete support (is_deleted flag)
- Comprehensive indexing for performance

