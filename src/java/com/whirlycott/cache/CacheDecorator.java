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

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;

import com.whirlycott.cache.policy.ExpirationTimePredicate;

/**
 * Owns the cache tuning thread and provides housekeeping facilities for
 * ManagedCache implementations. One CacheDecorator is created for each Cache
 * named in the whirlycache.xml configuration file.
 * 
 * @author Phil Jacob
 */
public class CacheDecorator implements Runnable, Cache {

	/** Logger */
	private final static Logger log = Logger.getLogger(CacheDecorator.class);

	/**
	 * This is the memory size for the adaptive caching in implementations that
	 * support it.
	 */
	// TODO - make this a percentage of the max size or something...?
	protected int adaptiveMemorySize = 5000;

	/** Overflow buffer for the adaptive array. */
	protected final int adaptiveMemorySizeOverflow = 512;

	/** The counter for our current position in the adaptive result array. */
	protected volatile int adaptiveResultCounter = 0;

	/** The current time (accurate to sleepTime ms). */
	protected volatile long currentTime = System.currentTimeMillis();

	/** The array that holds the adaptive results. */
	protected volatile int adaptiveResults[];

	/** This is the cache we're managing. */
	protected volatile ManagedCache managedCache;

	/** The soft limit of the max number of items that can be put in the cache. */
	protected int maxSize;

	/** Name of this cache */
	protected final String name;

	/** The policy used during maintenance. */
	private final CacheMaintenancePolicy policy;

	/** We keep all of the stats in the recordkeeper. */
	protected final RecordKeeper recordKeeper = new RecordKeeper();

	/** There's a default sleep time of 10 seconds. */
	protected long sleepTime = 10000L;

	/**
	 * Reference to the cache tuning thread.
	 */
	protected Thread tunerThread;

	/**
	 * Constructor for a CacheDecorator.
	 * 
	 * @param _managedCache
	 * @param _configuration
	 * @param policies
	 */
	public CacheDecorator(final ManagedCache _managedCache, final CacheConfiguration _configuration, final CacheMaintenancePolicy[] policies) {

		name = _configuration.getName();

		if (null == name) {
			throw new IllegalArgumentException(Messages.getString("CacheDecorator.cache_config_cannot_be_null")); //$NON-NLS-1$
		}

		if (null == policies) {
			throw new IllegalArgumentException(Messages.getString("CacheDecorator.policies_cannot_be_null")); //$NON-NLS-1$
		}

		/*
		 * See: https://whirlycache.dev.java.net/issues/show_bug.cgi?id=7 TODO -
		 * once we support multiple policies, this needs to be removed.
		 */
		if (1 != policies.length) {
			throw new IllegalArgumentException(Messages.getString("CacheDecorator.must_provide_single_policy")); //$NON-NLS-1$
		}

		policy = policies[0];

		// Adaptive size plus the buffer (to prevent
		// ArrayIndexOutOfBoundsExceptions)s
		adaptiveResults = new int[adaptiveMemorySize + adaptiveMemorySizeOverflow];

		// This is the cache which we are managing.
		managedCache = _managedCache;

		// Do some configuration.
		configure(_configuration);

		// Start up the management thread.
		tunerThread = new Thread(this);
		final Object[] args = { name };
		tunerThread.setName(Messages.getCompoundString("CacheDecorator.whirlycache_tuner", args)); //$NON-NLS-1$
		tunerThread.setDaemon(true);
		tunerThread.start();
	}

	/**
	 * Clears the cache.
	 */
	public void clear() {
		log.info(Messages.getString("CacheDecorator.clearing_cache")); //$NON-NLS-1$
		managedCache.clear();
		Arrays.fill(adaptiveResults, 0);
	}

	/**
	 * Configures the Cache being decorated.
	 */
	protected void configure(final CacheConfiguration configuration) {
		// Set up the max size.
		setMaxSize(configuration.getMaxSize());

		// Sleeptime.
		setSleepTime(configuration.getTunerSleepTime() * 1000L);
	}

