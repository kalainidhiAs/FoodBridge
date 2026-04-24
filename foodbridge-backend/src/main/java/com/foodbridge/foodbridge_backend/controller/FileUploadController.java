package com.foodbridge.foodbridge_backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.*;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/upload")
public class FileUploadController {

    private final String UPLOAD_DIR = "uploads/";

    public FileUploadController() {
        try {
            Files.createDirectories(Paths.get(UPLOAD_DIR));
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize folder for upload!");
        }
    }

    @PostMapping
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
             return ResponseEntity.badRequest().body(Map.of("error", "No file sent"));
        }
        
        long sizeBytes = file.getSize();
        if (sizeBytes > 5 * 1024 * 1024) {
             return ResponseEntity.badRequest().body(Map.of("error", "File size exceeds 5MB limit."));
        }
        
        String ext = StringUtils.getFilenameExtension(file.getOriginalFilename());
        if (ext == null || !ext.matches("(?i)(jpg|jpeg|png|pdf|webp|jfif)")) {
             return ResponseEntity.badRequest().body(Map.of("error", "Only Images (JPG, PNG, WEBP) and PDF allowed."));
        }

        try {
            String fileName = UUID.randomUUID().toString() + "." + ext;
            Path filePath = Paths.get(UPLOAD_DIR + fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            String fileUrl = "http://localhost:8080/uploads/" + fileName;
            return ResponseEntity.ok(Map.of("url", fileUrl));
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Could not save file: " + e.getMessage()));
        }
    }
}
