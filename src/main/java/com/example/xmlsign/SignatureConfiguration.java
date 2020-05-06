package com.example.xmlsign;

import java.util.ArrayList;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

/**
 *
 * @author a-zamury <a.zamury@gmail.com>
 */
@ConstructorBinding
@ConfigurationProperties(prefix = "signature")
public class SignatureConfiguration extends MethodConfiguration {

    private final MethodConfiguration canonicalization;
    private final List<ReferenceConfiguration> references;

    public SignatureConfiguration(String algorithm, String params, 
            MethodConfiguration canonicalization, 
            List<ReferenceConfiguration> references) {
        super(algorithm, params);
        this.canonicalization = canonicalization;
        this.references = new ArrayList(references);
    }

    public MethodConfiguration getCanonicalization() {
        return canonicalization;
    }

    public List<ReferenceConfiguration> getReferences() {
        return references;
    }
}
