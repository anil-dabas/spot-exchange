package com.spot.marketdata.model;

import java.io.IOException;
import java.util.Collections;
import java.util.TreeMap;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class MarketDepth {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String symbol;
    @Transient  // TreeMap is not directly persistable, thus marked as transient
    private TreeMap<Double, String> asks;
    @Transient  // Use descendingMap() to sort bids in descending order
    private TreeMap<Double, String> bids;
    private long timestamp;

    @Lob
    @JsonIgnore
    private String asksJson;

    @Lob
    @JsonIgnore
    private String bidsJson;

    public MarketDepth(String symbol) {
        this.symbol = symbol;
        this.asks = new TreeMap<>();
        this.bids = new TreeMap<>(Collections.reverseOrder()); // Bids in descending order
    }

    public void setAsks(TreeMap<Double, String> asks) {
        this.asks = asks;
        this.asksJson = serializeMap(asks);
    }

    public void setBids(TreeMap<Double, String> bids) {
        this.bids = bids;
        this.bidsJson = serializeMap(bids);
    }

    private String serializeMap(TreeMap<Double, String> map) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize map", e);
        }
    }

    public TreeMap<Double, String> getAsks() {
        if (asks == null && asksJson != null) {
            asks = deserializeMap(asksJson);
        }
        return asks;
    }

    public TreeMap<Double, String> getBids() {
        if (bids == null && bidsJson != null) {
            bids = deserializeMap(bidsJson);
        }
        return bids;
    }

    private TreeMap<Double, String> deserializeMap(String json) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(json, new TypeReference<TreeMap<Double, String>>() {});
        } catch (IOException e) {
            throw new RuntimeException("Failed to deserialize map", e);
        }
    }
}
