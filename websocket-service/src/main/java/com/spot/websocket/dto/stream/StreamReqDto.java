package com.spot.websocket.dto.stream;

import lombok.Data;

import java.util.List;

@Data
public class StreamReqDto {
    private String method;
    private List<String> params;
}
