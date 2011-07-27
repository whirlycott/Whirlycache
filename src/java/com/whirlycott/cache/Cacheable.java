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
 * This is an <i>optional</i> interface for object keys which may be stored in
 * the cache. After each retrieve(), store() or remove() operation for a
 * particular Cache, the corresponding method of the object implementing this
 * interface is called.
 * 
 * The onRemove(), onStore() and onRetrieve() methods are called both by cache
 * policies and regular client calls on the Cache interface (i.e. operations
 * within policies also call these methods).
 * 
 * Implementing this interface will not result in any performance increase. On
 * the contrary, additional work per Cache operation will only serve to slow
 * things down.
 * 
 * @author Philip Jacob <a href="phil@whirlycott.com">phil@whirlycott.com</a>
 */
public interface Cacheable {

	/**
	 * Hook for actions to be performed during a retrieve() operation.
	 * 
	 * @param _value
	 *            The value being retrieved. Can possibly be a null value.
	 */
	public void onRetrieve(Object _value);

	/**
	 * Hook for actions to be performed during a store() operation.
	 * 
	 * @param _value
	 *            The value being stored. Can possibly be a null value.
	 */
	public void onStore(Object _value);

	/**
	 * Hook for actions to be performed during a remove() operation.
	 * 
	 * @param _value
	 *            The value being removed. Can possibly be a null value.
	 */
	public void onRemove(Object _value);
}
