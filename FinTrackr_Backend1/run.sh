#!/bin/bash

echo "Starting Personal Finance Application..."
echo ""
echo "Make sure you have Java 17+ and Maven installed and in your PATH"
echo ""

if [ -f ".env" ]; then
    echo "Loading environment variables from .env..."
    set -a
    source .env
    set +a
    echo ""
fi

# Check if mvn is available
if ! command -v mvn &> /dev/null; then
    echo "ERROR: Maven (mvn) is not found in PATH"
    echo "Please install Maven and add it to your PATH"
    echo "Download from: https://maven.apache.org/download.cgi"
    exit 1
fi

# Check if java is available
if ! command -v java &> /dev/null; then
    echo "ERROR: Java is not found in PATH"
    echo "Please install Java 17+ and add it to your PATH"
    exit 1
fi

echo "Cleaning and compiling..."
mvn clean compile
if [ $? -ne 0 ]; then
    echo "ERROR: Compilation failed"
    exit 1
fi

echo ""
echo "Starting Spring Boot application..."
echo "The application will be available at: http://localhost:${PORT:-8080}"
echo "Spring profile: ${SPRING_PROFILES_ACTIVE:-default}"
echo ""

mvn spring-boot:run
