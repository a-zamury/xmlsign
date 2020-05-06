package com.example.xmlsign;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * 
 * @author a-zamury <a.zamury@gmail.com>
 */
@Component
public class DocumentParser {
    
    private DocumentBuilder builder;
    
    @PostConstruct
    private void init() {
        try {
            builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        } catch (ParserConfigurationException ex) {
            throw new RuntimeException("Creation of DocumentBuilder failed.", ex);
        }
    }
    
    /**
     * 
     * @param inputStream
     * @throws DocumentParsingException if parsing failed
     * @return Экземпляр класса 
     */
    public Document parse(InputStream inputStream) throws DocumentParsingException {
        try {
            return builder.parse(inputStream);
        } catch (SAXException | IOException ex) {
            throw new DocumentParsingException("Input stream parsing failed: " + ex.getMessage(), ex);
        }
    }
}
