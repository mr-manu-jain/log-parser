package main.java;
import java.util.*;
import java.util.regex.*;

public class APMLogHandler implements LogHandler {
    private LogHandler next;
    private Map<String, List<Double>> apmMetrics = new HashMap<>();

    @Override
    public void setNext(LogHandler nextHandler) {
        this.next = nextHandler;
    }

    @Override
    public boolean handle(String logEntry) {
    Pattern pattern = Pattern.compile("metric=(\\w+).*?value=(\\d+(\\.\\d+)?)", Pattern.DOTALL);    
    // Pattern pattern = Pattern.compile("metric=(\\w+) .* value=(\\d+)"); MANUJAIN DEC3
    Matcher matcher = pattern.matcher(logEntry);
    if (matcher.find()) {
        String metric = matcher.group(1);
        double value = Double.parseDouble(matcher.group(2));
        apmMetrics.computeIfAbsent(metric, k -> new ArrayList<>()).add(value);
        return true;
    } else if (next != null) {
        return next.handle(logEntry);
    }
    return false;
}

public Map<String, Map<String, Double>> getAggregatedMetrics() {
        Map<String, Map<String, Double>> result = new HashMap<>();
        for (String metric : apmMetrics.keySet()) {
            List<Double> values = apmMetrics.get(metric);
            Collections.sort(values);

            double min = values.get(0);
            double max = values.get(values.size() - 1);
            double median = values.size() % 2 == 0 ?
                (values.get(values.size() / 2 - 1) + values.get(values.size() / 2)) / 2 :
                values.get(values.size() / 2);
            double avg = values.stream().mapToDouble(Double::doubleValue).average().orElse(0);

            Map<String, Double> stats = new HashMap<>();
            stats.put("min", min);
            stats.put("median", median);
            stats.put("average", avg);
            stats.put("max", max);

            result.put(metric, stats);
        }
        return result;
    }
}