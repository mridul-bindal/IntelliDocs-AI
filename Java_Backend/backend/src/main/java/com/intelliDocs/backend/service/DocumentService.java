package com.intelliDocs.backend.service;

import java.nio.file.Path;
import java.util.Locale;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.intelliDocs.backend.entity.Document;
import com.intelliDocs.backend.entity.User;
import com.intelliDocs.backend.repository.DocumentRepository;
import com.intelliDocs.backend.repository.UserRepository;

@Service
public class DocumentService {

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(
            "csv", "tsv", "xls", "xlsx", "txt", "md", "json", "xml", "log");

    private final DocumentRepository documentRepository;
    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;

    public DocumentService(
            DocumentRepository documentRepository,
            UserRepository userRepository,
            FileStorageService fileStorageService) {
        this.documentRepository = documentRepository;
        this.userRepository = userRepository;
        this.fileStorageService = fileStorageService;
    }

    public Document upload(MultipartFile file) {
        validateFile(file);
        User user = getAuthenticatedUser();
        Path storedFile = fileStorageService.save(file);

        try {
            return documentRepository.save(Document.builder()
                    .fileName(file.getOriginalFilename())
                    .storedFileName(storedFile.getFileName().toString())
                    .filePath(storedFile.toString())
                    .fileType(file.getContentType() == null ? "application/octet-stream" : file.getContentType())
                    .fileSize(file.getSize())
                    .processingStatus("UPLOADED")
                    .totalPages(0)
                    .totalChunks(0)
                    .user(user)
                    .build());
        } catch (RuntimeException ex) {
            fileStorageService.delete(storedFile.getFileName().toString());
            throw ex;
        }
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A non-empty file is required");
        }

        String originalFileName = file.getOriginalFilename();
        String extension = originalFileName == null ? "" : originalFileName.substring(originalFileName.lastIndexOf('.') + 1)
                .toLowerCase(Locale.ROOT);

        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new ResponseStatusException(
                    HttpStatus.UNSUPPORTED_MEDIA_TYPE,
                    "Only CSV, Excel, and text-based files are supported");
        }
    }

    private User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getName())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication is required");
        }

        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authenticated user was not found"));
    }
}
