package com.spot.websocket.model.user;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Account {
    @JsonProperty("user_id")
    private String userId;

    private List<AccountItem> assets;
}
