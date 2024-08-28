package com.spot.marketdata.service.ohlc;

import com.spot.marketdata.model.DataCache;
import com.spot.marketdata.model.OHLC;
import com.spot.marketdata.model.Ticker;
import com.spot.marketdata.model.TimeIntervals;
import com.spot.marketdata.service.DBServices;
import com.spot.marketdata.service.kafka.publisher.KafkaTickerPublisher;
import com.spot.marketdata.service.redis.RedisOhlcPublisher;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class OHLCDataPublishScheduler {

    private final OhlcPublisher ohlcPublisher;
    private final RedisOhlcPublisher redisOhlcPublisher;
    private final KafkaTickerPublisher kafkaTickerPublisher;
    private final DBServices dbServices;
    private final DataCache dataCache;
    private final TaskScheduler scheduler;
    private final Map<String, ScheduledFuture<?>> scheduledTasks ;

    public OHLCDataPublishScheduler(OhlcPublisher ohlcPublisher,
                                    RedisOhlcPublisher redisOhlcPublisher, KafkaTickerPublisher kafkaTickerPublisher,
                                    DBServices dbServices, DataCache dataCache) {
        this.ohlcPublisher = ohlcPublisher;
        this.redisOhlcPublisher = redisOhlcPublisher;
        this.kafkaTickerPublisher = kafkaTickerPublisher;
        this.dbServices = dbServices;
        this.dataCache = dataCache;
        this.scheduler = new ConcurrentTaskScheduler();
        this.scheduledTasks = new ConcurrentHashMap<>();
    }

    @PostConstruct
    public void schedulePublishingTasks() {
        TimeIntervals.getIntervals().forEach((intervalKey, seconds) -> {
            long currentSecond = Instant.now().getEpochSecond();
            long delayInSeconds = seconds - (currentSecond % seconds);
            Duration delay = Duration.ofSeconds(delayInSeconds);

            // Create a Date object for the initial delay based on the Duration calculated
            Date firstExecutionTime = Date.from(Instant.now().plus(delay));

            ScheduledFuture<?> scheduledFuture = scheduler.scheduleWithFixedDelay(
                    () -> publishOHLCData(intervalKey),
                    firstExecutionTime,
                    TimeUnit.SECONDS.toMillis(seconds));
            scheduledTasks.put(intervalKey, scheduledFuture);
        });
    }



    private void publishOHLCData(String intervalKey) {
        log.debug("here for interval : {} " ,intervalKey);

        dataCache.getOhlcDataMap().forEach((symbol, ohlcMap) -> {
            // Get the current system time and calculate the interval start and end times based on the current time
            long currentTimeMillis = System.currentTimeMillis();
            long currentIntervalStart = (currentTimeMillis / TimeUnit.SECONDS.toMillis(TimeIntervals.getIntervals().get(intervalKey))) * TimeUnit.SECONDS.toMillis(TimeIntervals.getIntervals().get(intervalKey));
            long nextIntervalStart = currentIntervalStart + TimeUnit.SECONDS.toMillis(TimeIntervals.getIntervals().get(intervalKey));

            OHLC ohlcData = ohlcMap.computeIfPresent(intervalKey, (key, existingOHLC) -> {
               // log.debug("currentMIllis : {}, existingOhlc closeTIme : {}", currentTimeMillis,existingOHLC.getCloseTime());
                if (currentTimeMillis >= existingOHLC.getCloseTime()) {
                    // Reset OHLC for new interval if it's the first trade or past the close time
                    publishOhlcDataDownStream(existingOHLC);
                    return new OHLC(symbol,intervalKey,currentIntervalStart, nextIntervalStart - 1,existingOHLC.getClosePrice());
                }
                return existingOHLC;
            });

        });
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
        log.debug("Last close is : [{}]  dailyOhlc : {}", lastClose,dailyOHLC);

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


}
