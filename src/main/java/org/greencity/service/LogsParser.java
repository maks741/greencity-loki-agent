package org.greencity.service;

import org.greencity.constant.EnvVar;
import org.greencity.constant.LogMessage;
import org.greencity.constant.LogSource;
import org.greencity.entity.LokiPayload;
import org.greencity.entity.LokiStream;
import org.greencity.util.LokiAgentLogger;
import org.greencity.util.RemoveAnsiEscapeCodesFunction;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LogsParser {

    private static final String LOG_REGEX = "\\[(\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2})]\\s(\\w+)\\s(.*)";
    private static final Pattern LOG_PATTERN = Pattern.compile(LOG_REGEX);
    private static final Logger log = LokiAgentLogger.getLogger(LogsParser.class);
    private static String lastLogUnixTimestamp = "";

    public List<LokiPayload> parseToLokiPayloads(LogSource logSource, List<String> logLines) {
        StringBuilder exceptionStackTraceBuilder = new StringBuilder();
        List<LokiPayload> lokiPayloads = new ArrayList<>();

        logLines.stream()
                .map(new RemoveAnsiEscapeCodesFunction())
                .forEach(s -> {
                    Matcher logMatcher = LOG_PATTERN.matcher(s);

                    // line is either a log or a line from an exception stack trace
                    boolean log = logMatcher.find();

                    if (log) {
                        flushExceptionStackTraceBuilder(exceptionStackTraceBuilder, logSource, lokiPayloads);

                        String timestamp = parseToUnixTime(logMatcher.group(1));
                        String level = logMatcher.group(2);
                        String message = logMatcher.group(3);

                        lokiPayloads.add(buildLokiPayload(
                                logSource,
                                timestamp,
                                level,
                                message
                        ));
                    } else {
                        exceptionStackTraceBuilder.append(s).append("\n");
                    }
                });

        flushExceptionStackTraceBuilder(exceptionStackTraceBuilder, logSource, lokiPayloads);

        return lokiPayloads;
    }

    private void flushExceptionStackTraceBuilder(StringBuilder exceptionStackTraceBuilder, LogSource logSource, List<LokiPayload> lokiPayloads) {
        if (exceptionStackTraceBuilder.isEmpty()) {
            return;
        }

        lokiPayloads.add(buildExceptionLokiPayload(
                logSource,
                exceptionStackTraceBuilder
        ));
        exceptionStackTraceBuilder.setLength(0);
    }

    private LokiPayload buildExceptionLokiPayload(
            LogSource logSource,
            StringBuilder exceptionStackTraceBuilder
    ) {
        String exceptionLoggingLevel = "ERROR";
        String exceptionStackTrace = exceptionStackTraceBuilder.toString();

        return buildLokiPayload(
                logSource,
                lastLogUnixTimestamp,
                exceptionLoggingLevel,
                exceptionStackTrace
        );
    }

    private LokiPayload buildLokiPayload(
            LogSource logSource,
            String timestamp,
            String level,
            String message
    ) {
        lastLogUnixTimestamp = timestamp;

        List<List<String>> values = valuesForLokiPayload(
                timestamp,
                message
        );

        LokiStream lokiStream = buildLokiStream(logSource, level);

        return new LokiPayload(
                lokiStream,
                values,
                timestamp
        );
    }

    private LokiStream buildLokiStream(LogSource logSource, String level) {
        String jobName = logSource.jobName();
        return new LokiStream(
                jobName,
                level
        );
    }

    private List<List<String>> valuesForLokiPayload(
            String timestamp,
            String message
    ) {
        return List.of(
                List.of(
                        timestamp,
                        message
                )
        );
    }

    private String parseToUnixTime(String timestamp) {
        String logTimestampPattern = "yyyy-MM-dd HH:mm:ss";
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(logTimestampPattern);

        log.finest(LogMessage.PARSING_TIMESTAMP.message(timestamp, logTimestampPattern));

        LocalDateTime localDateTime = LocalDateTime.parse(timestamp, dateTimeFormatter);

        ZoneId localZone = ZoneId.of(EnvVar.SERVER_TIME_ZONE());

        ZonedDateTime localZonedDateTime = localDateTime.atZone(localZone);
        ZonedDateTime utcZonedDateTime = localZonedDateTime.withZoneSameInstant(ZoneOffset.UTC);

        log.finest(LogMessage.PARSED_TIMESTAMP_OFFSET.message(localZonedDateTime, utcZonedDateTime));

        Instant instant = utcZonedDateTime.toInstant();
        String unixTimeNanos = instantToNanosString(instant);

        log.finest(LogMessage.PARSED_UNIX_TIMESTAMP.message(utcZonedDateTime, unixTimeNanos));

        return unixTimeNanos;
    }

    private String instantToNanosString(Instant instant) {
        return String.valueOf(instant.toEpochMilli() * 1_000_000);
    }
}
