#!/bin/bash

# Function to check if Python is installed
function check_python_installation {
    python_command=$(command -v python3 || command -v python)
    if [ -z "$python_command" ]; then
        return 1
    else
        return 0
    fi
}

# Function to install Python
function install_python {
    echo "Python is not installed. Installing Python..."
    # Check the package manager and install Python (example for apt package manager)
    sudo apt update
    sudo apt install -y python3
    # Check if Python installation was successful
    check_python_installation
    if [ $? -eq 0 ]; then
        echo "Python installed successfully."
    else
        echo "Failed to install Python. Please install it manually."
        exit 1
    fi
}

# Function to install PyYAML
function install_pyyaml {
    echo "Installing PyYAML..."
    python_command=$(command -v python3 || command -v python)
    $python_command -m pip install pyyaml
    # Check if PyYAML installation was successful
    $python_command -c "import yaml"
    if [ $? -eq 0 ]; then
        echo "PyYAML installed successfully."
    else
        echo "Failed to install PyYAML. Please install it manually."
        exit 1
    fi
}

# Main script logic
check_python_installation
if [ $? -ne 0 ]; then
    install_python
fi

check_pyymal_installation
if [ $? -ne 0 ]; then
    install_pyyaml
fi

