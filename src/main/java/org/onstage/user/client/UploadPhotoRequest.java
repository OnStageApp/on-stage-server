package org.onstage.user.client;

import lombok.Builder;
import org.springframework.web.multipart.MultipartFile;

@Builder(toBuilder = true)
public record UploadPhotoRequest(
        String id,
        MultipartFile image
) {
}
