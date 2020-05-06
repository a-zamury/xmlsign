package com.example.xmlsign;

import java.io.IOException;
import java.io.InputStream;
import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Document;

/**
 *
 * @author a-zamury <a.zamury@gmail.com>
 */
@RestController
public class SignController {

    @Autowired
    private DocumentSigner signer;

    @Autowired
    private DocumentParser parser;

    @Autowired
    private DocumentConverter converter;

    @PostMapping("/sign")
    public String sign(@RequestParam MultipartFile document) throws
            DocumentParsingException, DocumentConvertingException,
            DocumentSigningException {
        try (InputStream documentStream = document.getInputStream()) {
            Document doc = parser.parse(documentStream);
            signer.sign(doc);
            return converter.convertToString(doc);
        } catch (IOException ex) {
            throw new DocumentParsingException(ex.getMessage(), ex);
        }
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
