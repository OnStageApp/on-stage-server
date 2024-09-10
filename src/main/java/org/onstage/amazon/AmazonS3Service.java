package org.onstage.amazon;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.util.IOUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.onstage.exceptions.BadRequestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Service
@Slf4j
@RequiredArgsConstructor
public class AmazonS3Service {

    private final AmazonS3 amazonS3;

    @Value("${clound.aws.s3.bucket}")
    private String bucketName;

    private final Integer DEFAULT_WIDTH = 200;
    private final Integer DEFAULT_HEIGHT = 200;
    private final Integer THUMBNAIL_WIDTH = 50;
    private final Integer THUMBNAIL_HEIGHT = 50;

    public void putObject(byte[] image, String key, String contentType) {
        try {
            byte[] resizedImage = resizeImage(image, getFormatFromContentType(contentType), DEFAULT_WIDTH, DEFAULT_HEIGHT);

            InputStream inputStream = new ByteArrayInputStream(resizedImage);
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(resizedImage.length);
            metadata.setContentType(contentType);

            amazonS3.putObject(bucketName, key.toLowerCase(), inputStream, metadata);
        } catch (AmazonServiceException | IOException e) {
            log.error("Failed to upload image with key {} with error {}", key, e.getMessage());
            throw BadRequestException.invalidRequest();
        }
    }

    public byte[] getObject(String key) {
        return getObject(key, DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    public byte[] getObject(String key, int width, int height) {
        try {
            S3Object s3Object = amazonS3.getObject(bucketName, key.toLowerCase());
            byte[] originalImage = IOUtils.toByteArray(s3Object.getObjectContent());
            return resizeImage(originalImage, getFormatFromContentType(s3Object.getObjectMetadata().getContentType()), width, height);
        } catch (AmazonServiceException | IOException e) {
            log.error("Failed to get image with key {} with error {}", key, e.getMessage());
            return null;
        }
    }

    public byte[] getThumbnail(String key) {
        return getObject(key, THUMBNAIL_WIDTH, THUMBNAIL_HEIGHT);
    }

    private byte[] resizeImage(byte[] originalImage, String extension, int width, int height) throws IOException {
        BufferedImage img = ImageIO.read(new ByteArrayInputStream(originalImage));
        java.awt.Image resizedImage = img.getScaledInstance(width, height, java.awt.Image.SCALE_SMOOTH);
        BufferedImage bufferedResizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        bufferedResizedImage.getGraphics().drawImage(resizedImage, 0, 0, null);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(bufferedResizedImage, extension, baos);
        return baos.toByteArray();
    }

    private String getFormatFromContentType(String contentType) {
        return switch (contentType) {
            case "image/png" -> "png";
            case "image/heif" -> "heif";
            default -> "jpg";
        };
    }
}
