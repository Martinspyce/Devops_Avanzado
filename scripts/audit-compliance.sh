#!/usr/bin/env bash
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT"

failures=0
check() {
  local description="$1"
  local command="$2"
  if bash -c "$command"; then
    printf 'PASS  %s\n' "$description"
  else
    printf 'FAIL  %s\n' "$description" >&2
    failures=$((failures + 1))
  fi
}

check "Métricas Prometheus en la aplicación" "grep -q 'micrometer-registry-prometheus' pom.xml && grep -q 'prometheus' src/main/resources/application-docker.properties"
check "Dashboard versionado" "test -s monitoring/grafana/dashboards/order-manager-ep3.json"
check "Stack de observabilidad en Compose" "grep -q '^  prometheus:' docker-compose.yml && grep -q '^  grafana:' docker-compose.yml && grep -q '^  pushgateway:' docker-compose.yml"
check "Logs centralizados con Loki" "grep -q '^  loki:' docker-compose.yml && grep -q '^  promtail:' docker-compose.yml && grep -q 'type: loki' monitoring/grafana/provisioning/datasources/prometheus.yml"
check "Imagen de aplicación sin root" "grep -q '^USER 10001:10001' Dockerfile && grep -q '^HEALTHCHECK' Dockerfile"
check "Pipeline bloquea por Trivy" "grep -q 'exit-code: \"1\"' .github/workflows/ci-cd.yml"
check "Pipeline espera Quality Gate" "grep -q 'sonar.qualitygate.wait=true' .github/workflows/ci-cd.yml"
check "Pipeline exige auditoría antes del deploy" "grep -q 'needs: \[sonar, build-scan, compliance\]' .github/workflows/ci-cd.yml"
check "Kubernetes define probes y recursos" "grep -q 'readinessProbe' k8s/app.yaml && grep -q 'resources:' k8s/app.yaml"
check "Overlay reproducible para AWS Academy" "test -s k8s/academy/kustomization.yaml && test -s k8s/academy/postgres.yaml && test -s k8s/academy/app-patch.yaml"
check "No hay llaves privadas versionadas" "! git grep -n -I -E 'BEGIN (RSA|EC|OPENSSH) PRIVATE KEY|AKIA[0-9A-Z]{16}' -- ':!scripts/audit-compliance.sh'"

if (( failures > 0 )); then
  printf '\nAuditoría fallida: %d regla(s) incumplida(s).\n' "$failures" >&2
  exit 1
fi

printf '\nAuditoría de cumplimiento aprobada.\n'
