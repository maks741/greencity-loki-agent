package org.greencity.entity;

import java.util.List;

public record LokiPayload (
        LokiStream stream,
        List<List<String>> values,
        String timestamp
) {}
