package com.gianmeza.portafolio.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.gianmeza.portafolio.model.UserProfile;
import com.gianmeza.portafolio.service.PortfolioDatabaseService;

@Controller
public class AuthController {
    private final PortfolioDatabaseService database;
    private final String fallbackAdminEmail;

    public AuthController() {
        this.database = null;
        this.fallbackAdminEmail = "admin@gianmeza.com";
    }

    @Autowired
    public AuthController(PortfolioDatabaseService database) {
        this.database = database;
        this.fallbackAdminEmail = "admin@gianmeza.com";
    }

    public AuthController(String fallbackAdminEmail) {
        this.database = null;
        this.fallbackAdminEmail = fallbackAdminEmail == null ? "admin@gianmeza.com" : fallbackAdminEmail;
    }

    @GetMapping("/login")
    public String login() { return "login"; }

    @PostMapping("/login")
    public String authenticate(@RequestParam String email, @RequestParam String password,
                               HttpSession session, Model model) {
        String normalizedEmail = normalize(email);
        try {
            if (database == null) {
                if (normalizedEmail.equalsIgnoreCase(fallbackAdminEmail) && "gianmeza".equals(password)) {
                    session.setAttribute("admin", true);
                    session.removeAttribute("user");
                    return "redirect:/backed";
                }
                if ("vlecmanusa18@gmail.com".equals(normalizedEmail) && "gianmeza".equals(password)) {
                    session.setAttribute("user", normalizedEmail);
                    session.removeAttribute("admin");
                    return "redirect:/perfil";
                }
                model.addAttribute("error", "Correo o contraseña incorrectos.");
                return "login";
            }
            if (database.authenticateAdmin(normalizedEmail, password)) {
                session.setAttribute("admin", true);
                session.removeAttribute("user");
                return "redirect:/backed";
            }
            if (database.findProfile(normalizedEmail)
                    .filter(profile -> profile.passwordHash() != null
                            && (profile.passwordHash().equals(password)
                                || profile.passwordHash().equals(hash(password))))
                    .isPresent()) {
                session.setAttribute("user", normalizedEmail);
                session.removeAttribute("admin");
                return "redirect:/perfil";
            }
        } catch (Exception exception) {
            model.addAttribute("error", "No se pudo conectar con la base de datos.");
            return "login";
        }
        model.addAttribute("error", "Correo o contraseña incorrectos.");
        return "login";
    }

    @GetMapping("/registro")
    public String register() { return "register"; }

    @PostMapping("/registro")
    public String register(@RequestParam String email, @RequestParam String password, Model model) {
        String normalizedEmail = normalize(email);
        if (normalizedEmail.isBlank() || !normalizedEmail.contains("@") || password == null || password.length() < 6) {
            model.addAttribute("error", "Usa un correo válido y una contraseña de al menos 6 caracteres.");
            return "register";
        }
        if (database == null) {
            model.addAttribute("message", "Registro completado. Ya puedes iniciar sesión con tu correo y contraseña.");
            return "register";
        }
        try {
            if (database.profileExists(normalizedEmail)) {
                model.addAttribute("error", "Ese correo ya está registrado.");
            } else {
                database.createProfile(normalizedEmail, password);
                model.addAttribute("message", "Registro completado. Ya puedes iniciar sesión con tu correo y contraseña.");
            }
        } catch (Exception exception) {
            model.addAttribute("error", "No se pudo completar el registro en la base de datos.");
        }
        return "register";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

    @GetMapping("/error")
    public String errorPage() { return "redirect:/login"; }

    @GetMapping("/perfil")
    public String perfil(HttpSession session, Model model) {
        String email = (String) session.getAttribute("user");
        if (email == null || email.isBlank()) return "redirect:/login";
        if (database == null) {
            model.addAttribute("userProfile", new UserProfile(email, "Usuario", "/img/perfil.jpg"));
            return "profile";
        }
        model.addAttribute("userProfile", database.findProfile(email)
                .orElseGet(() -> new UserProfile(email, "Usuario", "/img/perfil.jpg")));
        return "profile";
    }

    @PostMapping("/perfil")
    public String actualizarPerfil(@RequestParam(required = false) String name,
                                   @RequestParam(required = false) String email,
                                   @RequestParam(required = false) String password,
                                   @RequestParam(required = false, defaultValue = "/img/perfil.jpg") String photoUrl,
                                   HttpSession session, Model model) {
        String currentEmail = (String) session.getAttribute("user");
        if (currentEmail == null || currentEmail.isBlank()) return "redirect:/login";
        if (database == null) {
            String newEmail = email == null || email.isBlank() ? currentEmail : normalize(email);
            String newName = name == null || name.isBlank() ? "Usuario" : name.trim();
            String newPhoto = photoUrl == null || photoUrl.isBlank() ? "/img/perfil.jpg" : photoUrl.trim();
            session.setAttribute("user", newEmail);
            model.addAttribute("userProfile", new UserProfile(newEmail, newName, newPhoto));
            return "profile";
        }
        UserProfile current = database.findProfile(currentEmail)
                .orElse(new UserProfile(currentEmail, "Usuario", "/img/perfil.jpg"));
        String newEmail = email == null || email.isBlank() ? current.email() : normalize(email);
        String newName = name == null || name.isBlank() ? current.name() : name.trim();
        String newPhoto = photoUrl == null || photoUrl.isBlank() ? current.photoUrl() : photoUrl.trim();
        database.updateProfile(current.email(), newEmail, newName, newPhoto, password);
        session.setAttribute("user", newEmail);
        model.addAttribute("userProfile", new UserProfile(newEmail, newName, newPhoto));
        return "profile";
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase();
    }

    private String hash(String value) {
        try {
            return java.util.HexFormat.of().formatHex(java.security.MessageDigest.getInstance("SHA-256")
                    .digest(value.getBytes(java.nio.charset.StandardCharsets.UTF_8)));
        } catch (java.security.NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 no está disponible", exception);
        }
    }
}