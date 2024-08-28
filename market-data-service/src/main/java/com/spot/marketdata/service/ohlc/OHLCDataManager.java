package com.spot.marketdata.service.ohlc;

import com.spot.marketdata.model.*;
import com.spot.marketdata.model.*;
import com.spot.marketdata.service.DBServices;
import com.spot.marketdata.service.kafka.publisher.KafkaTickerPublisher;
import com.spot.marketdata.service.redis.RedisOhlcPublisher;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.Map;
import java.util.concurrent.*;

@Service
@Slf4j
public class OHLCDataManager {

    private final DataCache dataCache;
    private final OhlcPublisher ohlcPublisher;
    private final RedisOhlcPublisher redisOhlcPublisher;
    private final DBServices dbServices;
    private final KafkaTickerPublisher kafkaTickerPublisher;
    private final ConcurrentHashMap<String, BlockingQueue<Trade>> instrumentQueues = new ConcurrentHashMap<>();
    @Getter
    private final ExecutorService executorService;

    public OHLCDataManager(DataCache dataCache, OhlcPublisher ohlcPublisher, RedisOhlcPublisher redisOhlcPublisher,
                           DBServices dbServices, KafkaTickerPublisher kafkaTickerPublisher) {
        this.dataCache = dataCache;
        this.ohlcPublisher = ohlcPublisher;
        this.redisOhlcPublisher = redisOhlcPublisher;
        this.dbServices = dbServices;
        this.kafkaTickerPublisher = kafkaTickerPublisher;
        this.executorService = Executors.newCachedThreadPool();
        populatePreviousDataThroughDB();
    }

    private void populatePreviousDataThroughDB() {
        /* TODO */
    }

    public void processTrade(Trade trade) {
        instrumentQueues.computeIfAbsent(trade.getSymbol(), k -> {
            BlockingQueue<Trade> queue = new LinkedBlockingQueue<>();
            executorService.submit(() -> processQueue(queue));
            return queue;
        }).add(trade);
    }

    private void processQueue(BlockingQueue<Trade> queue) {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                Trade trade = queue.take();
                updateAndPublishOhlc(trade);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private void updateAndPublishOhlc(Trade trade) {
        Instant tradeTime = Instant.ofEpochMilli(trade.getTimestamp());
        ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(tradeTime, ZoneId.systemDefault());

        log.debug("tradeTime: {} and zoneDateTime : {}", tradeTime,zonedDateTime);

        dataCache.getOhlcDataMap().computeIfAbsent(trade.getSymbol(), k -> new ConcurrentHashMap<>());

        for (Map.Entry<String, Long> entry : TimeIntervals.getIntervals().entrySet()) {
            String intervalKey = entry.getKey();
            LocalDateTime intervalStart = calculateIntervalStart(zonedDateTime, intervalKey);
            long intervalStartEpoch = intervalStart.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

            OHLC ohlcData = dataCache.getOhlcDataMap().get(trade.getSymbol()).
                    computeIfAbsent(intervalKey, k -> new OHLC(trade.getSymbol(),intervalKey,intervalStartEpoch, intervalStartEpoch + (entry.getValue()* 1000L - 1)));
            ohlcData.update(Double.parseDouble(trade.getPrice()), Double.parseDouble(trade.getQuantity()));

            // Print updated OHLC data
            log.debug("Updated OHLC for {} at interval {}: {}", trade.getSymbol(), intervalKey, ohlcData);
            publishOhlcDataDownStream(ohlcData);
        }
    }

    private void publishOhlcDataDownStream(OHLC existingOHLC) {
        // Calculate and publish the Ticker data
        publishTickerData(existingOHLC);

        ohlcPublisher.publishOhlc(existingOHLC);
        redisOhlcPublisher.publishOhlc(existingOHLC);
        dbServices.saveOHLC(existingOHLC);

    }

    private void publishTickerData(OHLC ohlc) {
        // Retrieve the '1d' OHLC data, which is assumed to be up-to-date
        OHLC dailyOHLC = dataCache.getOhlcDataMap().get(ohlc.getSymbol()).get("1d");

        if (dailyOHLC == null) {
            log.warn("No daily OHLC data available for {}", ohlc.getSymbol());
            return;
        }

        // Compute the ticker data
        double lastClose = dbServices.getLastClosePriceForYesterday(ohlc.getSymbol());
        double priceChange = dailyOHLC.getClosePrice() - lastClose;
        double priceChangePercent = (lastClose != 0) ? (priceChange / lastClose) * 100 : 0;

        Ticker ticker = new Ticker(
                ohlc.getSymbol(),
                dailyOHLC.getOpenPrice(),
                dailyOHLC.getHighPrice(),
                dailyOHLC.getClosePrice(),
                dailyOHLC.getLowPrice(),
                priceChange,
                priceChangePercent,
                dailyOHLC.getQuoteAssetVolume(),
                dailyOHLC.getNumberOfTrades(),
                dailyOHLC.getVolume()
        );

        // Publish the ticker data
        kafkaTickerPublisher.publishTicker(ticker);
        dbServices.saveTicker(ticker);
    }

    private LocalDateTime calculateIntervalStart(ZonedDateTime dateTime, String intervalKey) {
        TimeIntervalEnum interval = TimeIntervalEnum.fromKey(intervalKey);
        switch (interval) {
            case ONE_SECOND:
                return dateTime.truncatedTo(ChronoUnit.SECONDS).toLocalDateTime();
            case ONE_MINUTE,THREE_MINUTES,FIVE_MINUTES,FIFTEEN_MINUTES,THIRTY_MINUTES:
                int minute = dateTime.getMinute();
                int durationMinutes = (int) (interval.getSeconds() / 60);
                return dateTime.withMinute(minute - (minute % durationMinutes)).truncatedTo(ChronoUnit.MINUTES).toLocalDateTime();
            case ONE_HOUR,TWO_HOURS,FOUR_HOURS,SIX_HOURS,EIGHT_HOURS,TWELVE_HOURS:
                int hour = dateTime.getHour();
                int durationHours = (int) (interval.getSeconds() / 3600);
                return dateTime.withHour(hour - (hour % durationHours)).truncatedTo(ChronoUnit.HOURS).toLocalDateTime();
            case ONE_DAY,THREE_DAYS:
                return dateTime.truncatedTo(ChronoUnit.DAYS).toLocalDateTime();
            case ONE_WEEK:
                return dateTime.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).truncatedTo(ChronoUnit.DAYS).toLocalDateTime();
            case ONE_MONTH:
                return dateTime.withDayOfMonth(1).truncatedTo(ChronoUnit.DAYS).toLocalDateTime();
            default:
                throw new IllegalArgumentException("Unsupported interval: " + interval);
        }
    }


}
