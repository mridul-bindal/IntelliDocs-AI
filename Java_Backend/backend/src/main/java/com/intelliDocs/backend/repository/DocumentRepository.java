package com.intelliDocs.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.intelliDocs.backend.entity.Document;

public interface DocumentRepository extends JpaRepository<Document, Long> {
    List<Document> findByUserIdOrderByUploadedAtDesc(Long userId);
}
