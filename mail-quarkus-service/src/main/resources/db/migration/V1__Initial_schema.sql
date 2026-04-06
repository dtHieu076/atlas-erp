-- Initial migration for mail-quarkus-service
-- Creates mail_jobs and mail_campaigns tables

CREATE TABLE IF NOT EXISTS mail_jobs (
    id BIGSERIAL PRIMARY KEY,
    recipient_email VARCHAR(255) NOT NULL,
    template_id VARCHAR(255) NOT NULL,
    template_data JSONB,
    status VARCHAR(50) DEFAULT 'PENDING' NOT NULL,
    retry_count INTEGER DEFAULT 0,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW(),
    error_message TEXT,
    campaign_id UUID
);

CREATE TABLE IF NOT EXISTS mail_campaigns (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    template_id VARCHAR(255) NOT NULL,
    send_type VARCHAR(50) NOT NULL,
    name VARCHAR(255),
    recipients_count INTEGER NOT NULL,
    success_count INTEGER DEFAULT 0,
    failed_count INTEGER DEFAULT 0,
    pending_count INTEGER,
    status VARCHAR(50) DEFAULT 'PROCESSING',
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);

-- Add foreign key
ALTER TABLE mail_jobs ADD CONSTRAINT IF NOT EXISTS fk_mail_jobs_campaign 
    FOREIGN KEY (campaign_id) REFERENCES mail_campaigns(id);

-- Create indexes for performance
CREATE INDEX IF NOT EXISTS idx_mail_jobs_status ON mail_jobs(status);
CREATE INDEX IF NOT EXISTS idx_mail_jobs_created ON mail_jobs(created_at);
CREATE INDEX IF NOT EXISTS idx_mail_jobs_campaign ON mail_jobs(campaign_id);
CREATE INDEX IF NOT EXISTS idx_campaigns_created ON mail_campaigns(created_at);
