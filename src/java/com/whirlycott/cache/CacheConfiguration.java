package com.whirlycott.cache;

import java.util.HashMap;
import java.util.Map;

/*

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

/**
 * Represents the configuration for an individual Cache object.
 * 
 * @author <a href="mailto:peter.royal@pobox.com">peter royal</a>
 */
public class CacheConfiguration {

	private final Map attributes = new HashMap();

	/**
	 * The backend implementation of ManagedCache that this cache will use.
	 */
	private String backend;

	/**
	 * The maximum number of elements that can be in this cache. This is a soft
	 * limit.
	 */
	private int maxSize;

	/**
	 * The name of the cache.
	 */
	private String name;

	/**
	 * The eviction policy that this cache will use.
	 */
	private String policy;

	/**
	 * The time between eviction runs.
	 */
	private int tunerSleepTime;

	public String getAttribute(final String attribute) {
		return (String) attributes.get(attribute);
	}

	public String getBackend() {
		return backend;
	}

	public int getMaxSize() {
		return maxSize;
	}

	public String getName() {
		return name;
	}

	public String getPolicy() {
		return policy;
	}

	public int getTunerSleepTime() {
		return tunerSleepTime;
	}

	public void setAttribute(final String attribute, final String value) {
		attributes.put(attribute, value);
	}

	public void setBackend(final String backend) {
		this.backend = backend;
	}

	public void setMaxSize(final int maxSize) {
		this.maxSize = maxSize;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public void setPolicy(final String policy) {
		this.policy = policy;
	}

	public void setTunerSleepTime(final int tunerSleepTime) {
		this.tunerSleepTime = tunerSleepTime;
	}
}