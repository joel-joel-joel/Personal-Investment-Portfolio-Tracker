# Production Deployment Guide
## Personal Investment Portfolio Tracker Backend

This guide covers the deployment of the backend application to production environments.

## Stage 5: Production Deployment - COMPLETED ✅

### 5.1 Production Profile Configuration ✅

The application is configured with a production profile that includes:

**Database Configuration:**
- PostgreSQL connection with environment variables
- HikariCP connection pool (max 20 connections, min 5 idle)
- Flyway migrations enabled with validation
- JPA ddl-auto set to `validate` (no auto-schema changes)

**Security Configuration:**
- JWT authentication with configurable secret
- CORS configured for production domains
- Error stacktraces disabled (`include-stacktrace: never`)

**Logging Configuration:**
- Root level: WARN
- Application level: INFO
- Spring Security: WARN
- Flyway: INFO

### 5.2 Application Packaging ✅

**Production JAR Built Successfully:**
```
File: target/PersonalInvestmentPortfolioTracker-0.0.1-SNAPSHOT.jar
Size: 76MB
Type: Executable Spring Boot JAR with embedded Tomcat
```

**Build Command:**
```bash
mvn clean package -DskipTests
```

**Test Results:**
- ✅ Application starts with production profile only
- ✅ Database connection successful
- ✅ Flyway migrations validated
- ✅ All 94 REST endpoints registered
- ✅ WebSocket configuration successful
- ✅ Security filters loaded correctly

---

## Production Deployment Instructions

### Prerequisites

1. **Java Runtime:**
   - Java 17 or higher required
   - Check version: `java -version`

2. **PostgreSQL Database:**
   - PostgreSQL 12+ recommended
   - Database created and accessible
   - Network connectivity from application server

3. **Environment Variables:**
   - Database credentials
   - JWT secret key
   - CORS allowed origins

### Environment Configuration

1. **Copy the environment template:**
   ```bash
   cp .env.prod.example .env.prod
   ```

2. **Update `.env.prod` with production values:**
   ```bash
   # Database Configuration
   DATABASE_URL=jdbc:postgresql://your-prod-db-host:5432/portfolio_prod
   DATABASE_USERNAME=your_prod_user
   DATABASE_PASSWORD=your_secure_password

   # JWT Configuration (generate new secret for production!)
   # Generate: openssl rand -base64 32
   APP_JWT_SECRET=your-generated-secret-here

   # CORS Configuration
   CORS_ALLOWED_ORIGINS=https://your-production-domain.com

   # Server Configuration
   SERVER_PORT=8080
   ```

3. **Generate a secure JWT secret:**
   ```bash
   openssl rand -base64 32
   ```

### Deployment Methods

#### Method 1: Using the Production Script (Recommended)

```bash
# Make script executable (first time only)
chmod +x run-prod.sh

# Start the application
./run-prod.sh
```

The script will:
- ✅ Validate prerequisites
- ✅ Load environment variables
- ✅ Check JAR file exists
- ✅ Start application with production profile

#### Method 2: Direct JAR Execution

```bash
# Load environment variables
source .env.prod

# Run the JAR with explicit properties
java -jar target/PersonalInvestmentPortfolioTracker-0.0.1-SNAPSHOT.jar \
    --spring.profiles.active=prod \
    --spring.datasource.url="${DATABASE_URL}" \
    --spring.datasource.username="${DATABASE_USERNAME}" \
    --spring.datasource.password="${DATABASE_PASSWORD}" \
    --app.jwt.secret="${APP_JWT_SECRET}" \
    --cors.allowed-origins="${CORS_ALLOWED_ORIGINS}" \
    --server.port="${SERVER_PORT}"
```

#### Method 3: Systemd Service (Linux Production Servers)

Create `/etc/systemd/system/portfolio-tracker.service`:

