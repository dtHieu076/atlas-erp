# Atlas ERP

Enterprise Resource Planning system for manufacturing company  
Architecture: Microservices

## Services
- IAM Service
- Organization Service
- HR Service
- Payroll Service
- Inventory Service
- Production Service
- Accounting Service
- Reporting Service
- Workflow Service
- log-ingest-service

## Connection string
- iam_db: psql 'postgresql://neondb_owner:npg_ODZx4pvMKCU1@ep-nameless-flower-ahqfkw3x-pooler.c-3.us-east-1.aws.neon.tech/iam_db?sslmode=require&channel_binding=require'
- organization_db: psql 'postgresql://neondb_owner:npg_ODZx4pvMKCU1@ep-nameless-flower-ahqfkw3x-pooler.c-3.us-east-1.aws.neon.tech/organization_db?sslmode=require&channel_binding=require'
- hr_db: psql 'postgresql://neondb_owner:npg_ODZx4pvMKCU1@ep-nameless-flower-ahqfkw3x-pooler.c-3.us-east-1.aws.neon.tech/hr_db?sslmode=require&channel_binding=require'
- payroll_db: psql 'postgresql://neondb_owner:npg_ODZx4pvMKCU1@ep-nameless-flower-ahqfkw3x-pooler.c-3.us-east-1.aws.neon.tech/payroll_db?sslmode=require&channel_binding=require'
- inventory_db: psql 'postgresql://neondb_owner:npg_ODZx4pvMKCU1@ep-nameless-flower-ahqfkw3x-pooler.c-3.us-east-1.aws.neon.tech/inventory_db?sslmode=require&channel_binding=require'
- production_db: psql 'postgresql://neondb_owner:npg_ODZx4pvMKCU1@ep-nameless-flower-ahqfkw3x-pooler.c-3.us-east-1.aws.neon.tech/production_db?sslmode=require&channel_binding=require'
- accounting_db: psql 'postgresql://neondb_owner:npg_ODZx4pvMKCU1@ep-nameless-flower-ahqfkw3x-pooler.c-3.us-east-1.aws.neon.tech/accounting_db?sslmode=require&channel_binding=require'
- reporting_db: psql 'postgresql://neondb_owner:npg_ODZx4pvMKCU1@ep-nameless-flower-ahqfkw3x-pooler.c-3.us-east-1.aws.neon.tech/reporting_db?sslmode=require&channel_binding=require'
- workflow_db: psql 'postgresql://neondb_owner:npg_ODZx4pvMKCU1@ep-nameless-flower-ahqfkw3x-pooler.c-3.us-east-1.aws.neon.tech/workflow_db?sslmode=require&channel_binding=require'
- logging_db: mongodb+srv://2224802010076_db_user:Atlas2224802010076@test0.mzf1z5y.mongodb.net/logging_db?retryWrites=true&w=majority
- cache_storage: 
UPSTASH_REDIS_REST_URL="https://noble-crow-8045.upstash.io"
UPSTASH_REDIS_REST_TOKEN="AR9tAAImcDE1ZGYzMGIxZDBkZWQ0ZTFkODc5MTQ4M2E0MmI5YWNlZHAxODA0NQ"

## PostgreSQL
- iam_db
- organization_db
- hr_db
- payroll_db
- inventory_db
- production_db
- accounting_db
- reporting_db
- workflow_db
## MongoDB
- logging_db
## Redis: 
- cache_storage

## Tech Stack
- Java Spring Boot
- Kafka
- Docker / Docker Compose
- Kong API Gateway
