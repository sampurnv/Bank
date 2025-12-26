#!/bin/bash

# Banking System Startup Script
# This script starts all microservices in the correct order

echo "=========================================="
echo "Banking System - Microservices Startup"
echo "=========================================="
echo ""

# Colors for output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Check if MongoDB is running
echo -e "${YELLOW}Checking MongoDB...${NC}"
if ! nc -z localhost 27017; then
    echo "MongoDB is not running!"
    echo "Starting MongoDB with Docker..."
    docker-compose up -d mongodb
    echo "Waiting for MongoDB to be ready..."
    sleep 5
fi
echo -e "${GREEN}MongoDB is running!${NC}"
echo ""

# Function to start a service
start_service() {
    local service_name=$1
    local port=$2
    local wait_time=$3
    
    echo -e "${YELLOW}Starting $service_name on port $port...${NC}"
    cd "$service_name"
    mvn spring-boot:run > "../logs/${service_name}.log" 2>&1 &
    cd ..
    echo "Waiting ${wait_time}s for $service_name to start..."
    sleep $wait_time
    echo -e "${GREEN}$service_name started!${NC}"
    echo ""
}

# Create logs directory
mkdir -p logs

# Start services in order
echo "Starting services..."
echo ""

start_service "eureka-server" 8761 15
start_service "auth-service" 8081 10
start_service "account-service" 8082 10
start_service "transaction-service" 8083 10
start_service "api-gateway" 8080 10

echo "=========================================="
echo -e "${GREEN}All services started!${NC}"
echo "=========================================="
echo ""
echo "Service URLs:"
echo "  - Eureka Dashboard: http://localhost:8761"
echo "  - API Gateway: http://localhost:8080"
echo "  - Auth Service: http://localhost:8081"
echo "  - Account Service: http://localhost:8082"
echo "  - Transaction Service: http://localhost:8083"
echo ""
echo "Starting Frontend..."
cd frontend
npm start &
cd ..
echo ""
echo "Frontend will be available at: http://localhost:3000"
echo ""
echo "To stop all services, run: ./stop-services.sh"
echo "=========================================="
