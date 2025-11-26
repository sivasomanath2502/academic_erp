package com.academic.erp.backend.controller;

import com.academic.erp.backend.dto.PhotoUploadResponse;
import com.academic.erp.backend.service.PhotoStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/uploads")
@RequiredArgsConstructor
@CrossOrigin
@Slf4j
public class PhotoUploadController {

    private final PhotoStorageService photoStorageService;

    @PostMapping(value = "/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public PhotoUploadResponse uploadPhoto(@RequestParam("file") MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Photo file is required");
        }
        
        try {
            String path = photoStorageService.storePhoto(file);
            return new PhotoUploadResponse(path, file.getOriginalFilename(), file.getSize());
        } catch (IllegalArgumentException e) {
            // Re-throw validation errors as-is
            throw e;
        } catch (Exception e) {
            log.error("Error uploading photo", e);
            throw new RuntimeException("Failed to upload photo: " + e.getMessage());
        }
    }
}

