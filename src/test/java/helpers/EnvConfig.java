package helpers;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class EnvConfig {
    private static final Properties properties = new Properties();

    static {
        try {
            InputStream input = EnvConfig.class.getClassLoader().getResourceAsStream("config.properties");
            if (input != null) {
                properties.load(input);
                input.close();
            }
        } catch (IOException e) {
            System.err.println("Aviso: Arquivo config.properties não encontrado no classpath");
        }
    }

    public static String get(String key) {
        String envKey = key.toUpperCase().replace('.', '_');
        
        String value = System.getenv(envKey);
        
        if (value == null || value.isBlank()) {
            value = properties.getProperty(key);
        }
        
        if (value == null || value.isBlank()) {
            value = properties.getProperty(envKey);
        }
        
        if (value == null || value.isBlank()) {
            throw new IllegalStateException(
                "Variável de ambiente obrigatória não definida: " + envKey
            );
        }
        return value;
    }
}
