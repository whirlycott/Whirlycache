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
 * Defines a simple interface that all caches should implement.
 * 
 * @author phil
 */
public interface Cache {

	/**
	 * Retrieve an object from the cache.
	 * 
	 * @param key
	 *            key associated with desired object.
	 * @return object identified by provided key.
	 */
	public Object retrieve(Object key);

	/**
	 * Retrieve an object whose key implements Cacheable. The onRetrieve()
	 * method will be executed here.
	 * 
	 * @param key
	 * @return the object retrieved from the cache.
	 */
	public Object retrieve(Cacheable key);

	/**
	 * Store an object in the cache.
	 * 
	 * @param key
	 *            key associated with object to store.
	 * @param value
	 *            object identified by provided key.
	 */
	public void store(Object key, Object value);

	/**
	 * Store an object in the cache with a Cacheable key. The onStore() method
	 * will be executed here.
	 * 
	 * @param key
	 * @param value
	 */
	public void store(Cacheable key, Object value);

	/**
	 * Store an object in the cache.
	 * 
	 * @param key
	 *            - the key to retrieve the object later.
	 * @param value
	 *            - the object to be cached.
	 * @param expireTime
	 *            - milliseconds that this item should be kept in the cache
	 *            (accurate to <code>tuner-sleeptime</code> seconds, as
	 *            specified in <code>whirlycache.xml</code>.
	 */
	public void store(Object key, Object value, long expireTime);

	/**
	 * Store an object in the cache with a Cacheable key. The onStore() method
	 * is execute here.
	 * 
	 * @param key
	 *            - the key to retrieve the object later.
	 * @param value
	 *            - the object to be cached.
	 * @param expireTime
	 *            - milliseconds that this item should be kept in the cache
	 *            (accurate to <code>tuner-sleeptime</code> seconds, as
	 *            specified in <code>whirlycache.xml</code>.
	 */
	public void store(Cacheable key, Object value, long expireTime);

	/**
	 * Remove an object from the cache.
	 * 
	 * @param key
	 *            key associated with object to remove.
	 * @return object that was removed.
	 */
	public Object remove(Object key);

	/**
	 * Removes an object from the cache and executes the onRemove() method.
	 * 
	 * @param key
	 * @return object that was removed
	 */
	public Object remove(Cacheable key);

	/**
	 * Clear the cache.
	 */
	public void clear();

	/**
	 * Get the current size of the cache.
	 */
	public int size();

}
