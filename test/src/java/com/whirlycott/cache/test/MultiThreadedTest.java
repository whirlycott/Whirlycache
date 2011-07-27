/*
 * Created on Aug 12, 2004 by pjacob
 *
 */
package com.whirlycott.cache.test;

import junit.framework.TestCase;

import org.apache.log4j.Logger;

import com.whirlycott.cache.Cache;
import com.whirlycott.cache.CacheException;
import com.whirlycott.cache.CacheManager;

/**
 * @author pjacob
 * 
 */
public class MultiThreadedTest extends TestCase {

	private static Logger log = Logger.getLogger(MultiThreadedTest.class);

	private static long counter = 0L;

	private static int flipper = 0;

	public static final String someString = "some long string should go here..................";

	public MultiThreadedTest() {
		super();
	}

	public static void main(final String[] args) throws CacheException {
		new MultiThreadedTest().testMulti();
	}

	public void testMulti() {
		for (int i = 0; i < 32; i++) {
			log.debug("Starting thread " + i);
			final Thread t = new Thread(new TestCache());
			t.setName("" + i);
			t.start();
		}
	}

	public class TestCache implements Runnable {

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Runnable#run()
		 */
		public void run() {
			final CacheManager cm = CacheManager.getInstance();
			Cache c = null;
			try {
				c = cm.getCache();
			} catch (final CacheException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			for (int i = 0; i < 100000; i++) {

				// Alternate gets and puts
				if (flipper == 0) {
					flipper = 1;

					// log.debug("Read...");

					// Do a read.
					final String value = (String) c.retrieve(new Long(counter).toString());

				} else {
					flipper = 0;

					// log.debug("Write...");

					// Do a write.
					c.store(new Long(counter).toString(), someString);

				}
			}
		}
	}

}