	/**
	 * Looks at the last 'n' queries to determine whether the Cache should turn
	 * on optimizations for a mostly-read environment (if the underlying
	 * implementation of ManagedCache supports this).
	 * 
	 * @param _value
	 *            varies depending on whether this was a read, write, or
	 *            removal.
	 */
	protected void doAdaptiveAccounting(final int _value) {
		// We only care about the last 'n' adaptiveResults.
		final int currentCounter = adaptiveResultCounter;
		if (currentCounter >= adaptiveMemorySize) {
			adaptiveResultCounter = 0;
			adaptiveResults[0] = _value;
		} else {
			adaptiveResults[currentCounter] = _value;
			adaptiveResultCounter++;
		}
	}

	/**
	 * @return Returns the adaptiveMemorySize.
	 */
	protected int getAdaptiveMemorySize() {
		return adaptiveMemorySize;
	}

	/**
	 * Calculates the adaptive hit rate for this cache.
	 * 
	 * @return adaptive hit ratio.
	 */
	public float getAdaptiveRatio() {
		final int copy[] = new int[adaptiveMemorySize];
		System.arraycopy(adaptiveResults, 0, copy, 0, adaptiveMemorySize);
		int positives = 0;
		for (final int element : copy) {
			if (element == 1) {
				positives++;
			}
		}
		// log.info("Positives: " + positives + "; Total: " +
		// adaptiveMemorySize);
		return new Float(positives).floatValue() / new Float(adaptiveMemorySize).floatValue();
	}

	/**
	 * Returns an efficiency report string for this cache.
	 * 
	 * @return efficiency report for this cache.
	 */
	public String getEfficiencyReport() {
		final Object[] args = { new Integer(managedCache.size()), new Long(recordKeeper.getTotalOperations()), new Long(recordKeeper.getHits()), new Float(getAdaptiveRatio()),
				new Float(getTotalHitrate())

		};
		return Messages.getCompoundString("CacheDecorator.efficiency_report", args); //$NON-NLS-1$
	}

	/**
	 * Get the maximum size of the cache.
	 * 
	 * @return Returns the maxSize.
	 */
	protected int getMaxSize() {
		return maxSize;
	}

	/**
	 * @return Returns the policy in use for managing this cache.
	 */
	protected CacheMaintenancePolicy getPolicy() {
		return policy;
	}

	/**
	 * @return Returns the sleepTime.
	 */
	protected long getSleepTime() {
		return sleepTime;
	}

