package org.greencity.constant;

public enum LogMessage {

    UNEXPECTED_RESPONSE_FROM_LOKI("Unexpected response status code from Loki: %s; Message: %s"),
    SUCCESSFUL_PUSH_TO_LOKI("Successfully pushed logs for job %s to Loki [%s]"),
    SUCCESSFUL_LOGS_FETCH("Successfully fetched logs for job %s from url: %s"),
    FAILED_LOGS_FETCH("Job %s can not fetch logs from  %s; Response: %s"),
    UNABLE_TO_CONNECT("Job %s can not connect to %s");

    private final String message;

    LogMessage(String message) {
        this.message = message;
    }

    public String message(Object... args) {
        return String.format(message, args);
    }
}
