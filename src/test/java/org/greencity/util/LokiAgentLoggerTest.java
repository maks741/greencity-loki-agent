package org.greencity.util;

import org.greencity.constant.EnvVar;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.logging.Handler;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class LokiAgentLoggerTest {

    @Test
    void getLoggerTest() {
        Class<LokiAgentLoggerTest> clazz = LokiAgentLoggerTest.class;
        String expectedLoggingLevelStr = "ALL";
        long expectedAmountOfLoggerHandlers = 1;

        try (MockedStatic<EnvVar> mockedStatic = Mockito.mockStatic(EnvVar.class)) {
            mockedStatic.when(EnvVar::LOGGING_LEVEL)
                    .thenReturn(expectedLoggingLevelStr);

            Logger actualResult = LokiAgentLogger.getLogger(clazz);

            assertEquals(expectedLoggingLevelStr, actualResult.getLevel().getName());
            assertFalse(actualResult.getUseParentHandlers());
            assertEquals(expectedAmountOfLoggerHandlers, Arrays.stream(actualResult.getHandlers()).count());

            Handler handler = Arrays.stream(actualResult.getHandlers()).toList().getFirst();
            assertEquals(expectedLoggingLevelStr, handler.getLevel().getName());
        }
    }

    @Test
    void getLoggerTestWithInvalidLoggingLevel() {
        Class<LokiAgentLoggerTest> clazz = LokiAgentLoggerTest.class;
        String invalidLoggingLevelStr = "invalid logging level";

        try (MockedStatic<EnvVar> mockedStatic = Mockito.mockStatic(EnvVar.class)) {
            mockedStatic.when(EnvVar::LOGGING_LEVEL)
                    .thenReturn(invalidLoggingLevelStr);

            assertThrows(
                    IllegalArgumentException.class,
                    () -> LokiAgentLogger.getLogger(clazz)
            );
        }
    }

}
