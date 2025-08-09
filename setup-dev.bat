@echo off
REM ============================================
REM Shopping Cart Application - Environment Setup Script
REM Sets up all required environment variables for development
REM ============================================

echo Setting up Shopping Cart Application Environment...

REM ============================================
REM JAVA AND BUILD TOOLS
REM ============================================
set JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-21.0.1.12-hotspot
set MAVEN_HOME=C:\Program Files\Apache\maven\apache-maven-3.9.11
set GRADLE_HOME=C:\Gradle\gradle-8.5

REM Add to PATH
set PATH=%JAVA_HOME%\bin;%MAVEN_HOME%\bin;%GRADLE_HOME%\bin;%PATH%

REM ============================================
REM AWS CONFIGURATION
REM ============================================
set AWS_REGION=us-east-1
set AWS_ACCESS_KEY_ID=dummy-access-key
set AWS_SECRET_ACCESS_KEY=dummy-secret-key
set DYNAMODB_ENDPOINT=http://localhost:8000

REM ============================================
REM REDIS CONFIGURATION
REM ============================================
set REDIS_HOST=localhost
set REDIS_PORT=6379
set REDIS_PASSWORD=

REM ============================================
REM KAFKA CONFIGURATION
REM ============================================
set KAFKA_BROKER_URL=localhost:9092
set KAFKA_BOOTSTRAP_SERVERS=localhost:9092
set KAFKA_CONSUMER_GROUP=shopping-cart-dev-group

REM ============================================
REM APPLICATION SECRETS
REM ============================================
set JWT_SECRET=dGhpcyBpcyBhIHZlcnkgc2VjdXJlIGp3dCBzZWNyZXQgZm9yIGRldmVsb3BtZW50IGVudmlyb25tZW50IG9ubHk=
set APP_SECRET_KEY=dev-secret-key-12345
set DATABASE_ENCRYPTION_KEY=encryption-key-for-sensitive-data

REM ============================================
REM APPLICATION CONFIGURATION
REM ============================================
set SERVER_PORT=8080
set SPRING_PROFILES_ACTIVE=dev
set DEFAULT_REGION=UK
set DEFAULT_CURRENCY=GBP
set CART_MAX_ITEMS=100

REM ============================================
REM LOGGING AND MONITORING
REM ============================================
set LOG_LEVEL_ROOT=INFO
set LOG_LEVEL_APP=DEBUG
set ACTUATOR_ENDPOINTS=health,info,metrics,prometheus
set PROMETHEUS_ENABLED=true

REM ============================================
REM TESTING CONFIGURATION
REM ============================================
set TESTCONTAINERS_RYUK_DISABLED=true
set TESTCONTAINERS_CHECKS_DISABLE=true

echo.
echo Environment variables set successfully!
echo.
echo JAVA_HOME=%JAVA_HOME%
echo MAVEN_HOME=%MAVEN_HOME%
echo GRADLE_HOME=%GRADLE_HOME%
echo AWS_REGION=%AWS_REGION%
echo DYNAMODB_ENDPOINT=%DYNAMODB_ENDPOINT%
echo REDIS_HOST=%REDIS_HOST%:%REDIS_PORT%
echo KAFKA_BROKER_URL=%KAFKA_BROKER_URL%
echo SERVER_PORT=%SERVER_PORT%
echo.
echo To verify setup, run:
echo   java -version
echo   mvn -version
echo   gradle -version
echo.
echo To start the application:
echo   gradle bootRun
echo   OR
echo   mvn spring-boot:run
echo.