	/**
	 * Returns the total hitrate since this Cache was started.
	 * 
	 * @return Total hitrate.
	 */
	protected float getTotalHitrate() {
		return new Long(recordKeeper.getHits()).floatValue() / new Long(recordKeeper.getTotalOperations()).floatValue();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.whirlycott.cache.Cache#remove(com.whirlycott.cache.Cacheable)
	 */
	public Object remove(final Cacheable _key) {
		final Object o = internalRemove(_key);
		_key.onRemove(o);
		return o;
	}

	/**
	 * Removes an Object from the Cache and returns the removed Object.
	 * 
	 * @param _key
	 *            key associated with object to remove.
	 */
	public Object remove(final Object _key) {
		return internalRemove(_key);
	}

	/**
	 * An internal remove operation.
	 * 
	 * @param _key
	 * @return the object that was removed
	 */
	protected Object internalRemove(final Object _key) {
		if (Constants.BUILD_STATS_ENABLED) {
			recordKeeper.incrementTotalOperations();
		}

		if (_key != null) {
			final Item cachedItem = (Item) managedCache.remove(_key);

			// The compiler will optimize this.
			if (Constants.BUILD_STATS_ENABLED) {
				doAdaptiveAccounting(0);
			}

			return null == cachedItem ? null : cachedItem.getItem();
		} else {
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.whirlycott.cache.Cache#retrieve(com.whirlycott.cache.Cacheable)
	 */
	public Object retrieve(final Cacheable _key) {
		final Object o = internalRetrieve(_key);
		_key.onRetrieve(o);
		return o;
	}

	protected Object internalRetrieve(final Object _key) {
		// Record a read.
		if (Constants.BUILD_STATS_ENABLED) {
			doAdaptiveAccounting(1);

			// Increment the number of totalQuestions.
			recordKeeper.incrementTotalOperations();
		}

		// Set up the return value
		final Item cachedItem = (Item) managedCache.get(_key);
		if (cachedItem != null) {

			// Bump the numbers.
			if (Constants.BUILD_STATS_ENABLED) {
				cachedItem.setUsed(recordKeeper.getTotalOperations());
				cachedItem.incrementCount();
			}

			// Increment the adaptive algorithm and the hitcounter.
			final Object retval = cachedItem.getItem();
			if (retval != null && Constants.BUILD_STATS_ENABLED) {
				recordKeeper.incrementHits();
			}

			return retval;
		} else {

			// Found nothing inside the cache.
			return null;
		}
	}

	/**
	 * Gets an Object from the Cache.
	 */
	public Object retrieve(final Object _key) {
		return internalRetrieve(_key);
	}

	/**
	 * Starts up the background maintenance thread.
	 */
	public void run() {
		log.debug(Messages.getString("CacheDecorator.tuning_thread_started")); //$NON-NLS-1$

		final Thread t = Thread.currentThread();

		try {
			while (tunerThread == t) {
				// updates the cache's notion of the "current time"
				if (Constants.ITEM_EXPIRATION_ENABLED) {
					currentTime = System.currentTimeMillis();
				}

				if (Constants.BUILD_STATS_ENABLED) {
					recordKeeper.startTuneCycle();

					// Adapt to mostly read or mostly write.
					tuneCache();
				}

				// expire Items in need of expiration
				// TODO move this to its own policy
				if (Constants.ITEM_EXPIRATION_ENABLED) {
					expireItems();
				}

				// Perform tuning and maintenance.
				policy.performMaintenance();

				try {
					// Sleep for a bit
					Thread.sleep(sleepTime);
				} catch (final InterruptedException e) {
					log.info(Messages.getString("CacheDecorator.tuning_thread_interrupted")); //$NON-NLS-1$
				}

				if (Constants.BUILD_STATS_ENABLED) {

					if (sleepTime > 0L) {
						recordKeeper.calculateQueriesPerSecond(sleepTime);
					}

					logStatistics();
				}

				if (log.isDebugEnabled()) {
					log.debug(Messages.getString("CacheDecorator.cache_tuning_complete")); //$NON-NLS-1$
				}
			}
		} catch (final RuntimeException e) {
			log.fatal(Messages.getString("CacheDecorator.unexpected_shutdown"), e); //$NON-NLS-1$
		}

		log.debug(Messages.getString("CacheDecorator.shutting_down")); //$NON-NLS-1$
	}

	/**
	 * Log some cache usage data depending on some conditions.
	 * 
	 */
	protected void logStatistics() {
		if (sleepTime > 0L && log.isDebugEnabled()) {
			final Object[] args = { new Long(recordKeeper.getQueriesPerSecond()) };
			log.debug(Messages.getCompoundString("CacheDecorator.query_rate", args)); //$NON-NLS-1$
		}

		// Print the efficiency report
		if (log.isInfoEnabled()) {
			log.info(getEfficiencyReport());
		}
	}

	/**
	 * @param adaptiveMemorySize
	 *            The adaptiveMemorySize to set.
	 */
	protected void setAdaptiveMemorySize(final int adaptiveMemorySize) {
		this.adaptiveMemorySize = adaptiveMemorySize;
	}

	/**
	 * Specify the ManagedCache that this Decorator owns.
	 * 
	 * @param cache
	 *            The ManagedCache to set.
	 */
	protected void setManagedCache(final ManagedCache cache) {
		managedCache = cache;
		log.debug(Messages.getString("CacheDecorator.managing_cache_with_type") + cache.getClass()); //$NON-NLS-1$
	}

	/**
	 * @param maxSize
	 *            The maxSize to set.
	 */
	protected void setMaxSize(final int maxSize) {
		this.maxSize = maxSize;
	}

	/**
	 * Tells the underlying ManagedCache implementation to turn on optimizations
	 * for a mostly-read environment (if supported).
	 * 
	 * @param _mostlyRead
	 *            whether most operations are expected to be reads.
	 */
	public void setMostlyRead(final boolean _mostlyRead) {
		if (Constants.BUILD_STATS_ENABLED) {
			recordKeeper.incrementTotalOperations();
		}

		managedCache.setMostlyRead(_mostlyRead);
	}

	/**
	 * @param sleepTime
	 *            The sleepTime to set.
	 */
	protected void setSleepTime(final long sleepTime) {
		this.sleepTime = sleepTime;
	}

	/** Shut down this cache. */
	public void shutdown() {
		if (log.isDebugEnabled()) {
			log.debug(Messages.getString("CacheDecorator.shutting_down_cache") + name); //$NON-NLS-1$
		}
		if (Constants.BUILD_STATS_ENABLED) {
			log.info(getEfficiencyReport());
			recordKeeper.reset();
		}

		final Thread tunerThreadToKill = tunerThread;
		tunerThread = null;
		tunerThreadToKill.interrupt();
	}

	/**
	 * Returns the number of items in the Cache.
	 * 
	 * @return number of items in the cache.
	 */
	public int size() {
		if (Constants.BUILD_STATS_ENABLED) {
			recordKeeper.incrementTotalOperations();
		}

		return managedCache.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.whirlycott.cache.Cache#store(com.whirlycott.cache.Cacheable,
	 * java.lang.Object)
	 */
	public void store(final Cacheable key, final Object value) {
		internalStore(key, value, -1L);
		key.onStore(value);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.whirlycott.cache.Cache#store(com.whirlycott.cache.Cacheable,
	 * java.lang.Object, long)
	 */
	public void store(final Cacheable _key, final Object _value, final long _expiresAfter) {
		internalStore(_key, _value, _expiresAfter);
		_key.onStore(_value);
	}

	/**
	 * Store an object in the cache.
	 */
	public void store(final Object _key, final Object _value) {
		internalStore(_key, _value, -1L);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.whirlycott.cache.Cache#store(java.lang.Object, java.lang.Object,
	 * long)
	 */
	public void store(final Object _key, final Object _value, final long _expiresAfter) {
		internalStore(_key, _value, _expiresAfter);
	}

	/**
	 * All stores go through this.
	 * 
	 * @param _key
	 * @param _value
	 * @param _expiresAfter
	 *            specified in milliseconds
	 */
	protected void internalStore(final Object _key, final Object _value, final long _expiresAfter) {
		if (Constants.BUILD_STATS_ENABLED) {
			recordKeeper.incrementTotalOperations();
		}

		if (_key != null && _value != null) {
			final Item cachedValue = new Item(_value, currentTime, _expiresAfter);
			managedCache.put(_key, cachedValue);

			if (Constants.BUILD_STATS_ENABLED) {
				doAdaptiveAccounting(0);
			}
		}
	}

	/**
	 * Tunes the managed cache for a mostly read or write environment.
	 */
	protected void tuneCache() {
		final float adaptiveRatio = getAdaptiveRatio();
		if (adaptiveRatio > 0.5F) {
			log.debug(Messages.getString("CacheDecorator.read_optimizations_on") + adaptiveRatio); //$NON-NLS-1$
			managedCache.setMostlyRead(true);
		} else {
			log.debug(Messages.getString("CacheDecorator.read_optimizations_off") + adaptiveRatio); //$NON-NLS-1$
			managedCache.setMostlyRead(false);
		}
	}

	/**
	 * Expires Items in need of expiration.
	 */
	protected void expireItems() {
		// Sort the entries in the cache.
		final List entries = new LinkedList(new ConcurrentHashMap(managedCache).entrySet());
		CollectionUtils.filter(entries, new ExpirationTimePredicate(currentTime));
		final Object[] args = { new Integer(entries.size()) };
		log.debug(Messages.getCompoundString("CacheDecorator.expiration_count", args)); //$NON-NLS-1$
		for (final Iterator i = entries.iterator(); i.hasNext();) {
			final Map.Entry entry = (Entry) i.next();
			if (entry != null) {
				// log.trace("Removing: " + entry.getKey());
				managedCache.remove(entry.getKey());
			}
		}
	}

}