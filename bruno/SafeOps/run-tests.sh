#!/bin/bash

# SafeOps API Test Runner
# Usage: ./run-tests.sh [category]
# Categories: all, auth, roles, core, security

set -e

CATEGORY=${1:-all}
BASE_URL="https://safeops-1.onrender.com"

echo "==============================================="
echo "SafeOps API Test Suite"
echo "Target: $BASE_URL"
echo "Category: $CATEGORY"
echo "==============================================="
echo ""

case $CATEGORY in
  all)
    echo "Running all tests..."
    bruno run Auth/Scenarios --env production
    bruno run Auth/Roles --env production
    bruno run Auth --env production
    bruno run Core --env production
    bruno run Dashboard --env production
    bruno run Inspections --env production
    bruno run Hazards --env production
    bruno run Safety --env production
    bruno run Templates --env production
    bruno run Security --env production
    ;;
  auth)
    echo "Running authentication tests..."
    bruno run Auth/Scenarios --env production
    ;;
  roles)
    echo "Running role-based tests..."
    bruno run Auth/Roles --env production
    ;;
  core)
    echo "Running core module tests..."
    bruno run Core --env production
    ;;
  security)
    echo "Running security tests..."
    bruno run Security --env production
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
