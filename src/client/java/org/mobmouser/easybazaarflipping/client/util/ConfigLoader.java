package org.mobmouser.easybazaarflipping.client.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public class ConfigLoader {
    private static final Path CONFIG_PATH = Path.of("config", "bazaar-optimizer.properties");
    private static Properties properties = null;
    private static String API_KEY = "";

    public static String get(String key) {
        if (properties == null) {
            load();
        }
        return properties.getProperty(key, "");
    }

    private static void load() {
        properties = new Properties();
        try {
            if (Files.exists(CONFIG_PATH)) {
                properties.load(Files.newBufferedReader(CONFIG_PATH));
            } else {
                Files.createDirectories(CONFIG_PATH.getParent());
                Files.writeString(CONFIG_PATH, "# Bazaar Optimizer Config\nAPI_KEY=" + API_KEY + "\n");
                System.out.println("[BazaarOptimizer] Config file created: " + CONFIG_PATH);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}