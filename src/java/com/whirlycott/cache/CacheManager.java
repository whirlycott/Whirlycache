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

import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * The CacheManager manages caches by creating and deleting them.
 * 
 * @author Phil Jacob
 */
public class CacheManager {

	/**
	 * Holds a representation of the configuration.
	 */
	protected final static Map configuration = new HashMap();

	static String defaultCacheName = null;

	/**
	 * Logger.
	 */
	private final static Logger log = Logger.getLogger(CacheManager.class);

	/*
	 * Got a better idea?
	 * http://www-106.ibm.com/developerworks/java/library/j-dcl.html?dwzone=java
	 */
	protected final static CacheManager singleton = new CacheManager();

	/**
	 * @return Returns the configurationProperties.
	 */
	public static Map getConfiguration() {
		return configuration;
	}

	/**
	 * Returns an instance of the CacheManager.
	 * 
	 * @return An instance of the CacheManager.
	 */
	public static CacheManager getInstance() {
		return singleton;
	}

	/**
	 * Holds a Map of the caches that have been created from the config file.
	 */
	protected final Map namedCaches = new HashMap();

	/**
	 * Private constructor (singleton pattern).
	 * 
	 */
	private CacheManager() {
		log.info(Messages.getString("CacheManager.creating_new_cache_manager_singleton")); //$NON-NLS-1$

		try {
			configure();

		} catch (final CacheException e) {
			// This should never happen.
			log.fatal(e.getMessage(), e);
		}
	}

	/**
	 * Initializes based on the whirlycache.xml configuration file.
	 * 
	 * @throws CacheException
	 *             When the CacheManager cannot be configured.
	 */
	protected void configure() throws CacheException {
		log.debug(Messages.getString("CacheManager.configuring_the_whirlycache")); //$NON-NLS-1$

		// The ref to store the config file in.
		Document doc;

		// Try to load the specified conf file.
		doc = loadConfigFile(Constants.CONFIG_FILE);

		if (doc == null) {
			log.warn("Could not load " + Constants.CONFIG_FILE + " file. Falling back to defaults."); //$NON-NLS-1$ //$NON-NLS-2$

			// Ok, so go and load up the whirlycache-default.xml file.
			doc = loadConfigFile(Constants.DEFAULT_CONFIG_FILE);

			// Now this is unusual... can't find *any* config files.
			if (doc == null) {
				final String msg = Messages.getString("CacheManager.cannot_load_default_config_file"); //$NON-NLS-1$
				// log.fatal(msg);
				throw new CacheException(msg);
			}
		}

		/*
		 * Build a Map of Maps containing key/value pairs of the options in the
		 * config file. Admittedly, this kind of sucks and I would be open to
		 * seeing another way of doing this. - phil.
		 */
		final Element root = doc.getDocumentElement();

		defaultCacheName = root.getElementsByTagName("default-cache").item(0).getFirstChild().getNodeValue(); //$NON-NLS-1$
		log.debug(Messages.getString("CacheManager.default_cache_name") + defaultCacheName); //$NON-NLS-1$

		final NodeList caches = root.getElementsByTagName(Constants.CONFIG_CACHE);
		for (int i = 0; i < caches.getLength(); i++) {
			final CacheConfiguration configuration = new CacheConfiguration();
			final Element elementCache = (Element) caches.item(i);
			final String cacheName = elementCache.getAttribute(Constants.CONFIG_NAME);
			log.debug(Messages.getString("CacheManager.cache_name") + cacheName); //$NON-NLS-1$
			configuration.setName(cacheName);

			final NodeList elementCacheAttributes = elementCache.getElementsByTagName("*"); //$NON-NLS-1$
			for (int j = 0; j < elementCacheAttributes.getLength(); j++) {
				final Element att = (Element) elementCacheAttributes.item(j);

				final String nodeName = att.getNodeName();
				final String nodeValue = att.getFirstChild().getNodeValue();
				log.debug("Node name: " + nodeName + "; Node value: " + nodeValue);

				if (Constants.CONFIG_BACKEND.equals(nodeName)) {
					configuration.setBackend(nodeValue);

				} else if (Constants.CONFIG_TUNER_SLEEPTIME.equals(nodeName)) {
					configuration.setTunerSleepTime(new Integer(nodeValue).intValue());

				} else if (Constants.CONFIG_POLICY.equals(nodeName)) {
					configuration.setPolicy(nodeValue);

				} else if (Constants.CONFIG_MAXSIZE.equals(nodeName)) {
					configuration.setMaxSize(new Integer(nodeValue).intValue());

				} else {
					// One has to wonder what purpose this serves... - phil.
					configuration.setAttribute(nodeName, nodeValue);
				}

				log.debug(" - " + nodeName + "=" + nodeValue); //$NON-NLS-1$ //$NON-NLS-2$
			}

			// Make all the caches listed in the config file.
			log.debug(Messages.getString("CacheManager.making_named_caches")); //$NON-NLS-1$
			createCache(configuration);

			// Store the configured information.
			CacheManager.configuration.put(elementCache.getAttribute(Constants.CONFIG_NAME), configuration);
		}

		// Verify that the default cache has been created.
		if (namedCaches.get(defaultCacheName) == null) {
			final Object[] args = { defaultCacheName };
			throw new CacheException(Messages.getCompoundString("CacheManager.nonexistent_default_cache", args)); //$NON-NLS-1$
		}

	}

