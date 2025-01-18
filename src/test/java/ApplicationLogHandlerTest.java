import main.java.ApplicationLogHandler;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ApplicationLogHandlerTest {

    @Test
    void testHandleValidLogEntry() {
        ApplicationLogHandler handler = new ApplicationLogHandler();
        String logEntry = "level=INFO Message processed";
        handler.handle(logEntry);

        Map<String, Integer> severityCounts = handler.getSeverityCounts();
        assertTrue(severityCounts.containsKey("INFO"));
        assertEquals(1, severityCounts.get("INFO"));
    }

    @Test
    void testHandleInvalidLogEntry() {
        ApplicationLogHandler handler = new ApplicationLogHandler();
        String logEntry = "Message processed without level";
        handler.handle(logEntry);

        assertTrue(handler.getSeverityCounts().isEmpty());
    }
}
