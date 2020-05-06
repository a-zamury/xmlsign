package com.example.xmlsign;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureException;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;

/**
 *
 * @author a-zamury <a.zamury@gmail.com>
 */
@Component
public class DocumentSigner {

    @Autowired
    private SigningKey signingKey;

    @Autowired
    private SignatureBuilder signatureBuilder;

    public void sign(Document doc) {
        try {            
            DOMSignContext signContext = new DOMSignContext(signingKey.getKey(),
                    doc.getDocumentElement());

            XMLSignature signature = signatureBuilder.build(signingKey);

            signature.sign(signContext);
        } catch (MarshalException | XMLSignatureException | SignatureBuildingException ex) {
            
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }
}
