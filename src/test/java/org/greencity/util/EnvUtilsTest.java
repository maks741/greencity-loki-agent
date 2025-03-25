package org.greencity.util;

import org.greencity.constant.EnvVar;
import org.greencity.constant.LogMessage;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.lang.reflect.Method;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class EnvUtilsTest {

    @Test
    void verifyEnvironmentVariablesTest() {
        try (MockedStatic<EnvVar> envVarMockedStatic = Mockito.mockStatic(EnvVar.class)) {
            String expectedServerTimeZone = "SERVER_TIME_ZONE";
            String expectedLoggingLevel = "LOGGING_LEVEL";
            String expectedLogTimestampPattern = "LOG_TIMESTAMP_PATTERN";
            String expectedLogRegex = "LOG_REGEX";
            String expectedFetchLogsFrom = "FETCH_LOGS_FROM";
            String expectedSecretKeyHeader = "SECRET_KEY_HEADER";
            String expectedSecretKey = "SECRET_KEY";
            String expectedResponseBodyField = "RESPONSE_BODY_FIELD";
            int expectedLogsDaysOffset = 5;
            String expectedAuthToken = "Bearer AUTH_TOKEN";
            int expectedLokiResponseStatusCode = 200;
            String expectedLokiPushUrl = "LOKI_PUSH_URL";
            String expectedGreencityLogsUrl = "GREENCITY_LOGS_URL";
            String expectedGreencityUbsLogsUrl = "GREENCITY_UBS_LOGS_URL";
            String expectedGreencityUserLogsUrl = "GREENCITY_USER_LOGS_URL";

            envVarMockedStatic.when(EnvVar::SERVER_TIME_ZONE).thenReturn(expectedServerTimeZone);
            envVarMockedStatic.when(EnvVar::LOGGING_LEVEL).thenReturn(expectedLoggingLevel);
            envVarMockedStatic.when(EnvVar::LOG_TIMESTAMP_PATTERN).thenReturn(expectedLogTimestampPattern);
            envVarMockedStatic.when(EnvVar::LOG_REGEX).thenReturn(expectedLogRegex);
            envVarMockedStatic.when(EnvVar::FETCH_LOGS_FROM).thenReturn(expectedFetchLogsFrom);
            envVarMockedStatic.when(EnvVar::SECRET_KEY_HEADER).thenReturn(expectedSecretKeyHeader);
            envVarMockedStatic.when(EnvVar::SECRET_KEY).thenReturn(expectedSecretKey);
            envVarMockedStatic.when(EnvVar::RESPONSE_BODY_FIELD).thenReturn(expectedResponseBodyField);
            envVarMockedStatic.when(EnvVar::LOGS_DAYS_OFFSET).thenReturn(expectedLogsDaysOffset);
            envVarMockedStatic.when(EnvVar::AUTH_TOKEN).thenReturn(expectedAuthToken);
            envVarMockedStatic.when(EnvVar::EXPECTED_LOKI_RESPONSE_STATUS_CODE).thenReturn(expectedLokiResponseStatusCode);
            envVarMockedStatic.when(EnvVar::LOKI_PUSH_URL).thenReturn(expectedLokiPushUrl);
            envVarMockedStatic.when(EnvVar::GREENCITY_LOGS_URL).thenReturn(expectedGreencityLogsUrl);
            envVarMockedStatic.when(EnvVar::GREENCITY_UBS_LOGS_URL).thenReturn(expectedGreencityUbsLogsUrl);
            envVarMockedStatic.when(EnvVar::GREENCITY_USER_LOGS_URL).thenReturn(expectedGreencityUserLogsUrl);

            assertDoesNotThrow(EnvUtils::verifyEnvironmentVariables);
            System.out.println("amount of inter: " + Mockito.mockingDetails(EnvVar.class).getInvocations().size());
            System.out.println("amount of methods: " + Arrays.stream(EnvVar.class.getDeclaredMethods()).filter(method -> method.canAccess(null)).count());

            assertEquals(expectedServerTimeZone, EnvVar.SERVER_TIME_ZONE());
            assertEquals(expectedLoggingLevel, EnvVar.LOGGING_LEVEL());
            assertEquals(expectedLogTimestampPattern, EnvVar.LOG_TIMESTAMP_PATTERN());
            assertEquals(expectedLogRegex, EnvVar.LOG_REGEX());
            assertEquals(expectedFetchLogsFrom, EnvVar.FETCH_LOGS_FROM());
            assertEquals(expectedSecretKeyHeader, EnvVar.SECRET_KEY_HEADER());
            assertEquals(expectedSecretKey, EnvVar.SECRET_KEY());
            assertEquals(expectedResponseBodyField, EnvVar.RESPONSE_BODY_FIELD());
            assertEquals(expectedLogsDaysOffset, EnvVar.LOGS_DAYS_OFFSET());
            assertEquals(expectedAuthToken, EnvVar.AUTH_TOKEN());
            assertEquals(expectedLokiResponseStatusCode, EnvVar.EXPECTED_LOKI_RESPONSE_STATUS_CODE());
            assertEquals(expectedLokiPushUrl, EnvVar.LOKI_PUSH_URL());
            assertEquals(expectedGreencityLogsUrl, EnvVar.GREENCITY_LOGS_URL());
            assertEquals(expectedGreencityUbsLogsUrl, EnvVar.GREENCITY_UBS_LOGS_URL());
            assertEquals(expectedGreencityUserLogsUrl, EnvVar.GREENCITY_USER_LOGS_URL());
        }
    }

    @Test
    void verifyEnvironmentVariablesTestWhenVariableIsNotFound() {
        String expectedExceptionMessage = LogMessage.ENV_VAR_NOT_FOUND.message("SERVER_TIME_ZONE");

        try (MockedStatic<EnvVar> envVarMockedStatic = Mockito.mockStatic(EnvVar.class)) {
            String expectedServerTimeZone = null;
            String expectedLoggingLevel = "LOGGING_LEVEL";
            String expectedLogTimestampPattern = "LOG_TIMESTAMP_PATTERN";
            String expectedLogRegex = "LOG_REGEX";
            String expectedFetchLogsFrom = "FETCH_LOGS_FROM";
            String expectedSecretKeyHeader = "SECRET_KEY_HEADER";
            String expectedSecretKey = "SECRET_KEY";
            String expectedResponseBodyField = "RESPONSE_BODY_FIELD";
            int expectedLogsDaysOffset = 5;
            String expectedAuthToken = "Bearer AUTH_TOKEN";
            int expectedLokiResponseStatusCode = 200;
            String expectedLokiPushUrl = "LOKI_PUSH_URL";
            String expectedGreencityLogsUrl = "GREENCITY_LOGS_URL";
            String expectedGreencityUbsLogsUrl = "GREENCITY_UBS_LOGS_URL";
            String expectedGreencityUserLogsUrl = "GREENCITY_USER_LOGS_URL";

            envVarMockedStatic.when(EnvVar::SERVER_TIME_ZONE).thenReturn(expectedServerTimeZone);
            envVarMockedStatic.when(EnvVar::LOGGING_LEVEL).thenReturn(expectedLoggingLevel);
            envVarMockedStatic.when(EnvVar::LOG_TIMESTAMP_PATTERN).thenReturn(expectedLogTimestampPattern);
            envVarMockedStatic.when(EnvVar::LOG_REGEX).thenReturn(expectedLogRegex);
            envVarMockedStatic.when(EnvVar::FETCH_LOGS_FROM).thenReturn(expectedFetchLogsFrom);
            envVarMockedStatic.when(EnvVar::SECRET_KEY_HEADER).thenReturn(expectedSecretKeyHeader);
            envVarMockedStatic.when(EnvVar::SECRET_KEY).thenReturn(expectedSecretKey);
            envVarMockedStatic.when(EnvVar::RESPONSE_BODY_FIELD).thenReturn(expectedResponseBodyField);
            envVarMockedStatic.when(EnvVar::LOGS_DAYS_OFFSET).thenReturn(expectedLogsDaysOffset);
            envVarMockedStatic.when(EnvVar::AUTH_TOKEN).thenReturn(expectedAuthToken);
            envVarMockedStatic.when(EnvVar::EXPECTED_LOKI_RESPONSE_STATUS_CODE).thenReturn(expectedLokiResponseStatusCode);
            envVarMockedStatic.when(EnvVar::LOKI_PUSH_URL).thenReturn(expectedLokiPushUrl);
            envVarMockedStatic.when(EnvVar::GREENCITY_LOGS_URL).thenReturn(expectedGreencityLogsUrl);
            envVarMockedStatic.when(EnvVar::GREENCITY_UBS_LOGS_URL).thenReturn(expectedGreencityUbsLogsUrl);
            envVarMockedStatic.when(EnvVar::GREENCITY_USER_LOGS_URL).thenReturn(expectedGreencityUserLogsUrl);

            RuntimeException runtimeException = assertThrows(
                    RuntimeException.class,
                    EnvUtils::verifyEnvironmentVariables
            );
            assertEquals(expectedExceptionMessage, runtimeException.getMessage());
        }
    }
}
