package org.greencity.dto;

public record LogsRequestDto(
        Integer daysOffset,
        Integer lastReceivedLineNumber
) {
}
