package misc;

import java.io.IOException;
import java.util.ArrayList;

import org.joda.time.DateTime;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

import com.xeiam.xchange.Exchange;
import com.xeiam.xchange.ExchangeFactory;
import com.xeiam.xchange.bitstamp.BitstampExchange;
import com.xeiam.xchange.bitstamp.service.streaming.BitstampStreamingConfiguration;
import com.xeiam.xchange.dto.marketdata.Trade;
import com.xeiam.xchange.service.streaming.ExchangeEvent;
import com.xeiam.xchange.service.streaming.StreamingExchangeService;

import eu.verdelhan.ta4j.OperationType;
import eu.verdelhan.ta4j.Strategy;
import eu.verdelhan.ta4j.TimeSeries;
import eu.verdelhan.ta4j.indicators.oscillators.StochasticOscillatorDIndicator;
import eu.verdelhan.ta4j.indicators.oscillators.StochasticOscillatorKIndicator;
import eu.verdelhan.ta4j.indicators.simple.ClosePriceIndicator;
import eu.verdelhan.ta4j.indicators.trackers.EMAIndicator;
import eu.verdelhan.ta4j.indicators.trackers.MACDIndicator;
import eu.verdelhan.ta4j.strategies.AlwaysOperateStrategy;
import eu.verdelhan.ta4j.strategies.CombinedEntryAndExitStrategy;
import eu.verdelhan.ta4j.strategies.IndicatorOverIndicatorStrategy;
import eu.verdelhan.ta4j.strategies.ResistanceStrategy;
import eu.verdelhan.ta4j.strategies.SupportStrategy;
import eu.verdelhan.ta4j.Tick;

public class TTB {

    private static Twitter TWITTER = TwitterFactory.getSingleton();

    private static int TICK_PERIOD = 300;

    private static int MAX_NB_TICKS = 80;

    private static ArrayList<Tick> TICKS = new ArrayList<Tick>();

    public static void main(String[] args) throws IOException {
        System.out.println("STARTED");
        Exchange bitstamp = ExchangeFactory.INSTANCE.createExchange(BitstampExchange.class.getName());
        BitstampStreamingConfiguration streamCfg = new BitstampStreamingConfiguration();
        streamCfg.getChannels().add("live_trades");

        // Interested in the public streaming market data feed (no
        // authentication)
        StreamingExchangeService streamService = bitstamp.getStreamingExchangeService(streamCfg);

        streamService.connect();
        System.out.println("CONNECTED");
        runBot(streamService);
        streamService.disconnect();
    }

    private static Strategy buildStrategy(TimeSeries series) {
        if (series == null) {
            throw new IllegalArgumentException("Series cannot be null");
        }

        ClosePriceIndicator closePrice = new ClosePriceIndicator(series);
        EMAIndicator shortEma = new EMAIndicator(closePrice, 9);
        EMAIndicator longEma = new EMAIndicator(closePrice, 26);

        IndicatorOverIndicatorStrategy shortEmaAboveLongEma = new IndicatorOverIndicatorStrategy(longEma, shortEma);

        StochasticOscillatorKIndicator stochasticOscillK = new StochasticOscillatorKIndicator(series, 14);
        StochasticOscillatorDIndicator stochasticOscillD = new StochasticOscillatorDIndicator(stochasticOscillK);

        SupportStrategy support20 = new SupportStrategy(stochasticOscillK, new AlwaysOperateStrategy().opposite(), 20);
        ResistanceStrategy resist80 = new ResistanceStrategy(stochasticOscillK, new AlwaysOperateStrategy().opposite(),
                80);

        MACDIndicator macd = new MACDIndicator(closePrice, 9, 26);
        EMAIndicator emaMacd = new EMAIndicator(macd, 18);

        IndicatorOverIndicatorStrategy macdAboveSignaLine = new IndicatorOverIndicatorStrategy(emaMacd, macd);

        return shortEmaAboveLongEma.and(new CombinedEntryAndExitStrategy(support20, resist80)).and(macdAboveSignaLine);
    }

    private static void runBot(StreamingExchangeService stream) throws IOException {

        try {
            for (;;) {
                ExchangeEvent evt = stream.getNextEvent();
                switch (evt.getEventType()) {
                case TRADE:
                    System.out.println("NEW TRADE");
                    addTransactionToTicks((Trade) evt.getPayload());
                    TimeSeries series = new TimeSeries(TICKS);
                    Strategy strategy = buildStrategy(series);
                    boolean buy = strategy.shouldEnter(series.getEnd());
                    boolean sell = strategy.shouldExit(series.getEnd());
                    if (buy && !sell) {
                        System.out.println("BUY");
                        // tweet(OperationType.BUY);
                    } else if (!buy && sell) {
                        System.out.println("SELL");
                        // tweet(OperationType.SELL);
                    }
                    break;
                default:
                    break;
                }

            }

        } catch (InterruptedException e) {
            e.printStackTrace(System.err);
        }
    }

    private static void addTransactionToTicks(Trade trade) {
        DateTime now = new DateTime();
        if (TICKS.isEmpty()) {
            // No tick yet
            Tick newTick = new Tick(now, now.plusSeconds(TICK_PERIOD));
            newTick.addTrade(trade.getTradableAmount().doubleValue(), trade.getPrice().doubleValue());
            TICKS.add(newTick);
        } else {
            Tick lastTick = TICKS.get(TICKS.size() - 1);
            if (lastTick.inPeriod(now)) {
                // In the last tick
                lastTick.addTrade(trade.getTradableAmount().doubleValue(), trade.getPrice().doubleValue());
            } else {
                // Out of the last tick
                Tick newTick = new Tick(now, now.plusSeconds(TICK_PERIOD));
                newTick.addTrade(trade.getTradableAmount().doubleValue(), trade.getPrice().doubleValue());
                TICKS.add(newTick);
            }
        }

        if (TICKS.size() > MAX_NB_TICKS) {
            TICKS.remove(0);
        }
    }

    private static void tweet(OperationType operationType) {
        if (operationType != null) {
            try {
                if (operationType == OperationType.BUY) {
                    TWITTER.updateStatus("Buy signal on #Bitstamp! #bitcoin #btc /cc @MarcdeVerdelhan");
                } else {
                    TWITTER.updateStatus("Sell signal on #Bitstamp! #bitcoin #btc /cc @MarcdeVerdelhan");
                }
            } catch (TwitterException te) {
                te.printStackTrace();
            }
        }
    }
}
