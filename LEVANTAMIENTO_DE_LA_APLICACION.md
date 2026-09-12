#  Levantamiento de la Aplicación y Guía de Trabajo

Esta guía explica paso a paso cómo clonar, configurar, ejecutar y desplegar localmente el microservicio REST **bdget** en puerto `8012`, además del flujo colaborativo con GitFlow y la regla `--no-ff`.

---

##  Requisitos Previos

- **JDK 17** instalado y configurado en el sistema.
- **Git** instalado.
- **Docker** (opcional para ejecución en contenedor).

---

##  Pasos para Levantamiento Local

### 1. Clonar e ingresar al repositorio
```bash
git clone https://github.com/Martinspyce/Devops_Avanzado.git
cd Devops_Avanzado
```

### 2. Ejecutar la aplicación con Spring Boot
El proyecto incluye el Maven Wrapper (`./mvnw` en Linux/Mac o `.\mvnw.cmd` en Windows).

```bash
# Windows
.\mvnw.cmd spring-boot:run

# Linux / macOS
./mvnw spring-boot:run
```

- **Servidor REST:** `http://localhost:8012/students`
- **Consola H2 en memoria:** `http://localhost:8012/h2-console`
  - **JDBC URL:** `jdbc:h2:mem:bdgetdb`
  - **Usuario:** `sa`
  - **Contraseña:** *(sin contraseña)*

---

##  Pruebas Automatizadas y Verificación

```bash
# Pruebas unitarias
.\mvnw.cmd clean test

# Verificación completa (Package + Jacoco Coverage)
.\mvnw.cmd clean verify
```

---

##  Levantamiento con Docker

```bash
# Construir la imagen local en puerto 8012
docker build -t bdget .

# Ejecutar el contenedor
docker run --rm -p 8012:8012 bdget
```

---

##  Flujo de Ramificación GitFlow (con `--no-ff`)

Para mantener la trazabilidad completa en el grafo de commits, **cada merge debe realizarse utilizando el parámetro `--no-ff`**:

```bash
# Sincronizar develop
git checkout develop
git pull origin develop

# Crear nueva feature
git checkout -b feature-busqueda-estudiante

# Realizar commits convencionales
git add .
git commit -m "feat: agrega búsqueda de estudiantes por nombre"

# Probar
.\mvnw.cmd clean test

# Push de la feature
git push -u origin feature-busqueda-estudiante

# Fusionar de regreso en develop con --no-ff
git checkout develop
git merge --no-ff feature-busqueda-estudiante -m "Merge branch 'feature-busqueda-estudiante' into develop"
```
