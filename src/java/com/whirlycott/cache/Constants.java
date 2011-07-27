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
 * Serves to encapsulate several constant values.
 * 
 * @author Philip Jacob
 */
public class Constants {

	/**
	 * Name of a node in the XML configuration file.
	 */
	public static final String CONFIG_CACHE = "cache"; //$NON-NLS-1$

	/**
	 * Name of an attribute in the XML configuration file.
	 */
	public static final String CONFIG_NAME = "name"; //$NON-NLS-1$

	/**
	 * Name of a node in the XML configuration file.
	 */
	public static final String CONFIG_BACKEND = "backend"; //$NON-NLS-1$

	/**
	 * Name of a node in the XML configuration file.
	 */
	public static final String CONFIG_MAXSIZE = "maxsize"; //$NON-NLS-1$

	/**
	 * The user-supplied configuration file.
	 */
	public static final String CONFIG_FILE = "/whirlycache.xml"; //$NON-NLS-1$

	/**
	 * Name of a node in the XML configuration file.
	 */
	public static final String CONFIG_TUNER_SLEEPTIME = "tuner-sleeptime"; //$NON-NLS-1$

	/**
	 * Name of a node in the XML configuration file.
	 */
	public static final String CONFIG_POLICY = "policy"; //$NON-NLS-1$

	/**
	 * The backup configuration file (only used in case whirlycache.xml is not
	 * found.
	 */
	public static final String DEFAULT_CONFIG_FILE = "/whirlycache-default.xml"; //$NON-NLS-1$

	/**
	 * If statistics collection is enabled at build time, this is set to true.
	 * This can provide some optimizations for those of you who don't care to
	 * collect data about how fast the cache is processing requests.
	 */
	public static final boolean BUILD_STATS_ENABLED = true;

	/**
	 * If Item expiration is enabled at build time, this is set to true. This
	 * allows expiration times to be set for individual items. Turning this off
	 * will speed up the tuner thread slightly.
	 */
	public static final boolean ITEM_EXPIRATION_ENABLED = true;
}
