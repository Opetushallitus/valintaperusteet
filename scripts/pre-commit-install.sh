#!/usr/bin/env bash

if [ -n "$CI" ]; then
  echo "Skipping installation of pre-commit hook in CI environment"
  exit 0
fi

output=$(pre-commit install -t pre-commit 2>&1)

if [[ $? -ne 0 ]]
then
  echo $output
  echo "Failed to install pre-commit hook. Please install pre-commit by running 'pip install pre-commit'. More info at https://pre-commit.com/"
  exit 1
else
  echo $output
fi