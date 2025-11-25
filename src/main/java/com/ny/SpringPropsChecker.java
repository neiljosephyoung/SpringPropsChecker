package com.ny;


import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

import java.io.*;
import java.nio.file.*;
import java.util.*;

@Mojo(name = "check", defaultPhase = LifecyclePhase.VALIDATE)
public class SpringPropsChecker extends AbstractMojo {

    private static final String BASE_PATH = "src/main/resources/";
    private static final String IGNORE_DEFAULT_FILE = "application.properties";

    /**
     * Main execution.
     * @throws MojoExecutionException MojoExecutionException
     */
    @Override
    public void execute() throws MojoExecutionException {
        try {
            Map<String, Properties> fileProperties = loadAllProperties();
            Set<String> allKeys = collectAllKeys(fileProperties);
            boolean hasMissing = false;

            getLog().info("=== Checking profile property keys ===");

            hasMissing = hasMissingEntries(fileProperties, allKeys, hasMissing);

            if (!hasMissing) {
                getLog().info("All profiles contain consistent keys.");
            } else {
                throw new MojoExecutionException("One or more profile files are missing property keys.");
            }
        } catch (IOException e) {
            throw new MojoExecutionException("Failed to load properties", e);
        }
    }

    /**
     * Handler method for determining if props are missing.
     * @param fileProperties Map
     * @param allKeys Set
     * @param hasMissing boolean
     * @return boolean
     */
    private boolean hasMissingEntries(Map<String, Properties> fileProperties, Set<String> allKeys, boolean hasMissing) {
        for (var entry : fileProperties.entrySet()) {
            String file = entry.getKey();
            Properties props = entry.getValue();

            Set<String> missing = new TreeSet<>(allKeys);
            missing.removeAll(props.stringPropertyNames());

            if (!missing.isEmpty()) {
                hasMissing = true;

                // Log each missing key
                getLog().error("Missing keys in " + file + ":");
                for (String key : missing) {
                    getLog().error("   - " + key);
                }
            }
        }
        return hasMissing;
    }

    /**
     * Load all application property files and properties to a map.
     * @return Map
     * @throws IOException IOException
     */
    private Map<String, Properties> loadAllProperties() throws IOException {
        Map<String, Properties> map = new LinkedHashMap<>();

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(BASE_PATH), "application*.properties")) {
            for (Path path : stream) {
                if (path.getFileName().toString().equals(IGNORE_DEFAULT_FILE)) {
                    getLog().info("Skipping: " + IGNORE_DEFAULT_FILE);
                    continue;
                }
                Properties props = new Properties();
                try (InputStream in = Files.newInputStream(path)) {
                    props.load(in);
                }
                map.put(path.getFileName().toString(), props);
            }
        }

        return map;
    }

    /**
     * Collect properties keys to a set.
     * @param fileProps Map
     * @return Set
     */
    private Set<String> collectAllKeys(Map<String, Properties> fileProps) {
        Set<String> all = new TreeSet<>();
        for (Properties props : fileProps.values()) {
            all.addAll(props.stringPropertyNames());
        }
        return all;
    }
}
