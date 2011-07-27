/*
 * Created on Oct 15, 2004 by pjacob
 *
 */
package com.whirlycott.cache.test;

import org.apache.log4j.Logger;

import com.whirlycott.cache.Cacheable;

/**
 * Dummy test class.
 * 
 * @author pjacob
 * 
 */
public class DummyCacheable implements Cacheable {

	private static final Logger log = Logger.getLogger(DummyCacheable.class);

	/**
	 * 
	 */
	public DummyCacheable() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.whirlycott.cache.Cacheable#onRetrieve(java.lang.Object)
	 */
	public void onRetrieve(final Object _value) {
		log.debug("Executing onRetrieve()");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.whirlycott.cache.Cacheable#onStore(java.lang.Object)
	 */
	public void onStore(final Object _value) {
		log.debug("Executing onStore()");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.whirlycott.cache.Cacheable#onRemove(java.lang.Object)
	 */
	public void onRemove(final Object _value) {
		log.debug("Executing onRemove()");
	}

}
