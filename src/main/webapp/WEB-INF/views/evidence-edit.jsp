<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Actualizar ${evidence.weekLabel()} | e-Portafolio</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css?v=delete-work">
</head>
<body class="edit-body">
<nav class="site-nav is-scrolled">
    <div class="container nav-inner">
        <a class="brand" href="${pageContext.request.contextPath}/backed">
            <span class="brand-mark">SA</span>
            <span>Editar trabajo</span>
        </a>
        <a class="button-secondary" href="${pageContext.request.contextPath}/backed">
            <i class="bi bi-arrow-left"></i> Volver al Backed
        </a>
    </div>
</nav>

<main class="backed-page">
    <div class="container">
        <div class="edit-heading">
            <div class="edit-heading-icon"><i class="bi bi-file-earmark-ruled"></i></div>
            <div>
                <div class="section-label">${evidence.weekLabel()}</div>
                <h1>Actualizar trabajo.</h1>
            </div>
        </div>
        <p class="backed-intro">Cambia los datos de la semana. Si seleccionas archivos nuevos, reemplazarán los archivos almacenados actualmente.</p>

        <c:if test="${not empty message}">
            <div class="edit-success"><i class="bi bi-check-circle-fill"></i> <c:out value="${message}"/></div>
        </c:if>
        <c:if test="${not empty error}">
            <div class="edit-error"><i class="bi bi-exclamation-circle-fill"></i> <c:out value="${error}"/></div>
        </c:if>

        <section class="upload-panel edit-panel">
            <form method="post" action="${pageContext.request.contextPath}/backed/actualizar/${evidence.week()}" enctype="multipart/form-data" class="auth-form">
                <label class="field">
                    Título del trabajo
                    <input type="text" name="title" value="${evidence.title()}" required>
                </label>
                <label class="field">
                    Descripción (opcional)
                    <textarea name="description" placeholder="Describe brevemente este trabajo">${evidence.description()}</textarea>
                </label>
                <label class="field">
                    Nuevos archivos (opcional)
                    <input type="file" name="files" multiple accept=".pdf,.doc,.docx,.txt,.png,.jpg,.jpeg,.gif,.svg,.zip">
                    <small>Al seleccionar archivos, reemplazarán los archivos guardados en esta semana.</small>
                </label>
                <div class="edit-actions">
                    <button class="button-primary" type="submit"><i class="bi bi-save"></i> Guardar cambios</button>
                    <a class="button-secondary" href="${pageContext.request.contextPath}/evidencias/${evidence.week()}"><i class="bi bi-eye"></i> Ver semana</a>
                </div>
            </form>
        </section>

        <form method="post" action="${pageContext.request.contextPath}/backed/eliminar/${evidence.week()}"
              class="edit-delete-form"
              onsubmit="return confirm('¿Eliminar definitivamente el trabajo, sus archivos y enlaces de esta semana? Esta acción no se puede deshacer.');">
            <div>
                <strong>Eliminar este trabajo</strong>
                <span>Se borrarán los archivos y enlaces asociados a esta semana.</span>
            </div>
            <button class="edit-delete-button" type="submit"><i class="bi bi-trash3"></i> Eliminar trabajo</button>
        </form>

        <section class="upload-panel current-files-panel">
            <h2><i class="bi bi-folder2-open"></i> Archivos actuales</h2>
            <c:choose>
                <c:when test="${empty evidence.files()}">
                    <p class="empty-state">No hay archivos almacenados para esta semana.</p>
                </c:when>
                <c:otherwise>
                    <div class="file-list">
                        <c:forEach items="${evidence.files()}" var="file">
                            <div class="file-item">
                                <i class="bi ${file.icon()}"></i>
                                <div>
                                    <strong><c:out value="${file.name()}"/></strong>
                                    <small><c:out value="${file.type()}"/></small>
                                </div>
                                <c:if test="${not file.external()}">
                                    <form method="post"
                                          action="${pageContext.request.contextPath}/backed/actualizar/${evidence.week()}/eliminar-archivo"
                                          class="file-delete-form"
                                          onsubmit="return confirm('¿Eliminar este archivo? Esta acción no se puede deshacer.');">
                                        <input type="hidden" name="filename" value="${file.name()}">
                                        <button class="file-delete-button" type="submit" aria-label="Eliminar ${file.name()}"
                                                title="Eliminar archivo">
                                            <i class="bi bi-trash3"></i> Eliminar
                                        </button>
                                    </form>
                                </c:if>
                            </div>
                        </c:forEach>
                    </div>
                </c:otherwise>
            </c:choose>
        </section>
    </div>
</main>
</body>
</html>
