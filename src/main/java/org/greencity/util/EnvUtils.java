package org.greencity.util;

import org.greencity.constant.EnvVar;
import org.greencity.constant.LogMessage;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class EnvUtils {

    public static void verifyEnvironmentVariables() {
        Method[] methods = EnvVar.class.getDeclaredMethods();

        for (Method method : methods) {
            try {
                Object invocationResult = method.invoke(null);

                if (invocationResult == null) {
                    throw new RuntimeException(
                            LogMessage.ENV_VAR_NOT_FOUND.message(method.getName())
                    );
                }
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
