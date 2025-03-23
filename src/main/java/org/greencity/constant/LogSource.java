package org.greencity.constant;

public enum LogSource {

    GREENCITY(EnvVar.GREENCITY_LOGS_URL.value(), "GreenCity"),
    GREENCITY_UBS(EnvVar.GREENCITY_UBS_LOGS_URL.value(), "GreenCity UBS"),
    GREENCITY_USER(EnvVar.GREENCITY_USER_LOGS_URL.value(), "GreenCity User");

    private final String logsUrl;
    private final String jobName;

    LogSource(String logsUrl, String jobName) {
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
