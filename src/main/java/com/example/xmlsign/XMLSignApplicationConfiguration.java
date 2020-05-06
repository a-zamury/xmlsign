package com.example.xmlsign;

import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;

/**
 *
 * @author a-zamury <a.zamury@gmail.com>
 */
@Configuration
@ConfigurationPropertiesScan
public class XMLSignApplicationConfiguration {

    @Bean
    public StandardServletMultipartResolver multipartResolver() {
        return new StandardServletMultipartResolver();
    }
    
}
