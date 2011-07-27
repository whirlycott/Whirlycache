/*
 * Created on Oct 18, 2004 by pjacob
 *
 */
package com.whirlycott.cache.policy;

import com.whirlycott.cache.CacheConfiguration;
import com.whirlycott.cache.CacheMaintenancePolicy;
import com.whirlycott.cache.ManagedCache;

/**
 * This policy does nothing and is sometimes useful for debugging.
 * 
 * @author pjacob
 * 
 */
public class NullPolicy implements CacheMaintenancePolicy {

	/**
	 * 
	 */
	public NullPolicy() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.whirlycott.cache.CacheMaintenancePolicy#setCache(com.whirlycott.cache
	 * .ManagedCache)
	 */
	public void setCache(final ManagedCache _cache) {
		// Empty
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.whirlycott.cache.CacheMaintenancePolicy#setConfiguration(com.whirlycott
	 * .cache.CacheConfiguration)
	 */
	public void setConfiguration(final CacheConfiguration _configuration) {
		// Empty
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.whirlycott.cache.CacheMaintenancePolicy#performMaintenance()
	 */
	public void performMaintenance() {
		// Empty
	}

}
