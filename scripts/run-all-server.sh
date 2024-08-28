#!/bin/bash

# Function to read properties from a YAML file
function read_yaml_property {
    local file=$1
    local key=$2
    python_command=/usr/bin/python
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

function stop_process_by_port {
   local port=$1
    pid=$(ps -ef | grep "server.port=$port" | grep java | awk '{print $2}')
       echo "PID is $pid"
    if [ -n "$pid" ]; then
        echo "Killing process $pid running on port $port"
        kill -9 $pid
    else
        echo "No process running on port $port"
    fi
}


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

# Run each subproject in the background and log output to separate files in service-logs directory
stop_process_by_port $MARKET_DATA_SERVICE_PORT
nohup java -jar deployment/market-data-service/market-data-service.jar --server.port=$MARKET_DATA_SERVICE_PORT --spring.config.location=deployment/market-data-service/application.yaml > "$LOGS_DIR/market-data-service.log" 2>&1 &
echo "Market Data Service started on port $MARKET_DATA_SERVICE_PORT"

stop_process_by_port $PORTFOLIO_SERVICE_PORT
nohup java -jar deployment/portfolio-service/portfolio-service.jar --server.port=$PORTFOLIO_SERVICE_PORT --spring.config.location=deployment/portfolio-service/application.yaml > "$LOGS_DIR/portfolio-service.log" 2>&1 &
echo "Portfolio Service started on port $PORTFOLIO_SERVICE_PORT"

stop_process_by_port $ORDER_SERVICE_PORT
nohup java -jar deployment/order-service/order-service.jar --server.port=$ORDER_SERVICE_PORT --spring.config.location=deployment/order-service/application.yaml > "$LOGS_DIR/order-service.log" 2>&1 &
echo "Order Service started on port $ORDER_SERVICE_PORT"

stop_process_by_port $USER_PREFERENCE_SERVICE_PORT
nohup java -jar deployment/user-preference-service/user-preference-service.jar --server.port=$USER_PREFERENCE_SERVICE_PORT --spring.config.location=deployment/user-preference-service/application.yaml > "$LOGS_DIR/user-preference-service.log" 2>&1 &
echo "User Preference Service started on port $USER_PREFERENCE_SERVICE_PORT"

stop_process_by_port $AUTH_SERVICE_PORT
nohup java -jar deployment/auth-service/auth-service.jar --server.port=$AUTH_SERVICE_PORT --spring.config.location=deployment/auth-service/application.yaml > "$LOGS_DIR/auth-service.log" 2>&1 &
echo "Auth Service started on port $AUTH_SERVICE_PORT"

stop_process_by_port $NOTIFICATION_SERVICE_PORT
nohup java -jar deployment/notification-service/notification-service.jar --server.port=$NOTIFICATION_SERVICE_PORT --spring.config.location=deployment/notification-service/application.yaml > "$LOGS_DIR/notification-service.log" 2>&1 &
echo "notification service started on port $NOTIFICATION_SERVICE_PORT"

stop_process_by_port $ONBOARDING_SERVICE_PORT
nohup java -jar deployment/onboarding-service/onboarding-service.jar --server.port=$ONBOARDING_SERVICE_PORT --spring.config.location=deployment/onboarding-service/application.yaml > "$LOGS_DIR/onboarding-service.log" 2>&1 &
echo "onboarding service started on port $ONBOARDING_SERVICE_PORT"

stop_process_by_port $ASSET_SERVICE_PORT
nohup java -jar deployment/asset-service/asset-service.jar --server.port=$ASSET_SERVICE_PORT --spring.config.location=deployment/asset-service/application.yaml > "$LOGS_DIR/asset-service.log" 2>&1 &
echo "asset service started on port $ASSET_SERVICE_PORT"

stop_process_by_port $WEBSOCKET_SERVICE_PORT
nohup java -jar deployment/websocket-service/websocket-service.jar --server.port=$WEBSOCKET_SERVICE_PORT --spring.config.location=deployment/websocket-service/application.yaml > "$LOGS_DIR/websocket-service.log" 2>&1 &
echo "Websocket Service started on port $WEBSOCKET_SERVICE_PORT"

echo "All services started."
