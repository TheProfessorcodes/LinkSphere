package com.LinkSphere.postService.client;

import com.LinkSphere.postService.config.AppConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@FeignClient(name = "Uploader-Service", path = "/uploads/file", url="${UPLOADER_SERVICE_URI:http://localhost:9050}", configuration = AppConfig.class)
public interface UploaderServiceClient {
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<String> uploadFile(@RequestPart("file") MultipartFile file);
}
