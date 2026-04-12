package com.romeogolf.residence.upload;

import com.romeogolf.residence.shared.ApiResponse;
import com.romeogolf.residence.shared.exception.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class UploadController {

    private static final Set<String> ALLOWED_TYPES = Set.of(
            "image/jpeg", "image/png", "image/webp", "image/gif"
    );

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    @PostMapping("/api/admin/upload")
    public ResponseEntity<ApiResponse<Map<String, String>>> upload(
            @RequestParam("file") MultipartFile file) throws IOException {

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_TYPES.contains(contentType)) {
            throw new ApiException("Apenas imagens são permitidas (JPEG, PNG, WEBP, GIF).", HttpStatus.BAD_REQUEST);
        }

        if (file.getSize() > 10 * 1024 * 1024) {
            throw new ApiException("O arquivo não pode ultrapassar 10 MB.", HttpStatus.BAD_REQUEST);
        }

        String ext = StringUtils.getFilenameExtension(file.getOriginalFilename());
        if (ext == null) ext = "jpg";
        String filename = UUID.randomUUID() + "." + ext.toLowerCase();

        Path uploadPath = Paths.get(uploadDir).toAbsolutePath();
        Files.createDirectories(uploadPath);
        Files.copy(file.getInputStream(), uploadPath.resolve(filename));

        String url = "/uploads/" + filename;
        return ResponseEntity.ok(ApiResponse.ok("Upload realizado.", Map.of("url", url)));
    }
}
