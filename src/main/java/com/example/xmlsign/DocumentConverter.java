package com.example.xmlsign;

import java.io.IOException;
import java.io.StringWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;

/**
 *
 * @author a-zamury <a.zamury@gmail.com>
 */
@Component
public class DocumentConverter {
    private Transformer transformer;
    
    @PostConstruct
    private void init() {        
        try {    
            transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "no");
        } catch (TransformerConfigurationException ex) {
            throw new RuntimeException("Creation of Transformer failed.", ex);
        }
    }

    public String convertToString(Document doc) throws DocumentConvertingException {
        try {
            StringWriter writer = new StringWriter();
            transformer.transform(new DOMSource(doc), new StreamResult(writer));
            return writer.toString();
        } catch (TransformerException ex) {
            throw new DocumentConvertingException("Document convert failed : " + ex.getMessage(), ex);
        }
    }
    
}
