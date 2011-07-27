/*
 * Created on Sep 29, 2005 by phil
 *
 */
package com.whirlycott.cache.test;

import junit.framework.TestCase;

import org.apache.log4j.Logger;

import com.whirlycott.cache.CacheException;
import com.whirlycott.cache.CacheManager;

public class AnotherShutdownTest extends TestCase {

	/**
	 * Logger
	 */
	private static final Logger log = Logger.getLogger(AnotherShutdownTest.class);

	public void testShutdown() throws CacheException, InterruptedException {
		for (int i = 0; i < 1; i++) {
			final CacheManager cm = CacheManager.getInstance();
			Thread.sleep(9999);
			log.debug("Shutting down now.");
			final long begin = System.currentTimeMillis();
			cm.shutdown();
			final long end = System.currentTimeMillis();
			log.debug("Shutdown complete: " + (end - begin));
		}
	}

}
