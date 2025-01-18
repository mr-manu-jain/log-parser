package main.java;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ApplicationLogHandler implements LogHandler {
    private LogHandler next;
    private Map<String, Integer> severityCount = new HashMap<>();

    @Override
    public void setNext(LogHandler nextHandler) {
        this.next = nextHandler;
    }

    @Override
    public boolean handle(String logEntry) {
        Pattern levelPattern = Pattern.compile("level=(\\w+)");
        Matcher matcher = levelPattern.matcher(logEntry);
        if (matcher.find()) {
            String level = matcher.group(1);
            severityCount.put(level, severityCount.getOrDefault(level, 0) + 1);
            return true;
        } else if (next != null) {
            next.handle(logEntry);
        }
        return false;
    }

    public Map<String, Integer> getSeverityCounts() {
        return severityCount;
    }
}