package org.greencity.constant;

import org.greencity.helper.Environment;

public enum LogsSource {

    GREENCITY(Environment.getenv("GREENCITY_LOGS_URL"), "GreenCity"),
    GREENCITY_UBS(Environment.getenv("GREENCITY_UBS_LOGS_URL"), "GreenCityUBS"),
    GREENCITY_USER(Environment.getenv("GREENCITY_USER_LOGS_URL"), "GreenCityUser");

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
