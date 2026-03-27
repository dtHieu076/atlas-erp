-- Migration for MailCampaign support
-- Run manually or with Flyway

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

ALTER TABLE mail_jobs ADD COLUMN IF NOT EXISTS campaign_id UUID REFERENCES mail_campaigns(id);

CREATE INDEX IF NOT EXISTS idx_mail_jobs_campaign ON mail_jobs(campaign_id);
CREATE INDEX IF NOT EXISTS idx_campaigns_created ON mail_campaigns(created_at);
