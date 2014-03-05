package eu.verdelhan.test;

import eu.verdelhan.ta4j.Runner;
import eu.verdelhan.ta4j.Strategy;
import eu.verdelhan.ta4j.Tick;
import eu.verdelhan.ta4j.TimeSeries;
import eu.verdelhan.ta4j.Trade;
import eu.verdelhan.ta4j.analysis.CashFlow;
import eu.verdelhan.ta4j.analysis.criteria.TotalProfitCriterion;
import eu.verdelhan.ta4j.indicators.simple.ClosePriceIndicator;
import eu.verdelhan.ta4j.indicators.trackers.SMAIndicator;
import eu.verdelhan.ta4j.runners.HistoryRunner;
import eu.verdelhan.ta4j.series.DefaultTimeSeries;
import eu.verdelhan.ta4j.series.RegularSlicer;
import eu.verdelhan.ta4j.strategies.IndicatorCrossedIndicatorStrategy;
import eu.verdelhan.ta4j.strategies.JustBuyOnceStrategy;
import eu.verdelhan.ta4j.strategies.StopLossStrategy;
import eu.verdelhan.ta4j.strategies.SupportStrategy;
import java.util.ArrayList;
import java.util.List;
import org.joda.time.Period;

/**
 * Quickstart for ta4j
 */
public class Quickstart {


	public static void main(String[] args) {

		// Getting a time series (from any provider: CSV, web service, etc.)
		TimeSeries series = createTimeSeries();


		// Getting the close price of the ticks
		double firstClosePrice = series.getTick(0).getClosePrice();
		// Or within an indicator:
		ClosePriceIndicator closePrice = new ClosePriceIndicator(series);
		// Here is the same close price:
		assert firstClosePrice == closePrice.getValue(0); // equal to firstClosePrice

		// Getting the simple moving average (SMA) of the close price over the last 12 ticks
		SMAIndicator shortSma = new SMAIndicator(closePrice, 12);
		// Here is the 12-ticks-SMA value at the 42nd index
		assert Math.abs(13.3777777777 - shortSma.getValue(42)) < 0.000001;

		// Getting a longer SMA (e.g. over the 20 last ticks)
		SMAIndicator longSma = new SMAIndicator(closePrice, 20);


		// Ok, now let's building our trading strategy!

		// Initial strategy:
		//  - Buy when 12-ticks SMA crosses over 20-ticks SMA
		//  - Sell when 12-ticks SMA crosses under 20-ticks SMA
		Strategy ourStrategy = new IndicatorCrossedIndicatorStrategy(longSma, shortSma);

		// Cutomizing our strategy...
		// We want to buy if the price go below a defined price (e.g 13.37)
		ourStrategy = new SupportStrategy(closePrice, ourStrategy, 13.37);
		// And we want to sell if we loose more than 3%
		ourStrategy = new StopLossStrategy(closePrice, ourStrategy, 3);


		// Running our juicy trading strategy...

		// Slicing/splitting the series (sub-series will last 1 day each)
		RegularSlicer slicer = new RegularSlicer(series, Period.days(1));
		// Getting the index of the last slice (sub-series)
		int lastSliceIndex = slicer.getNumberOfSlices() - 1;

		// Running our strategy over the last slice of the series
		Runner ourRunner = new HistoryRunner(slicer, ourStrategy);
		List<Trade> trades = ourRunner.run(lastSliceIndex);


		// Analysis

		// Getting the cash flow of the resulting trades
		CashFlow cashFlow = new CashFlow(slicer.getSlice(lastSliceIndex), trades);

		// Running a reference strategy (for comparison) in which we buy just once
		Runner referenceRunner = new HistoryRunner(slicer, new JustBuyOnceStrategy());
		List<Trade> referenceTrades = referenceRunner.run(lastSliceIndex);

		// Comparing our strategy to the just-buy-once strategy according to a criterion
		TotalProfitCriterion criterion = new TotalProfitCriterion();

		// Our strategy is better than a just-buy-once for the last slice
		assert criterion.calculate(slicer.getSlice(lastSliceIndex), trades)
				> criterion.calculate(slicer.getSlice(lastSliceIndex), referenceTrades);
	}

	private static TimeSeries createTimeSeries() {
		List<Tick> ticks = new ArrayList<Tick>();
//
//		DefaultTick t = new DefaultTick(DateTime., null)
//		ticks.add(new DefaultTick(null, null));

		return new DefaultTimeSeries("foo_time_series", ticks);
	}

}
