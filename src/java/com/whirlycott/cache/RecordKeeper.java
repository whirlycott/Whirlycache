/*
 Copyright 2004      Philip Jacob <phil@whirlycott.com>
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

package com.whirlycott.cache;

import java.io.Serializable;

/**
 * Encapsulates runtime stats for a Cache.
 * 
 * @author phil
 */
public class RecordKeeper implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8354128118267818665L;

	/** The total number of cache hits. */
	private volatile long hits;

	/** Lock used for the hits value. */
	private final Object hitLock = new Object();

	/** The total number of cache questions. */
	private volatile long totalOperations;

	/** Lock used for the operation value. */
	private final Object operationLock = new Object();

	private long totalOperationsStartTuneCycle;

	private long queriesPerSecond;

	/**
	 * @return Returns the hits.
	 */
	public long getHits() {
		return hits;
	}

	/**
	 * @param hits
	 *            The hits to set.
	 */
	public void setHits(final long hits) {
		this.hits = hits;
	}

	/**
	 * @return Returns the totalOperations.
	 */
	public long getTotalOperations() {
		return totalOperations;
	}

	/** Increment the total operation counter. */
	public void incrementTotalOperations() {
		synchronized (operationLock) {
			totalOperations++;
		}
	}

	/** Increment hits. */
	public void incrementHits() {
		synchronized (hitLock) {
			hits++;
		}
	}

	public void startTuneCycle() {
		totalOperationsStartTuneCycle = totalOperations;
	}

	public void calculateQueriesPerSecond(final long sleepTime) {
		if (sleepTime > 0L) {
			queriesPerSecond = (totalOperations - totalOperationsStartTuneCycle) / (sleepTime / 1000L);
		} else {
			queriesPerSecond = 0;
		}
	}

	public long getQueriesPerSecond() {
		return queriesPerSecond;
	}

	/**
	 * Reset the values.
	 */
	public void reset() {
		totalOperations = 0L;
		hits = 0L;
	}
}
