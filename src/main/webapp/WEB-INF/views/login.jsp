<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Acceso | e-Portafolio</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
    <style>
        :root {
            --login-white: #e6e7f6;
            --login-muted: #85879c;
            --login-border: rgba(174, 177, 222, .52);
            --login-panel: rgba(7, 10, 35, .72);
            --login-purple: #6871ce;
        }

        * { box-sizing: border-box; }
        html, body { min-height: 100%; }
        body.login-page {
            min-width: 320px;
            min-height: 100vh;
            margin: 0;
            overflow: hidden;
            color: var(--login-white);
            background: #080b20 url('${pageContext.request.contextPath}/img/fondo-login.png') center center / cover no-repeat;
            font-family: Arial, Helvetica, sans-serif;
        }

        .login-page::before {
            content: '';
            position: fixed;
            inset: 0;
            pointer-events: none;
            background: rgba(5, 7, 28, .08);
        }

        .login-main {
            position: relative;
            z-index: 1;
            min-height: 100vh;
            display: grid;
            place-items: center;
            padding: 24px 16px;
        }

        .login-card {
            position: relative;
            width: 370px;
            min-height: 309px;
            padding: 20px 34px 18px;
            overflow: hidden;
            background: var(--login-panel);
            border: 1px solid rgba(167, 171, 224, .48);
            border-radius: 13px;
            box-shadow: 0 18px 42px rgba(0, 0, 0, .4), inset 0 1px 0 rgba(255, 255, 255, .08);
            backdrop-filter: blur(6px);
        }

        .login-card::before {
            content: '';
            position: absolute;
            top: 0;
            left: 50%;
            width: 110px;
            height: 1px;
            transform: translateX(-50%);
            background: rgba(189, 193, 255, .7);
        }

        .login-brand {
            position: absolute;
            top: 11px;
            left: 14px;
            display: grid;
            width: 24px;
            height: 24px;
            place-items: center;
            color: #c9cced;
            border: 1px solid rgba(191, 195, 245, .6);
            border-radius: 50%;
            font-size: 8px;
            font-weight: 700;
            letter-spacing: .03em;
            text-decoration: none;
        }

        .login-ornament {
            margin: 0 0 7px;
            color: #b7badb;
            font-size: 13px;
            letter-spacing: 4px;
            line-height: 1;
            text-align: center;
        }

        .login-title {
            margin: 0;
            color: var(--login-white);
            font-size: 20px;
            font-weight: 400;
            letter-spacing: 7px;
            line-height: 1.2;
            text-align: center;
        }

        .login-subtitle {
            margin: 7px 0 19px;
            color: var(--login-muted);
            font-size: 13px;
            text-align: center;
        }

        .login-error {
            margin: 0 0 11px;
            padding: 8px 10px;
            color: #ffc1c8;
            background: rgba(130, 42, 65, .35);
            border: 1px solid rgba(255, 150, 170, .45);
            border-radius: 7px;
            font-size: 11px;
        }

        .login-form { display: grid; gap: 12px; }
        .login-field { position: relative; display: block; }
        .login-field > i {
            position: absolute;
            top: 50%;
            left: 14px;
            z-index: 1;
            color: #b9bbce;
            font-size: 14px;
            transform: translateY(-50%);
        }

        .login-input {
            width: 100%;
            height: 40px;
            padding: 0 38px;
            color: var(--login-white);
            background: rgba(7, 8, 31, .54);
            border: 1px solid var(--login-border);
            border-radius: 9px;
            outline: none;
            font-size: 13px;
        }

        .login-input::placeholder { color: #85879b; opacity: 1; }
        .login-input:focus { border-color: #a5adff; box-shadow: 0 0 0 3px rgba(126, 137, 255, .16); }
        .login-eye { left: auto !important; right: 13px; cursor: pointer; }

        .login-submit {
            display: flex;
            width: 100%;
            height: 40px;
            align-items: center;
            justify-content: center;
            gap: 8px;
            margin-top: 5px;
            color: #f3f3ff;
            background: linear-gradient(100deg, #6269be, #7886df);
            border: 1px solid #a5adf7;
            border-radius: 9px;
            box-shadow: 0 6px 15px rgba(54, 61, 157, .35);
            cursor: pointer;
            font-size: 13px;
        }

        .login-submit i { margin-left: auto; margin-right: 12px; font-size: 18px; }
        .login-submit:hover { background: linear-gradient(100deg, #7077ce, #8795ec); }
        .login-links { display: flex; justify-content: center; gap: 12px; margin-top: 13px; font-size: 11px; }
        .login-links a { color: var(--login-muted); text-decoration: none; }
        .login-links a:hover { color: var(--login-white); }
        .login-links span { color: rgba(174, 177, 222, .45); }

        @media (max-width: 600px) {
            body.login-page { overflow: auto; background-position: 58% center; }
            .login-main { min-height: 100svh; padding: 20px 14px; }
            .login-card { width: min(370px, 100%); padding-right: 25px; padding-left: 25px; }
        }
    </style>
</head>
<body class="login-page">
<main class="login-main">
    <section class="login-card" aria-labelledby="login-title">
        <a class="login-brand" href="${pageContext.request.contextPath}/#inicio" aria-label="Portafolio de Sergio Alex">SA</a>
        <div class="login-ornament" aria-hidden="true">— ◇ —</div>
        <h1 class="login-title" id="login-title">BIENVENIDO</h1>
        <p class="login-subtitle">Inicia sesión para continuar</p>

        <c:if test="${not empty error}">
            <div class="login-error"><i class="bi bi-exclamation-circle"></i> ${error}</div>
        </c:if>

        <form class="login-form" method="post" action="${pageContext.request.contextPath}/login">
            <label class="login-field">
                <i class="bi bi-person-fill" aria-hidden="true"></i>
                <input class="login-input" type="email" name="email" placeholder="Usuario" aria-label="Usuario" required>
            </label>
            <label class="login-field">
                <i class="bi bi-lock-fill" aria-hidden="true"></i>
                <input class="login-input" id="login-password" type="password" name="password" placeholder="Contraseña" aria-label="Contraseña" required>
                <i class="bi bi-eye login-eye" id="password-toggle" role="button" tabindex="0" aria-label="Mostrar contraseña"></i>
            </label>
            <button class="login-submit" type="submit">Iniciar sesión <i class="bi bi-arrow-right"></i></button>
        </form>

        <nav class="login-links" aria-label="Enlaces de acceso">
            <a href="${pageContext.request.contextPath}/#inicio">Volver al portafolio</a>
            <span aria-hidden="true">|</span>
            <a href="${pageContext.request.contextPath}/registro">Registrarse</a>
        </nav>
    </section>
</main>
<script>
    const password = document.getElementById('login-password');
    const toggle = document.getElementById('password-toggle');
    const togglePassword = () => {
        const visible = password.type === 'text';
        password.type = visible ? 'password' : 'text';
        toggle.className = visible ? 'bi bi-eye login-eye' : 'bi bi-eye-slash login-eye';
        toggle.setAttribute('aria-label', visible ? 'Mostrar contraseña' : 'Ocultar contraseña');
    };
    toggle.addEventListener('click', togglePassword);
    toggle.addEventListener('keydown', event => {
        if (event.key === 'Enter' || event.key === ' ') togglePassword();
    });
</script>
</body>
</html>
