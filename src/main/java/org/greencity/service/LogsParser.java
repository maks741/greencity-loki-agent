package org.greencity.service;

import org.greencity.constant.EnvVar;
import org.greencity.constant.LogSource;
import org.greencity.entity.LokiPayload;
import org.greencity.entity.LokiStream;
import org.greencity.helper.RemoveAnsiEscapeCodesFunction;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LogsParser {

    public List<LokiPayload> parseToLokiPayloads(LogSource logSource, List<String> logLines) {
        String logRegex = "\\[(\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2})]\\s(\\w+)\\s(.*)";
        Pattern logPattern = Pattern.compile(logRegex);

        StringBuilder exceptionStackTraceBuilder = new StringBuilder();
        List<LokiPayload> lokiPayloads = new ArrayList<>();

        logLines.stream()
                .map(new RemoveAnsiEscapeCodesFunction())
                .forEach(s -> {
                    Matcher logMatcher = logPattern.matcher(s);

                    // line is either a log or a line from an exception stack trace
                    boolean log = logMatcher.find();

                    if (log) {
                        if (!exceptionStackTraceBuilder.isEmpty()) {
                            lokiPayloads.add(buildExceptionLokiPayload(
                                    logSource,
                                    lokiPayloads,
                                    exceptionStackTraceBuilder
                            ));
                            exceptionStackTraceBuilder.setLength(0);
                        }

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

        return lokiPayloads;
    }

    private LokiPayload buildExceptionLokiPayload(
            LogSource logSource,
            List<LokiPayload> lokiPayloads,
            StringBuilder exceptionStackTraceBuilder
    ) {
        String timestamp = timestampOfLastPayload(lokiPayloads);
        String exceptionLoggingLevel = "ERROR";
        String exceptionStackTrace = exceptionStackTraceBuilder.toString();

        return buildLokiPayload(
                logSource,
                timestamp,
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

    private String timestampOfLastPayload(List<LokiPayload> lokiPayloads) {
        Optional<LokiPayload> last = Optional.of(lokiPayloads.getLast());

        return last.map(LokiPayload::timestamp)
                .orElseThrow(() -> new RuntimeException("Exception is the first log in the app and it's timestamp can not be determined"));
    }

    private String parseToUnixTime(String timestamp) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        LocalDateTime localDateTime = LocalDateTime.parse(timestamp, dateTimeFormatter);

        ZoneId localZone = ZoneId.of(EnvVar.SERVER_TIME_ZONE.value());

        ZonedDateTime localZonedDateTime = localDateTime.atZone(localZone);

        ZonedDateTime utcZonedDateTime = localZonedDateTime.withZoneSameInstant(ZoneOffset.UTC);

        Date date = Date.from(utcZonedDateTime.toInstant());

        return String.valueOf(date.getTime() * 1_000_000);
    }
}
