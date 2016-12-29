// Copyright 2006, 2012 The Apache Software Foundation
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package org.apache.tapestry5.internal.services;

import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.util.AbstractMessages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of {@link Messages} based on a simple Map (of string keys and values).
 */
public class MapMessages extends AbstractMessages
{

    private static final Logger staticLogger = LoggerFactory.getLogger(MapMessages.class);
    
    private final Logger logger;

    private final Map<String, String> properties;

    private final MissingMessagesHandlingMode missingMessagesHandlingMode;


    protected MapMessages(Locale locale, Map<String, String> properties,
            String missingMessagesHandling, Logger logger)
    {
        super(locale);

        this.properties = properties;
        this.logger = logger;
        this.missingMessagesHandlingMode = MissingMessagesHandlingMode
                .from(missingMessagesHandling);
    }
    
    /**
     * A new instance <strong>retaining</strong> (not copying) the provided map.
     */
    public MapMessages(Locale locale, Map<String, String> properties,
            String missingMessagesHandling) {
        this(locale, properties, missingMessagesHandling, staticLogger);
    }
    
    

    @Override
    public String get(String key)
    {
        if (contains(key))
            return valueForKey(key);

        switch (missingMessagesHandlingMode)
        {
            case PLACEHOLDER:
                // Nothing to do here
                break;

            case WARN:
                logger.warn("Missing Messages key: {}", key);
                break;

            case FAIL:
                throw new RuntimeException(String.format("Missing Messages key: %s", key));
        }

        return String.format("[[missing key: %s]]", key);
    }

    @Override
    protected String valueForKey(String key)
    {
        return properties.get(key);
    }

    public Set<String> getKeys()
    {
        return properties.keySet();
    }

    private enum MissingMessagesHandlingMode
    {
        PLACEHOLDER, WARN, FAIL;

        public static MissingMessagesHandlingMode from(String value)
        {

            if (value == null || value.length() == 0
                    || value.equalsIgnoreCase("placeholder"))
            {
                return PLACEHOLDER;
            }

            if (value.equalsIgnoreCase("warn"))
            {
                return WARN;
            }

            if (value.equalsIgnoreCase("fail"))
            {
                return FAIL;
            }

            return PLACEHOLDER;
        }
    }
}
