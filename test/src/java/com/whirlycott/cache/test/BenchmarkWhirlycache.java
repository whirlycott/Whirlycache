/*
Copyright 2004 		Philip Jacob <phil@whirlycott.com>
				  	Seth Fitzsimmons <seth@note.amherst.edu>

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */

package com.whirlycott.cache.test;

import junit.framework.TestCase;

import org.apache.log4j.Logger;

import com.whirlycott.cache.Cache;
import com.whirlycott.cache.CacheManager;

/**
 * @author phil
 */
public class BenchmarkWhirlycache extends TestCase {

	private static Logger log = Logger.getLogger(BenchmarkWhirlycache.class);

	public static void main(final String[] args) throws Exception {
		log.debug("Single threaded tests...");
		final BenchmarkWhirlycache whirlytest = new BenchmarkWhirlycache();
		whirlytest.testStore();
		whirlytest.testRetrieve();
		whirlytest.testMultiThreaded();
	}

	/**
	 * Some single threaded gets.
	 * 
	 * @throws Exception
	 */
	public void testRetrieve() throws Exception {
		final Cache c = CacheManager.getInstance().getCache();
		assertNotNull(c);

		// BEGIN RETRIEVES
		final long startGet = System.currentTimeMillis();
		for (int loop = 0; loop < Constants.RETRIEVE_COUNT; loop++) {
			for (int i = 0; i < Constants.STORE_COUNT; i++) {

				final Object o = c.retrieve(new Integer(i).toString());
				// assertEquals((String)o, "value" + new Integer(i).toString());

			}
		}
		// CacheManager.getInstance().destroy();
		final long endGet = System.currentTimeMillis();
		log.debug("Total RETRIEVE time was: " + (endGet - startGet));
		// END RETRIEVES
	}

	/**
	 * A bunch of single-threaded stores.
	 * 
	 * @throws Exception
	 */
	public void testStore() throws Exception {

		final Cache c = CacheManager.getInstance().getCache();

		assertNotNull(c);

		// BEGIN STORES
		final long start = System.currentTimeMillis();
		for (int i = 0; i < Constants.STORE_COUNT; i++) {
			c.store(new Integer(i).toString(), "value" + i);
		}
		final long end = System.currentTimeMillis();
		log.debug("Total STORE time was: " + (end - start));
		// END STORES

		log.debug("Cache size is: " + c.size());
	}

	/**
	 * Some multi-threaded tests.
	 * 
	 */
	public void testMultiThreaded() {
		new ThreadedGets().testMultiThreadedReads();
	}

	/**
	 * Inner class for running the multi-threaded tests.
	 * 
	 * @author pjacob
	 * 
	 */
	public class ThreadedGets implements Runnable {

		private void testMultiThreadedReads() {
			final Thread[] t = new Thread[Constants.THREAD_COUNT];
			final long beginGets = System.currentTimeMillis();

			for (int i = 0; i < Constants.THREAD_COUNT; i++) {
				log.debug("Starting thread " + i);
				t[i] = new Thread(new ThreadedGets());
				t[i].start();
			}

			for (int i = 0; i < Constants.THREAD_COUNT; i++) {
				try {
					t[i].join();
				} catch (final InterruptedException e) {
					e.printStackTrace();
				}
			}

			final long endGets = System.currentTimeMillis();

			log.debug("Total mx-thread time w/ concurrency level  " + Constants.THREAD_COUNT + " was " + (endGets - beginGets));

			/*
			 * final CacheManager cm = CacheManager.getInstance();
			 * 
			 * try { cm.shutdown(); } catch (CacheException e) {
			 * e.printStackTrace(); }
			 */
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Runnable#run()
		 */
		public void run() {
			try {
				if (new Double(Math.random() * 100D).intValue() % 2 == 0) {
					log.debug("Reader thread...");
					new BenchmarkWhirlycache().testRetrieve();
				} else {
					log.debug("Writer thread...");
					new BenchmarkWhirlycache().testStore();
				}
			} catch (final Exception e) {
				e.printStackTrace();
			}
		}
	}

}
