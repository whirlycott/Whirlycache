/*
 * Created on Oct 11, 2004 by pjacob
 *
 */
package com.whirlycott.cache.benchmarks;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import org.apache.log4j.Logger;

import com.whirlycott.cache.test.Constants;

/**
 * @author pjacob
 * 
 */
public class Ehcache implements Benchmarkable {

	private static final Logger log = Logger.getLogger(Ehcache.class);

	private Params params;

	/**
	 * 
	 */
	public Ehcache() {
		super();
	}

	/*
	 * (non-Javadoc)
	 */
	public Result benchmark(final Params _params) {
		params = _params;

		// Do some initialization
		final CacheManager cacheManager;
		try {
			cacheManager = CacheManager.create();
			final Cache c = new Cache("test", Constants.STORE_COUNT, false, false, 3600, 3600);
			cacheManager.addCache(c);

		} catch (final CacheException e1) {
			log.error(e1.getMessage(), e1);
		}

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

		try {
			// Clean up.
			CacheManager.getInstance().shutdown();
		} catch (final CacheException e) {
			log.error(e.getMessage(), e);
		}

		return r;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.whirlycott.cache.benchmarks.Benchmarkable#getName()
	 */
	public String getName() {
		return "Ehcache";
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
			final Cache c = CacheManager.getInstance().getCache("test");

			// Insert n elements.
			for (int i = 0; i <= params.getCacheSize(); i++) {
				final Element e = new Element(new Integer(i), "v-" + i);
				c.put(e);
			}

			// Get n elements.
			for (int i = 0; i <= params.getCacheSize(); i++) {
				// ~80% of the time, do reads, else do a remove and a store
				if (i % BenchmarkRunner.PERCENT_READONLY == 0) {
					c.remove(new Integer(i));
					c.put(new Element(new Integer(i), "v-" + i));
					i += 10 - BenchmarkRunner.PERCENT_READONLY;
				} else {
					final Object o = c.get(new Integer(i));
				}
			}

		} catch (final Exception e) {
			log.error(e.getMessage(), e);
		}
	}

}
