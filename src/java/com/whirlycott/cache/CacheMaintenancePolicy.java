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

package com.whirlycott.cache;

/**
 * This interface defines the operations in a cache maintenance policy. A cache
 * maintenance policy defines the set of rules that are used to automatically
 * expire items that are stored in a particular cache.
 * 
 * All policies must implement this interface and be configured in the
 * whirlycache.xml configuration file.
 * 
 * Each Cache has its own independent policy.
 * 
 * @author Phil Jacob
 */
public interface CacheMaintenancePolicy {

	/**
	 * Set the ManagedCache instance to which this policy applies.
	 * 
	 * @param _cache
	 *            ManagedCache instance.
	 */
	public void setCache(ManagedCache _cache);

	/**
	 * Provide a configuration for this policy.
	 * 
	 * @param _configuration
	 *            Configuration to use for this policy.
	 */
	public void setConfiguration(CacheConfiguration _configuration);

	/**
	 * Performs housekeeping on the associated ManagedCache instance (typically
	 * removes Items based on varying criteria).
	 */
	public void performMaintenance();
}
