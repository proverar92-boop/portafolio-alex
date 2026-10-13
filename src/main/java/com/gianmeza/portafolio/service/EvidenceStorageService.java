package com.gianmeza.portafolio.service;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.gianmeza.portafolio.model.Evidence;

@Service
public class EvidenceStorageService {

    private static final Set<String> DOCUMENT_EXTENSIONS = Set.of(
            "pdf", "doc", "docx", "xls", "xlsx", "ppt", "pptx");

    private final PortfolioDatabaseService database;
    private final Path storageRoot;

    public EvidenceStorageService(PortfolioDatabaseService database,
            @Value("${portfolio.storage-path:backed/evidencias}") String storagePath) {
        this.database = database;
        this.storageRoot = Path.of(storagePath);
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

    public void deleteWeek(int week) throws IOException {
        if (week < 1 || week > 16) {
            throw new IllegalArgumentException("La semana seleccionada no es válida.");
        }

        Path normalizedRoot = storageRoot.normalize();
        Path weekDirectory = normalizedRoot.resolve(String.format("semana-%02d", week)).normalize();
        if (!weekDirectory.startsWith(normalizedRoot) || !normalizedRoot.equals(weekDirectory.getParent())) {
            throw new IllegalStateException("La ruta de almacenamiento de la semana no es válida.");
        }

        database.deleteWeek(week);
        if (Files.exists(weekDirectory)) {
            try (var paths = Files.walk(weekDirectory)) {
                paths.sorted(Comparator.reverseOrder()).forEach(path -> {
                    try {
                        Files.deleteIfExists(path);
                    } catch (IOException exception) {
                        throw new UncheckedIOException(exception);
                    }
                });
            } catch (UncheckedIOException exception) {
                throw exception.getCause();
            }
        }
    }

    public void deleteFile(int week, String filename) throws IOException {
        if (week < 1 || week > 16) {
            throw new IllegalArgumentException("La semana seleccionada no es válida.");
        }
        if (filename == null || filename.isBlank() || filename.equals(".") || filename.equals("..")
                || filename.contains("/") || filename.contains("\\")) {
            throw new IllegalArgumentException("El nombre del archivo no es válido.");
        }

        Path normalizedRoot = storageRoot.normalize();
        Path weekDirectory = normalizedRoot.resolve(String.format("semana-%02d", week)).normalize();
        Path filePath = weekDirectory.resolve(filename).normalize();
        if (!weekDirectory.startsWith(normalizedRoot) || !filePath.startsWith(weekDirectory)
                || !normalizedRoot.equals(weekDirectory.getParent())) {
            throw new IllegalArgumentException("La ruta del archivo no es válida.");
        }

        int deletedDatabaseFiles = database.deleteFile(week, filename);
        boolean deletedLegacyFile = Files.isRegularFile(filePath) && Files.deleteIfExists(filePath);
        if (deletedDatabaseFiles == 0 && !deletedLegacyFile) {
            throw new IllegalArgumentException("No se encontró ese archivo en la semana seleccionada.");
        }
    }

    public void saveDetails(int week, String title, String description) {
        database.saveWeekDetails(week, title == null ? "" : title.trim(), description == null ? "" : description.trim());
    }

    public String titleFor(int week) {
        try {
            return database.findWeek(week).map(PortfolioDatabaseService.WeekData::title).orElse(loadTitleFromFolder(week));
        } catch (Exception exception) {
            return loadTitleFromFolder(week);
        }
    }

    public String descriptionFor(int week) {
        try {
            return database.findWeek(week).map(PortfolioDatabaseService.WeekData::description).orElse(loadDescriptionFromFolder(week));
        } catch (Exception exception) {
            return loadDescriptionFromFolder(week);
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
            // ignore database-only fallback
        }

        if (result.isEmpty()) {
            result.addAll(loadFilesFromFolder(week));
        }

        return result;
    }

    public PortfolioDatabaseService.StoredFile fileFor(int week, String filename) {
        try {
            var storedFile = database.findFile(week, filename);
            if (storedFile.isPresent()) {
                return storedFile.get();
            }

            Path folderFile = storageRoot.resolve(String.format("semana-%02d", week))
                    .resolve(filename).normalize();
            Path weekDir = storageRoot.resolve(String.format("semana-%02d", week)).normalize();
            if (!folderFile.startsWith(weekDir) || !Files.isRegularFile(folderFile)) {
                return null;
            }

            return new PortfolioDatabaseService.StoredFile(
                    folderFile.getFileName().toString(),
                    contentTypeFor(folderFile.getFileName().toString()),
                    Files.readAllBytes(folderFile));
        } catch (Exception exception) {
            return null;
        }
    }

    public List<PortfolioDatabaseService.DocumentInfo> documents() {
        return database.documents();
    }

    public void saveDocument(String title, String description, MultipartFile file) throws IOException {
        String cleanTitle = requireDocumentTitle(title);
        String safeName = validDocumentFilename(file);
        database.saveDocument(cleanTitle, cleanDocumentDescription(description), safeName,
                documentContentType(safeName), file.getBytes());
    }

    public void updateDocument(long id, String title, String description, MultipartFile replacement)
            throws IOException {
        String cleanTitle = requireDocumentTitle(title);
        String cleanDescription = cleanDocumentDescription(description);
        String safeName = null;
        String contentType = null;
        byte[] content = null;
        if (replacement != null && !replacement.isEmpty()) {
            safeName = validDocumentFilename(replacement);
            contentType = documentContentType(safeName);
            content = replacement.getBytes();
        }
        database.updateDocument(id, cleanTitle, cleanDescription, safeName, contentType, content);
    }

    public void deleteDocument(long id) {
        database.deleteDocument(id);
    }

    public PortfolioDatabaseService.StoredDocument document(long id) {
        return database.findDocument(id).orElse(null);
    }

    private String requireDocumentTitle(String title) {
        String cleanTitle = title == null ? "" : title.trim();
        if (cleanTitle.isEmpty() || cleanTitle.length() > 160) {
            throw new IllegalArgumentException("Escribe un título de hasta 160 caracteres.");
        }
        return cleanTitle;
    }

    private String cleanDocumentDescription(String description) {
        String cleanDescription = description == null ? "" : description.trim();
        if (cleanDescription.length() > 2000) {
            throw new IllegalArgumentException("La descripción no puede superar los 2000 caracteres.");
        }
        return cleanDescription;
    }

    private String validDocumentFilename(MultipartFile file) {
        if (file == null || file.isEmpty() || file.getOriginalFilename() == null) {
            throw new IllegalArgumentException("Selecciona un documento válido.");
        }
        String originalName = file.getOriginalFilename().replace('\\', '/');
        originalName = originalName.substring(originalName.lastIndexOf('/') + 1);
        int extensionIndex = originalName.lastIndexOf('.');
        String extension = extensionIndex < 0 ? "" : originalName.substring(extensionIndex + 1).toLowerCase(Locale.ROOT);
        if (!DOCUMENT_EXTENSIONS.contains(extension)) {
            throw new IllegalArgumentException("Solo se permiten documentos PDF, Word, Excel o PowerPoint.");
        }
        String safeName = originalName.replaceAll("[^a-zA-Z0-9._-]", "_");
        if (safeName.length() > 255) {
            throw new IllegalArgumentException("El nombre del archivo no puede superar los 255 caracteres.");
        }
        return safeName;
    }

    private String documentContentType(String filename) {
        return switch (filename.substring(filename.lastIndexOf('.') + 1).toLowerCase(Locale.ROOT)) {
            case "pdf" -> "application/pdf";
            case "doc" -> "application/msword";
            case "docx" -> "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            case "xls" -> "application/vnd.ms-excel";
            case "xlsx" -> "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            case "ppt" -> "application/vnd.ms-powerpoint";
            case "pptx" -> "application/vnd.openxmlformats-officedocument.presentationml.presentation";
            default -> throw new IllegalArgumentException("Tipo de documento no permitido.");
        };
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

    private List<Evidence.EvidenceFile> loadFilesFromFolder(int week) {
        Path weekDir = storageRoot.resolve(String.format("semana-%02d", week));
        if (!Files.isDirectory(weekDir)) {
            return List.of();
        }

        try (var stream = Files.list(weekDir)) {
            return stream
                    .filter(path -> !path.getFileName().toString().equals("detalles.properties"))
                    .filter(path -> !Files.isDirectory(path))
                    .map(path -> new Evidence.EvidenceFile(
                            path.getFileName().toString(),
                            typeOf(path.getFileName().toString()),
                            "/evidencias/ver/" + week + "/" + path.getFileName(),
                            iconOf(path.getFileName().toString()),
                            false))
                    .toList();
        } catch (IOException exception) {
            return List.of();
        }
    }

    private String loadTitleFromFolder(int week) {
        return loadPropertiesFromFolder(week).getProperty("title", "");
    }

    private String loadDescriptionFromFolder(int week) {
        return loadPropertiesFromFolder(week).getProperty("description", "");
    }

    private Properties loadPropertiesFromFolder(int week) {
        Properties properties = new Properties();
        Path props = storageRoot.resolve(String.format("semana-%02d", week)).resolve("detalles.properties");
        if (Files.isRegularFile(props)) {
            try (var reader = Files.newBufferedReader(props, java.nio.charset.StandardCharsets.UTF_8)) {
                properties.load(reader);
            } catch (IOException exception) {
                return properties;
            }
        }
        return properties;
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
