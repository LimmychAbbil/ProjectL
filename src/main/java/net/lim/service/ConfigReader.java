package net.lim.service;

import java.io.InputStream;
import java.util.Properties;

public class ConfigReader {
    private static final String CONFIG_FILE_NAME = "configuration/client.config";

    private static final Properties properties = loadProperties();

    public static Properties loadProperties() {
        Properties props;
        try (InputStream resourceConfigStream = ConfigReader.class
                .getClassLoader().getResourceAsStream("configuration/client.config")) {

            if (resourceConfigStream == null || resourceConfigStream.available() <= 0) {
                throw new RuntimeException("No config file found. Please check " + CONFIG_FILE_NAME + " exists.");
            }
            props = new Properties();
            props.load(resourceConfigStream);
        } catch (Exception e) {
            throw new RuntimeException("Exception happen when read resource file "
                    + CONFIG_FILE_NAME + ": " + e.getMessage(), e);
        }

        if (!props.containsKey("server.ip")) {
            throw new RuntimeException("Config file doesn't contain server.ip property");
        }
        return props;
    }

    public static Properties getProperties() {
        return properties;
    }
}
