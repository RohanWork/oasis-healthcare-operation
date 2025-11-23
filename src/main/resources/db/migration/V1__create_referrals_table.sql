-- Migration script to create the referrals table
-- This script creates the referrals table with all required columns based on the Referral entity

CREATE TABLE IF NOT EXISTS referrals (
    -- Base Entity fields (inherited from BaseEntity)
    id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    is_active BOOLEAN DEFAULT TRUE,
    is_deleted BOOLEAN DEFAULT FALSE,
    
    -- Organization (Multi-tenant)
    organization_id BIGINT NOT NULL,
    
    -- Referral Information
    referral_number VARCHAR(50) UNIQUE,
    referral_date DATE NOT NULL,
    referral_source VARCHAR(200),
    referral_source_contact VARCHAR(100),
    referral_source_phone VARCHAR(20),
    referral_source_email VARCHAR(100),
    referral_source_fax VARCHAR(20),
    referral_source_address VARCHAR(255),
    referral_source_city VARCHAR(100),
    referral_source_state VARCHAR(2),
    referral_source_zip VARCHAR(10),
    
    -- Patient Demographics
    patient_first_name VARCHAR(100),
    patient_middle_name VARCHAR(100),
    patient_last_name VARCHAR(100),
    patient_date_of_birth DATE,
    patient_gender VARCHAR(10),
    patient_ssn VARCHAR(11),
    patient_marital_status VARCHAR(20),
    patient_race VARCHAR(50),
    patient_ethnicity VARCHAR(50),
    patient_language VARCHAR(50),
    
    -- Patient Contact
    patient_phone_number VARCHAR(20),
    patient_mobile_number VARCHAR(20),
    patient_email VARCHAR(100),
    
    -- Patient Address
    patient_address_line1 VARCHAR(255),
    patient_address_line2 VARCHAR(255),
    patient_city VARCHAR(100),
    patient_state VARCHAR(2),
    patient_zip_code VARCHAR(10),
    patient_county VARCHAR(100),
    
    -- Emergency Contact
    emergency_contact_name VARCHAR(100),
    emergency_contact_relationship VARCHAR(50),
    emergency_contact_phone VARCHAR(20),
    
    -- Insurance/Payer Information
    primary_insurance VARCHAR(100),
    primary_insurance_id VARCHAR(50),
    primary_insurance_group VARCHAR(50),
    primary_insurance_phone VARCHAR(20),
    secondary_insurance VARCHAR(100),
    secondary_insurance_id VARCHAR(50),
    secondary_insurance_group VARCHAR(50),
    medicaid_number VARCHAR(50),
    medicare_number VARCHAR(50),
    
    -- Physician Information
    primary_physician_name VARCHAR(100),
    primary_physician_npi VARCHAR(10),
    primary_physician_phone VARCHAR(20),
    primary_physician_fax VARCHAR(20),
    primary_physician_address VARCHAR(255),
    primary_physician_city VARCHAR(100),
    primary_physician_state VARCHAR(2),
    primary_physician_zip VARCHAR(10),
    referring_physician_name VARCHAR(100),
    referring_physician_npi VARCHAR(10),
    referring_physician_phone VARCHAR(20),
    
    -- Diagnosis Information
    primary_diagnosis VARCHAR(200),
    primary_diagnosis_icd10 VARCHAR(20),
    primary_diagnosis_description TEXT,
    secondary_diagnosis1 VARCHAR(200),
    secondary_diagnosis1_icd10 VARCHAR(20),
    secondary_diagnosis2 VARCHAR(200),
    secondary_diagnosis2_icd10 VARCHAR(20),
    secondary_diagnosis3 VARCHAR(200),
    secondary_diagnosis3_icd10 VARCHAR(20),
    
    -- Service Information
    service_requested VARCHAR(50),
    service_start_date DATE,
    expected_frequency VARCHAR(50),
    expected_duration VARCHAR(50),
    special_instructions TEXT,
    
    -- Clinical Information
    allergies TEXT,
    current_medications TEXT,
    medical_history TEXT,
    functional_limitations TEXT,
    equipment_needs TEXT,
    
    -- Status
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    notes TEXT,
    
    -- Link to patient if admitted
    patient_id BIGINT,
    
    -- Foreign Key Constraints
    CONSTRAINT fk_referral_organization FOREIGN KEY (organization_id) REFERENCES organizations(id),
    CONSTRAINT fk_referral_patient FOREIGN KEY (patient_id) REFERENCES patients(id)
);

-- Create indexes for better query performance
CREATE INDEX IF NOT EXISTS idx_referral_number ON referrals(referral_number);
CREATE INDEX IF NOT EXISTS idx_referral_org ON referrals(organization_id);
CREATE INDEX IF NOT EXISTS idx_referral_status ON referrals(status);
CREATE INDEX IF NOT EXISTS idx_referral_date ON referrals(referral_date);
CREATE INDEX IF NOT EXISTS idx_referral_patient ON referrals(patient_id);

-- Add comments for documentation
COMMENT ON TABLE referrals IS 'Stores referral information from external sources before patient admission';
COMMENT ON COLUMN referrals.referral_number IS 'Unique referral number, auto-generated if not provided';
COMMENT ON COLUMN referrals.status IS 'Referral status: PENDING, ADMITTED, DECLINED';
COMMENT ON COLUMN referrals.patient_id IS 'Link to patient record if referral has been admitted';

