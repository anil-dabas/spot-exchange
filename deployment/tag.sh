#!/bin/bash

# Source the .env file to load variables into the script's environment
if [[ -f .env ]]; then
    source .env
else
    echo "Error: .env file not found."
    exit 1
fi

# Function to tag images
tag_image() {
    local service=$1
    docker tag ${service}:latest-arm64 $REPO_NAME/cex-${service}:$TAG_VERSION-arm64
    docker tag ${service}:latest-amd64 $REPO_NAME/cex-${service}:$TAG_VERSION-amd64
}

# List of services to tag
services=(
"websocket-service"
#"gateway-api"
#"customer-service"
)

# Loop through each service and tag the images
for service in "${services[@]}"; do
    tag_image $service
done
