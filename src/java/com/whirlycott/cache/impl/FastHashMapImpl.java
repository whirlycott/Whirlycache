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

import org.apache.commons.collections.FastHashMap;
import org.apache.log4j.Logger;

import com.whirlycott.cache.ManagedCache;
import com.whirlycott.cache.Messages;

/**
 * This is a generic object cache using the Apache Jakarta Commons FastHashMap
 * implementation.
 * 
 * @author Phil Jacob
 */
public class FastHashMapImpl extends AbstractMapBackedCache implements ManagedCache {

	private static final Logger log = Logger.getLogger(FastHashMapImpl.class);

	public FastHashMapImpl() {
		log.debug(Messages.getString("FastHashMapImpl.creating_a_new_instance_of_fasthashmapimpl")); //$NON-NLS-1$
		c = new FastHashMap();
		((FastHashMap) c).setFast(false);
	}

	@Override
	public void setMostlyRead(final boolean _mostlyRead) {
		((FastHashMap) c).setFast(_mostlyRead);
	}

}
