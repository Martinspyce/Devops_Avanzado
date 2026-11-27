# Observabilidad y entornos reales en DevOps

Implementación de la Evaluación Parcial N°3 de DOY0101 sobre el microservicio Order Manager. Esta rama extiende la solución de la EP2 con métricas, dashboards, auditoría automatizada y manifiestos Kubernetes preparados para un despliegue en AWS Academy.

## Guía rápida para revisar la entrega

La entrega se encuentra en la rama `ep3-observabilidad` y se revisa desde estos puntos:

1. **Código y observabilidad:** Actuator/Micrometer en `pom.xml` y `src/main/resources/application-docker.properties`.
2. **Pipeline activo:** [`.github/workflows/ci-cd.yml`](.github/workflows/ci-cd.yml). Sus puertas son `Pruebas y cobertura`, `Auditoría de cumplimiento`, `Análisis SonarQube` e `Imagen Docker y Trivy`.
3. **Auditoría:** [`scripts/audit-compliance.sh`](scripts/audit-compliance.sh), ejecutable localmente.
4. **Dashboard:** [`monitoring/grafana/dashboards/order-manager-ep3.json`](monitoring/grafana/dashboards/order-manager-ep3.json), provisionado en la carpeta `DevOps EP3`.
5. **Kubernetes:** [`k8s/`](k8s/) contiene el despliegue persistente para un EKS con EBS CSI; [`k8s/academy/`](k8s/academy/) contiene el perfil reproducible para el laboratorio AWS Academy.
6. **Capturas verificables:** [`docs/evidence/EP3_EVIDENCIAS.md`](docs/evidence/EP3_EVIDENCIAS.md) enlaza capturas del dashboard, logs, errores y targets de Prometheus.

