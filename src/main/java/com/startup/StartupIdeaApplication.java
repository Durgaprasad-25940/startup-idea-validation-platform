package com.startup;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

@SpringBootApplication
public class StartupIdeaApplication {

    public static void main(String[] args) {
        loadDotEnv();
        SpringApplication.run(StartupIdeaApplication.class, args);
    }

    /**
     * Loads local configuration from a .env file if present in the workspace root,
     * ensuring sensitive database passwords and API keys are never exposed in Git.
     */
    private static void loadDotEnv() {
        File envFile = new File(".env");
        if (envFile.exists() && envFile.isFile()) {
            try (InputStream in = new FileInputStream(envFile)) {
                Properties envProps = new Properties();
                envProps.load(in);
                for (String key : envProps.stringPropertyNames()) {
                    if (System.getProperty(key) == null && System.getenv(key) == null) {
                        System.setProperty(key, envProps.getProperty(key));
                    }
                }
            } catch (Exception e) {
                System.err.println("Note: .env file found but could not be read: " + e.getMessage());
            }
        }
    }
}
