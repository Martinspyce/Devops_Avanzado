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
3. **Crear una rama de trabajo:**
   ```bash
   git checkout -b feature-busqueda-estudiante
   ```
4. **Realizar los cambios en el código.**
5. **Ejecutar y validar las pruebas locales:**
   ```bash
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
   git push -u origin feature-busqueda-estudiante
   ```
8. **Abrir Pull Request en GitHub** respetando la plantilla `.github/pull_request.md`.
9. **Revisión por pares (Code Review)** y aprobación de checks en CI.
10. **Merge utilizando `--no-ff`:**
    ```bash
    git checkout develop
    git merge --no-ff feature-busqueda-estudiante -m "Merge branch 'feature-busqueda-estudiante' into develop"
    ```
