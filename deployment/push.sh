#!/bin/bash

# Source the .env file to load variables into the script's environment
if [[ -f .env ]]; then
    source .env
else
    echo "Error: .env file not found."
    exit 1
fi

# Log in to Docker Hub
docker login || { echo "Docker login failed"; exit 1; }

# Function to push images
push_image() {
    local service=$1
    docker push $REPO_NAME/cex-${service}:$TAG_VERSION-arm64 || { echo "Failed to push $REPO_NAME/cex-${service}:$TAG_VERSION-arm64"; exit 1; }
    docker push $REPO_NAME/cex-${service}:$TAG_VERSION-amd64 || { echo "Failed to push $REPO_NAME/cex-${service}:$TAG_VERSION-amd64"; exit 1; }
}

# List of services to push
services=(
"websocket-service"
#"customer-service"
)

# Loop through each service and push the images
for service in "${services[@]}"; do
    push_image $service
done
