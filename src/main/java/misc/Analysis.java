package misc;

import java.util.HashMap;
import java.util.Map;

import eu.verdelhan.ta4j.TimeSeries;
import eu.verdelhan.ta4j.Indicator;
import eu.verdelhan.ta4j.indicators.oscillators.CCIIndicator;
import eu.verdelhan.ta4j.indicators.oscillators.PPOIndicator;
import eu.verdelhan.ta4j.indicators.simple.ClosePriceIndicator;
import eu.verdelhan.ta4j.indicators.trackers.RSIIndicator;
import eu.verdelhan.ta4j.indicators.trackers.TripleEMAIndicator;
import eu.verdelhan.ta4j.indicators.trackers.WilliamsRIndicator;

public class Analysis {

	private static final Map<String, Indicator> indMap = new HashMap<String, Indicator>();
	
	private static void fillMap() {
		TimeSeries s300 = Loader.loadSeries(300);
		TimeSeries s840 = Loader.loadSeries(840); // 14 min
		TimeSeries s1740 = Loader.loadSeries(1740); // 29 min
		TimeSeries s3480 = Loader.loadSeries(3480); // 58 min
		
		
		ClosePriceIndicator s300Close = new ClosePriceIndicator(s300);
		indMap.put("s300_close", s300Close);
		ClosePriceIndicator s840Close = new ClosePriceIndicator(s840);
		indMap.put("s840_close", s840Close);
		ClosePriceIndicator s1740Close = new ClosePriceIndicator(s1740);
		indMap.put("s1740_close", s1740Close);
		ClosePriceIndicator s3480Close = new ClosePriceIndicator(s3480);
		indMap.put("s3480_close", s3480Close);
		
		// TEMA
		indMap.put("s300_cl_tema_8", new TripleEMAIndicator(s300Close, 8));
		indMap.put("s300_cl_tema_16", new TripleEMAIndicator(s300Close, 16));
		indMap.put("s300_cl_tema_24", new TripleEMAIndicator(s300Close, 24));
		
		indMap.put("s840_cl_tema_8", new TripleEMAIndicator(s840Close, 8));
		indMap.put("s840_cl_tema_16", new TripleEMAIndicator(s840Close, 16));
		indMap.put("s840_cl_tema_24", new TripleEMAIndicator(s840Close, 24));
		
		indMap.put("s1740_cl_tema_8", new TripleEMAIndicator(s1740Close, 8));
		indMap.put("s1740_cl_tema_16", new TripleEMAIndicator(s1740Close, 16));
		indMap.put("s1740_cl_tema_24", new TripleEMAIndicator(s1740Close, 24));
		
		indMap.put("s3480_cl_tema_8", new TripleEMAIndicator(s3480Close, 8));
		indMap.put("s3480_cl_tema_16", new TripleEMAIndicator(s3480Close, 16));
		indMap.put("s3480_cl_tema_24", new TripleEMAIndicator(s3480Close, 24));
		
		// RSI
		indMap.put("s300_cl_rsi_8", new RSIIndicator(s300Close, 8));
		indMap.put("s300_cl_rsi_16", new RSIIndicator(s300Close, 16));
		indMap.put("s300_cl_rsi_24", new RSIIndicator(s300Close, 24));
		
		indMap.put("s840_cl_rsi_8", new RSIIndicator(s840Close, 8));
		indMap.put("s840_cl_rsi_16", new RSIIndicator(s840Close, 16));
		indMap.put("s840_cl_rsi_24", new RSIIndicator(s840Close, 24));
		
		indMap.put("s1740_cl_rsi_8", new RSIIndicator(s1740Close, 8));
		indMap.put("s1740_cl_rsi_16", new RSIIndicator(s1740Close, 16));
		indMap.put("s1740_cl_rsi_24", new RSIIndicator(s1740Close, 24));
		
		indMap.put("s3480_cl_rsi_8", new RSIIndicator(s3480Close, 8));
		indMap.put("s3480_cl_rsi_16", new RSIIndicator(s3480Close, 16));
		indMap.put("s3480_cl_rsi_24", new RSIIndicator(s3480Close, 24));
		
		// PPO
		indMap.put("s300_cl_ppo_4_14", new PPOIndicator(s300Close, 4, 14));
		indMap.put("s300_cl_ppo_8_18", new PPOIndicator(s300Close, 8, 18));
		indMap.put("s300_cl_ppo_12_26", new PPOIndicator(s300Close, 12, 26));
		
		indMap.put("s840_cl_ppo_4_14", new PPOIndicator(s840Close, 4, 14));
		indMap.put("s840_cl_ppo_8_18", new PPOIndicator(s840Close, 8, 18));
		indMap.put("s840_cl_ppo_12_26", new PPOIndicator(s840Close, 12, 26));
		
		indMap.put("s1740_cl_ppo_4_14", new PPOIndicator(s1740Close, 4, 14));
		indMap.put("s1740_cl_ppo_8_18", new PPOIndicator(s1740Close, 8, 18));
		indMap.put("s1740_cl_ppo_12_26", new PPOIndicator(s1740Close, 12, 26));
		
		indMap.put("s3480_cl_ppo_4_14", new PPOIndicator(s3480Close, 4, 14));
		indMap.put("s3480_cl_ppo_8_18", new PPOIndicator(s3480Close, 8, 18));
		indMap.put("s3480_cl_ppo_12_26", new PPOIndicator(s3480Close, 12, 26));
		
		// W's R
		indMap.put("s300_willsr_8", new WilliamsRIndicator(s300, 8));
		indMap.put("s300_willsr_16", new WilliamsRIndicator(s300, 16));
		indMap.put("s300_willsr_24", new WilliamsRIndicator(s300, 24));
		
		indMap.put("s840_willsr_8", new WilliamsRIndicator(s840, 8));
		indMap.put("s840_willsr_16", new WilliamsRIndicator(s840, 16));
		indMap.put("s840_willsr_24", new WilliamsRIndicator(s840, 24));
		
		indMap.put("s1740_willsr_8", new WilliamsRIndicator(s1740, 8));
		indMap.put("s1740_willsr_16", new WilliamsRIndicator(s1740, 16));
		indMap.put("s1740_willsr_24", new WilliamsRIndicator(s1740, 24));
		
		indMap.put("s3480_willsr_8", new WilliamsRIndicator(s3480, 8));
		indMap.put("s3480_willsr_16", new WilliamsRIndicator(s3480, 16));
		indMap.put("s3480_willsr_24", new WilliamsRIndicator(s3480, 24));
		
		// CCI
		indMap.put("s300_cci_8", new CCIIndicator(s300, 8));
		indMap.put("s300_cci_16", new CCIIndicator(s300, 16));
		indMap.put("s300_cci_24", new CCIIndicator(s300, 24));
		
		indMap.put("s840_cci_8", new CCIIndicator(s840, 8));
		indMap.put("s840_cci_16", new CCIIndicator(s840, 16));
		indMap.put("s840_cci_24", new CCIIndicator(s840, 24));
		
		indMap.put("s1740_cci_8", new CCIIndicator(s1740, 8));
		indMap.put("s1740_cci_16", new CCIIndicator(s1740, 16));
		indMap.put("s1740_cci_24", new CCIIndicator(s1740, 24));
		
		indMap.put("s3480_cci_8", new CCIIndicator(s3480, 8));
		indMap.put("s3480_cci_16", new CCIIndicator(s3480, 16));
		indMap.put("s3480_cci_24", new CCIIndicator(s3480, 24));
	}
	
	private static String buildHeader() {
		StringBuilder sb = new StringBuilder();
		for (String name : indMap.keySet()) {
			sb.append(name).append(",");
		}
		return sb.substring(0, sb.length()-1);
	}
	
	private static String buildData() {
		
	}
	
	public static void main(String[] args) {
		
		fillMap();
		
		
		
	}
	
}
