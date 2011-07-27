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
 * Wraps an item in the cache and records some information about when it was
 * last used, added, etc.
 * 
 * @author Philip Jacob
 */
public class Item {

	protected Object item;

	/** Relative time that the Item was added to the cache. */
	protected long added;

	/** Relative time that the Item was last used. */
	protected long used;

	/** Expire this Item after this much time. */
	protected long expiresAfter = -1;

	/** Number of times that the Item has been accessed. */
	protected volatile long count;

	/**
	 * Lock for the counter.
	 */
	protected final Object countLock = new Object();

	public Item(final Object _item, final long _added, final long _expiresTime) {
		item = _item;
		added = _added;
		expiresAfter = _expiresTime;
	}

	/**
	 * @return Returns the item.
	 */
	public Object getItem() {
		return item;
	}

	void setUsed(final long _used) {
		used = _used;
	}

	void incrementCount() {
		synchronized (countLock) {
			count++;
		}
	}

	public synchronized long getAdded() {
		return added;
	}

	public synchronized long getCount() {
		return count;
	}

	public synchronized long getUsed() {
		return used;
	}

	public synchronized void setCount(final long count) {
		this.count = count;
	}

	public long getExpiresAfter() {
		return expiresAfter;
	}
}
