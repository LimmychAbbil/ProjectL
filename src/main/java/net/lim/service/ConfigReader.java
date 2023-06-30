package net.lim.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Properties;

public class ConfigReader {
    private static final String CONFIG_FILE_NAME = "configuration/config.ini";

    private static final Properties properties = loadProperties();

    public static Properties loadProperties() {
        File configFile = checkConfigExists();
        Properties properties;
        try (FileInputStream fileInputStream = new FileInputStream(configFile)){
            properties = new Properties();
            properties.load(fileInputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (!properties.containsKey("server.ip")) {
            throw new RuntimeException("Config file doesn't contain server.ip property");
        }
        return properties;
    }

    public static File checkConfigExists() {
        try {
            File file = new File(Objects.requireNonNull(ConfigReader.class
                    .getClassLoader().getResource("configuration/client.config")).toURI());
            if (!file.exists()) {
                throw new RuntimeException("No config file found. Please check " + CONFIG_FILE_NAME + " exists.");
            }
            return file;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Properties getProperties() {
        return properties;
    }
}
