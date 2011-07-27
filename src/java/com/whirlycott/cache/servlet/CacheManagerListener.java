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

package com.whirlycott.cache.servlet;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;

import com.whirlycott.cache.CacheException;
import com.whirlycott.cache.CacheManager;
import com.whirlycott.cache.Messages;

/**
 * Shuts down cache whenever the context reloads.
 * 
 * @author Seth Fitzsimmons
 */
public class CacheManagerListener implements ServletContextListener {

	/**
	 * Logger.
	 */
	private static final Logger log = Logger.getLogger(CacheManagerListener.class);

	/**
	 * Executes on context initialization.
	 */
	public void contextInitialized(final ServletContextEvent servletContextEvent) {
		// Empty
	}

	/**
	 * Executes on context shutdown.
	 */
	public void contextDestroyed(final ServletContextEvent servletContextEvent) {
		try {
			log.debug(Messages.getString("CacheManagerListener.shutting_down_whirlycache_due_to_servlet_destruction")); //$NON-NLS-1$
			CacheManager.getInstance().shutdown();
		} catch (final CacheException e) {
			log.error(e.getMessage(), e);
		}
	}
}
