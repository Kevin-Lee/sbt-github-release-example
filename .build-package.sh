#!/bin/bash -e

sbt clean
sbt writeVersion
echo "======================================================"
echo "Running: sbt test"
echo "------------------------------------------------------"
if ! sbt test ; then
  echo "Testing failed!" 2>&1
  echo "======================================================"
  exit 1
fi
  echo "======================================================"
  echo "Running: sbt one-jar"
  echo "------------------------------------------------------"


if ! sbt one-jar ; then
  echo "Creating one jar package failed" 2>&1
  echo "======================================================"
  exit 1
fi