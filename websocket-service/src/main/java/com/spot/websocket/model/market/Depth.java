package com.spot.websocket.model.market;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Depth {
    private String symbol;
    private List<List<String>> asks;
    private List<List<String>> bids;
    private long timestamp;
}
