# Observabilidad y entornos reales en DevOps

Implementación de la Evaluación Parcial N°3 de DOY0101 sobre el microservicio Order Manager. Esta rama extiende la solución de la EP2 con métricas, dashboards, auditoría automatizada y manifiestos Kubernetes preparados para un despliegue en AWS Academy.

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

El laboratorio AWS es necesario solamente para obtener la evidencia de un entorno cloud real. Los manifiestos no contienen credenciales ni secretos; antes de aplicarlos hay que crear el secreto en el clúster:

```bash
aws sts get-caller-identity
aws eks update-kubeconfig --region <REGION> --name <CLUSTER_NAME>

kubectl apply -f k8s/namespace.yaml
kubectl -n order-manager create secret generic order-manager-db-secret \
  --from-literal=username=orderuser \
  --from-literal=password='<CLAVE_NO_SUBIR_AL_REPO>' \
  --dry-run=client -o yaml | kubectl apply -f -

kubectl apply -k k8s/
kubectl -n order-manager rollout status statefulset/order-manager-db --timeout=180s
kubectl -n order-manager rollout status deployment/order-manager --timeout=180s
kubectl -n order-manager get pods,svc
kubectl -n order-manager get svc order-manager
```

El Service `LoadBalancer` entrega la URL pública de la API cuando AWS termina de provisionar el balanceador. Para instalar Prometheus y Grafana en el clúster se puede usar `kube-prometheus-stack` en el lab y conservar el mismo dashboard exportado en `monitoring/grafana/dashboards/`. El Service de la aplicación ya incluye las anotaciones de scraping de Prometheus.

Para usar la imagen publicada en GHCR, el paquete debe ser público o el clúster debe tener un `imagePullSecret`. En una demostración controlada, también se puede reemplazar `image` en `k8s/app.yaml` por el tag inmutable de la ejecución, por ejemplo `ghcr.io/martinspyce/devops_avanzado:<commit-corto>`.

## Protección de ramas en GitHub

Configurar en `Settings → Branches → Branch protection rules` para `main`, `develop` y `ep2`:

1. Exigir Pull Request y al menos una aprobación.
2. Exigir que pasen `Pruebas y cobertura`, `Auditoría de cumplimiento`, `Análisis SonarQube` e `Imagen Docker y Trivy`.
3. Exigir resolución de conversaciones y bloquear force push.
4. No permitir bypass de estas reglas a los administradores durante la evaluación.

Estas reglas son una configuración del repositorio remoto, por lo que deben verificarse en GitHub y documentarse con una captura como evidencia final.

## Evidencias que faltan ejecutar en el entorno del estudiante

El código deja preparada la solución, pero las siguientes evidencias dependen de los servicios disponibles en cada entorno y deben capturarse al ejecutar:

1. Resultado verde del workflow en GitHub Actions.
2. Dashboard de Grafana con datos de la ejecución y tráfico de la API.
3. `kubectl get pods,svc` mostrando los recursos en EKS.
4. Prueba negativa: introducir una vulnerabilidad bloqueante o bajar la cobertura para mostrar que el pipeline se detiene.
5. Configuración de branch protection en GitHub.

No se deben presentar como ejecutadas hasta obtener esas capturas del lab y del repositorio remoto.
