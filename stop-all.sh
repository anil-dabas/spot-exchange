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
CUSTODY_SERVICE_PORT=$(read_yaml_property "$CONFIG_FILE" "['custody-service']['port']")
WEBSOCKET_SERVICE_PORT=$(read_yaml_property "$CONFIG_FILE" "['websocket-service']['port']")

# Stop a process by its port
function stop_process_by_port {
    local port=$1
    local pid=$(lsof -t -i :$port)
    if [ ! -z "$pid" ]; then
        echo "Stopping process using port $port (PID: $pid)..."
        kill -9 $pid
    else
        echo "No process found running on port $port."
    fi
}

# Stop each service by its configured port
stop_process_by_port $MARKET_DATA_SERVICE_PORT
stop_process_by_port $PORTFOLIO_SERVICE_PORT
stop_process_by_port $ORDER_SERVICE_PORT
stop_process_by_port $USER_PREFERENCE_SERVICE_PORT
stop_process_by_port $AUTH_SERVICE_PORT
stop_process_by_port $NOTIFICATION_SERVICE_PORT
stop_process_by_port $ONBOARDING_SERVICE_PORT
stop_process_by_port $CUSTODY_SERVICE_PORT
stop_process_by_port $WEBSOCKET_SERVICE_PORT

echo "All services stopped."
