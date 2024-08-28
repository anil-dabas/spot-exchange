#!/bin/bash

# Function to read properties from a YAML file
function read_yaml_property {
    local file=$1
    local key=$2
    python_command=$(command -v python3 || command -v python)
    $python_command -c "import yaml; print(yaml.safe_load(open('$file'))$key)"
}

# Check for the environment argument
if [ -z "$1" ]; then
    echo "Please specify the environment (dev, test, prod)."
    exit 1
fi

ENV=$1

# Load the appropriate properties file based on the environment
CONFIG_FILE="config/${ENV}.yaml"

if [ ! -f "$CONFIG_FILE" ]; then
    echo "Configuration file for environment '$ENV' not found!"
    exit 1
fi

# Read port values from the configuration file
MARKET_DATA_SERVICE_PORT=$(read_yaml_property "$CONFIG_FILE" "['market-data-service']['port']")
PORTFOLIO_SERVICE_PORT=$(read_yaml_property "$CONFIG_FILE" "['portfolio-service']['port']")
ORDER_SERVICE_PORT=$(read_yaml_property "$CONFIG_FILE" "['order-service']['port']")
USER_PREFERENCE_SERVICE_PORT=$(read_yaml_property "$CONFIG_FILE" "['user-preference-service']['port']")
AUTH_SERVICE_PORT=$(read_yaml_property "$CONFIG_FILE" "['auth-service']['port']")
NOTIFICATION_SERVICE_PORT=$(read_yaml_property "$CONFIG_FILE" "['notification-service']['port']")
ONBOARDING_SERVICE_PORT=$(read_yaml_property "$CONFIG_FILE" "['onboarding-service']['port']")
ASSET_SERVICE_PORT=$(read_yaml_property "$CONFIG_FILE" "['asset-service']['port']")
WEBSOCKET_SERVICE_PORT=$(read_yaml_property "$CONFIG_FILE" "['websocket-service']['port']")

# Navigate to the root directory of the project
cd "$(dirname "$0")"
cd ..

# Create service-logs directory if it doesn't exist in the root
LOGS_DIR="$(pwd)/service-logs"
mkdir -p "$LOGS_DIR"

# Build all subprojects using parent project's gradlew
./gradlew clean build --rerun-tasks

# Check if build was successful
if [ $? -ne 0 ]; then
    echo "Build failed. Exiting."
    exit 1
fi

# Run each subproject in the background and log output to separate files in service-logs directory
nohup java -jar market-data-service/build/libs/market-data-service.jar --server.port=$MARKET_DATA_SERVICE_PORT > "$LOGS_DIR/market-data-service.log" 2>&1 &
echo "Market Data Service started on port $MARKET_DATA_SERVICE_PORT"

nohup java -jar portfolio-service/build/libs/portfolio-service.jar --server.port=$PORTFOLIO_SERVICE_PORT > "$LOGS_DIR/portfolio-service.log" 2>&1 &
echo "Portfolio Service started on port $PORTFOLIO_SERVICE_PORT"

nohup java -jar order-service/build/libs/order-service.jar --server.port=$ORDER_SERVICE_PORT > "$LOGS_DIR/order-service.log" 2>&1 &
echo "Order Service started on port $ORDER_SERVICE_PORT"

nohup java -jar user-preference-service/build/libs/user-preference-service.jar --server.port=$USER_PREFERENCE_SERVICE_PORT > "$LOGS_DIR/user-preference-service.log" 2>&1 &
echo "User Preference Service started on port $USER_PREFERENCE_SERVICE_PORT"

nohup java -jar auth-service/build/libs/auth-service.jar --server.port=$AUTH_SERVICE_PORT > "$LOGS_DIR/auth-service.log" 2>&1 &
echo "Auth Service started on port $AUTH_SERVICE_PORT"

nohup java -jar notification-service/build/libs/notification-service.jar --server.port=$NOTIFICATION_SERVICE_PORT > "$LOGS_DIR/notification-service.log" 2>&1 &
echo "Notification Service started on port $NOTIFICATION_SERVICE_PORT"

nohup java -jar onboarding-service/build/libs/onboarding-service.jar --server.port=$ONBOARDING_SERVICE_PORT > "$LOGS_DIR/onboarding-service.log" 2>&1 &
echo "Onboarding Service started on port $ONBOARDING_SERVICE_PORT"

nohup java -jar asset-service/build/libs/asset-service.jar --server.port=$ASSET_SERVICE_PORT > "$LOGS_DIR/asset-service.log" 2>&1 &
echo "Asset Service started on port $ASSET_SERVICE_PORT"

nohup java -jar websocket-service/build/libs/websocket-service.jar --server.port=$WEBSOCKET_SERVICE_PORT > "$LOGS_DIR/websocket-service.log" 2>&1 &
echo "Websocket Service started on port $WEBSOCKET_SERVICE_PORT"

echo "All services started."
