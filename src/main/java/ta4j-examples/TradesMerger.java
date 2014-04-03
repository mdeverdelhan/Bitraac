package eu.verdelhan.test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class TradesMerger {

    public static ArrayList<String[]> merge(List<String[]> bitstampTrades, List<String[]> mtgoxTrades) {
        ArrayList<String[]> merged = new ArrayList<String[]>(getAllTradesCount(bitstampTrades, mtgoxTrades));

        int nbParamsBitstamp = bitstampTrades.get(0).length;
        int nbParams = nbParamsBitstamp + mtgoxTrades.get(0).length - 2;

        for (String[] bv : bitstampTrades) {
            String[] values = new String[nbParams];
            System.arraycopy(bv, 0, values, 0, bv.length);
            merged.add(values);
        }

        for (String[] mv : mtgoxTrades) {
            String[] values = new String[nbParams];
            System.arraycopy(mv, 0, values, 0, 2);
            System.arraycopy(mv, 2, values, nbParamsBitstamp, mv.length-2);
            merged.add(values);
        }

        Collections.sort(merged, new Comparator<String[]>() {
            public int compare(String[] strings, String[] otherStrings) {
                return strings[0].compareTo(otherStrings[0]);
            }
        });

        return merged;
    }

    private static int getAllTradesCount(List<String[]>... tradesSources) {
        int count = 0;
        for (List<String[]> source : tradesSources) {
            count += source.size();
        }
        return count;
    }
}
