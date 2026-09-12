# Guía de Contribución al Proyecto `bdget` 🎓

---

##  Flujo de Trabajo para Contribuir (Paso a Paso)

1. **Instalar Git y Clonar el repositorio:**
   ```bash
   git clone https://github.com/Martinspyce/Devops_Avanzado.git
   cd Devops_Avanzado
   ```
2. **Sincronizar la rama `develop`:**
   ```bash
   git checkout develop
   git pull origin develop
   ```
3. **Crear una rama de trabajo** (`feature/` desde `develop`, o `hotfix/` desde `main`):
   ```bash
   git checkout -b feature/busqueda-estudiante-nombre
   ```
4. **Realizar los cambios en el código.**
5. **Ejecutar y validar las pruebas locales:**
   ```bash
   # macOS / Linux
   ./mvnw clean test
   ./mvnw clean verify

   # Windows
   .\mvnw.cmd clean test
   .\mvnw.cmd clean verify
   ```
6. **Registrar los cambios con Conventional Commits:**
   ```bash
   git add .
   git commit -m "feat: agrega búsqueda de estudiantes por nombre"
   ```
7. **Enviar la rama al repositorio remoto (Push):**
   ```bash
   git push -u origin feature/busqueda-estudiante-nombre
   ```
8. **Abrir Pull Request en GitHub** hacia `develop` (o hacia `main` si es un hotfix). GitHub carga automáticamente la plantilla `.github/pull_request_template.md`.
9. **Revisión por pares (Code Review):** otro integrante revisa y aprueba el PR, y los checks de GitHub Actions deben pasar.
10. **Merge desde GitHub:** se usa el botón *Merge pull request*, que crea un merge commit y deja registrado el cambio. No se hacen merges locales a `develop` ni a `main`.
11. **Si es un hotfix:** después del merge a `main`, abrir otro PR para llevar el cambio a `develop`.
12. **Actualizar tu copia local y borrar la rama:**
    ```bash
    git checkout develop
    git pull origin develop
    git branch -d feature/busqueda-estudiante-nombre
    ```
