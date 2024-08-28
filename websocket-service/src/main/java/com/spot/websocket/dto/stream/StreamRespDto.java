package com.spot.websocket.dto.stream;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class StreamRespDto {
    private String result;
    private UUID id;
}
