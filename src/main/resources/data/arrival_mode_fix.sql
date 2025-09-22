-- SQL script to fix the arrival_mode_id column to make it nullable
-- This should be run on the database to resolve the foreign key constraint issue

ALTER TABLE checkin MODIFY COLUMN arrival_mode_id VARCHAR(255) NULL;

-- Also ensure that other related columns are nullable
ALTER TABLE checkin MODIFY COLUMN company_id VARCHAR(255) NULL;
ALTER TABLE checkin MODIFY COLUMN plan_id VARCHAR(255) NULL;
ALTER TABLE checkin MODIFY COLUMN room_type_id VARCHAR(255) NULL;
ALTER TABLE checkin MODIFY COLUMN settlement_type_id VARCHAR(255) NULL;
ALTER TABLE checkin MODIFY COLUMN nationality_id VARCHAR(255) NULL;
ALTER TABLE checkin MODIFY COLUMN ref_mode_id VARCHAR(255) NULL;
ALTER TABLE checkin MODIFY COLUMN resv_source_id VARCHAR(255) NULL;
ALTER TABLE checkin MODIFY COLUMN arrival_details VARCHAR(255) NULL;