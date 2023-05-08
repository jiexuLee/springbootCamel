package com.sgm.esb.template.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "fuse.custom")
public class ESBProperties {
    private String restful;
    private String quartz;
    private String restUri;
}
