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

package com.whirlycott.cache.policy;

import java.util.Map.Entry;

import org.apache.commons.collections.Predicate;
import org.apache.log4j.Logger;

import com.whirlycott.cache.Item;

/**
 * A predicate for filtering Collections of Items based on their expiration
 * time.
 * 
 * @author Seth Fitzsimmons
 */
public class ExpirationTimePredicate implements Predicate {

	/**
	 * Logger.
	 */
	private static final Logger log = Logger.getLogger(ExpirationTimePredicate.class);

	private final long currentTime;

	/**
	 * Creates an ExpirationTimePredicate.
	 * 
	 * @param currentTime
	 *            Cache's notion of the "current time."
	 */
	public ExpirationTimePredicate(final long currentTime) {
		this.currentTime = currentTime;
	}

	/**
	 * Only Items with an expiration time that has passed will cause this to
	 * return true.
	 */
	public boolean evaluate(final Object obj) {
		boolean retval = false;

		if (obj instanceof Entry) {
			if (((Entry) obj).getValue() instanceof Item) {
				final Item item = (Item) ((Entry) obj).getValue();
				if (item.getExpiresAfter() > 0) {
					/*
					 * log.debug("Current time: " + currentTime);
					 * log.debug("Expiration time: " + (item.getAdded() +
					 * item.getExpiresAfter()));
					 */
					retval = item.getExpiresAfter() + item.getAdded() < currentTime;
				}
			}
		}

		return retval;
	}
}