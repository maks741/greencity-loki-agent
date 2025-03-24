package org.greencity.dto;

import java.util.List;

public record LogsResponseDto(
        List<String> logs
) {
}
