package ta4jexamples.indicators;

import eu.verdelhan.ta4j.TimeSeries;
import eu.verdelhan.ta4j.indicators.helpers.AverageTrueRangeIndicator;
import eu.verdelhan.ta4j.indicators.helpers.StandardDeviationIndicator;
import eu.verdelhan.ta4j.indicators.oscillators.PPOIndicator;
import eu.verdelhan.ta4j.indicators.simple.ClosePriceIndicator;
import eu.verdelhan.ta4j.indicators.simple.PriceVariationIndicator;
import eu.verdelhan.ta4j.indicators.simple.TypicalPriceIndicator;
import eu.verdelhan.ta4j.indicators.trackers.EMAIndicator;
import eu.verdelhan.ta4j.indicators.trackers.ROCIndicator;
import eu.verdelhan.ta4j.indicators.trackers.RSIIndicator;
import eu.verdelhan.ta4j.indicators.trackers.SMAIndicator;
import eu.verdelhan.ta4j.indicators.trackers.WilliamsRIndicator;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import ta4jexamples.loaders.CsvTradesLoader;

/**
 * This class builds a CSV file containing values from indicators.
 */
public class IndicatorsToCsv {

    public static void main(String[] args) {

        /**
         * Getting time series
         */
        TimeSeries series = CsvTradesLoader.loadBitstampSeries();

        /**
         * Creating indicators
         */
        // Close price
        ClosePriceIndicator closePrice = new ClosePriceIndicator(series);
        // Typical price
        TypicalPriceIndicator typicalPrice = new TypicalPriceIndicator(series);
        // Price variation
        PriceVariationIndicator priceVariation = new PriceVariationIndicator(series);
        // Simple moving averages
        SMAIndicator shortSma = new SMAIndicator(closePrice, 8);
        SMAIndicator longSma = new SMAIndicator(closePrice, 20);
        // Exponential moving averages
        EMAIndicator shortEma = new EMAIndicator(closePrice, 8);
        EMAIndicator longEma = new EMAIndicator(closePrice, 20);
        // Percentage price oscillator
        PPOIndicator ppo = new PPOIndicator(closePrice, 12, 26);
        // Rate of change
        ROCIndicator roc = new ROCIndicator(closePrice, 100);
        // Relative strength index
        RSIIndicator rsi = new RSIIndicator(closePrice, 14);
        // Williams %R
        WilliamsRIndicator williamsR = new WilliamsRIndicator(series, 20);
        // Average true range
        AverageTrueRangeIndicator atr = new AverageTrueRangeIndicator(series, 20);
        // Standard deviation
        StandardDeviationIndicator sd = new StandardDeviationIndicator(closePrice, 14);

        /**
         * Building header
         */
        StringBuilder sb = new StringBuilder("close,typical,variation,sma8,sma20,ema8,ema20,ppo,roc,rsi,williamsr,atr,sd\n");

        /**
         * Adding indicators values
         */
        final int nbTicks = series.getSize();
        for (int i = 0; i < nbTicks; i++) {
            sb.append(closePrice.getValue(i)).append(',')
            .append(typicalPrice.getValue(i)).append(',')
            .append(priceVariation.getValue(i)).append(',')
            .append(shortSma.getValue(i)).append(',')
            .append(longSma.getValue(i)).append(',')
            .append(shortEma.getValue(i)).append(',')
            .append(longEma.getValue(i)).append(',')
            .append(ppo.getValue(i)).append(',')
            .append(roc.getValue(i)).append(',')
            .append(rsi.getValue(i)).append(',')
            .append(williamsR.getValue(i)).append(',')
            .append(atr.getValue(i)).append(',')
            .append(sd.getValue(i)).append('\n');
        }

        /**
         * Writing CSV file
         */
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter("indicators.csv"));
            writer.write(sb.toString());
        } catch (IOException ioe) {
            Logger.getLogger(IndicatorsToCsv.class.getName()).log(Level.SEVERE, "Unable to write CSV file", ioe);
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException ioe) {
            }
        }

    }
}
