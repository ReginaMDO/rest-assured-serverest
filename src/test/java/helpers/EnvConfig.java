package helpers;

public class EnvConfig {

    public static String get(String key) {
        String envKey = key.toUpperCase().replace('.', '_');
        String value = System.getenv(envKey);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException(
                "Variável de ambiente obrigatória não definida: " + envKey
            );
        }
        return value;
    }
}
