package misc;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.joda.time.DateTime;
import org.joda.time.Instant;

import au.com.bytecode.opencsv.CSVReader;
import eu.verdelhan.ta4j.Tick;
import eu.verdelhan.ta4j.TimeSeries;
import org.joda.time.Period;

public class Loader {
	
    /**
     * @return a time series from Bitstamp (bitcoin exchange) trades
     * @param timeframe time frame (in seconds)
     */
    public static TimeSeries loadSeries(int timeframe) {

        // Reading all lines of the CSV file
        InputStream stream = Loader.class.getClassLoader().getResourceAsStream("misc/bitstamp_from_20140201.csv");
        CSVReader csvReader = null;
        List<String[]> lines = null;
        try {
            csvReader = new CSVReader(new InputStreamReader(stream, Charset.forName("UTF-8")), ',');
            lines = csvReader.readAll();
            lines.remove(0); // Removing header line
        } catch (IOException ioe) {
            Logger.getLogger(Loader.class.getName()).log(Level.SEVERE, "Unable to load trades from CSV", ioe);
        } finally {
            if (csvReader != null) {
                try {
                    csvReader.close();
                } catch (IOException ioe) {
                }
            }
        }

        List<Tick> ticks = null;
        if ((lines != null) && !lines.isEmpty()) {

            // Getting the first and last trades timestamps
            DateTime beginTime = new DateTime(Long.parseLong(lines.get(0)[0]) * 1000);
            DateTime endTime = new DateTime(Long.parseLong(lines.get(lines.size() - 1)[0]) * 1000);
            if (beginTime.isAfter(endTime)) {
                Instant beginInstant = beginTime.toInstant();
                Instant endInstant = endTime.toInstant();
                beginTime = new DateTime(endInstant);
                endTime = new DateTime(beginInstant);
            }
            // Building the empty ticks 
            ticks = buildEmptyTicks(beginTime, endTime, Period.seconds(timeframe));
            // Filling the ticks with trades
            for (String[] tradeLine : lines) {
                DateTime tradeTimestamp = new DateTime(Long.parseLong(tradeLine[0]) * 1000);
                for (Tick tick : ticks) {
                    if (tick.inPeriod(tradeTimestamp)) {
                        double tradePrice = Double.parseDouble(tradeLine[1]);
                        double tradeAmount = Double.parseDouble(tradeLine[2]);
                        tick.addTrade(tradeAmount, tradePrice);
                    }
                }
            }
            // Removing still empty ticks
            removeEmptyTicks(ticks);
        }

        return new TimeSeries("bitstamp_trades_"+timeframe, ticks);
    }

    /**
     * Builds a list of empty ticks.
     * @param beginTime the begin time of the whole period
     * @param endTime the end time of the whole period
     * @param duration the tick duration
     * @return the list of empty ticks
     */
    private static List<Tick> buildEmptyTicks(DateTime beginTime, DateTime endTime, Period duration) {

        List<Tick> emptyTicks = new ArrayList<Tick>();

        DateTime tickBeginTime = beginTime;
        DateTime tickEndTime;
        do {
            tickEndTime = tickBeginTime.plus(duration);
            emptyTicks.add(new Tick(duration, tickEndTime));
            tickBeginTime = tickEndTime;
        } while (tickEndTime.isBefore(endTime));

        return emptyTicks;
    }

    /**
     * Removes all empty (i.e. with no trade) ticks of the list.
     * @param ticks a list of ticks
     */
    private static void removeEmptyTicks(List<Tick> ticks) {
        for (int i = ticks.size() - 1; i >= 0; i--) {
            if (ticks.get(i).getTrades() == 0) {
                ticks.remove(i);
            }
        }
    }
}
