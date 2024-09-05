package org.onstage.amazon;

import com.amazonaws.AmazonClientException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.util.IOUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.onstage.exceptions.BadRequestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static com.amazonaws.util.StringUtils.lowerCase;

@Service
@Slf4j
@RequiredArgsConstructor
public class AmazonS3Service {

    private final AmazonS3 amazonS3;

    @Value("${clound.aws.s3.bucket}")
    private String bucketName;

    public void putObject(byte[] image, String key) {
        try {
            InputStream stream = new ByteArrayInputStream(image);
            ObjectMetadata meta = new ObjectMetadata();
            meta.setContentLength(image.length);
            meta.setContentType("image/jpeg");
            meta.setContentType("image/png");
            amazonS3.putObject(bucketName, lowerCase(key), stream, meta);
        } catch (SdkClientException e) {
            log.error("Failed to upload imageUrl with key {} with error {}", key, e.getMessage());
            throw BadRequestException.invalidRequest(); // add internal server error
        }
    }

    public byte[] getObject(String key) {
        byte[] photo = null;
        try {
            S3Object s3Object = amazonS3.getObject(bucketName, lowerCase(key));
            if (s3Object != null)
                photo = IOUtils.toByteArray(s3Object.getObjectContent().getDelegateStream());
        } catch (AmazonClientException | IOException e) {
            log.error("Failed to get imageUrl with key {} with error {}", key, e.getMessage());
        }
        return photo;
    }

}
