#!/bin/bash

# Check if Docker is running
if ! docker info > /dev/null 2>&1; then
    echo "Docker is not running. Please start Docker and try again."
    exit 1
fi

# Start local development environment
echo "Starting local development environment..."

# Start Redis
docker run -d --name redis -p 6379:6379 redis:latest

# Start DynamoDB Local
docker run -d --name dynamodb -p 8000:8000 amazon/dynamodb-local

# Start Kafka and Zookeeper
docker-compose up -d zookeeper kafka

# Wait for services to be ready
echo "Waiting for services to be ready..."
sleep 10

# Create Kafka topic
docker-compose exec kafka \
    kafka-topics.sh --create --topic shopping-cart-events \
    --partitions 3 --replication-factor 1 \
    --bootstrap-server localhost:9092

echo "Local development environment is ready!"
echo "Redis: localhost:6379"
echo "DynamoDB: localhost:8000"
echo "Kafka: localhost:9092"
echo ""
echo "To stop the environment, run: docker-compose down && docker rm -f redis dynamodb"
