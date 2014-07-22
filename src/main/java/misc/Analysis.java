package misc;

import eu.verdelhan.ta4j.Indicator;
import eu.verdelhan.ta4j.TimeSeries;
import eu.verdelhan.ta4j.indicators.helpers.AverageTrueRangeIndicator;
import eu.verdelhan.ta4j.indicators.oscillators.CCIIndicator;
import eu.verdelhan.ta4j.indicators.oscillators.PPOIndicator;
import eu.verdelhan.ta4j.indicators.simple.ClosePriceIndicator;
import eu.verdelhan.ta4j.indicators.simple.MaxPriceIndicator;
import eu.verdelhan.ta4j.indicators.simple.MedianPriceIndicator;
import eu.verdelhan.ta4j.indicators.simple.MinPriceIndicator;
import eu.verdelhan.ta4j.indicators.simple.TradeCountIndicator;
import eu.verdelhan.ta4j.indicators.simple.VolumeIndicator;
import eu.verdelhan.ta4j.indicators.trackers.ParabolicSarIndicator;
import eu.verdelhan.ta4j.indicators.trackers.ROCIndicator;
import eu.verdelhan.ta4j.indicators.trackers.RSIIndicator;
import eu.verdelhan.ta4j.indicators.trackers.TripleEMAIndicator;
import eu.verdelhan.ta4j.indicators.trackers.WilliamsRIndicator;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Analysis {

    private TimeSeries series;
    
    private int timeframe;
    
    private Map<String, Indicator> indMap = new TreeMap<String, Indicator>();

    public Analysis(int timeframe) {
        this.series = Loader.loadSeries(timeframe);
        System.out.println("Series loaded ("+series.getSize()+")");
        this.timeframe = timeframe;
        fillMap();
    }

    public void writeFile() {
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter("indicators_s"+timeframe+".csv"));
            writer.write(buildHeader() + buildData());
        } catch (IOException ioe) {
            Logger.getLogger(Analysis.class.getName()).log(Level.SEVERE, "Unable to write CSV file", ioe);
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException ioe) {
            }
        }
        System.out.println("End of writing");
    }
	
	private void fillMap() {
		
		ClosePriceIndicator closePrice = new ClosePriceIndicator(series);
		indMap.put("s"+timeframe+"_close", closePrice);
        MaxPriceIndicator maxPrice = new MaxPriceIndicator(series);
		indMap.put("s"+timeframe+"_max", maxPrice);
        MinPriceIndicator minPrice = new MinPriceIndicator(series);
		indMap.put("s"+timeframe+"_min", minPrice);
        MedianPriceIndicator medianPrice = new MedianPriceIndicator(series);
		indMap.put("s"+timeframe+"_med", medianPrice);
        VolumeIndicator volume = new VolumeIndicator(series);
		indMap.put("s"+timeframe+"_vol", volume);
        TradeCountIndicator nbTrades = new TradeCountIndicator(series);
		indMap.put("s"+timeframe+"_nbtrades", nbTrades);

        // ATR
		indMap.put("s"+timeframe+"_atr_8", new AverageTrueRangeIndicator(series, 8));
		indMap.put("s"+timeframe+"_atr_16", new AverageTrueRangeIndicator(series, 16));
		indMap.put("s"+timeframe+"_atr_24", new AverageTrueRangeIndicator(series, 24));

		// TEMA
		indMap.put("s"+timeframe+"_cl_tema_8", new TripleEMAIndicator(closePrice, 8));
		indMap.put("s"+timeframe+"_cl_tema_16", new TripleEMAIndicator(closePrice, 16));
		indMap.put("s"+timeframe+"_cl_tema_24", new TripleEMAIndicator(closePrice, 24));
		
		// RSI
		indMap.put("s"+timeframe+"_cl_rsi_8", new RSIIndicator(closePrice, 8));
		indMap.put("s"+timeframe+"_cl_rsi_16", new RSIIndicator(closePrice, 16));
		indMap.put("s"+timeframe+"_cl_rsi_24", new RSIIndicator(closePrice, 24));
		
		// PPO
		indMap.put("s"+timeframe+"_cl_ppo_4_14", new PPOIndicator(closePrice, 4, 14));
		indMap.put("s"+timeframe+"_cl_ppo_8_18", new PPOIndicator(closePrice, 8, 18));
		indMap.put("s"+timeframe+"_cl_ppo_12_26", new PPOIndicator(closePrice, 12, 26));

        // Parabolic SAR
		indMap.put("s"+timeframe+"_psar_8", new ParabolicSarIndicator(series, 8));
		indMap.put("s"+timeframe+"_psar_16", new ParabolicSarIndicator(series, 16));
		indMap.put("s"+timeframe+"_psar_24", new ParabolicSarIndicator(series, 24));

		// ROC
		indMap.put("s"+timeframe+"_cl_roc_8", new ROCIndicator(closePrice, 8));
		indMap.put("s"+timeframe+"_cl_roc_16", new ROCIndicator(closePrice, 16));
		indMap.put("s"+timeframe+"_cl_roc_24", new ROCIndicator(closePrice, 24));

		// W's R
		indMap.put("s"+timeframe+"_willsr_8", new WilliamsRIndicator(series, 8));
		indMap.put("s"+timeframe+"_willsr_16", new WilliamsRIndicator(series, 16));
		indMap.put("s"+timeframe+"_willsr_24", new WilliamsRIndicator(series, 24));
		
		// CCI
		indMap.put("s"+timeframe+"_cci_8", new CCIIndicator(series, 8));
		indMap.put("s"+timeframe+"_cci_16", new CCIIndicator(series, 16));
		indMap.put("s"+timeframe+"_cci_24", new CCIIndicator(series, 24));

        System.out.println("Map filled");
	}
	
	private String buildHeader() {
		StringBuilder sb = new StringBuilder("time,");
		for (String name : indMap.keySet()) {
			sb.append(name).append(",");
		}
        System.out.println("Header built");
		return sb.substring(0, sb.length()-1) + '\n';
	}
	
	private String buildData() {
        StringBuilder sb = new StringBuilder();
		final int nbTicks = series.getSize();
        for (int i = 0; i < nbTicks; i++) {
            System.out.println("Tick: "+i);
            sb.append(series.getTick(i).getEndTime().getMillis() / 1000d).append(',');

            for (int j = 0; j < indMap.values().size(); j++) {
                Object o = ((Indicator)indMap.values().toArray()[j]).getValue(i);
                sb.append(o).append(",");
            }
            sb.replace(sb.length()-1, sb.length(), "\n");
        }
        System.out.println("Data built");
        return sb.toString();
	}

	public static void main(String[] args) {

//        System.out.println("300");
//        Analysis a300 = new Analysis(300);
//        a300.writeFile();
//
//        System.out.println("840");
//        Analysis a840 = new Analysis(840); // 14 min
//        a840.writeFile();

        System.out.println("1740");
        Analysis a1740 = new Analysis(1740); // 29 min
        a1740.writeFile();

        System.out.println("3480");
        Analysis a3480 = new Analysis(3480); // 58 min
        a3480.writeFile();
	}
	
}
