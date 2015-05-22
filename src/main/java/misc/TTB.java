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
import eu.verdelhan.ta4j.Order;

import eu.verdelhan.ta4j.Strategy;
import eu.verdelhan.ta4j.TimeSeries;
import eu.verdelhan.ta4j.Tick;
import eu.verdelhan.ta4j.trading.rules.BooleanRule;
import org.joda.time.Period;

public class TTB {

    private static final Twitter TWITTER = TwitterFactory.getSingleton();

    private static final Period TICK_PERIOD = Period.seconds(300);

    private static final int MAX_NB_TICKS = 80;

    private static final ArrayList<Tick> TICKS = new ArrayList<Tick>();

    public static void main(String[] args) throws IOException {
        System.out.println("STARTED");
        Exchange bitstamp = ExchangeFactory.INSTANCE.createExchange(BitstampExchange.class.getName());
        BitstampStreamingConfiguration streamCfg = new BitstampStreamingConfiguration();
        streamCfg.getChannels().add("live_trades");

        // Interested in the public streaming market data feed (no authentication)
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

        return new Strategy(BooleanRule.TRUE, BooleanRule.TRUE);
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
            Tick newTick = new Tick(TICK_PERIOD, now);
            newTick.addTrade(trade.getTradableAmount().doubleValue(), trade.getPrice().doubleValue());
            TICKS.add(newTick);
        } else {
            Tick lastTick = TICKS.get(TICKS.size() - 1);
            if (lastTick.inPeriod(now)) {
                // In the last tick
                lastTick.addTrade(trade.getTradableAmount().doubleValue(), trade.getPrice().doubleValue());
            } else {
                // Out of the last tick
                Tick newTick = new Tick(TICK_PERIOD, now);
                newTick.addTrade(trade.getTradableAmount().doubleValue(), trade.getPrice().doubleValue());
                TICKS.add(newTick);
            }
        }

        if (TICKS.size() > MAX_NB_TICKS) {
            TICKS.remove(0);
        }
    }

    private static void tweet(Order.OrderType orderType) {
        if (orderType != null) {
            try {
                if (orderType == Order.OrderType.BUY) {
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
