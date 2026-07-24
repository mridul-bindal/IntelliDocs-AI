package com.intelliDocs.backend.dto.document;

import java.time.Instant;

import com.intelliDocs.backend.entity.Document;

public record DocumentUploadResponse(
        Long id,
        String fileName,
        String fileType,
        Long fileSize,
        Instant uploadedAt,
        String processingStatus,
        Integer totalPages,
        Integer totalChunks) {

    public static DocumentUploadResponse from(Document document) {
        return new DocumentUploadResponse(
                document.getId(),
                document.getFileName(),
                document.getFileType(),
                document.getFileSize(),
                document.getUploadedAt(),
                document.getProcessingStatus(),
                document.getTotalPages(),
                document.getTotalChunks());
    }
}
