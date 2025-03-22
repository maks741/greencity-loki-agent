package org.greencity.entity;

import java.util.List;

public record LokiChunk (
        List<LokiPayload> streams
) {}
