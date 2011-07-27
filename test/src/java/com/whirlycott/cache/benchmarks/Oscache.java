/*
 * Created on Oct 11, 2004 by pjacob
 *
 */
package com.whirlycott.cache.benchmarks;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import com.opensymphony.oscache.base.Cache;
import com.opensymphony.oscache.general.GeneralCacheAdministrator;

/**
 * @author pjacob
 * 
 */
public class Oscache implements Benchmarkable {

	/**
	 * Logger
	 */
	private static final Logger log = Logger.getLogger(Oscache.class);

	/**
	 * Test params
	 */
	private Params params;

	/**
	 * 
	 */
	public Oscache() {
		super();
	}

	/*
	 * (non-Javadoc)
	 */
	public Result benchmark(final Params _params) {
		params = _params;

		// Do some initialization
		final Cache c = new GeneralCacheAdministrator().getCache();

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
		return r;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.whirlycott.cache.benchmarks.Benchmarkable#getName()
	 */
	public String getName() {
		return "Oscache";
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
			final Cache c = new GeneralCacheAdministrator().getCache();

			// Insert n elements.
			for (int i = 0; i <= params.getCacheSize(); i++) {
				c.putInCache(new Integer(i).toString(), "v-" + i);
			}

			// Get n elements.
			for (int i = 0; i <= params.getCacheSize(); i++) {
				// ~80% of the time, do reads, else do a remove and a store
				if (i % BenchmarkRunner.PERCENT_READONLY == 0) {
					c.flushEntry(new Integer(i).toString());
					c.putInCache(new Integer(i).toString(), "v-" + i);
					i += 10 - BenchmarkRunner.PERCENT_READONLY;
				} else {
					final Object o = c.getFromCache(new Integer(i).toString());
				}
			}

		} catch (final Exception e) {
			log.error(e.getMessage(), e);
		}
	}

}
