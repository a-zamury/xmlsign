package com.example.xmlsign;

import java.util.ArrayList;
import java.util.List;
import org.springframework.boot.context.properties.ConstructorBinding;

/**
 *
 * @author a-zamury <a.zamury@gmail.com>
 */
@ConstructorBinding
public class ReferenceConfiguration {
    private final String uri;
    private final MethodConfiguration digest;
    private final List<MethodConfiguration> transforms;

    public ReferenceConfiguration(String uri, MethodConfiguration digest, List<MethodConfiguration> transforms) {
        this.uri = uri;
        this.digest = digest;
        this.transforms = new ArrayList(transforms);
    }

    public String getUri() {
        return uri;
    }

    public MethodConfiguration getDigest() {
        return digest;
    }

    public List<MethodConfiguration> getTransforms() {
        return transforms;
    }
        
}
