import main.java.RequestLogHandler;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class RequestLogHandlerTest {

    @Test
    void testHandleValidLogEntry() {
        RequestLogHandler handler = new RequestLogHandler();
        String logEntry = "request_url=\"/api/v1/users\" response_status=200 response_time_ms=123";
        handler.handle(logEntry);

        Map<String, Object> logs = handler.getAggregatedRequestLogs();
        assertTrue(logs.containsKey("/api/v1/users"));
        Map<String, Object> routeData = (Map<String, Object>) logs.get("/api/v1/users");
        Map<String, Integer> responseTimes = (Map<String, Integer>) routeData.get("response_times");

        assertEquals(123, responseTimes.get("min"));
    }

    @Test
    void testHandleInvalidLogEntry() {
        RequestLogHandler handler = new RequestLogHandler();
        String logEntry = "invalid_log_entry";
        handler.handle(logEntry);

        assertTrue(handler.getAggregatedRequestLogs().isEmpty());
    }
}
