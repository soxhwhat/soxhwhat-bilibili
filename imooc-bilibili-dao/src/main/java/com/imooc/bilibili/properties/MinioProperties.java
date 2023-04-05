package com.imooc.bilibili.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * <p>在开始处详细描述该类的作用</p>
 * <p>描述请遵循 javadoc 规范</p>
 *
 * @author Soxhwhat
 * @date 2023/3/29 17:27
 * @update [序号][日期YYYY-MM-DD] [更改人姓名][变更描述]
 */
@Data
@Component
@ConfigurationProperties(prefix = "minio")
public class MinioProperties {

    private String endpoint = "http://127.0.0.1:9000";

    private String accessKey = "admin";

    private String secretKey = "password";

    private String bucketName = "test";


}
