package org.greencity.constant;

public enum LogMessage {

    ENV_VAR_NOT_FOUND("Environment variable not found: %s"),

    UNEXPECTED_RESPONSE_FROM_LOKI("Unexpected response status code from Loki: %s; Response: %s"),
    SUCCESSFUL_PUSH_TO_LOKI("Successfully pushed logs for job %s to Loki [%s]"),
    SUCCESSFUL_LOGS_FETCH("Job %s successfully fetched logs from %s"),
    FAILED_LOGS_FETCH("Job %s can not fetch logs from %s; Response: %s"),
    FAILED_LOGS_PUSH("Job %s can not push logs to %s; Response: %s"),
    UNABLE_TO_CONNECT("Job %s can not connect to %s"),
    INVALID_CLIENT_PROTOCOL("Job %s unable to connect due to invalid protocol"),
    UNABLE_TO_EXECUTE_HTTP_REQUEST("Job %s unable to execute HTTP request. Error: %s"),

    STARTING_TO_FETCH_LOGS("Job %s is starting to fetch logs from %s"),
    STARTING_TO_PUSH_LOGS("Job %s is starting to push logs to %s"),
    SUCCESSFULLY_PREPARED_FETCH_LOGS_REQUEST("Job %s successfully prepared request to fetch logs from %s"),
    SUCCESSFULLY_PREPARED_PUSH_LOGS_REQUEST("Job %s successfully prepared request to push logs to %s"),
    EXECUTED_FETCH_LOGS_REQUEST("Job %s executed request to fetch logs from %s and received %s status code"),
    EXECUTED_PUSH_LOGS_REQUEST("Job %s executed request to push logs to %s and received %s status code"),
    UNEXPECTED_RESPONSE_FOR_LOGS_REQUEST("Job %s received unexpected response status code [%s] when fetching logs from %s"),
    AMOUNT_OF_LOG_LINES("Job %s received %s log lines as a response from %s"),

    PARSING_TIMESTAMP("Starting to parse log timestamp %s of %s pattern"),
    PARSED_TIMESTAMP_OFFSET("Parsed log timestamp based of server time zone: %s; After offsetting to UTC: %s"),
    PARSED_UNIX_TIMESTAMP("UTC time %s successfully parsed to %s Unix timestamp");

    private final String message;

    LogMessage(String message) {
        this.message = message;
    }

    public String message(Object... args) {
        return String.format(message, args);
    }
}
