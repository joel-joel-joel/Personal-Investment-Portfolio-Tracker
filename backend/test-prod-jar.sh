#!/bin/bash
cd /Users/joelong/Documents/SWE-Projects/Personal\ Investment\ Portfolio\ Tracker/backend
source .env.prod
java -jar target/PersonalInvestmentPortfolioTracker-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
