# bdget 🎓 — Microservicio REST para Gestión de Estudiantes

Microservicio REST en **Java 17 + Spring Boot 3** para la gestión de estudiantes (CRUD). Incluye tests unitarios, validaciones Jakarta, persistencia con Spring Data JPA y H2 en memoria, contenedores Docker y pipeline automatizado con GitHub Actions.

Este repositorio corresponde a la **Evaluación Parcial N°1 de Ingeniería DevOps (DOY0101)** — Nivel Avanzado.

---

## 🚀 Cómo levantar el proyecto localmente

Requiere **JDK 17** y Maven (o usar el wrapper incluido `./mvnw` / `.\mvnw.cmd`).

### 1. Ejecución directa con Spring Boot:
```bash
./mvnw spring-boot:run
```
> **Nota de puerto:** El proyecto está configurado para ejecutarse en el puerto **`8012`** (`server.port=8012`).

- **API REST:** `http://localhost:8012/students`
- **Consola H2:** `http://localhost:8012/h2-console`
  - **JDBC URL:** `jdbc:h2:mem:bdgetdb`
  - **Usuario:** `sa`
  - **Contraseña:** *(vacío)*

### 2. Pruebas y Verificación:
```bash
# Ejecutar suite de pruebas unitarias
./mvnw clean test

# Verificación completa (compile, test, package, jacoco report)
./mvnw clean verify
```

---

## 📁 Estructura del Repositorio

```
bdget/
├── .github/
│   ├── workflows/
│   │   └── main.yml               → Workflow CI/CD en GitHub Actions
│   └── PULL_REQUEST_TEMPLATE.md   → Plantilla estandarizada para Pull Requests
├── src/
│   ├── main/
│   │   ├── java/com/example/bdget/
│   │   │   ├── controller/        → Endpoints REST (StudentController)
│   │   │   ├── model/             → Entidades JPA y validaciones (Student)
│   │   │   ├── repository/        → Repositorios Spring Data JPA (StudentRepository)
│   │   │   └── service/           → Capa de servicio e implementación (StudentService)
│   │   └── resources/
│   │       └── application.properties → Configuración del servidor (puerto 8012) y H2
│   └── test/
│       └── java/com/example/bdget/ → Pruebas unitarias e integración (Controller, Service, Model)
├── Dockerfile                      → Configuración de contenedor multi-stage (Java 17, puerto 8012)
├── pom.xml                         → Gestión de dependencias y plugins de Maven
├── CONTRIBUTING.md                 → Guía paso a paso para la colaboración del equipo
└── README.md                       → Documentación principal del proyecto
```

---

## 🌳 Estrategia de Ramificación

### Comparativa de Estrategias de Control de Versiones

#### 1. GitFlow
Estrategia estructurada ideal para proyectos con ciclos de lanzamiento definidos y múltiples entornos. Utiliza dos ramas principales de larga duración (`main` y `develop`) complementadas por ramas temporales (`feature/*`, `release/*`, `hotfix/*`).
- **Ventajas:** Excelente control sobre versiones en producción, aislamiento total del desarrollo activo, ideal para equipos que manejan versiones formales (`v1.0.0`).
- **Desventajas:** Mayor sobrecarga en la gestión de fusiones (merges), integración diferida que puede generar conflictos complejos si las ramas se mantienen abiertas por mucho tiempo.

#### 2. GitHub Flow
Modelo ligero y centrado en despliegues continuos. Todo el desarrollo ocurre en ramas creadas directamente desde `main`. Una vez que el código se prueba y revisa vía Pull Request, se fusiona inmediatamente a `main` y se despliega.
- **Ventajas:** Simple, rápido, minimiza conflictos de integración, fomenta entregas continuas.
- **Desventajas:** No soporta fácilmente la gestión simultánea de múltiples versiones en producción ni ambientes de pre-producción estables antes del release.

#### 3. Trunk-Based Development
Los desarrolladores integran pequeños cambios frecuentemente en una única rama principal (`trunk` o `main`). Se apoya fuertemente en Feature Flags y pruebas automatizadas robustas.
- **Ventajas:** Elimina los "merge hells", retroalimentación instantánea, máxima agilidad.
- **Desventajas:** Requiere una madurez técnica muy alta en CI/CD, automatización de pruebas al 100% y disciplina del equipo para no romper `main`.

