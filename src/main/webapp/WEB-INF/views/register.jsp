<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Registro | e-Portafolio</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
    <style>
        :root { --white:#e6e7f6; --muted:#85879c; --border:rgba(174,177,222,.52); --panel:rgba(7,10,35,.72); }
        * { box-sizing:border-box; }
        html, body { min-height:100%; }
        body.register-page { min-width:320px; min-height:100vh; margin:0; overflow:hidden; color:var(--white); background:#080b20 url('${pageContext.request.contextPath}/img/fonodologin.png') center/cover no-repeat; font-family:Arial,Helvetica,sans-serif; }
        .register-page:before { content:''; position:fixed; inset:0; pointer-events:none; background:rgba(5,7,28,.08); }
        .register-main { position:relative; z-index:1; min-height:100vh; display:grid; place-items:center; padding:24px 16px; }
        .register-card { position:relative; width:300px; min-height:318px; padding:20px 29px 18px; overflow:hidden; background:var(--panel); border:1px solid rgba(167,171,224,.48); border-radius:13px; box-shadow:0 18px 42px rgba(0,0,0,.4),inset 0 1px 0 rgba(255,255,255,.08); backdrop-filter:blur(6px); }
        .register-card:before { content:''; position:absolute; top:0; left:50%; width:110px; height:1px; transform:translateX(-50%); background:rgba(189,193,255,.7); }
        .register-brand { position:absolute; top:11px; left:14px; display:grid; width:24px; height:24px; place-items:center; color:#c9cced; border:1px solid rgba(191,195,245,.6); border-radius:50%; font-size:8px; font-weight:700; text-decoration:none; }
        .register-ornament { margin:0 0 7px; color:#b7badb; font-size:13px; letter-spacing:4px; line-height:1; text-align:center; }
        .register-title { margin:0; color:var(--white); font-size:19px; font-weight:400; letter-spacing:5px; line-height:1.2; text-align:center; }
        .register-subtitle { margin:7px 0 19px; color:var(--muted); font-size:12px; text-align:center; }
        .register-message { margin:0 0 11px; padding:8px 10px; border-radius:7px; font-size:11px; }
        .register-success { color:#b9f0dd; background:rgba(29,117,91,.3); border:1px solid rgba(120,224,188,.45); }
        .register-error { color:#ffc1c8; background:rgba(130,42,65,.35); border:1px solid rgba(255,150,170,.45); }
        .register-form { display:grid; gap:11px; }
        .register-field { position:relative; display:block; }
        .register-field > i { position:absolute; top:50%; left:13px; z-index:1; color:#b9bbce; font-size:13px; transform:translateY(-50%); }
        .register-input { width:100%; height:36px; padding:0 35px; color:var(--white); background:rgba(7,8,31,.54); border:1px solid var(--border); border-radius:8px; outline:none; font-size:12px; }
        .register-input::placeholder { color:#85879b; opacity:1; }
        .register-input:focus { border-color:#a5adff; box-shadow:0 0 0 3px rgba(126,137,255,.16); }
        .register-eye { left:auto !important; right:12px; cursor:pointer; }
        .register-submit { display:flex; width:100%; height:38px; align-items:center; justify-content:center; gap:8px; margin-top:5px; color:#f3f3ff; background:linear-gradient(100deg,#6269be,#7886df); border:1px solid #a5adf7; border-radius:8px; box-shadow:0 6px 15px rgba(54,61,157,.35); cursor:pointer; font-size:12px; }
        .register-submit i { margin-left:auto; margin-right:11px; font-size:16px; }
        .register-submit:hover { background:linear-gradient(100deg,#7077ce,#8795ec); }
        .register-footer { margin-top:12px; color:var(--muted); font-size:10px; text-align:center; }
        .register-footer a { color:inherit; text-decoration:none; }
        .register-footer a:hover { color:var(--white); }
        .register-footer span { margin:0 6px; color:rgba(174,177,222,.45); }
        @media (max-width:600px) { body.register-page { overflow:auto; background-position:58% center; } .register-main { min-height:100svh; padding:20px 14px; } .register-card { width:min(300px,100%); } }
    </style>
</head>
<body class="register-page">
<main class="register-main">
    <section class="register-card" aria-labelledby="register-title">
        <a class="register-brand" href="${pageContext.request.contextPath}/#inicio" aria-label="Portafolio de Sergio Alex">SA</a>
        <div class="register-ornament" aria-hidden="true">— ◇ —</div>
        <h1 class="register-title" id="register-title">REGÍSTRATE</h1>
        <p class="register-subtitle">Crea tu cuenta para comenzar</p>
        <c:if test="${not empty message}"><div class="register-message register-success"><i class="bi bi-check-circle"></i> ${message}</div></c:if>
        <c:if test="${not empty error}"><div class="register-message register-error"><i class="bi bi-exclamation-circle"></i> ${error}</div></c:if>
        <form class="register-form" method="post" action="${pageContext.request.contextPath}/registro">
            <label class="register-field"><i class="bi bi-person-fill" aria-hidden="true"></i><input class="register-input" type="email" name="email" placeholder="Usuario" aria-label="Usuario o correo electrónico" required></label>
            <label class="register-field"><i class="bi bi-envelope-fill" aria-hidden="true"></i><input class="register-input" type="email" name="emailConfirmation" placeholder="Correo Electrónico" aria-label="Confirmar correo electrónico"></label>
            <label class="register-field"><i class="bi bi-lock-fill" aria-hidden="true"></i><input class="register-input" id="register-password" type="password" name="password" placeholder="Contraseña" minlength="6" aria-label="Contraseña" required><i class="bi bi-eye register-eye" id="register-password-toggle" role="button" tabindex="0" aria-label="Mostrar contraseña"></i></label>
            <button class="register-submit" type="submit">Crear Cuenta <i class="bi bi-arrow-right"></i></button>
        </form>
        <div class="register-footer">¿Ya tienes una cuenta?<span aria-hidden="true">|</span><a href="${pageContext.request.contextPath}/login">Inicia sesión</a></div>
    </section>
</main>
<script>
    const form = document.querySelector('.register-form');
    const password = document.getElementById('register-password');
    const toggle = document.getElementById('register-password-toggle');
    const togglePassword = () => {
        const visible = password.type === 'text';
        password.type = visible ? 'password' : 'text';
        toggle.className = visible ? 'bi bi-eye register-eye' : 'bi bi-eye-slash register-eye';
        toggle.setAttribute('aria-label', visible ? 'Mostrar contraseña' : 'Ocultar contraseña');
    };
    toggle.addEventListener('click', togglePassword);
    toggle.addEventListener('keydown', event => { if (event.key === 'Enter' || event.key === ' ') togglePassword(); });
    form.addEventListener('submit', event => {
        const email = form.elements.email.value.trim();
        const passwordValue = form.elements.password.value.trim();
        if (!email || !email.includes('@')) { event.preventDefault(); alert('Por favor ingresa un correo válido'); }
        else if (passwordValue.length < 6) { event.preventDefault(); alert('La contraseña debe tener al menos 6 caracteres'); }
    });
</script>
</body>
</html>
