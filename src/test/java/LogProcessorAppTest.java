import main.java.LogProcessorApp;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileWriter;

import static org.junit.jupiter.api.Assertions.assertTrue;

class LogProcessorAppTest {

    @Test
    void testMainWithValidFile() throws Exception {
        // Create a temporary log file
        File tempFile = File.createTempFile("test-log", ".txt");
        try (FileWriter writer = new FileWriter(tempFile)) {
            writer.write("metric=cpu_usage value=85\n");
            writer.write("level=INFO Message processed\n");
            writer.write("request_url=\"/api/v1/users\" response_status=200 response_time_ms=123\n");
        }

        // Run the application
        String[] args = {"--file", tempFile.getAbsolutePath()};
        LogProcessorApp.main(args);

        // Check generated JSON files
        assertTrue(new File("apm.json").exists());
        assertTrue(new File("application.json").exists());
        assertTrue(new File("request.json").exists());
    }
}
