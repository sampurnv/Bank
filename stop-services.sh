#!/bin/bash

echo "Stopping all Banking System services..."

# Stop all Spring Boot applications
pkill -f "spring-boot:run"

# Stop frontend
pkill -f "react-scripts"

# Stop MongoDB container
docker-compose down

echo "All services stopped!"
