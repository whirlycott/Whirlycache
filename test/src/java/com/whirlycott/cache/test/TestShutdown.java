package com.whirlycott.cache.test;

import com.whirlycott.cache.Cache;
import com.whirlycott.cache.CacheException;
import com.whirlycott.cache.CacheManager;

public class TestShutdown {

	//
	// Inner class
	//

	class ShutdownHook extends Thread {
		public ShutdownHook() {
			super();
		}

		@Override
		public void run() {
			System.out.println("Shutdown hook running");
			try {
				CacheManager.getInstance().shutdown();
			} catch (final CacheException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static void main(final String[] args_) throws CacheException {
		for (int i = 0; i < args_.length; i++) {
			System.out.println(">>args[" + i + "]:" + args_[i]);
		}

		/*
		 * if (args_.length != 1) { System.out.println("Usage: <JVM>
		 * TestShutdown <config> log4j.properties <config>");
		 * System.out.println("where <config> is the configuration file.");
		 * System.exit(1); }
		 */

		// PropertyConfigurator.configure(args_[0]);
		final TestShutdown test = new TestShutdown();
		test.doIt();
	}

	public TestShutdown() {
		super();

		final ShutdownHook shutdownHook = new ShutdownHook();
		Runtime.getRuntime().addShutdownHook(shutdownHook);
	}

	private void doIt() {
		try {
			final Cache c = CacheManager.getInstance().getCache();
			c.store("hey", "there");
		} catch (final CacheException e) {
			e.printStackTrace();
		}

		try {
			System.out.println("3 seconds till shutdown...");
			Thread.sleep(3000);
		} catch (final InterruptedException e) {
			e.printStackTrace();
		}
	}

}