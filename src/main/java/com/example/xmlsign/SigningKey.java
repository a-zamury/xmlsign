package com.example.xmlsign;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author a-zamury <a.zamury@gmail.com>
 */
@Component
public class SigningKey {

    @Autowired
    private SigningKeyConfiguration conf;

    private Key key;
    private Certificate certificate;

    @PostConstruct
    private void init() {
        try {
            KeyStore keyStore = openKeyStore(conf.getKeyStore().getType(),
                    conf.getKeyStore().getName(),
                    conf.getKeyStore().getPassword());
            key = loadKey(keyStore, conf.getAlias(), conf.getPassword());
            certificate = loadCertificate(keyStore, conf.getAlias());
        } catch (SigningKeyException ex) {
            throw new IllegalArgumentException(ex.getMessage(), ex);
        }
    }

    private KeyStore openKeyStore(final String type, final String name,
            final String password) throws SigningKeyException {
        try {
            KeyStore keyStore = KeyStore.getInstance(type);
            if (!name.isEmpty()) {
                try (InputStream keyStoreStream = new FileInputStream(name)) {
                    keyStore.load(keyStoreStream, password.toCharArray());
                } catch (FileNotFoundException ex) {
                    throw new SigningKeyException("keystore is not found", ex);
                }
            } else {
                keyStore.load(null, password.toCharArray());
            }
            return keyStore;
        } catch (KeyStoreException ex) {
            throw new SigningKeyException("invalid keystore type - " + type + ".", ex);
        } catch (NoSuchAlgorithmException | CertificateException | IOException ex) {
            if (ex.getCause() instanceof UnrecoverableKeyException) {
                throw new SigningKeyException("invalid keystore password.", ex);
            }
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }

    private Key loadKey(KeyStore keyStore, final String alias,
            final String password) throws SigningKeyException {
        try {
            Key key = keyStore.getKey(alias, password.toCharArray());
            if (key == null) {
                throw new SigningKeyException("Invalid key alias - " + alias);
            }
            return key;
        } catch (UnrecoverableKeyException ex) {
            throw new SigningKeyException("Invalid key password.", ex);
        } catch (KeyStoreException | NoSuchAlgorithmException ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }

    private Certificate loadCertificate(KeyStore keyStore, final String alias) 
            throws SigningKeyException{
        try {
            Certificate cert = keyStore.getCertificate(alias);
            if (cert == null) {
                throw new SigningKeyException("Key certificate not found.");
            }
            return cert;
        } catch (KeyStoreException ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }        
    }
    
    public Key getKey() {
        return key;
    }

    public Certificate getCertificate() {
        return certificate;
    }

}