---

### Estrategia Seleccionada por el Equipo: GitFlow

El equipo utilizará la estrategia **GitFlow**.

#### Flujo Técnico de Ramas:
- **`main`**: Contiene únicamente código estable en estado de producción. Cada commit en `main` representa una versión liberada.
- **`develop`**: Rama principal de integración para el desarrollo activo. Contiene las últimas funcionalidades integradas listas para la próxima entrega.
- **`feature/<nombre-descriptivo>`**: Ramas temporales creadas a partir de `develop` para construir nuevas funcionalidades. Se fusionan de regreso en `develop` vía Pull Request (`feature/* -> develop`).
- **`hotfix/<nombre-descriptivo>`**: Ramas temporales creadas directamente desde `main` para resolver errores críticos en producción. Al finalizar, se fusionan tanto en `main` (`hotfix/* -> main`) como en `develop` (`hotfix/* -> develop`).

---

### JUSTIFICACIÓN DEL EQUIPO - COMPLETAR MANUALMENTE

> ✏️ **A COMPLETAR POR LOS INTEGRANTES:**
>
> Indiquen aquí la justificación técnica específica acordada por el equipo para la elección de GitFlow en este proyecto (por ejemplo: justificar considerando la separación entre código estable y desarrollo en curso, el tamaño del equipo de 2 personas, el control estricto de entregas para la evaluación parcial, etc.).

---

## 🔀 Convenciones de Naming de Ramas

Para garantizar la consistencia en el repositorio, todas las ramas deben cumplir con las siguientes reglas:
- Utilizar letras **minúsculas**.
- Separar las palabras exclusivamente con **guiones (`-`)**.
- **No utilizar espacios** ni caracteres especiales.
- Mantener nombres **cortos y descriptivos**.
- Cada rama debe representar un cambio **único y concreto**.

### Ejemplos Estándar:
- **Features:**
  - `feature/busqueda-estudiante`
  - `feature/validacion-email`
  - `feature/agregar-campo-telefono`
- **Hotfixes:**
  - `hotfix/corregir-validacion-estudiante`
  - `hotfix/error-endpoint-estudiantes`

---

## 📝 Convenciones de Commits (Conventional Commits)

Los mensajes de commit deben seguir la especificación de **Conventional Commits** para mantener un historial limpio, legible y automatizable.

### Formato General:
```text
<prefijo>: <descripción breve en presente y minúsculas>
```

### Prefijos Autorizados:
- **`feat:`**: Nueva funcionalidad para el usuario.
- **`fix:`**: Corrección de un error o bug en el código.
- **`docs:`**: Cambios únicamente en la documentación.
- **`test:`**: Añadir o corregir pruebas unitarias o de integración.
- **`refactor:`**: Cambios de código que no corrigen bugs ni agregan funcionalidades (limpieza/optimización).
- **`chore:`**: Tareas secundarias de mantenimiento, dependencias o configuración del proyecto.
- **`ci:`**: Modificaciones en los archivos de configuración de CI/CD (GitHub Actions).

### Ejemplos:
- `feat: agrega búsqueda de estudiantes por nombre`
- `fix: corrige validación de email y campos requeridos`
- `test: agrega pruebas unitarias para StudentService`
- `docs: actualiza estrategia GitFlow y guía de contribución`
- `ci: mejora workflow de GitHub Actions con verificación Maven`

---

## 🛠️ Simulación de Desarrollo Colaborativo

Para la Evaluación Parcial, el proyecto cuenta con los siguientes cambios preparados en el microservicio:

1. **FEATURE 1 (`feature/busqueda-estudiante`):**
   - Implementación de la búsqueda de estudiantes por coincidencia de nombre (case-insensitive).
   - Incluye método en `StudentRepository`, `StudentService`, endpoint `GET /students/search?name=...` en `StudentController` y pruebas unitarias correspondientes.
2. **FEATURE 2 (`feature/validacion-email`):**
   - Incorporación del campo `email` en la entidad `Student` con anotaciones de validación Jakarta (`@Email`, `@NotBlank`).
   - Pruebas unitarias asociadas en el modelo, servicio y controlador.
