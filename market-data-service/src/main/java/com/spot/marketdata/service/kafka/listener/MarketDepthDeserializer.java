package com.spot.marketdata.service.kafka.listener;
import com.spot.marketdata.model.MarketDepth;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.TreeMap;

public class MarketDepthDeserializer extends JsonDeserializer<MarketDepth> {

    @Override
    public MarketDepth deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        JsonNode node = jp.getCodec().readTree(jp);
        String symbol = node.get("symbol").asText();
        long timestamp = node.get("timestamp").asLong();

        TreeMap<Double, String> asks = convertToTreeMap(node.get("asks"), false);
        TreeMap<Double, String> bids = convertToTreeMap(node.get("bids"), true);

        MarketDepth marketDepth = new MarketDepth(symbol);
        marketDepth.setAsks(asks);
        marketDepth.setBids(bids);
        marketDepth.setTimestamp(timestamp);

        return marketDepth;
    }

    private TreeMap<Double, String> convertToTreeMap(JsonNode arrayNode, boolean reverseOrder) {
        TreeMap<Double, String> map = new TreeMap<>(reverseOrder ? Collections.reverseOrder() : Comparator.naturalOrder());
        if (arrayNode != null) {
            for (JsonNode entryNode : arrayNode) {
                Double price = Double.parseDouble(entryNode.get(0).asText());
                String quantity = entryNode.get(1).asText();
                map.put(price, quantity);
            }
        }
        return map;
    }
}
