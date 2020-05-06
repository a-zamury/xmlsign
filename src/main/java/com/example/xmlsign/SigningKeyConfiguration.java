package com.example.xmlsign;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

/**
 * Конфигурация для объекта с ключевой информацией
 *
 * @author a-zamury <a.zamury@gmail.com>
 */
@ConstructorBinding
@ConfigurationProperties(prefix = "signing-key")
public class SigningKeyConfiguration {

    private final KeyStoreConfiguration keyStore;
    private final String alias;
    private final String password;

    public SigningKeyConfiguration(KeyStoreConfiguration keyStore, String alias, String password) {
        this.keyStore = keyStore;
        this.alias = alias;
        this.password = password;
    }

    public KeyStoreConfiguration getKeyStore() {
        return keyStore;
    }

    public String getAlias() {
        return alias;
    }

    public String getPassword() {
        return password;
    }
    
    public static class KeyStoreConfiguration {

        private final String name;
        private final String type;
        private final String password;

        public KeyStoreConfiguration(String name, String type, String password) {
            this.name = name;
            this.type = type;
            this.password = password;
        }

        public String getName() {
            return name;
        }

        public String getType() {
            return type;
        }

        public String getPassword() {
            return password;
        }
    }
}
