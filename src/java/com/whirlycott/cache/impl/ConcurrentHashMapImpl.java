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

import java.util.concurrent.ConcurrentHashMap;

import com.whirlycott.cache.ManagedCache;

/**
 * This is a ManagedCache implementation based on {@link ConcurrentHashMap}.
 * 
 * @author Phil Jacob
 */
public class ConcurrentHashMapImpl extends AbstractMapBackedCache implements ManagedCache {

	public ConcurrentHashMapImpl() {
		c = new ConcurrentHashMap();
	}

	@Override
	public void setMostlyRead(final boolean _mostlyRead) {
		// Nothing to do here.
		return;
	}

}
