# Backed privado

El contenido de `evidencias/semana-01` a `semana-16` se gestiona desde el panel privado `/backed`. No se sirve como carpeta estática: la aplicación valida la sesión del administrador y entrega los archivos mediante la página de evidencia.

El acceso se valida contra la tabla `administrador` de MySQL. Usa el `usuario` o `correo` y la contraseña que existan en esa tabla.
