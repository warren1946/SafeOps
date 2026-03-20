#!/bin/bash

# SafeOps API Test Runner
# Requires Bruno CLI: npm install -g @usebruno/cli

echo "🚀 SafeOps API Test Suite"
echo "=========================="

# Check if bru is installed
if ! command -v bru &> /dev/null; then
    echo "❌ Bruno CLI not found. Installing..."
    npm install -g @usebruno/cli
fi

# Default to local environment
ENV=${1:-local}

echo ""
echo "📋 Environment: $ENV"
echo ""

# Run tests
echo "🧪 Running API Tests..."
bru run --env $ENV --output results.json

# Check results
if [ $? -eq 0 ]; then
    echo ""
    echo "✅ All tests passed!"
else
    echo ""
    echo "❌ Some tests failed. Check results.json for details."
fi
