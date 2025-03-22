package org.greencity.service;

import com.maks.test_grafana_plugin.entity.LokiPayload;
import com.maks.test_grafana_plugin.entity.LokiStream;
import com.maks.test_grafana_plugin.helper.RemoveAnsiEscapeCodesFunction;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LogsParser {

    public List<LokiPayload> parseToLokiPayloads(List<String> logLines) {
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
                                    lokiPayloads,
                                    exceptionStackTraceBuilder
                            ));
                            exceptionStackTraceBuilder.setLength(0);
                        }

                        String timestamp = parseToUnixTime(logMatcher.group(1));
                        String level = logMatcher.group(2);
                        String message = logMatcher.group(3);

                        lokiPayloads.add(buildLokiPayload(
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
            List<LokiPayload> lokiPayloads,
            StringBuilder exceptionStackTraceBuilder
    ) {
        String timestamp = timestampOfLastPayload(lokiPayloads);
        String exceptionLoggingLevel = "ERROR";
        String exceptionStackTrace = exceptionStackTraceBuilder.toString();

        return buildLokiPayload(
                timestamp,
                exceptionLoggingLevel,
                exceptionStackTrace
        );
    }

    private LokiPayload buildLokiPayload(
            String timestamp,
            String level,
            String message
    ) {
        List<List<String>> values = valuesForLokiPayload(
                timestamp,
                message
        );

        LokiStream lokiStream = buildLokiStream(level);

        return new LokiPayload(
                lokiStream,
                values,
                timestamp
        );
    }

    private LokiStream buildLokiStream(String level) {
        String jobName = "java-app";
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
        SimpleDateFormat logsDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        logsDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        Date date;
        try {
            date = logsDateFormat.parse(timestamp);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        return String.valueOf(date.getTime() * 1_000_000);
    }
}