El Pull Request de la EP3 es [#9](https://github.com/Martinspyce/Devops_Avanzado/pull/9). No se deben subir contraseñas, tokens ni archivos `.env`; el secreto de PostgreSQL se crea directamente en el clúster.

## Alcance implementado

| Indicador | Evidencia en el repositorio | Resultado esperado |
|---|---|---|
| IE1 | Actuator + Micrometer, Prometheus, Loki/Promtail y Compose | Logs del contenedor, salud, latencia, errores y métricas de JVM visibles en Grafana |
| IE2 | `k8s/`, probes, recursos y Service `LoadBalancer` | Aplicación y PostgreSQL desplegables en Kubernetes/EKS |
| IE3 | `monitoring/grafana/dashboards/order-manager-ep3.json` | Dashboard con despliegue, cobertura, CPU, memoria y errores |
| IE4 | Este documento y comentarios del workflow | Trazabilidad desde commit, imagen y pipeline hasta el entorno |
| IE5 | SonarQube, Trivy, `scripts/audit-compliance.sh` y branch protection documentada | La auditoría se ejecuta como job obligatorio |
| IE6 | `needs` del workflow, Quality Gate, cobertura mínima y Trivy con `exit-code: "1"` | Una falla crítica impide construir o desplegar |

## Ejecución local

Requisitos: Docker Desktop y Git.

```bash
cp .env.example .env
docker compose up --build -d --wait --wait-timeout 180

# API y salud
curl http://localhost:8098/actuator/health

# Métricas Prometheus de la aplicación
curl http://localhost:8098/actuator/prometheus

# Interfaces
# Prometheus: http://localhost:9090
# Grafana:    http://localhost:3000  (admin / valor de GRAFANA_ADMIN_PASSWORD)
# Loki:       http://localhost:3100
```

Para generar tráfico y observar métricas, crear un pedido y consultar la API:

```bash
curl -X POST http://localhost:8098/api/orders \
  -H 'Content-Type: application/json' \
  -d '{"customerName":"Demo","items":[{"productName":"Libro","quantity":2,"unitPrice":10.50}]}'
curl http://localhost:8098/api/orders
```

El dashboard se provisiona automáticamente en la carpeta **DevOps EP3** de Grafana. Las métricas de cobertura y tiempo de despliegue aparecen después de una ejecución del workflow, porque se publican en Pushgateway desde el job `deploy`.

### Capturas de la validación local

La validación local del stack Compose se documenta en [`docs/evidence/EP3_EVIDENCIAS.md`](docs/evidence/EP3_EVIDENCIAS.md). Las imágenes muestran el dashboard provisionado, los logs recolectados por Loki/Promtail, los errores HTTP observados y los dos targets de Prometheus en estado `UP`.

## Flujo CI/CD y puertas de control

```text
test + cobertura
      ├── compliance
      ├── SonarQube + Quality Gate
      └── build de imagen + Trivy
                    ↓
          deploy Compose + smoke tests
                    ↓
          publicación de métricas CI
                    ↓
                 GHCR
```

El despliegue solo depende de resultados aprobados:

- `mvn verify` ejecuta pruebas y falla si JaCoCo queda bajo 70 % de líneas.
- SonarQube usa `sonar.qualitygate.wait=true`; un Quality Gate fallido detiene el flujo.
- Trivy rechaza vulnerabilidades `HIGH` y `CRITICAL` corregibles.
- `scripts/audit-compliance.sh` comprueba la existencia de observabilidad, dashboards, probes, recursos, ejecución de Trivy, Quality Gate y ausencia de llaves privadas.
- El job `deploy` necesita `sonar`, `build-scan` y `compliance`; por tanto, el fallo de cualquiera bloquea el despliegue.
- El commit se inyecta en la imagen y se valida en `/actuator/info` durante las pruebas de humo.

## Despliegue en AWS Academy con EKS

El laboratorio AWS se utilizó para obtener evidencia de un entorno cloud real. Los manifiestos no contienen credenciales ni secretos; antes de aplicarlos hay que crear el secreto en el clúster.

### Perfil recomendado para AWS Academy

El lab puede entregar un nodo pequeño y no siempre incluye el controlador EBS CSI. Por eso el perfil `k8s/academy/` usa una sola réplica de la aplicación, recursos reducidos y un `emptyDir` para PostgreSQL. Es reproducible, pero **los datos de PostgreSQL son efímeros** y se pierden al recrear el pod. Para persistencia se debe usar el perfil base con EBS CSI, descrito más abajo.

```bash
aws sts get-caller-identity
aws eks update-kubeconfig --region us-east-1 --name ep3-observabilidad-v2

kubectl apply -f k8s/namespace.yaml
kubectl -n order-manager create secret generic order-manager-db-secret \
  --from-literal=username=orderuser \
  --from-literal=password='<CLAVE_NO_SUBIR_AL_REPO>' \
  --dry-run=client -o yaml | kubectl apply -f -

kubectl apply -k k8s/academy/
kubectl -n order-manager rollout status deployment/order-manager --timeout=180s
kubectl -n order-manager rollout status deployment/order-manager-db --timeout=180s
kubectl -n order-manager get pods,svc
kubectl -n order-manager get svc order-manager
```

El Service `LoadBalancer` entrega la URL pública de la API cuando AWS termina de provisionar el balanceador. El Service de la aplicación incluye las anotaciones de scraping de Prometheus.

### Evidencia AWS verificada en esta entrega

La ejecución del laboratorio se verificó en `us-east-1`, clúster `ep3-observabilidad-v2`:

```bash
kubectl -n order-manager get pods
# order-manager-*       1/1 Running
# order-manager-db-*    1/1 Running

kubectl -n order-manager get svc order-manager
curl http://<EXTERNAL-IP>/actuator/health
# {"status":"UP","groups":["liveness","readiness"]}

curl http://<EXTERNAL-IP>/actuator/prometheus | head
# HELP application_ready_time_seconds ...
```

Durante la sesión, AWS entregó temporalmente este endpoint: `http://a2460f12cab904d3388ce4849b1f4d64-1025548174.us-east-1.elb.amazonaws.com`. El hostname puede cambiar cuando el laboratorio expire y se cree otro Load Balancer; por eso el comando `kubectl get svc` es la fuente de verdad.

### Perfil persistente para EKS con EBS CSI

Si el clúster tiene instalado y autorizado el controlador EBS CSI, se puede usar el perfil base:

```bash
kubectl apply -k k8s/
kubectl -n order-manager rollout status statefulset/order-manager-db --timeout=180s
kubectl -n order-manager rollout status deployment/order-manager --timeout=180s
```

Este perfil usa `k8s/postgres.yaml`, un `StatefulSet` y un PVC `1Gi` con la StorageClass `gp2`. En AWS Academy se debe confirmar primero que exista un provisionador `ebs.csi.aws.com`; si no existe, usar `k8s/academy/`.

Para usar la imagen publicada en GHCR, el paquete debe ser público o el clúster debe tener un `imagePullSecret`. En una demostración controlada, también se puede reemplazar `image` en `k8s/app.yaml` por el tag inmutable de la ejecución, por ejemplo `ghcr.io/martinspyce/devops_avanzado:<commit-corto>`.

## Protección de ramas en GitHub

Configurar en `Settings → Branches → Branch protection rules` para `main`, `develop` y `ep2`:

1. Exigir Pull Request y al menos una aprobación.
2. Exigir que pasen `Pruebas y cobertura`, `Auditoría de cumplimiento`, `Análisis SonarQube` e `Imagen Docker y Trivy`.
3. Exigir resolución de conversaciones y bloquear force push.
4. No permitir bypass de estas reglas a los administradores durante la evaluación.

Estas reglas son una configuración del repositorio remoto, por lo que deben verificarse en GitHub y documentarse con una captura como evidencia final.

## Evidencias para entregar

Las evidencias que el profesor debe revisar o capturar son:

1. Resultado verde del workflow en GitHub Actions.
2. Dashboard de Grafana con datos de la ejecución y tráfico de la API.
3. `kubectl get pods,svc` mostrando los recursos en EKS.
4. Prueba negativa: introducir una vulnerabilidad bloqueante o bajar la cobertura para mostrar que el pipeline se detiene.
5. Configuración de branch protection en GitHub.

La evidencia AWS de salud y métricas ya fue verificada contra el Load Balancer indicado arriba. El dashboard de Grafana y la protección de ramas deben documentarse con capturas desde sus respectivas interfaces si la rúbrica las exige; el repositorio contiene los archivos necesarios para reproducirlas.
