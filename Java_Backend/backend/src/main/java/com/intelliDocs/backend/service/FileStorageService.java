package com.intelliDocs.backend.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileStorageService {

    private final Path uploadDirectory;

    public FileStorageService(@Value("${file.upload-dir}") String uploadDirectory) {
        try {
            this.uploadDirectory = Path.of(uploadDirectory).toAbsolutePath().normalize();
            Files.createDirectories(this.uploadDirectory);
        } catch (IOException ex) {
            throw new IllegalStateException("Could not create the upload directory", ex);
        }
    }

    public Path save(MultipartFile file) {
        String storedFileName = generateUniqueFileName(file.getOriginalFilename());
        Path destination = uploadDirectory.resolve(storedFileName).normalize();

        if (!destination.startsWith(uploadDirectory)) {
            throw new IllegalArgumentException("Invalid file name");
        }

        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, destination, StandardCopyOption.REPLACE_EXISTING);
            return destination;
        } catch (IOException ex) {
            throw new IllegalStateException("Could not store the uploaded file", ex);
        }
    }

    public String generateUniqueFileName(String originalFileName) {
        String cleanedName = StringUtils.cleanPath(originalFileName == null ? "" : originalFileName);
        String extension = StringUtils.getFilenameExtension(cleanedName);
        return UUID.randomUUID() + (StringUtils.hasText(extension) ? "." + extension.toLowerCase() : "");
    }

    public void delete(String storedFileName) {
        try {
            Files.deleteIfExists(resolveStoredFile(storedFileName));
        } catch (IOException ex) {
            throw new IllegalStateException("Could not delete the stored file", ex);
        }
    }

    public Resource load(String storedFileName) {
        try {
            Resource resource = new UrlResource(resolveStoredFile(storedFileName).toUri());
            if (resource.exists() && resource.isReadable()) {
                return resource;
            }
        } catch (IOException ex) {
            // The consistent error below avoids exposing filesystem details.
        }
        throw new IllegalArgumentException("Stored file was not found");
    }

    private Path resolveStoredFile(String storedFileName) {
        Path resolvedPath = uploadDirectory.resolve(storedFileName).normalize();
        if (!resolvedPath.startsWith(uploadDirectory) || !resolvedPath.getFileName().toString().equals(storedFileName)) {
            throw new IllegalArgumentException("Invalid stored file name");
        }
        return resolvedPath;
    }
}
