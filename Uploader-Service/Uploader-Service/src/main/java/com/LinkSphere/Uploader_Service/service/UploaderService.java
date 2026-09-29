package com.LinkSphere.Uploader_Service.service;

import org.springframework.web.multipart.MultipartFile;

public interface UploaderService {
    String upload(MultipartFile file);
}
