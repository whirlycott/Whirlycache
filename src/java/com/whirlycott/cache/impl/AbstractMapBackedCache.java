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

package com.whirlycott.cache.impl;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import com.whirlycott.cache.ManagedCache;

/**
 * This is an abstract class for help in developing a cache backend using an
 * implementation of java.util.Map.
 * 
 * @author Phil Jacob
 */
public abstract class AbstractMapBackedCache implements ManagedCache {

	/** Underlying Map to store cached objects in. */
	protected Map c;

	public void destroy() {
		c.clear();
		c = null;
	}

	public abstract void setMostlyRead(final boolean _mostlyRead);

	public int size() {
		return c.size();
	}

	public void clear() {
		c.clear();
	}

	public boolean isEmpty() {
		return c.isEmpty();
	}

	public boolean containsKey(final Object key) {
		return c.containsKey(key);
	}

	public boolean containsValue(final Object value) {
		return c.containsValue(value);
	}

	public Collection values() {
		return c.values();
	}

	public void putAll(final Map t) {
		c.putAll(t);
	}

	public Set entrySet() {
		return c.entrySet();
	}

	public Set keySet() {
		return c.keySet();
	}

	public Object get(final Object key) {
		return c.get(key);
	}

	public Object remove(final Object key) {
		return c.remove(key);
	}

	public Object put(final Object key, final Object value) {
		return c.put(key, value);
	}

	public void store(final Object key, final Object value) {
		c.put(key, value);
	}

	public Object retrieve(final Object key) {
		return c.get(key);
	}

}
