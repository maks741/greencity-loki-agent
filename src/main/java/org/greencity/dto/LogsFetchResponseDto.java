package org.greencity.dto;

import java.util.Collections;
import java.util.List;

public record LogsFetchResponseDto(
        List<String> logs,
        boolean fetched
) {

    public static LogsFetchResponseDto unfetched() {
        return new LogsFetchResponseDto(
                Collections.emptyList(),
                false
        );
    }
}
