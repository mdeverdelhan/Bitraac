package eu.verdelhan.test;

import eu.verdelhan.ta4j.Tick;
import eu.verdelhan.ta4j.TimeSeries;
import eu.verdelhan.ta4j.indicators.simple.ClosePriceIndicator;
import eu.verdelhan.ta4j.indicators.trackers.SMAIndicator;
import eu.verdelhan.ta4j.series.DefaultTimeSeries;
import java.util.ArrayList;
import java.util.List;

/**
 * Quickstart for ta4j
 */
public class Quickstart {


	public static void main(String[] args) {

		TimeSeries series = createTimeSeries();

		// Getting the close price of the ticks
		double firstClosePrice = series.getTick(0).getClosePrice();
		// Or within an indicator:
		ClosePriceIndicator closePrice = new ClosePriceIndicator(series);
		// Here is the same close price:
		closePrice.getValue(0); // equal to firstClosePrice

		// Getting the simple moving average of the close price over the last 12 ticks
		SMAIndicator sma = new SMAIndicator(closePrice, 12);
		// Here is the SMA value at the 42nd index
		double smaAnswer = sma.getValue(42);


		// Ok, now let's building our trading strategy!

		// Of course I want to sell if I loose more than 3%
	}

	private static TimeSeries createTimeSeries() {
		List<Tick> ticks = new ArrayList<Tick>();
//
//		DefaultTick t = new DefaultTick(DateTime., null)
//		ticks.add(new DefaultTick(null, null));

		return new DefaultTimeSeries("foo_time_series", ticks);
	}

}
