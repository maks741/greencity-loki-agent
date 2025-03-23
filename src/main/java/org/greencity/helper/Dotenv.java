package org.greencity.helper;

import org.greencity.constant.EnvVar;
import org.greencity.constant.LogMessage;

public class Dotenv {

    public static void verifyEnvironmentVariables() {
        for (EnvVar envVar : EnvVar.values()) {
            if (envVar.value() == null) {
                throw new RuntimeException(
                        LogMessage.ENV_VAR_NOT_FOUND.message(envVar.name())
                );
            }
        }
    }

}
