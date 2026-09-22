<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Panel de trabajo | e-Portafolio</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css?v=admin-v2">
</head>
<body class="admin-body admin-v2">
    <header class="admin-topbar">
        <div class="admin-topbar-inner">
            <a class="admin-brand" href="${pageContext.request.contextPath}/">
                <span class="admin-brand-mark">SA</span>
                <span><strong>e-Portafolio</strong><small>Panel de trabajo</small></span>
            </a>
            <nav class="admin-nav" aria-label="Navegación del panel">
                <a class="admin-nav-link is-current" href="${pageContext.request.contextPath}/backed"><i class="bi bi-grid-1x2"></i> Escritorio</a>
                <a class="admin-nav-link" href="${pageContext.request.contextPath}/"><i class="bi bi-box-arrow-up-right"></i> Ver portafolio</a>
                <a class="admin-logout" href="${pageContext.request.contextPath}/logout"><i class="bi bi-box-arrow-right"></i> Salir</a>
            </nav>
        </div>
    </header>

    <main class="admin-main">
        <section class="admin-heading">
            <div>
                <div class="admin-kicker"><span></span> Área privada</div>
                <h1>Publicar evidencias</h1>
                <p>Organiza tus entregas, actualiza cada semana y mantén tu portafolio al día.</p>
            </div>
            <div class="admin-heading-meta"><i class="bi bi-database-check"></i><span>Guardado en<br><strong>portalex</strong></span></div>
        </section>

        <c:if test="${not empty message}"><div class="admin-alert is-success"><i class="bi bi-check-circle-fill"></i> ${message}</div></c:if>
        <c:if test="${not empty error}"><div class="admin-alert is-error"><i class="bi bi-exclamation-circle-fill"></i> ${error}</div></c:if>

        <section class="admin-workspace">
            <article class="admin-card publish-card">
                <div class="card-heading">
                    <div class="card-icon publish-icon"><i class="bi bi-cloud-arrow-up"></i></div>
                    <div><span class="card-index">01 / PUBLICAR</span><h2>Subir un trabajo</h2><p>Agrega los archivos de una evidencia y hazla visible en tu portafolio.</p></div>
                </div>
                <form method="post" action="${pageContext.request.contextPath}/backed/subir" enctype="multipart/form-data" class="admin-form">
                    <label class="admin-field">Semana
                        <select name="week" required>
                            <option value="">Selecciona una semana</option>
                            <c:forEach items="${weeks}" var="week"><option value="${week}">Semana <fmt:formatNumber value="${week}" minIntegerDigits="2" groupingUsed="false"/></option></c:forEach>
                        </select>
                    </label>
                    <label class="admin-field">Título del trabajo
                        <input type="text" name="title" placeholder="Ej. Fundamentos del desarrollo web" required>
                    </label>
                    <label class="admin-field">Descripción <span>(opcional)</span>
                        <textarea name="description" placeholder="Escribe qué aprendiste o qué entregaste"></textarea>
                    </label>
                    <label class="admin-field">Archivos
                        <input type="file" name="files" multiple accept=".pdf,.doc,.docx,.txt,.png,.jpg,.jpeg,.gif,.svg,.zip" required>
                        <small>PDF, DOC, DOCX, TXT, PNG, JPG, SVG, GIF o ZIP. Puedes seleccionar varios.</small>
                    </label>
                    <button class="admin-submit" type="submit"><i class="bi bi-arrow-up-circle"></i> Publicar trabajo</button>
                </form>
            </article>

            <article class="admin-card manage-card">
                <div class="card-heading compact-heading">
                    <div class="card-icon manage-icon"><i class="bi bi-pencil-square"></i></div>
                    <div><span class="card-index">02 / EDITAR</span><h2>Actualizar trabajos</h2><p>Modifica títulos, descripciones o archivos existentes.</p></div>
                </div>
                <div class="admin-week-list">
                    <c:forEach items="${weeks}" var="week"><a href="${pageContext.request.contextPath}/backed/actualizar/${week}"><span><fmt:formatNumber value="${week}" minIntegerDigits="2" groupingUsed="false"/></span> Semana <fmt:formatNumber value="${week}" minIntegerDigits="2" groupingUsed="false"/><i class="bi bi-arrow-up-right"></i></a></c:forEach>
                </div>
            </article>

            <div class="admin-side-column">
                <article class="admin-card link-card">
                    <div class="card-heading compact-heading">
                        <div class="card-icon link-icon"><i class="bi bi-link-45deg"></i></div>
                        <div><span class="card-index">03 / ENLACE</span><h2>Agregar un enlace</h2><p>Comparte un repositorio, presentación o recurso externo.</p></div>
                    </div>
                    <form method="post" action="${pageContext.request.contextPath}/backed/enlace" class="admin-form">
                        <label class="admin-field">Semana
                            <select name="week" required><option value="">Selecciona una semana</option><c:forEach items="${weeks}" var="week"><option value="${week}">Semana <fmt:formatNumber value="${week}" minIntegerDigits="2" groupingUsed="false"/></option></c:forEach></select>
                        </label>
                        <label class="admin-field">Nombre del enlace
                            <input type="text" name="name" placeholder="Ej. Repositorio de GitHub" required>
                        </label>
                        <label class="admin-field">URL
                            <input type="url" name="url" placeholder="https://ejemplo.com/recurso" required>
                        </label>
                        <button class="admin-submit secondary-submit" type="submit"><i class="bi bi-plus-circle"></i> Guardar enlace</button>
                    </form>
                </article>

                <aside class="admin-guide">
                    <div class="guide-title"><i class="bi bi-lightbulb"></i><strong>Una pequeña guía</strong></div>
                    <p>Elige una semana, completa sus datos y publica. Tus archivos se guardan en <strong>portalex</strong> y quedan disponibles desde la página pública.</p>
                </aside>
            </div>
        </section>
    </main>

    <script>
        document.querySelectorAll('form').forEach(form => form.addEventListener('submit', function(event) {
            const week = this.querySelector('select[name="week"]');
            const fileInput = this.querySelector('input[type="file"]');
            if (week && !week.value) { event.preventDefault(); alert('Selecciona una semana.'); return; }
            if (fileInput && !fileInput.files.length) { event.preventDefault(); alert('Selecciona al menos un archivo.'); }
        }));
    </script>
</body>
</html>