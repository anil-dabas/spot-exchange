#!/bin/bash

# Enable experimental features in Docker (if not already enabled)
#export DOCKER_CLI_EXPERIMENTAL=enabled

# Create a new builder instance
#docker buildx create --use


# Function to build a service
build_service() {
  local service_path=$1
  echo "Building ${service_path}..."
  (cd "${service_path}" && ./build.sh)
}

# List of services to build
services=(
  "websocket-service"
#  "discovery"
#  "customer-service"
)


# Change to the services directory
cd ..

# Loop through each service and build it
for service in "${services[@]}"; do
  build_service "$service"
done
