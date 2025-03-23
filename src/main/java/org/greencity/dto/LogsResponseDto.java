package org.greencity.dto;

import java.util.Collections;
import java.util.List;

public record LogsResponseDto(
        List<String> logs,
        boolean fetched
) {

    public static LogsResponseDto unfetched() {
        return new LogsResponseDto(
                Collections.emptyList(),
                false
        );
    }
}
