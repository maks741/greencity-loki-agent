package org.greencity.util;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RemoveAnsiEscapeCodesFunctionTest {

    @ParameterizedTest
    @MethodSource("applyTestArguments")
    void applyTest(String input, String expectedResult) {
        RemoveAnsiEscapeCodesFunction removeAnsiEscapeCodesFunction = new RemoveAnsiEscapeCodesFunction();

        String actualResult = removeAnsiEscapeCodesFunction.apply(input);

        assertEquals(expectedResult, actualResult);
    }

    static Stream<Arguments> applyTestArguments() {
        return Stream.of(
                Arguments.of("[2025-03-04 19:05:33] \u001B[34mINFO\u001B[0;39m \u001B[36mo.h.validator.internal.util.Version     \u001B[0;39m \u001B[35m:21  \u001B[0;39m HV000001: Hibernate Validator 8.0.1.Final",
                        "[2025-03-04 19:05:33] INFO o.h.validator.internal.util.Version      :21   HV000001: Hibernate Validator 8.0.1.Final"),
                Arguments.of("[2025-03-04 19:05:33] \u001B[34mINFO\u001B[0;39m \u001B[36mgreencity.GreenCityApplication          \u001B[0;39m \u001B[35m:50  \u001B[0;39m Starting GreenCityApplication using Java 21.0.6 with PID 26390 (/home/maks/me/programming/greencity/GreenCity/core/target/classes started by maks in /home/maks/me/programming/greencity/GreenCity)",
                        "[2025-03-04 19:05:33] INFO greencity.GreenCityApplication           :50   Starting GreenCityApplication using Java 21.0.6 with PID 26390 (/home/maks/me/programming/greencity/GreenCity/core/target/classes started by maks in /home/maks/me/programming/greencity/GreenCity)"),
                Arguments.of("[2025-03-04 19:05:33] \u001B[34mINFO\u001B[0;39m \u001B[36mgreencity.GreenCityApplication          \u001B[0;39m \u001B[35m:660 \u001B[0;39m The following 1 profile is active: \"dev\"",
                        "[2025-03-04 19:05:33] INFO greencity.GreenCityApplication           :660  The following 1 profile is active: \"dev\""),
                Arguments.of("[2025-03-04 19:05:33] \u001B[34mINFO\u001B[0;39m \u001B[36m.e.DevToolsPropertyDefaultsPostProcessor\u001B[0;39m \u001B[35m:252 \u001B[0;39m Devtools property defaults active! Set 'spring.devtools.add-properties' to 'false' to disable",
                        "[2025-03-04 19:05:33] INFO .e.DevToolsPropertyDefaultsPostProcessor :252  Devtools property defaults active! Set 'spring.devtools.add-properties' to 'false' to disable"),
                Arguments.of("[2025-03-04 19:05:34] \u001B[34mINFO\u001B[0;39m \u001B[36m.s.d.r.c.RepositoryConfigurationDelegate\u001B[0;39m \u001B[35m:208 \u001B[0;39m Finished Spring Data repository scanning in 142 ms. Found 42 JPA repository interfaces.",
                        "[2025-03-04 19:05:34] INFO .s.d.r.c.RepositoryConfigurationDelegate :208  Finished Spring Data repository scanning in 142 ms. Found 42 JPA repository interfaces."),
                Arguments.of("[2025-03-04 19:05:37] \u001B[34mINFO\u001B[0;39m \u001B[36mo.hibernate.jpa.internal.util.LogHelper  \u001B[0;39m \u001B[35m:31  \u001B[0;39m HHH000204: Processing PersistenceUnitInfo [name: default]",
                        "[2025-03-04 19:05:37] INFO o.hibernate.jpa.internal.util.LogHelper   :31   HHH000204: Processing PersistenceUnitInfo [name: default]")
        );
    }


}
