/*
 * Created on Oct 20, 2004 by pjacob
 *
 */
package com.whirlycott.cache.benchmarks;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author pjacob
 * 
 */
public class AnotherHashmap implements Map {

	private Map map = new HashMap();

	/**
	 * 
	 */
	public AnotherHashmap() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 */
	public synchronized void clear() {
		map.clear();
	}

	/**
	 * @param key
	 * @return
	 */
	public synchronized boolean containsKey(final Object key) {
		return map.containsKey(key);
	}

	/**
	 * @param value
	 * @return
	 */
	public synchronized boolean containsValue(final Object value) {
		return map.containsValue(value);
	}

	/**
	 * @return
	 */
	public synchronized Set entrySet() {
		return map.entrySet();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public synchronized boolean equals(final Object obj) {
		return map.equals(obj);
	}

	/**
	 * @param key
	 * @return
	 */
	public synchronized Object get(final Object key) {
		return map.get(key);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public synchronized int hashCode() {
		return map.hashCode();
	}

	/**
	 * @return
	 */
	public synchronized boolean isEmpty() {
		return map.isEmpty();
	}

	/**
	 * @return
	 */
	public synchronized Set keySet() {
		return map.keySet();
	}

	/**
	 * @param key
	 * @param value
	 * @return
	 */
	public synchronized Object put(final Object key, final Object value) {
		return map.put(key, value);
	}

	/**
	 * @param t
	 */
	public synchronized void putAll(final Map t) {
		map.putAll(t);
	}

	/**
	 * @param key
	 * @return
	 */
	public synchronized Object remove(final Object key) {
		return map.remove(key);
	}

	/**
	 * @return
	 */
	public synchronized int size() {
		return map.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public synchronized String toString() {
		return map.toString();
	}

	/**
	 * @return
	 */
	public synchronized Collection values() {
		return map.values();
	}
}
