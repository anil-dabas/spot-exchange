package com.spot.websocket.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class AccountDto {
    @JsonProperty("e")
    private String eventType;

    @JsonProperty("E")
    private long eventTime;

    @JsonProperty("u")
    private long lastUpdate;

    @JsonProperty("B")
    private List<AccountItemDto> items;

}
