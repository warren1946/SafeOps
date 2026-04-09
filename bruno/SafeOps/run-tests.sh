#!/bin/bash

# SafeOps API Test Runner
# Usage: ./run-tests.sh [category]
# Categories: all, auth, roles, core, security
#
# Environment Variables:
#   SAFEOPS_API_URL - Base URL for API (default: http://localhost:8080)
#   SAFEOPS_ENV     - Bruno environment to use (default: local)

set -e

CATEGORY=${1:-all}
BASE_URL="${SAFEOPS_API_URL:-http://localhost:8080}"
ENV="${SAFEOPS_ENV:-local}"

echo "==============================================="
echo "SafeOps API Test Suite"
echo "Target: $BASE_URL"
echo "Environment: $ENV"
echo "Category: $CATEGORY"
echo "==============================================="
echo ""

case $CATEGORY in
  all)
    echo "Running all tests..."
    bruno run Auth/Scenarios --env "$ENV"
    bruno run Auth/Roles --env "$ENV"
    bruno run Auth --env "$ENV"
    bruno run Core --env "$ENV"
    bruno run Dashboard --env "$ENV"
    bruno run Inspections --env "$ENV"
    bruno run Hazards --env "$ENV"
    bruno run Safety --env "$ENV"
    bruno run Templates --env "$ENV"
    bruno run Security --env "$ENV"
    ;;
  auth)
    echo "Running authentication tests..."
    bruno run Auth/Scenarios --env "$ENV"
    ;;
  roles)
    echo "Running role-based tests..."
    bruno run Auth/Roles --env "$ENV"
    ;;
  core)
    echo "Running core module tests..."
    bruno run Core --env "$ENV"
    ;;
  security)
    echo "Running security tests..."
    bruno run Security --env "$ENV"
    ;;
  *)
    echo "Unknown category: $CATEGORY"
    echo "Usage: ./run-tests.sh [all|auth|roles|core|security]"
    exit 1
    ;;
esac

echo ""
echo "==============================================="
echo "Test execution completed!"
echo "==============================================="
