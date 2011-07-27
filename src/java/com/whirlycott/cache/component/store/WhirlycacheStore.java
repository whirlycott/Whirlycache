/*
Copyright 2004
Peter Royal <peter.royal@pobox.com>

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

package com.whirlycott.cache.component.store;

import java.io.IOException;
import java.util.Enumeration;

import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.thread.ThreadSafe;
import org.apache.excalibur.store.Store;

import com.whirlycott.cache.Cache;
import com.whirlycott.cache.CacheConfiguration;
import com.whirlycott.cache.CacheDecorator;
import com.whirlycott.cache.CacheMaintenancePolicy;
import com.whirlycott.cache.Constants;
import com.whirlycott.cache.ManagedCache;
import com.whirlycott.cache.Messages;

/**
 * Implementation of the <a href="http://excalibur.apache.org/store/">Apache
 * Excalibur Store</a> using Whirlycache as the backend.
 * 
 * @author <a href="mailto:peter.royal@pobox.com">peter royal</a>
 */
public class WhirlycacheStore implements Store, ThreadSafe, Configurable, Initializable, Disposable {

	/**
	 * This is the cache that holds the data that we are going to use.
	 */
	private Cache cache;

	/**
	 * Represents the cache configuration for this specific store.
	 */
	private CacheConfiguration cacheConfiguration;

	/**
	 * The class that implements the Policy interface.
	 */
	private Class cacheMaintenancePolicyClass;

	/**
	 * The Class that implements the backend ManagedCache interface.
	 */
	private Class managedCacheClass;

	/**
	 * Clears all items in the cache.
	 */
	public void clear() {
		cache.clear();
	}

	/**
	 * Configure the cache using a Configuration object.
	 */
	public void configure(final Configuration configuration) throws ConfigurationException {
		final Configuration backend = configuration.getChild(Constants.CONFIG_BACKEND);
		final Configuration policy = configuration.getChild(Constants.CONFIG_POLICY);

		cacheConfiguration = new CacheConfiguration();

		cacheConfiguration.setName(configuration.getChild(Constants.CONFIG_NAME).getValue(toString()));
		cacheConfiguration.setBackend(backend.getValue());
		cacheConfiguration.setMaxSize(configuration.getChild(Constants.CONFIG_MAXSIZE).getValueAsInteger());
		cacheConfiguration.setPolicy(policy.getValue());
		cacheConfiguration.setTunerSleepTime(configuration.getChild(Constants.CONFIG_TUNER_SLEEPTIME).getValueAsInteger());

		managedCacheClass = loadManagedCacheClass(backend.getLocation());
		cacheMaintenancePolicyClass = loadCacheMaintenancePolicyClass(policy.getLocation());
	}

	/**
	 * I can't imagine why anybody would rely on this method.
	 */
	public boolean containsKey(final Object key) {
		boolean retval = false;
		if (cache.retrieve(key) != null) {
			retval = true;
		}
		return retval;
	}

	private CacheMaintenancePolicy[] createCacheMaintenancePolicies(final ManagedCache cache) throws IllegalAccessException, InstantiationException {
		final CacheMaintenancePolicy policy = (CacheMaintenancePolicy) cacheMaintenancePolicyClass.newInstance();

		policy.setCache(cache);
		policy.setConfiguration(cacheConfiguration);

		return new CacheMaintenancePolicy[] { policy };
	}

	/**
	 * Shuts down the Whirlycache and its associated tuning thread.
	 */
	public void dispose() {
		cache.clear();
		/*
		 * The field 'cache' is really a CacheDecorator. We don't expose it
		 * because it shouldn't be used directly. But we'll cast it here.
		 */
		((CacheDecorator) cache).shutdown();
	}

	/**
	 * This method is not supported by Whirlycache.
	 */
	public void free() {
		// we don't support this
	}

	/**
	 * Gets an object out of the whirlycache
	 */
	public Object get(final Object key) {
		return cache.retrieve(key);
	}

	/**
	 * Initializes a Whirlycache.
	 */
	public void initialize() throws Exception {
		final ManagedCache managedCache = (ManagedCache) managedCacheClass.newInstance();
		cache = new CacheDecorator(managedCache, cacheConfiguration, createCacheMaintenancePolicies(managedCache));
	}

	/**
	 * We don't support keys().
	 */
	public Enumeration keys() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Loads up the specified cache maintenance class
	 * 
	 * @param _location
	 * @return
	 * @throws ConfigurationException
	 */
	private Class loadCacheMaintenancePolicyClass(final String _location) throws ConfigurationException {
		final String policy = cacheConfiguration.getPolicy();

		try {
			final Class clazz = Thread.currentThread().getContextClassLoader().loadClass(policy);

			if (!CacheMaintenancePolicy.class.isAssignableFrom(clazz)) {
				final Object[] args = { policy, _location };
				throw new ConfigurationException(Messages.getCompoundString("WhirlycacheStore.not_cache_maintenance_policy", args)); //$NON-NLS-1$
			}

			return clazz;
		} catch (final ClassNotFoundException e) {
			final Object[] args = { policy, _location };
			throw new ConfigurationException(Messages.getCompoundString("WhirlycacheStore.cannot_load_policy", args), e); //$NON-NLS-1$
		}
	}

	/**
	 * Loads up the specified managed cache class.
	 * 
	 * @param _location
	 * @return
	 * @throws ConfigurationException
	 */
	private Class loadManagedCacheClass(final String _location) throws ConfigurationException {
		final String backend = cacheConfiguration.getBackend();

		try {
			final Class clazz = Thread.currentThread().getContextClassLoader().loadClass(backend);

			if (!ManagedCache.class.isAssignableFrom(clazz)) {
				final Object[] args = { backend, _location };
				throw new ConfigurationException(Messages.getCompoundString("WhirlycacheStore.not_managed_cache", args)); //$NON-NLS-1$
			}

			return clazz;
		} catch (final ClassNotFoundException e) {
			final Object[] args = { backend, _location };
			throw new ConfigurationException(Messages.getCompoundString("WhirlycacheStore.cannot_load_backend", args), e); //$NON-NLS-1$
		}
	}

	/**
	 * Removes the specified object from the cache.
	 */
	public void remove(final Object key) {
		cache.remove(key);
	}

	/**
	 * Returns the number of items in the cache.
	 */
	public int size() {
		return cache.size();
	}

	/**
	 * Stores a value in the cache that can be retrieved using 'key'.
	 */
	public void store(final Object key, final Object value) throws IOException {
		cache.store(key, value);
	}
}