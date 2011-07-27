/*
 * Created on Oct 10, 2004 by pjacob
 *
 */
package com.whirlycott.cache.benchmarks;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryAxis3D;
import org.jfree.chart.axis.NumberAxis3D;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.BarRenderer3D;
import org.jfree.data.category.DefaultCategoryDataset;

/**
 * @author pjacob
 * 
 */
public class BenchmarkRunner {

	/**
	 * Number of concurrent threads.
	 */
	public final static int CONCURRENT_THREADS = 20;

	/**
	 * Number of elements in the cache
	 */
	private static final int ELEMENTS_CACHED = 50000;

	/**
	 * Number of iterations that the benchmark will be run.
	 */
	public final static int ITERATIONS_PER_BENCHMARK = 10;

	/**
	 * Logger.
	 */
	private static final Logger log = Logger.getLogger(BenchmarkRunner.class);

	/**
	 * Percent of read-only activity: 8 = 80%, 5 = 50%, etc.
	 */
	public final static int PERCENT_READONLY = 8;

	/**
	 * How many milliseconds should we do a Thread.sleep(n) between tests.
	 */
	public final static int SLEEPTIME_BETWEEN_TESTS = 11000;

	/**
	 * Entry point into the benchmarking code.
	 * 
	 * @param args
	 * @throws IOException
	 */
	public static void main(final String[] args) throws Exception {

		// Just add in new cache bencharkables here and the rest is automatic.
		final Map benchmarkableCaches = new HashMap();

		// Test params
		final Params testParams = new Params(ELEMENTS_CACHED, CONCURRENT_THREADS);

		benchmarkableCaches.put(new StandardHashmap(), testParams);

		benchmarkableCaches.put(new Ehcache(), testParams);

		benchmarkableCaches.put(new Whirlycache(), testParams);

		benchmarkableCaches.put(new Oscache(), testParams);

		benchmarkableCaches.put(new ApacheJCS(), testParams);

		benchmarkableCaches.put(new JBossCache(), testParams);

		// Store the results in a list.
		final List results = new ArrayList();

		/*
		 * Run each benchmark.
		 */
		for (final Iterator i = benchmarkableCaches.keySet().iterator(); i.hasNext();) {
			final Benchmarkable b = (Benchmarkable) i.next();
			log.debug("Starting test: " + b.getName());

			final Params p = (Params) benchmarkableCaches.get(b);
			final Result result = b.benchmark(p);
			results.add(result);
			log.debug("Benchmark for " + b.getName() + " with settings " + p.getShortName() + ": " + result.getTime());

			// Do a gc between each test.
			System.gc();

			log.debug("Sleeping between tests...");
			Thread.sleep(SLEEPTIME_BETWEEN_TESTS);

		}

		// Print out a graphic.
		makeGraph(results);

	}

	/**
	 * Make the graphic.
	 * 
	 * @param _results
	 * @throws IOException
	 */
	public static void makeGraph(final List _results) throws IOException {
		final CategoryPlot plot = new CategoryPlot();

		// Create jfree objects based on the List of results passed in
		int counter = 0;
		final DefaultCategoryDataset d = new DefaultCategoryDataset();
		for (final Iterator i = _results.iterator(); i.hasNext();) {

			final Result result = (Result) i.next();
			d.addValue(result.getTime(), result.getName(), result.getName());
			log.debug("Adding: " + result.getName() + " with time " + result.getTime());
			plot.setDataset(counter, d);
			counter++;
		}

		plot.setRenderer(new BarRenderer3D());

		final CategoryAxis domainAxis = new CategoryAxis3D();
		plot.setDomainAxis(domainAxis);

		final ValueAxis rangeAxis = new NumberAxis3D();
		rangeAxis.setLabel("Milliseconds");
		plot.setRangeAxis(rangeAxis);
		plot.setForegroundAlpha(0.7f);

		final JFreeChart chart = new JFreeChart("Whirlycache Benchmarks at " + new Date(), plot);
		chart.setAntiAlias(true);

		final String file = System.getProperty("java.io.tmpdir") + "whirlycache-results.png";
		log.debug("Writing results to " + file);
		final File f = new File(file);

		ChartUtilities.saveChartAsPNG(f, chart, 500, 500);
	}

	public BenchmarkRunner() {
		super();
	}

}