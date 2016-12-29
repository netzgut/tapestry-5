// Copyright 2006, 2007, 2009, 2010, 2011 The Apache Software Foundation
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package org.apache.tapestry5.internal.services;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.tapestry5.internal.test.InternalBaseTestCase;
import org.slf4j.Logger;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Tests handling of missing message keys.
 * TAP5-2550
 */
@Test
public class MapMessagesTest extends InternalBaseTestCase
{

    private final Map<String, String> properties = new HashMap<>();

    @BeforeClass
    public void setup()
    {
        properties.put("existing-key", "Key exists");
    }

    @Test
    public void handling_placeholder()
    {
        MapMessages messages = new MapMessages(Locale.ENGLISH, properties, "placeholder");

        assertEquals(messages.get("existing-key"), "Key exists");
        assertEquals(messages.get("non-existing-key"), "[[missing key: non-existing-key]]");
    }

    @Test
    public void handling_empty()
    {
        MapMessages messages = new MapMessages(Locale.ENGLISH, properties, "");

        assertEquals(messages.get("existing-key"), "Key exists");
        assertEquals(messages.get("non-existing-key"), "[[missing key: non-existing-key]]");
    }

    @Test
    public void handling_warn_existing()
    {
        Logger logger = mockLogger();
        replay();
        MapMessages messages = new MapMessages(Locale.ENGLISH, properties, "warn", logger);

        assertEquals(messages.get("existing-key"), "Key exists");
        verify();
    }

    @Test
    public void handling_warn_nonexisting()
    {
        String key = "non-existing-key";
        Logger logger = mockLogger();
        logger.warn("Missing Messages key: {}", key);
        replay();
        
        MapMessages messages = new MapMessages(Locale.ENGLISH, properties, "warn", logger);

        messages.get("non-existing-key");
        verify();
    }

    @Test
    public void handling_fail_existing()
    {
        MapMessages messages = new MapMessages(Locale.ENGLISH, properties, "fail");

        assertEquals(messages.get("existing-key"), "Key exists");
    }

    @Test(expectedExceptions = RuntimeException.class)
    public void handling_fail_nonexisting()
    {
        MapMessages messages = new MapMessages(Locale.ENGLISH, properties, "fail");

        messages.get("non-existing-key");
    }
}
