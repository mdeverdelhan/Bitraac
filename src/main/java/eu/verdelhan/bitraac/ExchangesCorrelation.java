
package eu.verdelhan.bitraac;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;
import com.xeiam.xchange.currency.Currencies;
import com.xeiam.xchange.dto.marketdata.Trade;
import eu.verdelhan.bitraac.data.ExchangeMarket;
import eu.verdelhan.bitraac.indicators.PPO;
import eu.verdelhan.bitraac.indicators.ROC;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.joda.money.BigMoney;
import org.joda.money.CurrencyUnit;

/**
 * Prototype for echanges correlation test.
 */
public class ExchangesCorrelation {

    public static void main(String[] args) {

        ArrayList<String[]> bitstampTrades = getBistampTrades();
        addIndicators(bitstampTrades);

        ExchangeMarket.clearHistory();

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
        System.out.println("Adding indicators");
        for (int i = 0; i < trades.size(); i++) {
            String[] tradeStr = trades.get(i);
            Trade trade = buildTrade(tradeStr[0], tradeStr[1], tradeStr[2]);
            ExchangeMarket.addTrade(trade);
            String[] indicators = getIndicatorValues();
            String[] tradeWithIndic = new String[tradeStr.length + 1 + indicators.length];
            tradeWithIndic[0] = get20yoUnixTime(Long.parseLong(tradeStr[0]));
            System.arraycopy(tradeStr, 0, tradeWithIndic, 1, tradeStr.length);
            System.arraycopy(indicators, 0, tradeWithIndic, 4, indicators.length);
            trades.set(i, tradeWithIndic);
        }
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

    private static Trade buildTrade(String unixtime, String usdPrice, String tradableAmount) {
        Date timestamp = new Date(Long.parseLong(unixtime) * 1000);
        BigMoney price = BigMoney.of(CurrencyUnit.USD, new BigDecimal(usdPrice));
        return new Trade(null, new BigDecimal(tradableAmount), Currencies.BTC, Currencies.USD, price, timestamp, 0);
    }

    private static String get20yoUnixTime(long unixtime) {
        return unixtime - 631152000 + "";
    }

    private static String[] getIndicatorValues() {
        if (ExchangeMarket.isEnoughPeriods(20)) {
            return new String[] {
                new PPO(ExchangeMarket.getPreviousPeriods(), 10, 20).execute().doubleValue() + "",
                new ROC(ExchangeMarket.getPreviousPeriods(), 2).execute().doubleValue() + "",
                getRoc1() + ""
            };
        } else {
            return new String[]{
                "",
                "",
                ""
            };
        }
    }

    private static double getRoc1() {
        double roc1 = new ROC(ExchangeMarket.getPreviousPeriods(), 2).execute().doubleValue();
        if (roc1 > 5) {
            return 1;
        } else if (roc1 < -5) {
            return -1;
        } else {
            return 0;
        }
    }
}
