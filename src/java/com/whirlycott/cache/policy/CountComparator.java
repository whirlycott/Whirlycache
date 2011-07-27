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

import java.util.Comparator;
import java.util.Map;

import org.apache.log4j.Logger;

import com.whirlycott.cache.Item;
import com.whirlycott.cache.Messages;

/**
 * A comparison function, used by LFUMaintenancePolicy, which determines whether
 * one Item has been used more than a second Item, using said Items' count
 * properties.
 * 
 * @author Seth Fitzsimmons
 */
public class CountComparator implements Comparator {

	private static final Logger log = Logger.getLogger(CountComparator.class);

	/**
	 * Compares two Item objects based on their count properties.
	 */
	public int compare(final Object o1, final Object o2) {
		int retval = 0;

		if (o1 instanceof Map.Entry && o2 instanceof Map.Entry) {

			final Item lh = (Item) ((Map.Entry) o1).getValue();
			final Item rh = (Item) ((Map.Entry) o2).getValue();

			if (lh != null && rh != null) {

				if (lh.getCount() < rh.getCount()) {
					retval = -1;
				}

				if (lh.getCount() > rh.getCount()) {
					retval = 1;
				}

			}

		} else {
			log.warn(Messages.getString("CountComparator.values_were_not_map_entry")); //$NON-NLS-1$
		}
		return retval;
	}
}
