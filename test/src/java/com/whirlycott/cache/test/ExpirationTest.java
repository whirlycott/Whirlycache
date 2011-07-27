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
public class ExpirationTest extends TestCase {

	private static Logger log = Logger.getLogger(ExpirationTest.class);

	public static void main(final String[] args) throws Exception {
		log.debug("Single threaded tests...");
		final ExpirationTest whirlytest = new ExpirationTest();
		whirlytest.testExpiration();
	}

	public void testExpiration() throws Exception {
		final Cache c = CacheManager.getInstance().getCache();
		assertNotNull(c);

		final long startTime = System.currentTimeMillis();

		// Store some values
		c.store("first", "10 seconds", 10000L);
		c.store("second", "30 seconds", 30000L);
		c.store("third", "45 seconds", 45000L);
		c.store("fourth", "2 minutes", 120000L);

		// BEGIN RETRIEVES
		boolean empty = false;
		while (!empty) {
			log.debug("Elapsed time: " + new Long(System.currentTimeMillis() - startTime));
			final Object first = c.retrieve("first");
			final Object second = c.retrieve("second");
			final Object third = c.retrieve("third");
			final Object fourth = c.retrieve("fourth");
			log.debug("First: " + first);
			log.debug("Second: " + second);
			log.debug("Third: " + third);
			log.debug("Fourth: " + fourth);
			empty = null == fourth;

			log.debug("Sleeping for 5 seconds");
			Thread.sleep(5000L);
		}
	}
}
