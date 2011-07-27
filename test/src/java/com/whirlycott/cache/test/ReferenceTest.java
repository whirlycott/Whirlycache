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

/*
 * Created on May 16, 2004
 *
 */
package com.whirlycott.cache.test;

import java.io.Serializable;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import org.apache.log4j.Logger;

/**
 * @author phil
 */
public class ReferenceTest {

	private static Logger log = Logger.getLogger(ReferenceTest.class);

	public static void main(final String[] args) throws CacheException {

		final CacheManager cm = CacheManager.create();

		final Cache cache = new Cache("test", 1000, true, false, 500, 200);
		cm.addCache(cache);

		final Outer outer = new Outer();

		log.debug("The outer default value is: " + outer.getIn().innerName);
		final Element e = new Element("outer", outer);
		cache.put(e);
		log.debug("Now the outer is in the cache");

		outer.getIn().innerName = "changed the inner string!!!!!!!";

		final Element modifiedElement = cache.get("outer");

		log.debug("The value from the cache is: " + ((Outer) modifiedElement.getValue()).getIn().innerName);

		cache.put(e);

		cm.shutdown();

	}
}

class Outer implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -689418001067150615L;

	private Inner in;

	Outer() {
		in = new Inner();
	}

	/**
	 * @return Returns the in.
	 */
	public Inner getIn() {
		return in;
	}

	/**
	 * @param in
	 *            The in to set.
	 */
	public void setIn(final Inner in) {
		this.in = in;
	}

}

class Inner {

	public String innerName = "default phil inner name";

}
