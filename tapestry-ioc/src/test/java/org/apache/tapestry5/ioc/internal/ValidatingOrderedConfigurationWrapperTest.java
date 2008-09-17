// Copyright 2006, 2007, 2008 The Apache Software Foundation
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

package org.apache.tapestry5.ioc.internal;

import org.apache.tapestry5.ioc.OrderedConfiguration;
import org.apache.tapestry5.ioc.def.ContributionDef;
import org.slf4j.Logger;
import org.testng.annotations.Test;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ValidatingOrderedConfigurationWrapperTest extends IOCInternalTestCase
{
    @Test
    public void valid_type_long_form()
    {
        ContributionDef def = mockContributionDef();
        Logger logger = mockLogger();
        OrderedConfiguration<Runnable> configuration = mockOrderedConfiguration();
        Runnable contribution = mockRunnable();

        configuration.add("id", contribution, "after:pre", "before:post");

        replay();

        OrderedConfiguration<Runnable> wrapper = new ValidatingOrderedConfigurationWrapper<Runnable>(
                "Service", def, logger, Runnable.class, configuration);

        wrapper.add("id", contribution, "after:pre", "before:post");

        verify();
    }

    @Test
    public void contribute_valid_class()
    {
        ContributionDef def = mockContributionDef();
        Logger logger = mockLogger();
        OrderedConfiguration<Map> configuration = mockOrderedConfiguration();

        configuration.addInstance("id", HashMap.class, "after:pre", "before:post");

        replay();

        OrderedConfiguration<Map> wrapper = new ValidatingOrderedConfigurationWrapper<Map>(
                "Service", def, logger, Map.class, configuration);

        wrapper.addInstance("id", HashMap.class, "after:pre", "before:post");

        verify();
    }

    @Test
    public void contribute_invalid_class()
    {
        ContributionDef def = mockContributionDef();
        Logger logger = mockLogger();
        OrderedConfiguration<Object> configuration = mockOrderedConfiguration();

        replay();

        OrderedConfiguration<Object> wrapper = new ValidatingOrderedConfigurationWrapper<Object>(
                "Service", def, logger, Map.class, configuration);

        try
        {
            wrapper.addInstance("id", ArrayList.class, "after:pre", "before:post");
            unreachable();
        }
        catch (IllegalArgumentException ex)
        {
            assertMessageContains(ex,
                                  "Unable to contribute instance of class java.util.ArrayList",
                                  "to service 'Service'",
                                  "as it is not assignable to expected contribution type java.util.Map.");
        }

        verify();
    }

    @Test
    public void valid_type_short_form()
    {
        ContributionDef def = mockContributionDef();
        Logger logger = mockLogger();
        OrderedConfiguration<Runnable> configuration = mockOrderedConfiguration();
        Runnable contribution = mockRunnable();

        configuration.add("id", contribution);

        replay();

        OrderedConfiguration<Runnable> wrapper = new ValidatingOrderedConfigurationWrapper<Runnable>(
                "Service", def, logger, Runnable.class, configuration);

        wrapper.add("id", contribution);

        verify();
    }

    @Test
    public void null_object_passed_through()
    {
        ContributionDef def = mockContributionDef();
        Logger logger = mockLogger();
        OrderedConfiguration<Runnable> configuration = mockOrderedConfiguration();

        configuration.add("id", null);

        replay();

        OrderedConfiguration<Runnable> wrapper = new ValidatingOrderedConfigurationWrapper<Runnable>(
                "Service", def, logger, Runnable.class, configuration);

        wrapper.add("id", null);

        verify();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void incorrect_contribution_type_is_passed_through_as_null()
    {
        Method method = findMethod("contributeBarneyService");

        ContributionDef def = new ContributionDefImpl("Service", method, getClassFactory());
        Logger log = mockLogger();
        OrderedConfiguration<Runnable> configuration = mockOrderedConfiguration();

        log.warn(IOCMessages.contributionWrongValueType(
                "Service",
                def,
                String.class,
                Runnable.class));

        configuration.add("id", null);

        replay();

        OrderedConfiguration wrapper = new ValidatingOrderedConfigurationWrapper("Service", def,
                                                                                 log, Runnable.class, configuration);

        wrapper.add("id", "string");

        verify();
    }

    public void contributeBarneyService(OrderedConfiguration<Runnable> configuration)
    {

    }
}
