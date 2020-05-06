package com.example.xmlsign;

/**
 * 
 * @author a-zamury <a.zamury@gmail.com>
 */
public class SigningKeyException extends Exception {
    private static String errorText = "Signing key initialization error:";
    
    SigningKeyException(String message) {
        super(errorText + message);
    }
    
    SigningKeyException(String message, Throwable cause) {
        super(errorText + message, cause);
    }
}
