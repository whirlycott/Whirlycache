/*
 * Created on Oct 11, 2004 by pjacob
 *
 */
package com.whirlycott.cache.benchmarks;

/**
 * Caches that we benchmark need to implement this interface.
 * 
 * @author pjacob
 * 
 */
public interface Benchmarkable extends Runnable {

	public Result benchmark(final Params _params);

	public String getName();

	public Params getParams();

}
