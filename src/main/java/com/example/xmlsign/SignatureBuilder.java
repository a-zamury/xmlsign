package com.example.xmlsign;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.xml.crypto.dsig.CanonicalizationMethod;
import javax.xml.crypto.dsig.DigestMethod;
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.SignatureMethod;
import javax.xml.crypto.dsig.SignedInfo;
import javax.xml.crypto.dsig.Transform;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.keyinfo.X509Data;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.DigestMethodParameterSpec;
import javax.xml.crypto.dsig.spec.SignatureMethodParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author a-zamury <a.zamury@gmail.com>
 */
@Component
public class SignatureBuilder {

    private static Logger log = Logger.getLogger(SignatureBuilder.class.getName());

    @Autowired
    private SignatureConfiguration conf;

    private XMLSignatureFactory signatureFactory;
    private KeyInfoFactory keyInfoFactory;
    private CertificateFactory certificateFactory;

    @PostConstruct
    private void init() {
        log.info("Creating signature factory.");
        signatureFactory = XMLSignatureFactory.getInstance("DOM");
        
        log.info("Creating key info factory.");
        keyInfoFactory = signatureFactory.getKeyInfoFactory();
        
        try {
            log.info("Creating X.509 certificate factory.");
            certificateFactory = CertificateFactory.getInstance("X.509");
        } catch (CertificateException ex) {
            throw new RuntimeException(
                    "Create X.509 certificate factory failed:" + ex.getMessage(), ex);
        }
    }

    protected DigestMethod buildDigestMethod(MethodConfiguration c)
            throws SignatureBuildingException {
        try {
            return signatureFactory.newDigestMethod(
                    c.getAlgorithm(),
                    (DigestMethodParameterSpec) null
            );
        } catch (NoSuchAlgorithmException | InvalidAlgorithmParameterException ex) {
            throw new SignatureBuildingException(
                    "Build digest method failed:" + ex.getMessage(), ex);
        }
    }

    protected List buildTransforms(List<MethodConfiguration> c)
            throws SignatureBuildingException {
        try {
            List<Transform> transforms = new ArrayList<>();
            for (MethodConfiguration transformConfiguration : c) {
                transforms.add(signatureFactory.newTransform(
                        transformConfiguration.getAlgorithm(),
                        (TransformParameterSpec) null
                ));
            }
            return transforms;
        } catch (NoSuchAlgorithmException | InvalidAlgorithmParameterException ex) {
            throw new SignatureBuildingException(
                    "Build transform list failed:" + ex.getMessage(), ex);
        }
    }

    protected CanonicalizationMethod buildCanonicalizationMethod()
            throws SignatureBuildingException {
        try {
            return signatureFactory.newCanonicalizationMethod(
                    conf.getCanonicalization().getAlgorithm(),
                    (C14NMethodParameterSpec) null
            );
        } catch (NoSuchAlgorithmException | InvalidAlgorithmParameterException ex) {
            throw new SignatureBuildingException(
                    "Build canonicalization failed: " + ex.getMessage(), ex);
        }
    }

    protected SignatureMethod buildSignatureMethod()
            throws SignatureBuildingException {
        try {
            return signatureFactory.newSignatureMethod(
                    conf.getAlgorithm(),
                    (SignatureMethodParameterSpec) null
            );
        } catch (NoSuchAlgorithmException | InvalidAlgorithmParameterException ex) {
            throw new SignatureBuildingException(
                    "Build signature failed:" + ex.getMessage(), ex);
        }
    }

    private List buildReferences() throws SignatureBuildingException {
        List<Reference> refs = new ArrayList<>();
        for (ReferenceConfiguration referenceConfiguration : conf.getReferences()) {
            refs.add(signatureFactory.newReference(
                    referenceConfiguration.getUri(),
                    buildDigestMethod(referenceConfiguration.getDigest()),
                    buildTransforms(referenceConfiguration.getTransforms()),
                    null,
                    null)
            );
        }
        return refs;
    }

    protected KeyInfo buildKeyInfo(SigningKey key) throws SignatureBuildingException {
        log.fine("Building key info.");
        try (InputStream is = new ByteArrayInputStream(key.getCertificate().getEncoded())) {
            X509Certificate cert = (X509Certificate) certificateFactory.generateCertificate(is);
            X509Data data = keyInfoFactory.newX509Data(Collections.singletonList(cert));
            return keyInfoFactory.newKeyInfo(Collections.singletonList(data));
        } catch (CertificateException | IOException ex) {
            throw new SignatureBuildingException(
                    "Build key info failed : " + ex.getMessage(), ex);
        }
    }

    public XMLSignature build(SigningKey key)
            throws SignatureBuildingException {        
        SignedInfo signedInfo = signatureFactory.newSignedInfo(
                buildCanonicalizationMethod(),
                buildSignatureMethod(),
                buildReferences());
        return signatureFactory.newXMLSignature(signedInfo, buildKeyInfo(key));
    }

}
