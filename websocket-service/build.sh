#!/bin/bash

gradle clean build

# Build the multi-architecture image
#docker buildx build --platform linux/amd64,linux/arm64 -t websocket-service:latest --load .
docker buildx build --platform linux/amd64 -t websocket-service:latest-amd64 --load .
docker buildx build --platform linux/arm64 -t websocket-service:latest-arm64 --load .
