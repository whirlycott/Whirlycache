/*
 * Created on Oct 14, 2004 by pjacob
 *
 */
package com.whirlycott.cache.benchmarks;

/**
 * Encapsulates the result of a benchmark.
 * 
 * @author pjacob
 * 
 */
public class Result {

	private Benchmarkable benchmarkable;

	private long time;

	/**
	 * @return Returns the time.
	 */
	public long getTime() {
		return time;
	}

	/**
	 * @param time
	 *            The time to set.
	 */
	public void setTime(final long time) {
		this.time = time;
	}

	/**
	 * 
	 */
	public Result(final Benchmarkable _b) {
		super();
		benchmarkable = _b;
	}

	/**
	 * @return
	 */
	public String getName() {
		return benchmarkable.getName();
	}

	/**
	 * @return Returns the benchmarkable.
	 */
	public Benchmarkable getBenchmarkable() {
		return benchmarkable;
	}

	/**
	 * @param benchmarkable
	 *            The benchmarkable to set.
	 */
	public void setBenchmarkable(final Benchmarkable benchmarkable) {
		this.benchmarkable = benchmarkable;
	}
}