package org.onstage.amazon;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.onstage.utils.DateUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.util.Date;

@Service
@Slf4j
@RequiredArgsConstructor
public class AmazonS3Service {

    private final AmazonS3 amazonS3;

    @Value("${clound.aws.s3.bucket}")
    private String bucketName;

    public URL generatePresignedUrl(String key, HttpMethod httpMethod) {
        GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(bucketName, key, httpMethod);
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
}
