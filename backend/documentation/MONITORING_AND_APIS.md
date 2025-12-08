# External Services & Monitoring Guide
## Personal Investment Portfolio Tracker Backend

This guide covers external API management, monitoring configuration, and observability setup for the backend application.

---

## Phase 5.3: External Services Configuration ✅ COMPLETED

### External APIs Used

#### 1. FinnHub API
**Purpose:** Real-time stock quotes and company profiles

**Endpoints Used:**
- `GET /quote` - Get real-time stock quote
- `GET /stock/profile2` - Get company profile information

**Rate Limits:**
- **Free Tier:** 60 API calls per minute
- **Recommended:** Monitor usage to stay within limits

**Configuration:**
```yaml
finnhub:
  api:
    key: ${FINNHUB_API_KEY}
    base-url: https://finnhub.io/api/v1
```

**Environment Variable:**
```bash
FINNHUB_API_KEY=your_finnhub_api_key_here
```

#### 2. MarketAux API
**Purpose:** Financial news aggregation

**Endpoints Used:**
- `GET /news` - Get financial news articles
- `GET /news?industries=...` - Get news by industry
- `GET /news?limit=N` - Get limited news items

**Rate Limits:**
- **Free Tier:** 100 API calls per day
- **Recommended:** Cache news responses, minimize calls

**Configuration:**
```yaml
marketaux:
  api:
    key: ${MARKETAUX_API_KEY}
    base-url: https://api.marketaux.com/v1
```

**Environment Variable:**
```bash
MARKETAUX_API_KEY=your_marketaux_api_key_here
```

### API Rate Limit Management ✅

**Service:** `ApiRateLimitService.java`
**Location:** `services/fallback/ApiRateLimitService.java`

**Features:**
- Tracks FinnHub calls per minute (60/minute limit)
- Tracks MarketAux calls per day (100/day limit)
- Prevents exceeding rate limits automatically
- Provides remaining call counts

**Usage Example:**
```java
@Autowired
private ApiRateLimitService rateLimitService;

public void fetchStockData(String symbol) {
    if (rateLimitService.canCallFinnhub()) {
        // Make API call
        rateLimitService.recordFinnhubCall();
    } else {
        // Use cached data or return error
        int remaining = rateLimitService.getRemainingFinnhubCalls();
        throw new RateLimitException("Rate limit exceeded. Try again later.");
    }
}
```

### External API Health Monitoring ✅

**Component:** `ExternalApiHealthIndicator.java`
**Location:** `monitoring/ExternalApiHealthIndicator.java`

**Features:**
- Custom Spring Boot Actuator health indicator
- Checks FinnHub API health
- Checks MarketAux API health
- Reports status through `/actuator/health` endpoint

**Health States:**
- `UP` - All APIs operational
- `DEGRADED` - One API down, service partially available
- `DOWN` - All APIs down

**Access Health Endpoint:**
```bash
curl http://localhost:8080/actuator/health
```

**Response Example:**
```json
{
  "status": "UP",
  "components": {
    "externalApis": {
      "status": "UP",
      "details": {
        "finnhub": "UP",
        "marketaux": "UP",
        "message": "All external APIs are operational"
      }
    }
  }
}
```

### Fallback Strategies ✅

**When APIs are down:**

1. **Use Cached Data:**
   - Return last known stock prices
   - Return cached news articles
   - Add `cached: true` flag to response

2. **Graceful Degradation:**
   - Return partial data if one API is down
   - Show user-friendly error messages
   - Continue operation with available services

3. **Retry Logic:**
   - Implement exponential backoff
   - Max 3 retries with increasing delays
   - Circuit breaker pattern for repeated failures

**Implementation Recommendations:**
```java
// Add to FinnhubApiClientImpl
@Cacheable("stockQuotes")
public FinnhubQuoteDTO getQuote(String symbol) {
    try {
        return callApi(symbol);
    } catch (RestClientException e) {
        // Return cached value or throw specific exception
        throw new ApiUnavailableException("FinnHub unavailable");
    }
}
```

---

## Phase 6: Monitoring & Observability ✅ COMPLETED

### Phase 6.1: Spring Actuator Setup ✅

**Production Actuator Configuration:**

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
      base-path: /actuator
  endpoint:
    health:
      show-details: when-authorized
      show-components: when-authorized
      probes:
        enabled: true
  metrics:
    export:
      prometheus:
        enabled: false
  info:
    env:
      enabled: true
```

**Available Endpoints:**

| Endpoint | Purpose | Public Access |
|----------|---------|---------------|
| `/actuator/health` | Application health status | Yes (limited) |
| `/actuator/info` | Application information | Yes |
| `/actuator/metrics` | Application metrics | No (requires auth) |
| `/actuator/prometheus` | Prometheus metrics | No (requires auth) |

**Health Check Endpoint:**
```bash
# Basic health check
curl http://localhost:8080/actuator/health