3. **HOTFIX (`hotfix/corregir-validacion-estudiante`):**
   - Corrección del método `creaStudent` a `createStudent` e inclusión de la anotación `@Valid` en los endpoints `@PostMapping` y `@PutMapping` de `StudentController` para asegurar la aplicación de validaciones al recibir peticiones HTTP.

---

## 🔍 Estrategia de Code Review y Pull Requests

### Importancia de los Pull Requests
Los Pull Requests son el mecanismo central de control de calidad y colaboración. Permiten:
- **Revisión por pares:** Inspección previa del código por otro integrante.
- **Colaboración activa:** Discusión de mejoras antes de incorporar cambios a la rama principal.
- **Trazabilidad:** Registro histórico de qué se cambió, por qué y quién lo aprobó.
- **Control de Calidad:** Verificación automática de pruebas antes del merge.
- **Reducción de errores:** Prevención de fallos en producción o desestabilización de `develop`.

### Reglas para Aprobar un Pull Request:
1. El proyecto debe **compilar limpiamente** sin errores de construcción.
2. Todos los **tests unitarios y de integración deben pasar (100% exitosos)**.
3. No deben existir **conflictos de fusión** con la rama destino.
4. El PR debe incluir una **descripción clara** del cambio realizado utilizando la plantilla `.github/PULL_REQUEST_TEMPLATE.md`.
5. Debe contar con la aprobación explícita de **otro integrante del equipo**.
6. Evitar commits innecesarios o desordenados (limpiar historial si corresponde).
7. Verificar efectivamente que **no se incluyan credenciales, contraseñas ni secretos**.
8. Confirmar que el código respeta las convenciones de nombres, arquitectura y estilo del proyecto.

---

## ⚙️ Automatización y Pipeline CI/CD

El repositorio cuenta con un workflow de automatización en `.github/workflows/main.yml` configurado con GitHub Actions.

### Diagrama del Pipeline:

```text
  Developer
      |
      v
  feature/*  /  hotfix/*
      |
      v
  Pull Request
      |
      v
┌─────────────────────────────────────────┐
│           GitHub Actions (CI)           │
│  1. Checkout (actions/checkout@v4)      │
│  2. Setup JDK 17 (actions/setup-java@v4)│
│  3. Pruebas Unitarias (mvn clean test)  │
│  4. Verificación y Build (mvn verify)   │
│  5. Artefacto JAR (upload-artifact@v4)  │
│  6. Simulación Docker Build             │
└─────────────────────────────────────────┘
      |
      v
  develop / main
```

### Conceptos CI / CD:
- **CI (Integración Continua):** Práctica de automatizar la compilación y ejecución de pruebas cada vez que un desarrollador sube cambios. Garantiza la detección temprana de errores.
- **CD (Entrega/Despliegue Continuo):** Automatización del empaquetado del artefacto (`.jar` / contenedor Docker) para que esté listo para ser desplegado en entornos de staging o producción.
- **Implementación en la Evaluación:** Se ha construido la base sólida de **CI** (compilación, pruebas automatizadas y reporte Jacoco) junto a la preparación para **CD** (generación de artefacto JAR cargado como artifact de GitHub y simulación del build de la imagen Docker).

---

## 🐳 Docker (Preparación para Despliegue)

El proyecto incluye un `Dockerfile` optimizado utilizando **multi-stage build** para mantener la imagen ligera y segura, alineada a **Java 17**.

### Comandos de Uso Local:
```bash
# Construir la imagen del microservicio
docker build -t bdget .

# Ejecutar el contenedor mapeando el puerto 8012
docker run -p 8012:8012 bdget
```
La aplicación quedará accesible en `http://localhost:8012/students`.

> **Nota sobre CI/CD:** En el pipeline de GitHub Actions se simula la construcción de la imagen con `docker build -t bdget:${{ github.sha }} .` para verificar que el contenedor empaqueta correctamente sin necesidad de realizar `docker push` a registros externos sin credenciales.

---

##  Flujo de Trabajo Git (Trazabilidad)

### Comandos Principales de Git:
- `git clone`: Clona un repositorio remoto en la máquina local.
- `git checkout` / `git switch`: Cambia de rama o crea nuevas ramas.
- `git pull`: Descarga e integra los cambios del repositorio remoto.
- `git add`: Prepara los archivos modificados para el próximo commit.
- `git commit`: Guarda el estado de los cambios en el historial local.
- `git push`: Sube los commits locales a la rama remota en GitHub.
- `git merge`: Fusiona los cambios de una rama en otra.

