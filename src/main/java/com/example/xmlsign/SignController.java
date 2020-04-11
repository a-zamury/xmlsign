package com.example.xmlsign;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 *
 * @author a-zamury <a.zamury@gmail.com>
 */
@RestController
public class SignController {
    @Autowired
    private Signer signer;
    
    private DocumentBuilder builder;
    private Transformer transformer;
    
    @PostConstruct
    private void Init() {        
        try {
            builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "no");
        } catch (ParserConfigurationException ex) {
            throw new RuntimeException("Creation of DocumentBuilder failed.", ex);
        } catch (TransformerConfigurationException ex) {
            throw new RuntimeException("Creation of Transformer failed.", ex);
        }
    }
       
    @PostMapping("/sign")
    public String sign(@RequestParam MultipartFile document) {
        String result = "Empty";
        try {
            Document doc = builder.parse(document.getInputStream());
            signer.sign(doc);
            
            StringWriter writer = new StringWriter();
            transformer.transform(new DOMSource(doc), new StreamResult(writer));
            result =  writer.toString();
        } catch (SAXException ex) {
            throw new IllegalArgumentException("Document parameter is not valid xml.", ex);            
        } catch (IOException ex) {
            Logger.getLogger(SignController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TransformerException ex) {
            Logger.getLogger(SignController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }
    
    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleRuntimeException(RuntimeException ex) {
        return ex.getMessage();
    }
    
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleIllegalArgumentException(IllegalArgumentException ex) {
        return ex.getMessage();
    }
    
}