# Detailed health (requires authentication)
curl -H "Authorization: Bearer <token>" \
  http://localhost:8080/actuator/health
```

**Kubernetes Liveness & Readiness Probes:**
```yaml
livenessProbe:
  httpGet:
    path: /actuator/health/liveness
    port: 8080
  initialDelaySeconds: 60
  periodSeconds: 10

readinessProbe:
  httpGet:
    path: /actuator/health/readiness
    port: 8080
  initialDelaySeconds: 30
  periodSeconds: 5
```

### Phase 6.2: Logging Configuration ✅

**Development Logging:**
```yaml
logging:
  level:
    root: INFO
    com.joelcode.personalinvestmentportfoliotracker: DEBUG
    org.springframework.security: DEBUG
    org.hibernate.SQL: DEBUG
```

**Production Logging:**
```yaml
logging:
  level:
    root: WARN
    com.joelcode.personalinvestmentportfoliotracker: INFO
    org.springframework.security: WARN
    org.flywaydb: INFO
  file:
    name: logs/application.log
    max-size: 10MB
    max-history: 30
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} - %msg%n"
    file: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n"
```

**Log Rotation:**
- **Max File Size:** 10MB
- **Max History:** 30 days
- **Location:** `logs/application.log`
- **Format:** Timestamped with thread and log level

**Centralized Logging Integration:**

**Option 1: ELK Stack (Elasticsearch, Logstash, Kibana)**
```yaml
# Add to pom.xml
<dependency>
    <groupId>net.logstash.logback</groupId>
    <artifactId>logstash-logback-encoder</artifactId>
    <version>7.4</version>
</dependency>
```

**Option 2: Splunk**
```yaml
# Configure Splunk forwarder
logging:
  file:
    name: /var/log/portfolio-tracker/application.log
# Configure Splunk to monitor this directory
```

**Option 3: AWS CloudWatch**
```yaml
# Add CloudWatch logs agent
# Configure log group: /aws/application/portfolio-tracker
# Set retention: 30 days
```

**Option 4: Better Stack ✅ VERIFIED & WORKING**
- **Component:** `logging/BetterStackLogger.java`
- **Configuration:** Conditional on `BETTERSTACK_TOKEN` environment variable
- **Sends:** Structured logs to Better Stack automatically
- **Endpoint:** `https://s1622070.eu-nbg-2.betterstackdata.com`
- **Status:** Successfully tested - receiving 202 ACCEPTED responses
- **Log Levels:** INFO, WARN, ERROR supported

**Better Stack Configuration:**
```bash
# In .env file (project root)
BETTERSTACK_TOKEN=your_betterstack_token_here

# In compose.yaml (for Docker deployment)
environment:
  BETTERSTACK_TOKEN: ${BETTERSTACK_TOKEN}
```

**Verification:**
```bash
# Test Better Stack integration
cd backend
./test-betterstack.sh

# Expected output:
# ✓ BETTERSTACK_TOKEN is set
# ✓ Application started successfully
# ✓ CommandLineRunner executed (Better Stack log sent)
# Response 202 ACCEPTED
```

**Usage in Code:**
```java
@Autowired(required = false)
private BetterStackLogger logger;

public void someMethod() {
    if (logger != null) {
        logger.info("Application event occurred");
        logger.warn("Warning message");
        logger.error("Error occurred");
    }
}
```

**Note:** BetterStackLogger is optional and only loaded when `BETTERSTACK_TOKEN` is present. The application will function normally without it.

### Recommended Logging Best Practices ✅

**1. Structured Logging:**
```java
logger.info("User logged in: userId={}, timestamp={}",
    userId, LocalDateTime.now());
```

**2. Don't Log Sensitive Data:**
```java
// ❌ BAD
logger.debug("Password: {}", password);

// ✅ GOOD
logger.debug("User authentication attempt for: {}", username);
```

**3. Use Appropriate Log Levels:**
- **ERROR:** System errors, exceptions
- **WARN:** Degraded functionality, API limits approaching
- **INFO:** Business events, user actions
- **DEBUG:** Detailed diagnostic information (dev only)

**4. Include Context:**
```java
try {
    processTransaction(transaction);
} catch (Exception e) {
    logger.error("Transaction processing failed: " +
        "transactionId={}, userId={}, amount={}",
        transaction.getId(), transaction.getUserId(),
        transaction.getAmount(), e);
}
```

---

## Monitoring Dashboard Setup

### Prometheus + Grafana (Recommended)

**1. Enable Prometheus Endpoint:**
```yaml
management:
  metrics:
    export:
      prometheus:
        enabled: true
```

