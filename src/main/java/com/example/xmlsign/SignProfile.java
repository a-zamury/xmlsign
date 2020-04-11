package com.example.xmlsign;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 *
 * @author a-zamury <a.zamury@gmail.com>
 */
@Component
public class SignProfile {
    @Value("${digest.algorithm:http://www.w3.org/2000/09/xmldsig#sha1}")
    private String digestAlgorithm;
    
    @Value("${digest.params:")
    private String digestParams;
    
    @Value("${canonicalization.algorithm:http://www.w3.org/TR/2001/REC-xml-c14n-20010315#WithComments}")
    private String canonicalizationAlgorithm;
    
    @Value("${canonicalization.params:")
    private String canonicalizationParams;
    
    @Value("${signature.algorithm:http://www.w3.org/2009/xmldsig11#dsa-sha256}")
    private String signatureAlgorithm;
    
    @Value("${transform.algorithm:http://www.w3.org/2000/09/xmldsig#enveloped-signature}")
    private String transformAlgorithm;
    
    @Value("${transform.params:}")
    private String transformParams;

    @Value("${uri:}")
    private String uri;
    
    @Value("${xml.mechanism:DOM}")
    private String XMLMechanismType;
    
    @Value("${keystore.type:PKCS12}")
    private String keyStoreType;
    
    @Value("${keystore.name:}")
    private String keyStoreName;
    
    @Value("${keystore.password:}")
    private String keyStorePassword;

    @Value("${key.alias:mykey}")
    private String keyAlias;
    
    @Value("${key.password:}")
    private String keyPassword;

    public String getDigestAlgorithm() {
        return digestAlgorithm;
    }

    public String getDigestParams() {
        return digestParams;
    }

    public String getCanonicalizationAlgorithm() {
        return canonicalizationAlgorithm;
    }

    public String getCanonicalizationParams() {
        return canonicalizationParams;
    }

    public String getSignatureAlgorithm() {
        return signatureAlgorithm;
    }

    public String getTransformAlgorithm() {
        return transformAlgorithm;
    }

    public String getTransformParams() {
        return transformParams;
    }

    public String getUri() {
        return uri;
    }

    public String getXMLMechanismType() {
        return XMLMechanismType;
    }

    public String getKeyStoreType() {
        return keyStoreType;
    }

    public String getKeyStoreName() {
        return keyStoreName;
    }

    public String getKeyStorePassword() {
        return keyStorePassword;
    }

    public String getKeyAlias() {
        return keyAlias;
    }

    public String getKeyPassword() {
        return keyPassword;
    }
        
}
