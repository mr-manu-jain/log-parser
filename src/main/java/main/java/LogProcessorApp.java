package main.java;
// import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.logging.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import java.io.*;
import java.nio.file.*;


public class LogProcessorApp {
    private static final Logger logger;
    
    static {
    logger = Logger.getLogger(LogProcessorApp.class.getName());
    logger.setLevel(Level.ALL);

    }
    private final LogHandlerConfig config;
    private final List<String> unprocessedLines;
    private final APMLogHandler apmLogHandler;
    private final ApplicationLogHandler appLogHandler;
    private final RequestLogHandler requestLogHandler;

    public LogProcessorApp() {
        this.config = new LogHandlerConfig();
        this.unprocessedLines = new ArrayList<>();
        this.apmLogHandler = new APMLogHandler();
        this.appLogHandler = new ApplicationLogHandler();
        this.requestLogHandler = new RequestLogHandler();
        
        // Chain setup
        apmLogHandler.setNext(appLogHandler);
        appLogHandler.setNext(requestLogHandler);
    }

    public void processLogs(String filename) {
        try {
            validateInputFile(filename);
            processLogFile(filename);
            writeOutputFiles();
            logger.log(Level.INFO, "Log processing completed successfully");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error processing logs: " + e.getMessage(), e);
            System.exit(1);
        }
    }

    private void validateInputFile(String filename) throws IllegalArgumentException {
        if (!Files.exists(Paths.get(filename))) {
            throw new IllegalArgumentException("Input file does not exist: " + filename);
        }
    }

    private void processLogFile(String filename) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                try {
                    processLogLine(line);
                } catch (Exception e) {
                    logger.log(Level.WARNING,"Skipping log line: " + line, e);
                }
            }
        }
    }

    private void processLogLine(String line) {
        if (line != null && !line.trim().isEmpty()) {
            boolean processed = false;
            try {
                // Add a boolean return to LogHandler interface
                processed = apmLogHandler.handle(line.trim());
                if (!processed) {
                    unprocessedLines.add(line);
                }
            } catch (Exception e) {
                logger.log(Level.WARNING,"Error processing line: " + line, e);
                unprocessedLines.add(line);
            }
        }
    }

    private void writeOutputFiles() throws IOException {
        final ObjectMapper mapper = config.getObjectMapper();
        
        // Write APM logs
        mapper.writerWithDefaultPrettyPrinter()
              .writeValue(new File(config.getOutputPath("apm.json")), 
                         apmLogHandler.getAggregatedMetrics());

        // Write Application logs
        mapper.writerWithDefaultPrettyPrinter()
              .writeValue(new File(config.getOutputPath("application.json")), 
                         appLogHandler.getSeverityCounts());

        // Write Request logs
        mapper.writerWithDefaultPrettyPrinter()
              .writeValue(new File(config.getOutputPath("request.json")), 
                         requestLogHandler.getAggregatedRequestLogs());

        // Write unprocessed lines
        Map<String, List<String>> errorOutput = new HashMap<>();
        errorOutput.put("unprocessedLines", unprocessedLines);
        mapper.writerWithDefaultPrettyPrinter()
              .writeValue(new File(config.getErrorLogPath()), errorOutput);
    }

    public static void main(String[] args) {
        if (args.length < 2 || !args[0].equals("--file")) {
            logger.log(Level.SEVERE,"Usage: --file <filename.txt>");
            System.exit(1);
        }

        LogProcessorApp app = new LogProcessorApp();
        app.processLogs(args[1]);
    }
}