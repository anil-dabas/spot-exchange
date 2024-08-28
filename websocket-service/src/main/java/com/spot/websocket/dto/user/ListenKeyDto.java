package com.spot.websocket.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class ListenKeyDto {
    private String listenKey;
}