### Ejemplo Completo de Desarrollo de una Feature:

```bash
# 1. Posicionarse en develop y actualizar
git checkout develop
git pull origin develop

# 2. Crear nueva rama especificando el tipo feature y la función realizada
git checkout -b feature-busqueda-estudiante

# 3. Realizar cambios en el código y verificar ejecuciones locales
./mvnw clean test

# 4. Agregar cambios y realizar commit convencional describiendo la función
git add .
git commit -m "feat: agrega búsqueda de estudiantes por nombre"

# 5. Hacer push al repositorio remoto asociando la rama a la función implementada
git push -u origin feature-busqueda-estudiante

# 6. Crear el Pull Request en la interfaz de GitHub:
#    Origen: feature-busqueda-estudiante -> Destino: develop
```

---

##  Control de Versiones (Semantic Versioning)

El proyecto adopta **Semantic Versioning 2.0.0 (SemVer)** en el formato `MAJOR.MINOR.PATCH` (Ejemplo: `v1.0.0`).

- **MAJOR (Versión Mayor):** Incrementa ante cambios de arquitectura o cambios incompatibles en la API REST que rompen la compatibilidad con clientes existentes.
- **MINOR (Versión Menor):** Incrementa al añadir nuevas funcionalidades de forma compatible hacia atrás (ej. integración de una rama `feature/*`).
- **PATCH (Parche):** Incrementa al aplicar correcciones de errores o bugs compatibles (ej. integración de una rama `hotfix/*` o `fix`).

---

##  Guía de Buenas Prácticas DevOps

Para dar cumplimiento integral a la rúbrica (IE5), el equipo debe guiarse por los siguientes principios:

1. **Gestión Estricta de Ramas:** Nunca realizar commits directos sobre la rama `main` ni sobre `develop`. Todo cambio debe ingresar vía Pull Request.
2. **Commits Pequeños y Frecuentes:** Realizar atómicos con mensajes claros según Conventional Commits. Evitar commits gigantes "monolíticos".
3. **Pruebas Automatizadas Pre-push:** Ejecutar `./mvnw clean test` localmente antes de enviar código al repositorio remoto.
4. **Gestión Segura de Secretos:** Prohibido subir contraseñas, tokens de API, llaves privadas o credenciales al repositorio. Utilizar variables de entorno o GitHub Secrets.
5. **Revisión por Pares Exigente:** Ningún PR se aprueba sin la lectura exhaustiva del código por parte de otro integrante y la aprobación de los checks de CI/CD.
6. **Contenedores Inmutables:** Mantener el `Dockerfile` actualizado y alineado con la versión del JDK del proyecto (Java 17).
7. **Documentación Viva:** Mantener el `README.md` y `CONTRIBUTING.md` siempre al día con los cambios del proyecto.

---

## Protección de Ramas (`main` y `develop`)

Se recomienda configurar **Branch Protection Rules** en GitHub para resguardar las ramas principales:

### Configuración Recomendada para `main`:
1. Ir a **GitHub** → **Settings** → **Branches** → **Add branch protection rule**.
2. **Branch name pattern:** `main`
3. Marcar **Require a pull request before merging**.
4. Marcar **Require status checks to pass before merging** (seleccionar los jobs de `main.yml`).
5. Marcar **Require conversation resolution before merging**.
6. Marcar **Do not allow bypassing the above settings** / **Block force pushes**.

### Configuración Recomendada para `develop`:
1. **Branch name pattern:** `develop`
2. Marcar **Require a pull request before merging**.
3. Marcar **Require status checks to pass before merging**.

---

## Uso de herramientas de Inteligencia Artificial

Herramienta:
[COMPLETAR POR EL EQUIPO]

Uso realizado:
[COMPLETAR POR EL EQUIPO]

Contenido revisado y validado por:
[COMPLETAR POR EL EQUIPO]

---

## 👥 Autores

- [COMPLETAR NOMBRE INTEGRANTE 1]
- [COMPLETAR NOMBRE INTEGRANTE 2]
