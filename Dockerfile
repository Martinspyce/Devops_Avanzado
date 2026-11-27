# syntax=docker/dockerfile:1

# ============================================================
# Etapa 1: dependencies
# Solo copia el pom.xml para que Docker reutilice esta capa
# (y el caché de Maven) mientras las dependencias no cambien.
# ============================================================
FROM maven:3.9-eclipse-temurin-17 AS dependencies
WORKDIR /app
COPY pom.xml .
RUN mvn -B -q dependency:go-offline

# ============================================================
# Etapa 2: test
# Ejecuta las pruebas unitarias. Si una falla, el build se
# detiene y no se genera ninguna imagen.
# ============================================================
FROM dependencies AS test
COPY src ./src
RUN mvn -B test

# ============================================================
# Etapa 3: compile
# Reutiliza el código ya validado y genera el .jar ejecutable.
# ============================================================
FROM test AS compile
RUN mvn -B package -DskipTests

# ============================================================
# Etapa 4: prod
# Imagen final: solo JRE + el .jar. Sin código fuente, sin
# Maven y ejecutando como usuario sin privilegios.
# (Se usa noble y no alpine porque eclipse-temurin:17-jre-alpine
# no publica imagen para arm64, p. ej. Mac con Apple Silicon.)
# ============================================================
FROM eclipse-temurin:17-jre-noble AS prod

# Trazabilidad: commit y versión que originaron esta imagen
ARG GIT_COMMIT=unknown
ARG VERSION=dev
LABEL org.opencontainers.image.title="order-manager" \
      org.opencontainers.image.description="Order Manager API (DOY0101)" \
      org.opencontainers.image.revision="${GIT_COMMIT}" \
      org.opencontainers.image.version="${VERSION}"

RUN groupadd --system app && useradd --system --gid app --no-create-home app
WORKDIR /app
COPY --from=compile --chown=app:app /app/target/*.jar /app/app.jar

USER app
ENV GIT_COMMIT=${GIT_COMMIT} \
    JAVA_OPTS="-XX:MaxRAMPercentage=75"

EXPOSE 8098
HEALTHCHECK --interval=15s --timeout=3s --start-period=40s --retries=5 \
  CMD wget -qO- http://localhost:8098/actuator/health | grep -q '"status":"UP"' || exit 1

ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar /app/app.jar"]
