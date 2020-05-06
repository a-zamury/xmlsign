package com.example.xmlsign;

import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.boot.context.properties.bind.DefaultValue;

/**
 *
 * @author a-zamury <a.zamury@gmail.com>
 */
@ConstructorBinding
public class MethodConfiguration {
    private final String algorithm;
    private final String params;

    public MethodConfiguration(String algorithm, 
            @DefaultValue("") String params) {
        this.algorithm = algorithm;
        this.params = params;
    }

    public String getAlgorithm() {
        return algorithm;
    }

    public String getParams() {
        return params;
    }
        
}
