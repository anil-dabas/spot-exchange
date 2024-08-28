package com.spot.websocket.model.response;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ApiResponse <T> {
    private boolean success;
    private String msg;
    private T data;
}
