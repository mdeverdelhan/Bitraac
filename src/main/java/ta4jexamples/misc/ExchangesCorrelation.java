
package ta4jexamples.misc;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Prototype for echanges correlation test.
 */
public class ExchangesCorrelation {

    public static void main(String[] args) {

        ArrayList<String[]> bitstampTrades = getBistampTrades();
        addIndicators(bitstampTrades);

        ArrayList<String[]> mtgoxTrades = getMtgoxTrades();
        addIndicators(mtgoxTrades);

        ArrayList<String[]> merged = TradesMerger.merge(bitstampTrades, mtgoxTrades);
        addHeader(merged);

        exportToCSV(merged, "D:/dataCSV/btc/merged.csv");
    }

    private static void exportToCSV(ArrayList<String[]> data, String file) {
        System.out.println("Writing to CSV");
        CSVWriter csvWriter = null;
        try {
            csvWriter = new CSVWriter(new FileWriter(file), ',', CSVWriter.NO_QUOTE_CHARACTER);
            csvWriter.writeAll(data);
        } catch (IOException ioe) {
            Logger.getLogger(ExchangesCorrelation.class.getName()).log(Level.SEVERE, "Unable to write trades to CSV", ioe);
        } finally {
            if (csvWriter != null) {
                try {
                    csvWriter.close();
                } catch (IOException ioe) {
                }
            }
        }
    }

    private static void addHeader(ArrayList<String[]> trades) {
        trades.add(0, new String[] {
            "t1990",
            "unixtime",
            "bstmp_price",
            "bstmp_amount",
            "bstmp_ppo",
            "bstmp_roc",
            "bstmp_roc1",
            "mtg_price",
            "mtg_amount",
            "mtg_ppo",
            "mtg_roc",
            "mtg_roc1"
        });
        trades.add(1, new String[] {
            "S",
            "S",
            "USD",
            "BTC",
            "",
            "",
            "",
            "USD",
            "BTC",
            "",
            "",
            ""
        });
    }

    private static void addIndicators(ArrayList<String[]> trades) {
//        System.out.println("Adding indicators");
//        for (int i = 0; i < trades.size(); i++) {
//            String[] tradeStr = trades.get(i);
//            Trade trade = buildTrade(tradeStr[0], tradeStr[1], tradeStr[2]);
//            ExchangeMarket.addTrade(trade);
//            String[] indicators = getIndicatorValues();
//            String[] tradeWithIndic = new String[tradeStr.length + 1 + indicators.length];
//            tradeWithIndic[0] = get20yoUnixTime(Long.parseLong(tradeStr[0]));
//            System.arraycopy(tradeStr, 0, tradeWithIndic, 1, tradeStr.length);
//            System.arraycopy(indicators, 0, tradeWithIndic, 4, indicators.length);
//            trades.set(i, tradeWithIndic);
//        }
    }

    private static ArrayList<String[]> getBistampTrades() {
        System.out.println("Getting Bitstamp trades");
        ArrayList<String[]> trades = null;
        CSVReader csvReader = null;
        try {
            csvReader = new CSVReader(new FileReader(ExchangesCorrelation.class.getClassLoader().getResource("exchange_link/bitstampUSD_from_20131125000000.csv").getPath()), ',');
            trades = new ArrayList<String[]>(csvReader.readAll());
        } catch (IOException ioe) {
            Logger.getLogger(ExchangesCorrelation.class.getName()).log(Level.SEVERE, "Unable to load trades from CSV", ioe);
        } finally {
            if (csvReader != null) {
                try {
                    csvReader.close();
                } catch (IOException ioe) {
                }
            }
        }
        return trades;
    }

    private static ArrayList<String[]> getMtgoxTrades() {
        System.out.println("Getting Mtgox trades");
        ArrayList<String[]> trades = null;
        CSVReader csvReader = null;
        try {
            csvReader = new CSVReader(new FileReader(ExchangesCorrelation.class.getClassLoader().getResource("exchange_link/mtgoxUSD_from_20131125000000.csv").getPath()), ',');
            trades = new ArrayList<String[]>(csvReader.readAll());
        } catch (IOException ioe) {
            Logger.getLogger(ExchangesCorrelation.class.getName()).log(Level.SEVERE, "Unable to load trades from CSV", ioe);
        } finally {
            if (csvReader != null) {
                try {
                    csvReader.close();
                } catch (IOException ioe) {
                }
            }
        }
        return trades;
    }

    private static String get20yoUnixTime(long unixtime) {
        return unixtime - 631152000 + "";
    }
}
