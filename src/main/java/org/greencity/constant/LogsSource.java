package org.greencity.constant;

public enum LogsSource {

    GREENCITY(Environment.GREENCITY_LOGS_URL.value(), "GreenCity"),
    GREENCITY_UBS(Environment.GREENCITY_UBS_LOGS_URL.value(), "GreenCityUBS"),
    GREENCITY_USER(Environment.GREENCITY_USER_LOGS_URL.value(), "GreenCityUser");

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
