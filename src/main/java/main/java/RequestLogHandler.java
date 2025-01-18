package main.java;
import java.util.*;
import java.util.regex.*;

public class RequestLogHandler implements LogHandler {
    private LogHandler next;
    private Map<String, List<Integer>> responseTimesByRoute = new HashMap<>();
    private Map<String, Map<String, Integer>> statusCodesByRoute = new HashMap<>();

    @Override
    public void setNext(LogHandler nextHandler) {
        this.next = nextHandler;
    }
    @Override
public boolean handle(String logEntry) {
    Pattern pattern = Pattern.compile(
        "request_url=\"([^\"]+)\" response_status=(\\d+) response_time_ms=(\\d+)",
        Pattern.DOTALL
    );
    Matcher matcher = pattern.matcher(logEntry);

    if (matcher.find()) {
        String route = matcher.group(1);
        int statusCode = Integer.parseInt(matcher.group(2));
        int responseTime = Integer.parseInt(matcher.group(3));

        responseTimesByRoute.computeIfAbsent(route, k -> new ArrayList<>()).add(responseTime);

        String statusCategory = statusCode / 100 + "XX";
        statusCodesByRoute.computeIfAbsent(route, k -> new HashMap<>())
            .merge(statusCategory, 1, Integer::sum);
        return true;
    } else if (next != null) {
        next.handle(logEntry);
    }
    return false;
}
    public Map<String, Object> getAggregatedRequestLogs() {
        Map<String, Object> result = new HashMap<>();
        
        for (String route : responseTimesByRoute.keySet()) {
            List<Integer> times = responseTimesByRoute.get(route);
            Collections.sort(times);

            int minTime = times.get(0);
            int maxTime = times.get(times.size() - 1);

            int p50 = times.get(times.size() * 50 / 100);
            int p90 = times.get(times.size() * 90 / 100);
            int p95 = times.get(times.size() * 95 / 100);
            int p99 = times.get(times.size() * 99 / 100);

            Map<String, Object> routeData = new HashMap<>();
            
            // Response time aggregation
            Map<String, Integer> responseTimesData = new HashMap<>();
            responseTimesData.put("min", minTime);
            responseTimesData.put("50_percentile", p50);
            responseTimesData.put("90_percentile", p90);
            responseTimesData.put("95_percentile", p95);
            responseTimesData.put("99_percentile", p99);
            responseTimesData.put("max", maxTime);

            routeData.put("response_times", responseTimesData);

            // Status code counts aggregation
            routeData.put("status_codes", statusCodesByRoute.get(route));

            result.put(route, routeData);
        }

        return result;
    }
}