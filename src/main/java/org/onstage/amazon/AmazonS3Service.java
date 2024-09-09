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

    private final Integer MAX_DIMENSION = 300;
    private final Integer THUMBNAIL_DIMENSION = 80;

    public void putObject(byte[] image, String key, String contentType) {
        try {
            byte[] resizedImage = resizeImage(image, getFormatFromContentType(contentType), MAX_DIMENSION);

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
        try {
            S3Object s3Object = amazonS3.getObject(bucketName, key.toLowerCase());
            return IOUtils.toByteArray(s3Object.getObjectContent());
        } catch (AmazonServiceException | IOException e) {
            log.error("Failed to get image with key {} with error {}", key, e.getMessage());
            return null; // Consider throwing a custom exception instead
        }
    }

    public byte[] getThumbnail(String key) {
        try {
            S3Object s3Object = amazonS3.getObject(bucketName, key.toLowerCase());
            byte[] originalImage = IOUtils.toByteArray(s3Object.getObjectContent());
            return resizeImage(originalImage, getFormatFromContentType(s3Object.getObjectMetadata().getContentType()), THUMBNAIL_DIMENSION);
        } catch (AmazonServiceException | IOException e) {
            log.error("Failed to get thumbnail with key {} with error {}", key, e.getMessage());
            return null; // Consider throwing a custom exception instead
        }
    }

    private byte[] resizeImage(byte[] originalImage, String format, int maxDimension) throws IOException {
        BufferedImage img = ImageIO.read(new ByteArrayInputStream(originalImage));
        int originalWidth = img.getWidth();
        int originalHeight = img.getHeight();

        int newWidth, newHeight;
        if (originalWidth > originalHeight) {
            newWidth = maxDimension;
            newHeight = (originalHeight * maxDimension) / originalWidth;
        } else {
            newHeight = maxDimension;
            newWidth = (originalWidth * maxDimension) / originalHeight;
        }

        java.awt.Image resizedImage = img.getScaledInstance(newWidth, newHeight, java.awt.Image.SCALE_SMOOTH);
        BufferedImage bufferedResizedImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
        bufferedResizedImage.getGraphics().drawImage(resizedImage, 0, 0, null);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(bufferedResizedImage, format, baos);
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
