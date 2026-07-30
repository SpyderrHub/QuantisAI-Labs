while true; do
  sleep 2
  if grep -q "BUILD SUCCESSFUL" app/build/reports/lint-results.xml 2>/dev/null; then break; fi
done
