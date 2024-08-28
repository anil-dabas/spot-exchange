package com.spot.marketdata.service.depth;


import com.spot.marketdata.model.DataCache;
import com.spot.marketdata.model.MarketDepth;
import com.spot.marketdata.service.DBServices;
import lombok.Getter;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@Component
public class DepthDataHandler {
    private final Map<String, MarketDepth> marketDepthBySymbol;
    private final DBServices dbServices;
    private final DataCache dataCache;

    public DepthDataHandler(DBServices dbServices, DataCache dataCache) {
        this.dbServices = dbServices;
        this.dataCache = dataCache;
        marketDepthBySymbol = new ConcurrentHashMap<>();
    }

    @Async
    public void handleNewDepth(MarketDepth newDepth) {
        MarketDepth existingDepth = marketDepthBySymbol.computeIfAbsent(newDepth.getSymbol(), MarketDepth::new);

        updateDepth(existingDepth.getAsks(), newDepth.getAsks());
        updateDepth(existingDepth.getBids(), newDepth.getBids());
        existingDepth.setTimestamp(newDepth.getTimestamp());

        // Set the updated TreeMap to trigger serialization
        existingDepth.setAsks(existingDepth.getAsks());
        existingDepth.setBids(existingDepth.getBids());

        dataCache.getDepthCache().put(existingDepth.getSymbol(), existingDepth);
        dbServices.saveMarketDepth(existingDepth);
    }

    private void updateDepth(TreeMap<Double, String> existingMap, TreeMap<Double, String> newMap) {
        newMap.forEach((price, quantity) -> {
            if (quantity.equals("0")) {  // Assuming quantity "0" means removal
                existingMap.remove(price);
            } else {
                existingMap.put(price, quantity);
            }
        });
    }
}
