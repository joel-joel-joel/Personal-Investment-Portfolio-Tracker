#!/bin/bash

# Test Better Stack Logging Integration

echo "🧪 Testing Better Stack Logging Integration..."
echo ""

# Load environment variables from parent .env file
if [ -f "../.env" ]; then
    export $(cat ../.env | grep -v '^#' | xargs)
    echo "✓ Loaded environment variables from ../.env"
else
    echo "❌ .env file not found in parent directory"
    exit 1
fi

# Verify BETTERSTACK_TOKEN is set
if [ -z "$BETTERSTACK_TOKEN" ]; then
    echo "❌ BETTERSTACK_TOKEN not found in .env file"
    exit 1
else
    echo "✓ BETTERSTACK_TOKEN is set: ${BETTERSTACK_TOKEN:0:8}..."
fi

echo ""
echo "🚀 Starting application with Better Stack enabled..."
echo ""

# Start the application in background on port 8081 to avoid conflict
java -jar target/PersonalInvestmentPortfolioTracker-0.0.1-SNAPSHOT.jar \
  --spring.profiles.active=prod \
  --spring.datasource.url=jdbc:postgresql://localhost:5433/portfolio_dev \
  --spring.datasource.username=devuser \
  --spring.datasource.password=devpass \
  --app.jwt.secret="${APP_JWT_SECRET}" \
  --server.port=8081 > /tmp/betterstack-test.log 2>&1 &

APP_PID=$!
echo "Application started with PID: $APP_PID"

# Wait for application to start
echo "Waiting for application to start (15 seconds)..."
sleep 15

# Check if BetterStackLogger was loaded
echo ""
echo "📊 Checking Better Stack integration..."
echo ""

if grep -q "BetterStackLogger" /tmp/betterstack-test.log; then
    echo "✓ BetterStackLogger component mentioned in logs"
fi

if grep -q "ConditionalOnProperty" /tmp/betterstack-test.log; then
    echo "✓ Conditional component loading working"
fi

if grep -q "Started PersonalInvestmentPortfolioTrackerApplication" /tmp/betterstack-test.log; then
    echo "✓ Application started successfully"
fi

if grep -q "Backend started successfully!" /tmp/betterstack-test.log; then
    echo "✓ CommandLineRunner executed (Better Stack log sent)"
fi

# Check for errors related to Better Stack
if grep -q "Failed to send log to Better Stack" /tmp/betterstack-test.log; then
    echo "⚠️  Warning: Failed to send log to Better Stack (check token/connectivity)"
    grep "Failed to send log to Better Stack" /tmp/betterstack-test.log
fi

echo ""
echo "📋 Last 30 lines of application log:"
echo "─────────────────────────────────────"
tail -30 /tmp/betterstack-test.log

# Cleanup
echo ""
echo "🧹 Cleaning up..."
kill $APP_PID 2>/dev/null || true
sleep 2
pkill -f "PersonalInvestmentPortfolioTracker" 2>/dev/null || true

echo ""
echo "✅ Test complete! Full log available at: /tmp/betterstack-test.log"
