#!/bin/bash -e

./.env-info.sh

sbt clean
echo "======================================================"
echo "Running: sbt compile test"
echo "------------------------------------------------------"
if ! sbt compile test ; then
  echo "Testing failed!" 2>&1
  echo "======================================================"
  exit 1
fi
echo "Done: sbt compile test"
echo "======================================================"
