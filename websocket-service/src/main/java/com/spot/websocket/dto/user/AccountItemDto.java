package com.spot.websocket.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class AccountItemDto {

    @JsonProperty("a")
    private String assetCode;

    @JsonProperty("f")
    private String free;

    @JsonProperty("l")
    private String locked;

    @JsonProperty("fz")
    private String freeze;

    @JsonProperty("w")
    private String withdrawing;

}
