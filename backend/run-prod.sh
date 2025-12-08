#!/bin/bash
# Production Startup Script for Personal Investment Portfolio Tracker
#
# This script loads environment variables and starts the application with production profile
#
# Usage: ./run-prod.sh
#
# Prerequisites:
#   - Java 17 or higher installed
#   - PostgreSQL database accessible
#   - .env.prod file with production environment variables

set -e

# Colors for output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

echo -e "${GREEN}========================================${NC}"
echo -e "${GREEN}Personal Investment Portfolio Tracker${NC}"
echo -e "${GREEN}Production Startup${NC}"
echo -e "${GREEN}========================================${NC}"
echo ""

# Check if .env.prod exists
if [ ! -f ".env.prod" ]; then
    echo -e "${RED}ERROR: .env.prod file not found!${NC}"
    echo -e "${YELLOW}Please create .env.prod from .env.prod.example${NC}"
    echo -e "${YELLOW}Command: cp .env.prod.example .env.prod${NC}"
    exit 1
fi

# Load environment variables
echo -e "${YELLOW}Loading environment variables...${NC}"
export $(cat .env.prod | grep -v '^#' | xargs)

# Check if JAR exists
JAR_FILE="target/PersonalInvestmentPortfolioTracker-0.0.1-SNAPSHOT.jar"
if [ ! -f "$JAR_FILE" ]; then
    echo -e "${RED}ERROR: JAR file not found at $JAR_FILE${NC}"
    echo -e "${YELLOW}Please build the application first: mvn clean package${NC}"
    exit 1
fi

# Verify Java version
JAVA_VERSION=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}' | awk -F '.' '{print $1}')
if [ "$JAVA_VERSION" -lt 17 ]; then
    echo -e "${RED}ERROR: Java 17 or higher is required${NC}"
    echo -e "${YELLOW}Current Java version: $(java -version 2>&1 | head -n 1)${NC}"
    exit 1
fi

echo -e "${GREEN}Starting application...${NC}"
echo -e "${YELLOW}Profile: production${NC}"
echo -e "${YELLOW}Database: ${DATABASE_URL}${NC}"
echo -e "${YELLOW}Server Port: ${SERVER_PORT:-8080}${NC}"
echo ""

# Start the application with explicit property overrides
java -jar "$JAR_FILE" \
    --spring.profiles.active=prod \
    --spring.datasource.url="${DATABASE_URL}" \
    --spring.datasource.username="${DATABASE_USERNAME}" \
    --spring.datasource.password="${DATABASE_PASSWORD}" \
    --app.jwt.secret="${APP_JWT_SECRET}" \
    --cors.allowed-origins="${CORS_ALLOWED_ORIGINS}" \
    --server.port=${SERVER_PORT:-8080}
