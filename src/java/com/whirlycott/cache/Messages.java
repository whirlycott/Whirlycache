/*
Copyright 2004 Philip Jacob <phil@whirlycott.com>
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
 * Created on Dec 20, 2004 by pjacob
 *
 */
package com.whirlycott.cache;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Utility class for getting access to internationalized log messages.
 * 
 * @author pjacob
 * 
 */
public class Messages {

	private static final String BUNDLE_NAME = "com.whirlycott.cache.MessagesBundle"; //$NON-NLS-1$

	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME, Locale.getDefault());

	private Messages() {
		// Empty
	}

	public static String getString(final String _key) {
		try {
			return RESOURCE_BUNDLE.getString(_key);
		} catch (final MissingResourceException e) {
			return '!' + _key + '!';
		}
	}

	public static String getCompoundString(final String key, final Object[] args) {
		final MessageFormat formatter = new MessageFormat(Messages.getString(key));
		return formatter.format(args);
	}
}
