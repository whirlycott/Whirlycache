/*
 * Created on Dec 19, 2004 by pjacob
 *
 */
package com.whirlycott.cache.hibernate;

import net.sf.hibernate.cache.Timestamper;

import org.apache.log4j.Logger;

import com.whirlycott.cache.Cache;
import com.whirlycott.cache.CacheException;
import com.whirlycott.cache.CacheManager;
import com.whirlycott.cache.Messages;

/**
 * @author pjacob
 * 
 */
public class WhirlycachePlugin implements net.sf.hibernate.cache.Cache {

	/**
	 * Logger.
	 */
	private static final Logger log = Logger.getLogger(WhirlycachePlugin.class);

	/**
	 * Number of milliseconds in 1 minute.
	 */
	private static final int MS_PER_MINUTE = 60000;

	/**
	 * Reference to the Whirlycache that we are going to use.
	 */
	private Cache cache;

	/**
	 * Name of the cache we're using.
	 */
	private final String cacheName;

	/**
	 * 
	 * @param _name
	 */
	public WhirlycachePlugin(final String _name) throws net.sf.hibernate.cache.CacheException {
		super();

		// Short circuit if there's any nonsense.
		if (_name == null) {
			throw new IllegalArgumentException(Messages.getString("WhirlycachePlugin.cannot_lookup_cache_with_null_name")); //$NON-NLS-1$
		}

		// Store the cache name away for using with the destroy() method.
		cacheName = _name;

		try {
			cache = CacheManager.getInstance().getCache(_name);
		} catch (final IllegalArgumentException e) {
			// Rethrow this whirlycache-specific exception as a hibernate
			// exception.
			throw new net.sf.hibernate.cache.CacheException(e.getMessage());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.hibernate.cache.Cache#clear()
	 */
	public void clear() throws net.sf.hibernate.cache.CacheException {
		cache.clear();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.hibernate.cache.Cache#destroy()
	 */
	public void destroy() throws net.sf.hibernate.cache.CacheException {
		try {
			CacheManager.getInstance().destroy(cacheName);
		} catch (final CacheException e) {
			log.error(e.getMessage(), e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.hibernate.cache.Cache#get(java.lang.Object)
	 */
	public Object get(final Object _key) throws net.sf.hibernate.cache.CacheException {
		return cache.retrieve(_key);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.hibernate.cache.Cache#getTimeout()
	 */
	public int getTimeout() {
		return Timestamper.ONE_MS * MS_PER_MINUTE;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.hibernate.cache.Cache#lock(java.lang.Object)
	 */
	public void lock(final Object arg0) throws net.sf.hibernate.cache.CacheException {
		return;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.hibernate.cache.Cache#nextTimestamp()
	 */
	public long nextTimestamp() {
		return Timestamper.next();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.hibernate.cache.Cache#put(java.lang.Object, java.lang.Object)
	 */
	public void put(final Object _key, final Object _val) throws net.sf.hibernate.cache.CacheException {
		cache.store(_key, _val);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.hibernate.cache.Cache#remove(java.lang.Object)
	 */
	public void remove(final Object _key) throws net.sf.hibernate.cache.CacheException {
		cache.remove(_key);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.hibernate.cache.Cache#unlock(java.lang.Object)
	 */
	public void unlock(final Object arg0) throws net.sf.hibernate.cache.CacheException {
		return;
	}

}
