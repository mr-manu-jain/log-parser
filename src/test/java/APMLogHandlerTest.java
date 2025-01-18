import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import main.java.APMLogHandler;

class APMLogHandlerTest {

    @Test
    void testHandleValidLogEntry() {
        APMLogHandler handler = new APMLogHandler();
        String logEntry = "metric=cpu_usage some_text value=85"; // Updated log entry to match the regex pattern
        handler.handle(logEntry);

        Map<String, Map<String, Double>> metrics = handler.getAggregatedMetrics();
        Assertions.assertTrue(metrics.containsKey("cpu_usage"));
        assertEquals(85.0, metrics.get("cpu_usage").get("average"));
    }

    @Test
    void testHandleInvalidLogEntry() {
        APMLogHandler handler = new APMLogHandler();
        String logEntry = "invalid_log_entry";
        handler.handle(logEntry);

        assertTrue(handler.getAggregatedMetrics().isEmpty());
    }
}
