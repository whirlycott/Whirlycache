/*
 * Created on Oct 11, 2004 by pjacob
 *
 */
package com.whirlycott.cache.benchmarks;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.jcs.JCS;
import org.apache.jcs.access.exception.CacheException;
import org.apache.log4j.Logger;

/**
 * @author pjacob
 * 
 */
public class ApacheJCS implements Benchmarkable {

	private static final Logger log = Logger.getLogger(ApacheJCS.class);

	static JCS cache;

	private Params params;

	/**
	 * 
	 */
	public ApacheJCS() {
		super();
		try {
			cache = JCS.getInstance("DC");
		} catch (final CacheException e) {
			log.error(e.getMessage(), e);
		}
	}

	/*
	 * (non-Javadoc)
	 */
	public Result benchmark(final Params _params) {
		params = _params;

		// Do some initialization

		final Result r = new Result(this);
		final long begin = System.currentTimeMillis();

		// Run some iterations.
		for (int iterations = 0; iterations <= BenchmarkRunner.ITERATIONS_PER_BENCHMARK; iterations++) {

			// Run the benchmark() method
			final List threads = new ArrayList();
			for (int i = 0; i < params.getThreads(); i++) {
				final Thread t = new Thread(this);
				threads.add(t);
				t.start();
				try {
					Thread.sleep(100);
				} catch (final InterruptedException e2) {
					log.error(e2.getMessage(), e2);
				}
			}

			// Wait for the threads to finish.
			for (final Iterator i = threads.iterator(); i.hasNext();) {
				try {
					((Thread) i.next()).join();
				} catch (final InterruptedException e) {
					log.error(e.getMessage(), e);
				}
			}

		}

		// Return the time.
		final long end = System.currentTimeMillis();
		r.setTime(end - begin);

		// Clean up.
		cache.dispose();

		return r;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.whirlycott.cache.benchmarks.Benchmarkable#getName()
	 */
	public String getName() {
		return "JCS";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.whirlycott.cache.benchmarks.Benchmarkable#getParams()
	 */
	public Params getParams() {
		return params;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		try {

			// Insert n elements.
			for (int i = 0; i <= params.getCacheSize(); i++) {
				cache.put(new Integer(i), "v-" + i);
			}

			// Get n elements.
			for (int i = 0; i <= params.getCacheSize(); i++) {
				// ~80% of the time, do reads, else do a remove and a store
				if (i % BenchmarkRunner.PERCENT_READONLY == 0) {
					cache.remove(new Integer(i));
					cache.put(new Integer(i), "v-" + i);
					i += 10 - BenchmarkRunner.PERCENT_READONLY;
				} else {
					final Object o = cache.get(new Integer(i));
				}
			}

		} catch (final Exception e) {
			log.error(e.getMessage(), e);
		}
	}

}
