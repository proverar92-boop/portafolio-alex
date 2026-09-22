package com.gianmeza.portafolio.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.gianmeza.portafolio.model.UserProfile;

@Service
public class PortfolioDatabaseService {

    private final JdbcTemplate jdbc;

    public PortfolioDatabaseService(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public boolean authenticateAdmin(String email, String password) {
        List<String> passwords = jdbc.query(
                "SELECT password FROM administrador WHERE LOWER(correo) = LOWER(?) OR LOWER(usuario) = LOWER(?)",
                (result, row) -> result.getString(1), email, email);
        return passwords.stream().anyMatch(stored -> matchesPassword(password, stored));
    }

    public Optional<UserProfile> findProfile(String email) {
        return jdbc.query(
                "SELECT correo, nombre, foto, password_hash FROM perfil WHERE LOWER(correo) = LOWER(?) LIMIT 1",
                (result, row) -> new UserProfile(result.getString("correo"), result.getString("nombre"),
                        result.getString("foto"), result.getString("password_hash")), email)
                .stream().findFirst();
    }

    public boolean profileExists(String email) {
        Integer count = jdbc.queryForObject("SELECT COUNT(*) FROM perfil WHERE LOWER(correo) = LOWER(?)", Integer.class, email);
        return count != null && count > 0;
    }

    public void createProfile(String email, String password) {
        jdbc.update("INSERT INTO perfil (nombre, correo, foto, password_hash) VALUES (?, ?, ?, ?)",
                "Usuario", email, "/img/perfil.jpg", hash(password));
    }

    public void updateProfile(String currentEmail, String email, String name, String photo, String password) {
        if (password == null || password.isBlank()) {
            jdbc.update("UPDATE perfil SET nombre = ?, correo = ?, foto = ? WHERE LOWER(correo) = LOWER(?)",
                    name, email, photo, currentEmail);
        } else {
            jdbc.update("UPDATE perfil SET nombre = ?, correo = ?, foto = ?, password_hash = ? WHERE LOWER(correo) = LOWER(?)",
                    name, email, photo, hash(password), currentEmail);
        }
    }

    public Optional<WeekData> findWeek(int week) {
        return jdbc.query("SELECT numero_semana, titulo, descripcion, enlace, archivo, estado FROM semanas WHERE numero_semana = ? LIMIT 1",
                (result, row) -> new WeekData(result.getInt("numero_semana"), result.getString("titulo"),
                        result.getString("descripcion"), result.getString("enlace"), result.getString("archivo"),
                        result.getString("estado")), week).stream().findFirst();
    }

    public void saveWeekDetails(int week, String title, String description) {
        if (findWeek(week).isPresent()) {
            jdbc.update("UPDATE semanas SET titulo = ?, descripcion = ? WHERE numero_semana = ?",
                    title, description, week);
        } else {
            jdbc.update("INSERT INTO semanas (numero_semana, titulo, descripcion, estado) VALUES (?, ?, ?, 'Activo')",
                    week, title, description);
        }
    }

    public void saveFileName(int week, String filename) {
        ensureWeek(week);
        jdbc.update("UPDATE semanas SET archivo = ? WHERE numero_semana = ?", filename, week);
    }

    public void saveFile(int week, String filename, String contentType, byte[] content) {
        ensureWeek(week);
        jdbc.update("""
                INSERT INTO archivos_evidencia (id_semana, nombre_archivo, tipo_mime, contenido)
                SELECT id_semana, ?, ?, ? FROM semanas WHERE numero_semana = ?
                ON DUPLICATE KEY UPDATE tipo_mime = VALUES(tipo_mime), contenido = VALUES(contenido)
                """, filename, contentType, content, week);
    }

    public void deleteFiles(int week) {
        jdbc.update("DELETE FROM archivos_evidencia WHERE id_semana IN (SELECT id_semana FROM semanas WHERE numero_semana = ?)", week);
    }

    public List<StoredFile> filesFor(int week) {
        return jdbc.query("""
                SELECT a.nombre_archivo, a.tipo_mime, a.contenido
                FROM archivos_evidencia a JOIN semanas s ON s.id_semana = a.id_semana
                WHERE s.numero_semana = ? ORDER BY a.nombre_archivo
                """, (result, row) -> new StoredFile(result.getString("nombre_archivo"),
                        result.getString("tipo_mime"), result.getBytes("contenido")), week);
    }

    public Optional<StoredFile> findFile(int week, String filename) {
        return filesFor(week).stream().filter(file -> file.filename().equals(filename)).findFirst();
    }

    public void clearFileName(int week) {
        ensureWeek(week);
        jdbc.update("UPDATE semanas SET archivo = NULL WHERE numero_semana = ?", week);
    }

    public void saveLink(int week, String url) {
        ensureWeek(week);
        jdbc.update("UPDATE semanas SET enlace = ? WHERE numero_semana = ?", url, week);
    }

    public void saveLink(int week, String name, String url) {
        ensureWeek(week);
        jdbc.update("""
                INSERT INTO enlaces_evidencia (id_semana, nombre, url)
                SELECT id_semana, ?, ? FROM semanas WHERE numero_semana = ?
                """, name, url, week);
    }

    public List<StoredLink> linksFor(int week) {
        return jdbc.query("""
                SELECT e.nombre, e.url FROM enlaces_evidencia e JOIN semanas s ON s.id_semana = e.id_semana
                WHERE s.numero_semana = ? ORDER BY e.id_enlace
                """, (result, row) -> new StoredLink(result.getString("nombre"), result.getString("url")), week);
    }

    private void ensureWeek(int week) {
        if (findWeek(week).isEmpty()) saveWeekDetails(week, "Evidencia de la Semana " + String.format("%02d", week), "");
    }

    private boolean matchesPassword(String raw, String stored) {
        return stored != null && (stored.equals(raw) || stored.equals(hash(raw)));
    }

    private String hash(String value) {
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256")
                    .digest(value.getBytes(StandardCharsets.UTF_8)));
        } catch (java.security.NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 no está disponible", exception);
        }
    }

    public record WeekData(int week, String title, String description, String link, String filename, String status) {}
    public record StoredFile(String filename, String contentType, byte[] content) {}
    public record StoredLink(String name, String url) {}
}