	/**
	 * Initialize a Cache.
	 * 
	 * @param _configuration
	 *            Configuration to initialize cache with.
	 * @return Empty Cache, ready for use.
	 * @throws CacheException
	 *             When the cache cannot be initialized.
	 */
	public Cache createCache(final CacheConfiguration _configuration) throws CacheException {
		if (_configuration == null || _configuration.getName() == null) {
			final String msg = Messages.getString("CacheManager.cache_name_cannot_be_null"); //$NON-NLS-1$
			log.error(msg);
			throw new CacheException(msg);
		}

		log.debug("Creating cache: " + _configuration.getName());

		final Cache c;

		// Try to get an object out of the map
		final Object o = namedCaches.get(_configuration.getName());

		// If it's null, create one.
		if (o == null) {
			try {
				final String backend = _configuration.getBackend();
				log.debug("Cache backend is " + backend); //$NON-NLS-1$

				final Object possiblyC = Class.forName(backend).newInstance();

				if (!(possiblyC instanceof ManagedCache)) {
					throw new CacheException("Problem creating an instance of " + backend + " because it does not implement the ManagedCache interface."); //$NON-NLS-1$ //$NON-NLS-2$
				}

				final ManagedCache managedCache = (ManagedCache) possiblyC;
				final CacheMaintenancePolicy policy = createPolicy(managedCache, _configuration);

				c = new CacheDecorator(managedCache, _configuration, new CacheMaintenancePolicy[] { policy });

			} catch (final Exception e) {
				log.fatal(Messages.getString("CacheManager.cannot_create_instance_of_impl"), e); //$NON-NLS-1$
				throw new CacheException(e);
			}
			namedCaches.put(_configuration.getName(), c);
		} else {
			// ... otherwise, return it.
			c = (Cache) o;
		}
		return c;
	}

	/**
	 * Create a cache policy.
	 * 
	 * @param managedCache
	 * @param configuration
	 * @return
	 * @throws CacheException
	 */
	private CacheMaintenancePolicy createPolicy(final ManagedCache managedCache, final CacheConfiguration configuration) throws CacheException {
		// Policy class config.
		final String policyClass = configuration.getPolicy();

		if (null == policyClass) {
			throw new IllegalArgumentException(Messages.getString("CacheManager.cache_config_get_policy_cannot_be_null")); //$NON-NLS-1$
		}

		final CacheMaintenancePolicy policy;
		try {
			policy = (CacheMaintenancePolicy) Class.forName(policyClass).newInstance();
		} catch (final Exception e) {
			throw new CacheException("Cannot make an instance of policy class " + policyClass, e); //$NON-NLS-1$
		}
		if (policy != null) {
			policy.setCache(managedCache);
			policy.setConfiguration(configuration);
		}

		return policy;
	}

	/**
	 * Destroys the default cache.
	 * 
	 * @throws CacheException
	 */
	public void destroy() throws CacheException {
		destroy(defaultCacheName);
	}

	/**
	 * Shut down an individual Cache.
	 * 
	 * @param _cacheName
	 *            cache to shut down.
	 * @throws CacheException
	 *             if there was a problem shutting down the cache.
	 */
	public void destroy(final String _cacheName) throws CacheException {
		// If there is a cache, we ought to clear it first.
		final CacheDecorator c = (CacheDecorator) getCache(_cacheName);
		c.clear();
		c.shutdown();
		namedCaches.remove(_cacheName);
	}

	/**
	 * Gets a reference to the default Cache.
	 * 
	 * @throws CacheException
	 * @return A reference to the default Cache.
	 */
	public Cache getCache() throws CacheException {
		return getCache(defaultCacheName);
	}

	/**
	 * Get a Cache by name.
	 * 
	 * @param _name
	 *            Name of cache to retrieve.
	 * @return Cache, specified by name.
	 * @throws CacheException
	 */
	public Cache getCache(final String _name) {
		// Short circuit an invalid name.
		if (_name == null) {
			throw new IllegalArgumentException(Messages.getString("CacheManager.cannot_get_cache_with_null_name")); //$NON-NLS-1$
		}

		final Cache c = (Cache) namedCaches.get(_name);

		if (c == null) {
			throw new IllegalArgumentException("There is no cache called '" + _name + "'"); //$NON-NLS-1$ //$NON-NLS-2$
		}

		return c;
	}

	/**
	 * Loads the specified config file.
	 * 
	 * @param _filename
	 * @return a Document object representing the structure of the config file
	 * @throws CacheException
	 */
	protected Document loadConfigFile(final String _filename) throws CacheException {
		if (_filename == null) {
			throw new CacheException(Messages.getString("CacheManager.cannot_load_null_config_file")); //$NON-NLS-1$
		}

		final InputStream is = getClass().getResourceAsStream(_filename);
		if (is != null) {
			try {
				return DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is);
			} catch (final Exception e) {
				log.error("Problem loading " + _filename); //$NON-NLS-1$
			}
		}

		// If we get this far, it means we haven't found the file.
		return null;
	}

	/**
	 * Shuts down the manager of all of the Caches, which in turn shuts down all
	 * of the Caches.
	 * 
	 * @throws CacheException
	 *             When the Cache cannot be shutdown properly.
	 */
	public void shutdown() throws CacheException {
		synchronized (namedCaches) {
			final Iterator cacheNames = new LinkedList(namedCaches.keySet()).iterator();
			// Loop and shut down all the individual caches.
			while (cacheNames.hasNext()) {
				try {
					destroy((String) cacheNames.next());
				} catch (final CacheException e) {
					throw new CacheException(Messages.getString("CacheManager.problem_shutting_down")); //$NON-NLS-1$
				}
			}
		}
	}

	/**
	 * Returns an array of the named caches that this CacheManager knows about
	 * (including the default cache).
	 * 
	 * @return array of named caches
	 */
	public String[] getCacheNames() {
		final Object[] caches = namedCaches.keySet().toArray();
		final int size = caches.length;
		final String[] names = new String[size];
		for (int i = 0; i < size; i++) {
			names[i] = (String) caches[i];
		}
		return names;
	}
}