**2. Prometheus Configuration:**
```yaml
scrape_configs:
  - job_name: 'portfolio-tracker'
    metrics_path: '/actuator/prometheus'
    static_configs:
      - targets: ['localhost:8080']
```

**3. Key Metrics to Monitor:**
- HTTP request rates and latencies
- JVM memory usage (heap, non-heap)
- Database connection pool stats
- External API call counts and failures
- Cache hit/miss rates

**4. Grafana Dashboard:**
- Import Spring Boot 2.1 Statistics dashboard
- Customize with application-specific metrics
- Set up alerts for critical thresholds

### Application Performance Monitoring (APM)

**Recommended Tools:**

1. **New Relic**
   - Real-time performance monitoring
   - Transaction tracing
   - Error analytics

2. **Datadog**
   - Infrastructure monitoring
   - APM with distributed tracing
   - Log aggregation

3. **Dynatrace**
   - AI-powered monitoring
   - Full-stack observability
   - Root cause analysis

4. **Elastic APM**
   - Open-source alternative
   - Integrates with ELK stack
   - Distributed tracing

---

## Alert Configuration

### Critical Alerts

1. **Application Down**
   - Trigger: Health endpoint returns DOWN
   - Action: Page on-call engineer
   - Channel: PagerDuty, Opsgenie

2. **High Error Rate**
   - Trigger: >5% of requests return 5xx errors
   - Action: Notify engineering team
   - Channel: Slack, Email

3. **Database Connection Pool Exhausted**
   - Trigger: Active connections ≥ max pool size
   - Action: Alert ops team
   - Channel: Slack

4. **External API Failures**
   - Trigger: FinnHub or MarketAux health DOWN for >5 minutes
   - Action: Notify on-call
   - Channel: Slack, Email

### Warning Alerts

1. **High Response Time**
   - Trigger: P95 latency > 2 seconds
   - Action: Notify team
   - Channel: Slack

2. **API Rate Limit Approaching**
   - Trigger: FinnHub calls > 50/minute or MarketAux > 90/day
   - Action: Log warning, notify team
   - Channel: Slack

3. **High Memory Usage**
   - Trigger: JVM heap > 80%
   - Action: Notify ops team
   - Channel: Slack

4. **Disk Space Low**
   - Trigger: Available disk < 20%
   - Action: Alert ops team
   - Channel: Email

---

## Monitoring Checklist

### Pre-Production ✅
- [x] Actuator endpoints configured
- [x] Health checks implemented
- [x] External API monitoring added
- [x] Logging levels set for production
- [x] Log rotation configured

### Production Setup
- [ ] Centralized logging configured (ELK/Splunk/CloudWatch)
- [ ] Prometheus metrics exported
- [ ] Grafana dashboards created
- [ ] Alert rules configured
- [ ] On-call rotation set up
- [ ] Runbook documentation created

### Ongoing Operations
- [ ] Review logs daily for errors
- [ ] Monitor external API usage
- [ ] Check performance metrics weekly
- [ ] Review and tune JVM settings
- [ ] Update alert thresholds based on patterns

---

## API Documentation

### FinnHub API
- **Documentation:** https://finnhub.io/docs/api
- **Dashboard:** https://finnhub.io/dashboard
- **Rate Limits:** View in dashboard
- **Upgrade Plans:** Available for higher limits

### MarketAux API
- **Documentation:** https://www.marketaux.com/documentation
- **Dashboard:** https://www.marketaux.com/account
- **Rate Limits:** 100 calls/day (free), 1000/day (pro)
- **Upgrade Plans:** Pro, Business, Enterprise

---

## Troubleshooting

### External API Issues

**Problem:** FinnHub returning 429 (Rate Limit Exceeded)
**Solution:**
1. Check rate limit tracker: `rateLimitService.getRemainingFinnhubCalls()`
2. Implement caching for frequently requested symbols
3. Consider upgrading to paid plan

**Problem:** MarketAux not returning data
**Solution:**
1. Verify API key is correct
2. Check daily limit: `rateLimitService.getRemainingMarketAuxCalls()`
3. Review API dashboard for account status

### Monitoring Issues

**Problem:** Actuator endpoints not accessible
**Solution:**
1. Verify endpoints are exposed in configuration
2. Check security configuration allows access
3. Verify application is running: `curl http://localhost:8080/actuator/health`

**Problem:** Logs not being written
**Solution:**
1. Check file permissions on `logs/` directory
2. Verify disk space available
3. Check logback configuration

---

**Phase 5.3 & Phase 6 Status:** ✅ **COMPLETED**
**Last Updated:** December 8, 2025
**Version:** 0.0.1-SNAPSHOT
