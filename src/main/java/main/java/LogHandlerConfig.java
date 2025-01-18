package main.java;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.nio.file.*;

public class LogHandlerConfig {
    private final String outputDirectory;
    private final ObjectMapper objectMapper;

    public LogHandlerConfig() {
        this(".");  // Default to current directory
    }

    public LogHandlerConfig(String outputDirectory) {
        this.outputDirectory = outputDirectory;
        this.objectMapper = configureObjectMapper();
    }

    private ObjectMapper configureObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        return mapper;
    }

    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    public String getOutputPath(String filename) {
        return Paths.get(outputDirectory, filename).toString();
    }

    public String getOutputDirectory() {
        return outputDirectory;
    }

    public String getErrorLogPath() {
        return Paths.get(outputDirectory,"unableToProcess.json").toString();
    }
}