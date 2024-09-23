package org.onstage.amazon;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.onstage.exceptions.BadRequestException;
import org.onstage.utils.DateUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;

@Service
@Slf4j
@RequiredArgsConstructor
public class AmazonS3Service {

    private final Integer DEFAULT_WIDTH = 200;
    private final Integer DEFAULT_HEIGHT = 200;
    private final Integer THUMBNAIL_WIDTH = 50;
    private final Integer THUMBNAIL_HEIGHT = 50;

    private final AmazonS3 amazonS3;

    @Value("${clound.aws.s3.bucket}")
    private String bucketName;

    public URL generateUserThumbnailPresignedUrl(String userId, HttpMethod httpMethod) {
        GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(bucketName, getUserThumbnailKey(userId), httpMethod);
        request.setExpiration(getExpirationDate(httpMethod));
        return amazonS3.generatePresignedUrl(request);
    }


    private Date getExpirationDate(HttpMethod httpMethod) {
        return switch (httpMethod) {
            case GET -> DateUtils.addHours(new Date(), 1);
            case PUT -> DateUtils.addMinutes(new Date(), 10L);
            default -> DateUtils.addMinutes(new Date(), 1L);
        };
    }

    public void putObject(byte[] image, String userId, String contentType) {
        try {
            String format = getFormatFromContentType(contentType);

            // Resize and upload main image (max width 200px)
            byte[] resizedImage = resizeImage(image, format, 200, 0);
            uploadToS3(resizedImage, getUserProfileKey(userId), contentType);

            // Resize and upload thumbnail (max width 50px)
            byte[] thumbnailImage = resizeImage(image, format, 50, 0);
            uploadToS3(thumbnailImage, getUserThumbnailKey(userId), contentType);
        } catch (AmazonServiceException | IOException e) {
            log.error("Failed to upload images for user {} with error {}", userId, e.getMessage());
            throw BadRequestException.invalidRequest();
        }
    }

    private void uploadToS3(byte[] image, String key, String contentType) {
        InputStream inputStream = new ByteArrayInputStream(image);
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(image.length);
        metadata.setContentType(contentType);

        amazonS3.putObject(bucketName, key.toLowerCase(), inputStream, metadata);
    }

    private byte[] resizeImage(byte[] originalImage, String format, int maxWidth, int maxHeight) throws IOException {
        BufferedImage img = ImageIO.read(new ByteArrayInputStream(originalImage));
        int originalWidth = img.getWidth();
        int originalHeight = img.getHeight();

        int newWidth = maxWidth;
        int newHeight = (int) ((float) originalHeight / originalWidth * newWidth);

        if (maxHeight > 0 && newHeight > maxHeight) {
            newHeight = maxHeight;
            newWidth = (int) ((float) originalWidth / originalHeight * newHeight);
        }

        Image resizedImage = img.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
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

    private String getUserProfileKey(String userId) {
        return "user/".concat(userId).concat("/profilePicture");
    }

    private String getUserThumbnailKey(String userId) {
        return "user/".concat(userId).concat("/thumbnail");
    }
}
