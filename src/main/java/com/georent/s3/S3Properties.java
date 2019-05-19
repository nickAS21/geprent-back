package com.georent.s3;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "aws")
public class S3Properties {

    private String secretKey;
    private String accessKey;
    private String bucketName;
    private  String andPointUrl;

}
