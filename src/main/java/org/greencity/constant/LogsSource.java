package org.greencity.constant;

public enum LogsSource {

    GREENCITY("http://192.168.0.170:8080/logs", "GreenCity"),
    GREENCITY_UBS("http://192.168.0.170:8080/logs", "GreenCityUBS"),
    GREENCITY_USER("http://192.168.0.170:8080/logs", "GreenCityUser");

    private final String logsUrl;
    private final String jobName;

    LogsSource(String logsUrl, String jobName) {
        this.logsUrl = logsUrl;
        this.jobName = jobName;
    }

    public String logsUrl() {
        return logsUrl;
    }

    public String jobName() {
        return jobName;
    }
}
