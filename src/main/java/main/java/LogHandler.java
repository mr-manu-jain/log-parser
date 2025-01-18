package main.java;
public interface LogHandler {
    void setNext(LogHandler nextHandler);
    boolean handle(String logEntry);
}