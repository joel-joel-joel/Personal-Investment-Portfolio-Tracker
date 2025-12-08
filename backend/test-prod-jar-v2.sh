#!/bin/bash
# Test Production JAR with environment variables passed as JVM properties

cd /Users/joelong/Documents/SWE-Projects/Personal\ Investment\ Portfolio\ Tracker/backend

# Load environment variables
source .env.prod

# Run JAR with environment variables as JVM properties
java -jar target/PersonalInvestmentPortfolioTracker-0.0.1-SNAPSHOT.jar \
  --spring.profiles.active=prod \
  --spring.datasource.url="${DATABASE_URL}" \
  --spring.datasource.username="${DATABASE_USERNAME}" \
  --spring.datasource.password="${DATABASE_PASSWORD}" \
  --app.jwt.secret="${APP_JWT_SECRET}" \
  --cors.allowed-origins="${CORS_ALLOWED_ORIGINS}" \
  --server.port="${SERVER_PORT}"
