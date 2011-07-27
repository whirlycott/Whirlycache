/*
 * Created on Oct 11, 2004 by pjacob
 *
 */
package com.whirlycott.cache.benchmarks;

/**
 * Holds the input parameters for the benchmark.
 * 
 * @author pjacob
 * 
 */
public class Params {

	final private int cacheSize;

	final private int threads;

	public Params(final int _cacheSize, final int _threads) {
		cacheSize = _cacheSize;
		threads = _threads;
	}

	/**
	 * @return Returns the cacheSize.
	 */
	public int getCacheSize() {
		return cacheSize;
	}

	/**
	 * @return Returns the readers.
	 */
	public int getThreads() {
		return threads;
	}

	public String getShortName() {
		return "c" + getCacheSize() + "/" + "r" + getThreads() + "/";
	}

}
