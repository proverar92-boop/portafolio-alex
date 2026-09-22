# e-Portafolio Profesional

Portafolio digital de Gian Meza para el curso **Proyecto de Aplicación Profesional**. La aplicación está preparada para crecer semana a semana con nuevas evidencias, proyectos y aprendizajes.

## Tecnologías

- Java 25, Spring Boot y JSP
- Maven, JDBC y MySQL
- HTML5, CSS3 y JavaScript
- Bootstrap 5, Bootstrap Icons y AOS
- Git y GitHub

## Instalación y ejecución

1. Requiere JDK 25, Maven 3.9+ y MySQL.
2. Ejecuta `portalex.sql` en MySQL para crear la base `portalex`.
3. Ejecuta `database/portalex-migration.sql` para agregar la contraseña de los perfiles registrados y las tablas de archivos y enlaces.
4. Configura `DB_URL`, `DB_USERNAME` y `DB_PASSWORD`, o modifica sus valores predeterminados en `application.properties`.
5. Ejecuta `mvn spring-boot:run` desde la raíz.
6. Abre `http://localhost:8080`.

Para generar el artefacto ejecutable: `mvn clean package`.

## Estructura

- `src/main/java`: aplicación y controlador MVC.
- `src/main/webapp/WEB-INF/views`: vistas JSP.
- `src/main/resources/static`: estilos y scripts del frontend.
- `database/portalex-migration.sql`: migración de perfiles, archivos binarios y enlaces.

## Panel privado de evidencias

1. Abre `http://localhost:8080/login`.
2. El acceso público permite registrarse con cualquier correo válido y contraseña.
3. El administrador privado entra con el correo o usuario guardado en `administrador`.
4. En `/backed` selecciona una semana para subir archivos de cualquier formato o agregar enlaces.
5. Los trabajos publicados aparecen en la evidencia correspondiente y se entregan desde rutas controladas por la aplicación.

La aplicación usa únicamente la base `portalex`. Los usuarios registrados se guardan en `perfil`; el panel privado queda reservado para las cuentas de `administrador`. Los datos de cada semana se guardan en `semanas`, los archivos completos en `archivos_evidencia` y los enlaces en `enlaces_evidencia`.

Para Render configura estas variables de entorno: `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` y `PORT`. La base MySQL debe ser accesible desde Render; una base instalada en tu PC no será accesible desde Internet.

## Autor

**Gian Meza** · Estudiante de Ingeniería de Sistemas · Desarrollo Web con Java

## Licencia

Proyecto académico personal. Todos los derechos reservados.
