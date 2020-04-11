package com.example.xmlsign;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.Key;
import java.security.KeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.dsig.CanonicalizationMethod;
import javax.xml.crypto.dsig.DigestMethod;
import javax.xml.crypto.dsig.SignatureMethod;
import javax.xml.crypto.dsig.SignedInfo;
import javax.xml.crypto.dsig.Transform;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureException;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.keyinfo.KeyValue;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.DigestMethodParameterSpec;
import javax.xml.crypto.dsig.spec.SignatureMethodParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;

/**
 *
 * @author a-zamury <a.zamury@gmail.com>
 */
@Component
public class Signer {

    @Autowired
    SignProfile profile;
    
    private Key key;
    private Certificate certificate;
    
    private XMLSignatureFactory signatureFactory;
    private KeyInfoFactory keyInfoFactory;
    
    
    @PostConstruct
    private void init() {
        signatureFactory = XMLSignatureFactory.getInstance(profile.getXMLMechanismType());
        keyInfoFactory = signatureFactory.getKeyInfoFactory();

        KeyStore keyStore = openKeyStore(profile.getKeyStoreType(),
                profile.getKeyStoreName(), profile.getKeyStorePassword());
        try {
            key = keyStore.getKey(profile.getKeyAlias(), profile.getKeyPassword().toCharArray());
            if (key == null) {
                throw new IllegalArgumentException("Key not found: " + profile.getKeyAlias());
            }
            certificate = keyStore.getCertificate(profile.getKeyAlias());
            if (certificate == null) {
                throw new IllegalArgumentException("Certificate not found: " + profile.getKeyAlias());
            }
        } catch (KeyStoreException | NoSuchAlgorithmException ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        } catch (UnrecoverableKeyException ex) {
            throw new IllegalArgumentException("Wrong key password.", ex);
        }
    }
    
    private KeyStore openKeyStore(final String type, final String name, final String password) {
        try {
            KeyStore keyStore = KeyStore.getInstance(type);
            InputStream keyStoreStream = null;
            if (!name.isEmpty()) {
                keyStoreStream = new FileInputStream(name);
            }
            keyStore.load(keyStoreStream, password.toCharArray());
            return keyStore;
        } catch (KeyStoreException ex) {
            throw new IllegalArgumentException("Invalid keystore type:" + type, ex);
        } catch (FileNotFoundException ex) {
            throw new IllegalArgumentException("Invalid keystore name:" + name, ex);
        } catch (IOException ex) {
            if (ex.getCause() instanceof UnrecoverableKeyException) 
                throw new IllegalArgumentException("Invalid keystore password.", ex);
            throw new RuntimeException("Error while reading keystore.", ex);
        } catch (NoSuchAlgorithmException | CertificateException ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }
    
    private DigestMethod getDigestMethod(final String digestAlgorithm, DigestMethodParameterSpec params) {
        try {
            return signatureFactory.newDigestMethod(digestAlgorithm, params);
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalArgumentException("Unknown digest algorithm:" + digestAlgorithm, ex);
        } catch (InvalidAlgorithmParameterException ex) {
            throw new IllegalArgumentException("Invalid digest parameters.", ex);
        }        
    }
    
    private Transform getTransform(final String transformAlgorithm, TransformParameterSpec params) {
        try {
            return signatureFactory.newTransform(transformAlgorithm, params);
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalArgumentException("Unknown transform algorithm:" + transformAlgorithm, ex);
        } catch (InvalidAlgorithmParameterException ex) {
            throw new IllegalArgumentException("Invalid transform parameters.", ex);
        }                
    }

    private CanonicalizationMethod getCanonicalizationMethod(final String canonicalizationAlgorithm, 
            C14NMethodParameterSpec params) {        
        try {
            return signatureFactory.newCanonicalizationMethod(canonicalizationAlgorithm, params);
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalArgumentException("Unknown canonicalization algorithm: " + canonicalizationAlgorithm, ex);
        } catch (InvalidAlgorithmParameterException ex) {
            throw new IllegalArgumentException("Invalid canonicalization parameters.", ex);
        }        
    }
        
    private SignatureMethod getSignatureMethod(final String signatureAlgorithm, SignatureMethodParameterSpec params) {
        try {
            return signatureFactory.newSignatureMethod(signatureAlgorithm, 
                    params);
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalArgumentException("Unknown signatire algorithm: " + signatureAlgorithm, ex);
        } catch (InvalidAlgorithmParameterException ex) {
            throw new IllegalArgumentException("Invalid signature parameters.", ex);
        }
    }
    
    private KeyInfo getKeyInfo() {
        try {
            KeyValue kv = keyInfoFactory.newKeyValue(certificate.getPublicKey());
            return keyInfoFactory.newKeyInfo(Collections.singletonList(kv));
        } catch (KeyException ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }
    
    private List getReferences() {
        DigestMethod digestMethod = getDigestMethod(profile.getDigestAlgorithm(), null);        
        Transform transform = getTransform(profile.getTransformAlgorithm(), null);        
        return Collections.singletonList(
                signatureFactory.newReference(profile.getUri(),
                        digestMethod,
                        Collections.singletonList(transform),
                        null,
                        null)
        );
    }
    
    private XMLSignature buildSignature() {
        CanonicalizationMethod canonicalizationMethod = getCanonicalizationMethod(
                profile.getCanonicalizationAlgorithm(), null);        
        SignatureMethod signatureMethod = getSignatureMethod(
                profile.getSignatureAlgorithm(), null);                
        SignedInfo signedInfo = signatureFactory.newSignedInfo(
                canonicalizationMethod,
                signatureMethod,
                getReferences());
        KeyInfo keyInfo = getKeyInfo();        
        return signatureFactory.newXMLSignature(signedInfo, keyInfo);
    }
    
    public void sign(Document doc) {
        DOMSignContext dsc = new DOMSignContext(
                key,
                doc.getDocumentElement());
        XMLSignature signature = buildSignature();
        try {
            signature.sign(dsc);
        } catch (MarshalException | XMLSignatureException ex) {
            Logger.getLogger(Signer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
