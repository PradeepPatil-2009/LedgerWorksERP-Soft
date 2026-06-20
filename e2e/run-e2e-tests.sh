#!/bin/bash
# Boot backend (MySQL) + serve the React build, then run the FUNCTIONAL
# Playwright e2e suite (real Chrome, with assertions) against it.
# Exits non-zero if any scenario fails. Cleans up servers on exit.
set -u
REPO=/Users/rahul/Documents/workspace/rahul/LedgerWorksERP-Soft
BE=$REPO/ledgerworks-backEnd
UI=$REPO/ledgerworks-ui
E2E=$REPO/e2e
L=/tmp/lwe2e

cleanup() {
  [ -n "${SERVE_PID:-}" ] && kill "$SERVE_PID" 2>/dev/null
  [ "${STARTED_BE:-0}" = "1" ] && { lsof -ti tcp:8080 | xargs kill -9 2>/dev/null; }
}
trap cleanup EXIT

echo "=== 1. backend on :8080 ==="
STARTED_BE=0
if ! lsof -ti tcp:8080 >/dev/null 2>&1; then
  rm -f $L-be.log
  ( mvn -f $BE/pom.xml -q -DskipTests spring-boot:run > $L-be.log 2>&1 & )
  STARTED_BE=1
  for i in $(seq 1 120); do grep -q "Started LedgerWorksApplication" $L-be.log 2>/dev/null && break; sleep 1; done
fi
lsof -ti tcp:8080 >/dev/null 2>&1 && echo "backend up" || { echo "BACKEND FAILED"; tail -30 $L-be.log; exit 1; }

echo "=== 2. build frontend ==="
( cd $UI && CI=false npm run build > $L-build.log 2>&1 ) && grep -q "Compiled successfully" $L-build.log && echo "build ok" || { echo "FE BUILD FAILED"; tail -20 $L-build.log; exit 1; }

echo "=== 3. serve build on :3000 ==="
lsof -ti tcp:3000 | xargs kill -9 2>/dev/null
node $E2E/static-server.mjs "$UI/build" 3000 > $L-serve.log 2>&1 &
SERVE_PID=$!
for i in $(seq 1 20); do curl -s -o /dev/null localhost:3000 && break; sleep 1; done
echo "frontend served"

echo "=== 4. run functional e2e suite ==="
cd $E2E
BASE_URL=http://localhost:3000 API_URL=http://localhost:8080/api node $E2E/e2e-tests.mjs
RC=$?

echo "=== 5. done (exit $RC) ==="
exit $RC
