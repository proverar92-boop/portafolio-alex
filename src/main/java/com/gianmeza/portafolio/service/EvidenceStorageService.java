package com.gianmeza.portafolio.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.gianmeza.portafolio.model.Evidence;

@Service
public class EvidenceStorageService {

    private final PortfolioDatabaseService database;

    public EvidenceStorageService(PortfolioDatabaseService database) {
        this.database = database;
    }

    public List<Evidence.EvidenceFile> filesFor(int week) {
        try {
            return database.filesFor(week).stream()
                    .map(file -> new Evidence.EvidenceFile(file.filename(), typeOf(file.filename()),
                            "/evidencias/archivo/" + week + "/" + file.filename(), iconOf(file.filename()), false))
                    .toList();
        } catch (Exception exception) {
            return List.of();
        }
    }

    public Evidence.EvidenceFile saveFile(int week, MultipartFile file) throws IOException {
        if (file == null || file.isEmpty() || file.getOriginalFilename() == null) {
            throw new IllegalArgumentException("Selecciona un archivo válido");
        }
        String safeName = file.getOriginalFilename().replaceAll("[^a-zA-Z0-9._-]", "_");
        String contentType = file.getContentType() == null ? contentTypeFor(safeName) : file.getContentType();
        database.saveFile(week, safeName, contentType, file.getBytes());
        return new Evidence.EvidenceFile(safeName, typeOf(safeName), "/evidencias/archivo/" + week + "/" + safeName,
                iconOf(safeName), false);
    }

    public void replaceFiles(int week, MultipartFile[] files) throws IOException {
        database.deleteFiles(week);
        if (files != null) {
            for (MultipartFile file : files) {
                if (file != null && !file.isEmpty()) saveFile(week, file);
            }
        }
    }

    public void saveDetails(int week, String title, String description) {
        database.saveWeekDetails(week, title == null ? "" : title.trim(), description == null ? "" : description.trim());
    }

    public String titleFor(int week) {
        try {
            return database.findWeek(week).map(PortfolioDatabaseService.WeekData::title).orElse("");
        } catch (Exception exception) {
            return "";
        }
    }

    public String descriptionFor(int week) {
        try {
            return database.findWeek(week).map(PortfolioDatabaseService.WeekData::description).orElse("");
        } catch (Exception exception) {
            return "";
        }
    }

    public Evidence.EvidenceFile saveLink(int week, String name, String url) {
        if (url == null || !(url.startsWith("https://") || url.startsWith("http://"))) {
            throw new IllegalArgumentException("El enlace debe comenzar con http:// o https://");
        }
        String safeName = (name == null || name.isBlank()) ? url : name.trim();
        database.saveLink(week, safeName.replace("|", "-"), url);
        return new Evidence.EvidenceFile(safeName, "Enlace", url, "bi-link-45deg", true);
    }

    public List<Evidence.EvidenceFile> allFor(int week) {
        List<Evidence.EvidenceFile> result = new ArrayList<>(filesFor(week));
        try {
            database.linksFor(week).forEach(link -> result.add(
                    new Evidence.EvidenceFile(link.name(), "Enlace", link.url(), "bi-link-45deg", true)));
        } catch (Exception exception) {
            return result;
        }
        return result;
    }

    public PortfolioDatabaseService.StoredFile fileFor(int week, String filename) {
        try {
            return database.findFile(week, filename).orElse(null);
        } catch (Exception exception) {
            return null;
        }
    }

    public java.nio.file.Path resolveFile(int week, String filename) {
        return fileFor(week, filename) == null ? null : java.nio.file.Path.of(filename);
    }

    public String contentType(String filename) {
        return contentTypeFor(filename);
    }

    private String contentTypeFor(String filename) {
        String name = filename.toLowerCase(Locale.ROOT);
        if (name.endsWith(".png")) return "image/png";
        if (name.endsWith(".jpg") || name.endsWith(".jpeg")) return "image/jpeg";
        if (name.endsWith(".gif")) return "image/gif";
        if (name.endsWith(".svg")) return "image/svg+xml";
        if (name.endsWith(".pdf")) return "application/pdf";
        if (name.endsWith(".txt")) return "text/plain";
        if (name.endsWith(".html") || name.endsWith(".htm")) return "text/html";
        return "application/octet-stream";
    }

    private String typeOf(String filename) {
        String name = filename.toLowerCase(Locale.ROOT);
        if (name.endsWith(".png") || name.endsWith(".jpg") || name.endsWith(".jpeg")
                || name.endsWith(".gif") || name.endsWith(".svg")) return "Imagen";
        if (name.endsWith(".pdf")) return "PDF";
        if (name.endsWith(".doc") || name.endsWith(".docx")) return "Documento";
        if (name.endsWith(".xls") || name.endsWith(".xlsx")) return "Hoja de cálculo";
        return "Archivo";
    }

    private String iconOf(String filename) {
        return switch (typeOf(filename)) {
            case "Imagen" -> "bi-image";
            case "PDF" -> "bi-file-earmark-pdf";
            case "Documento" -> "bi-file-earmark-text";
            case "Hoja de cálculo" -> "bi-file-earmark-spreadsheet";
            default -> "bi-file-earmark-arrow-down";
        };
    }
}