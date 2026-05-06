package com.finance.config;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CloudinaryConfig {

    @Bean
    public Cloudinary cloudinary(
            @Value("${CLOUDINARY_URL:${CLAUDINARY_URL:}}") String cloudinaryUrl,
            @Value("${cloudinary.cloud-name:${CLOUDINARY_CLOUD_NAME:${CLAUDINARY_CLOUD_NAME:}}}") String cloudName,
            @Value("${cloudinary.api-key:${CLOUDINARY_API_KEY:${CLAUDINARY_API_KEY:}}}") String apiKey,
            @Value("${cloudinary.api-secret:${CLOUDINARY_API_SECRET:${CLAUDINARY_API_SECRET:}}}") String apiSecret) {

        if (!isBlank(cloudinaryUrl)) {
            return new Cloudinary(cloudinaryUrl);
        }

        if (isBlank(cloudName) || isBlank(apiKey) || isBlank(apiSecret)) {
            throw new IllegalStateException(
                    "Cloudinary configuration missing. Set CLOUDINARY_URL or CLOUDINARY_CLOUD_NAME, CLOUDINARY_API_KEY, and CLOUDINARY_API_SECRET.");
        }

        return new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret));
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
