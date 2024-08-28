package com.spot.websocket.model.user;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AccountItem {
    @JsonProperty("asset_code")
    private String assetCode;
    private String free;
    private String locked;
    private String freeze;
    private String withdrawing;
}
