# Evidencias visuales de la EP3

Estas capturas se generaron el **26 de noviembre de 2026** ejecutando el stack completo con Docker Compose en la rama `ep3-observabilidad`, commit `adc1e63`.

## Qué debe revisar el profesor

| Evidencia | Qué demuestra | Relación con la rúbrica |
|---|---|---|
| [Dashboard de Grafana](ep3/01-grafana-dashboard.png) | Dashboard provisionado con tiempo de despliegue, cobertura, CPU de JVM y memoria heap. La cobertura visible es 78,2 %. | IE3 / IL3.2 |
| [Errores y logs](ep3/02-grafana-errores-y-logs.png) | Errores HTTP 400/404 generados durante la prueba y logs de la aplicación consultados desde Loki mediante Grafana. | IE1 / IL3.1 |
| [Targets de Prometheus](ep3/03-prometheus-targets-up.png) | Prometheus recolectando desde `/actuator/prometheus` de la aplicación y desde Pushgateway; ambos targets aparecen `UP`. | IE1 / IE3 |

## Cómo se reprodujo

```bash
docker compose --env-file .env.example up --build -d --wait --wait-timeout 180
curl http://localhost:8098/actuator/health
curl http://localhost:8098/actuator/prometheus
```

Se generó un pedido válido y también solicitudes 400/404 para que las métricas de errores fueran visibles. La cobertura de 78,21 % corresponde al informe JaCoCo local. Las métricas `ci_*` visibles en el dashboard fueron publicadas en el Pushgateway local con la referencia del commit para hacer reproducible la evidencia; en CI/CD el workflow las publica automáticamente con los valores de cada ejecución.

## Componentes observados

- **Micrometer/Actuator:** salud, métricas HTTP, CPU y memoria de la JVM.
- **Prometheus:** scraping de la aplicación y Pushgateway.
- **Loki + Promtail:** recolección y consulta de logs de los contenedores.
- **Grafana:** datasource provisionado y dashboard personalizado de la EP3.
