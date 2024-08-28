#!/bin/bash

# Run script to install dependencies (Python and PyYAML)
./scripts/dependencies.sh

# Check if installation was successful
if [ $? -ne 0 ]; then
    echo "Failed to install Python and/or PyYAML. Exiting."
    exit 1
fi

# Run script to start services
./scripts/run-all.sh "$@"