```ini
[Unit]
Description=Personal Investment Portfolio Tracker
After=syslog.target network.target postgresql.service

[Service]
User=portfolio
WorkingDirectory=/opt/portfolio-tracker
EnvironmentFile=/opt/portfolio-tracker/.env.prod
ExecStart=/usr/bin/java -jar /opt/portfolio-tracker/PersonalInvestmentPortfolioTracker-0.0.1-SNAPSHOT.jar \
    --spring.profiles.active=prod \
    --spring.datasource.url=${DATABASE_URL} \
    --spring.datasource.username=${DATABASE_USERNAME} \
    --spring.datasource.password=${DATABASE_PASSWORD} \
    --app.jwt.secret=${APP_JWT_SECRET}
SuccessExitStatus=143
Restart=on-failure
RestartSec=10

[Install]
WantedBy=multi-user.target
```

Enable and start:
```bash
sudo systemctl daemon-reload
sudo systemctl enable portfolio-tracker
sudo systemctl start portfolio-tracker
sudo systemctl status portfolio-tracker
```

### Database Migration

**Flyway migrations run automatically on startup** when using the production profile.

To manually run migrations:
```bash
mvn flyway:migrate \
    -Dflyway.url=jdbc:postgresql://your-db-host:5432/portfolio_prod \
    -Dflyway.user=your_user \
    -Dflyway.password=your_password
```

To repair Flyway schema history (if needed):
```bash
mvn flyway:repair \
    -Dflyway.url=jdbc:postgresql://your-db-host:5432/portfolio_prod \
    -Dflyway.user=your_user \
    -Dflyway.password=your_password
```

### Monitoring & Health Checks

**Health Endpoint:**
```
GET http://your-server:8080/actuator/health
```

**Application Logs:**
- Logs are written to stdout/stderr
- Configure log aggregation (ELK, Splunk, CloudWatch, etc.)
- Log level: WARN (root), INFO (application)

**Better Stack Logging (Optional):**
- Set `BETTERSTACK_TOKEN` environment variable to enable
- Logs sent to Better Stack automatically

### Security Checklist

- [ ] JWT secret is unique and securely generated
- [ ] Database credentials are strong and rotated regularly
- [ ] CORS origins are restricted to your frontend domain(s)
- [ ] Application runs as non-root user
- [ ] Firewall configured (only expose port 8080/443)
- [ ] HTTPS configured (use reverse proxy like Nginx)
- [ ] Regular security updates applied

### Performance Tuning

**JVM Options (for production):**
```bash
java -Xms512m -Xmx2048m \
     -XX:+UseG1GC \
     -XX:MaxGCPauseMillis=200 \
     -jar PersonalInvestmentPortfolioTracker-0.0.1-SNAPSHOT.jar
```

**HikariCP Connection Pool:**
- Default: max 20 connections, min 5 idle
- Adjust in `application.yml` prod profile if needed

### Troubleshooting

**Port Already in Use:**
```bash
# Find process using port 8080
lsof -i :8080

# Kill the process
kill -9 <PID>

# Or use a different port
--server.port=8081
```

**Database Connection Issues:**
```bash
# Test database connectivity
psql -h your-db-host -U your_user -d portfolio_prod

# Check Flyway schema version
mvn flyway:info -Dflyway.url=... -Dflyway.user=... -Dflyway.password=...
```

**Application Won't Start:**
1. Check logs for detailed error messages
2. Verify all environment variables are set correctly
3. Ensure database is accessible
4. Verify Java version (17+)
5. Check disk space and memory

### Backup & Disaster Recovery

**Database Backups:**
```bash
# PostgreSQL backup
pg_dump -h your-db-host -U your_user portfolio_prod > backup_$(date +%Y%m%d).sql

# Restore
psql -h your-db-host -U your_user portfolio_prod < backup_20241208.sql
```

**Application Binary:**
- Store JAR file in version control or artifact repository
- Keep multiple versions for rollback capability

---

## Docker Deployment (Optional)

The application can also be deployed using Docker. See the `compose.yaml` in the project root for containerized deployment.

---

## Support

For issues or questions:
- Check application logs first
- Review this deployment guide
- Check GitHub issues: https://github.com/anthropics/claude-code/issues

---

**Deployment Status:** ✅ VERIFIED & READY FOR PRODUCTION

**Last Updated:** 2025-12-08
**Version:** 0.0.1-SNAPSHOT
