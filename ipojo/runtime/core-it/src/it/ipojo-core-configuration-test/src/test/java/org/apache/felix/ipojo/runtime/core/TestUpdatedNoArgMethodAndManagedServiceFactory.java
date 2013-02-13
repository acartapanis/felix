/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.felix.ipojo.runtime.core;

import org.apache.felix.ipojo.ComponentInstance;
import org.apache.felix.ipojo.runtime.core.services.FooService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.osgi.framework.ServiceReference;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedServiceFactory;
import org.ow2.chameleon.testing.helpers.IPOJOHelper;
import org.ow2.chameleon.testing.helpers.OSGiHelper;

import java.util.Hashtable;
import java.util.Properties;

import static org.junit.Assert.*;


public class TestUpdatedNoArgMethodAndManagedServiceFactory extends Common {



    ComponentInstance instance, instance2;



    @Before
    public void setUp() {
        osgiHelper = new OSGiHelper(bc);
        ipojoHelper = new IPOJOHelper(bc);
        String type = "CONFIG-FooProviderType-3Updated2";

        Hashtable<String, String> p1 = new Hashtable<String, String>();
        p1.put("instance.name", "instance");
        p1.put("foo", "foo");
        p1.put("bar", "2");
        p1.put("baz", "baz");
        instance = ipojoHelper.createComponentInstance(type, p1);

        Hashtable<String, String> p2 = new Hashtable<String, String>();
        p2.put("instance.name", "instance2");

        instance2 = ipojoHelper.createComponentInstance(type, p2);
    }

    @After
    public void tearDown() {
        instance.dispose();
        instance2.dispose();
        instance2 = null;
        instance = null;
    }

    @Test
    public void testStatic() {

        ServiceReference fooRef = ipojoHelper.getServiceReferenceByName(FooService.class.getName(), instance.getInstanceName());
        assertNotNull("Check FS availability", fooRef);
        String fooP = (String) fooRef.getProperty("foo");
        Integer barP = (Integer) fooRef.getProperty("bar");
        String bazP = (String) fooRef.getProperty("baz");
        assertEquals("Check foo equality -1", fooP, "foo");
        assertEquals("Check bar equality -1", barP, new Integer(2));
        assertEquals("Check baz equality -1", bazP, "baz");

        ServiceReference msRef = ipojoHelper.getServiceReferenceByName(ManagedServiceFactory.class.getName(), instance.getFactory().getName());
        assertNotNull("Check ManagedServiceFactory availability", msRef);


        // Configuration of baz
        Properties conf = new Properties();
        conf.put("baz", "zab");
        conf.put("bar", new Integer(2));
        conf.put("foo", "foo");
        ManagedServiceFactory ms = (ManagedServiceFactory) osgiHelper.getServiceObject(msRef);
        try {
            ms.updated(instance.getInstanceName(), conf);
        } catch (ConfigurationException e) {
            fail("Configuration Exception : " + e);
        }

        // Recheck props
        fooRef = ipojoHelper.getServiceReferenceByName(FooService.class.getName(), instance.getInstanceName());
        fooP = (String) fooRef.getProperty("foo");
        barP = (Integer) fooRef.getProperty("bar");
        bazP = (String) fooRef.getProperty("baz");
        assertEquals("Check foo equality -2", fooP, "foo");
        assertEquals("Check bar equality -2", barP, new Integer(2));
        assertEquals("Check baz equality -2", bazP, "zab");

        // Get Service
        FooService fs = (FooService) osgiHelper.getServiceObject(fooRef);
        Integer updated = (Integer) fs.fooProps().get("updated");

        assertEquals("Check updated", 1, updated.intValue());

    }

    @Test
    public void testStaticNoValue() {
        ServiceReference fooRef = ipojoHelper.getServiceReferenceByName(FooService.class.getName(), instance2.getInstanceName());
        assertNotNull("Check FS availability", fooRef);
        Object fooP = fooRef.getProperty("foo");
        Object barP = fooRef.getProperty("bar");
        Object bazP = fooRef.getProperty("baz");
        assertEquals("Check foo equality -1", fooP, null);
        assertEquals("Check bar equality -1", barP, null);
        assertEquals("Check baz equality -1", bazP, null);

        ServiceReference msRef = ipojoHelper.getServiceReferenceByName(ManagedServiceFactory.class.getName(), instance2.getFactory().getName());
        assertNotNull("Check ManagedServiceFactory availability", msRef);


        // Configuration of baz
        Properties conf = new Properties();
        conf.put("baz", "zab");
        conf.put("bar", new Integer(2));
        conf.put("foo", "foo");
        ManagedServiceFactory ms = (ManagedServiceFactory) osgiHelper.getServiceObject(msRef);
        try {
            ms.updated(instance2.getInstanceName(), conf);
        } catch (ConfigurationException e) {
            fail("Configuration Exception : " + e);
        }

        // Recheck props
        fooRef = ipojoHelper.getServiceReferenceByName(FooService.class.getName(), instance2.getInstanceName());
        fooP = (String) fooRef.getProperty("foo");
        barP = (Integer) fooRef.getProperty("bar");
        bazP = (String) fooRef.getProperty("baz");
        assertEquals("Check foo equality -2", fooP, "foo");
        assertEquals("Check bar equality -2", barP, new Integer(2));
        assertEquals("Check baz equality -2", bazP, "zab");

        // Get Service
        FooService fs = (FooService) osgiHelper.getServiceObject(fooRef);
        Integer updated = (Integer) fs.fooProps().get("updated");

        assertEquals("Check updated", 1, updated.intValue());
    }

    @Test
    public void testDynamic() {
        ServiceReference fooRef = ipojoHelper.getServiceReferenceByName(FooService.class.getName(), instance.getInstanceName());
        assertNotNull("Check FS availability", fooRef);

        String fooP = (String) fooRef.getProperty("foo");
        Integer barP = (Integer) fooRef.getProperty("bar");
        String bazP = (String) fooRef.getProperty("baz");

        assertEquals("Check foo equality", fooP, "foo");
        assertEquals("Check bar equality", barP, new Integer(2));
        assertEquals("Check baz equality", bazP, "baz");

        ServiceReference msRef = ipojoHelper.getServiceReferenceByName(ManagedServiceFactory.class.getName(), instance.getFactory().getName());
        assertNotNull("Check ManagedServiceFactory availability", msRef);

        // Configuration of baz
        Properties conf = new Properties();
        conf.put("baz", "zab");
        conf.put("foo", "oof");
        conf.put("bar", new Integer(0));
        ManagedServiceFactory ms = (ManagedServiceFactory) osgiHelper.getServiceObject(msRef);
        try {
            ms.updated(instance.getInstanceName(), conf);
        } catch (ConfigurationException e) {
            fail("Configuration Exception : " + e);
        }

        // Recheck props
        fooRef = ipojoHelper.getServiceReferenceByName(FooService.class.getName(), instance.getInstanceName());
        fooP = (String) fooRef.getProperty("foo");
        barP = (Integer) fooRef.getProperty("bar");
        bazP = (String) fooRef.getProperty("baz");

        assertEquals("Check foo equality", fooP, "oof");
        assertEquals("Check bar equality", barP, new Integer(0));
        assertEquals("Check baz equality", bazP, "zab");

        // Check field value
        FooService fs = (FooService) osgiHelper.getServiceObject(fooRef);
        Properties p = fs.fooProps();
        fooP = (String) p.get("foo");
        barP = (Integer) p.get("bar");

        assertEquals("Check foo field equality", fooP, "oof");
        assertEquals("Check bar field equality", barP, new Integer(0));

        Integer updated = (Integer) fs.fooProps().get("updated");

        assertEquals("Check updated", 1, updated.intValue());
    }

    @Test
    public void testDynamicNoValue() {
        ServiceReference fooRef = ipojoHelper.getServiceReferenceByName(FooService.class.getName(), instance2.getInstanceName());
        assertNotNull("Check FS availability", fooRef);

        Object fooP = fooRef.getProperty("foo");
        Object barP = fooRef.getProperty("bar");
        Object bazP = fooRef.getProperty("baz");
        assertEquals("Check foo equality -1", fooP, null);
        assertEquals("Check bar equality -1", barP, null);
        assertEquals("Check baz equality -1", bazP, null);

        ServiceReference msRef = ipojoHelper.getServiceReferenceByName(ManagedServiceFactory.class.getName(), instance2.getFactory().getName());
        assertNotNull("Check ManagedServiceFactory availability", msRef);

        // Configuration of baz
        Properties conf = new Properties();
        conf.put("baz", "zab");
        conf.put("foo", "oof");
        conf.put("bar", new Integer(0));
        ManagedServiceFactory ms = (ManagedServiceFactory) osgiHelper.getServiceObject(msRef);
        try {
            ms.updated(instance2.getInstanceName(), conf);
        } catch (ConfigurationException e) {
            fail("Configuration Exception : " + e);
        }

        // Recheck props
        fooRef = ipojoHelper.getServiceReferenceByName(FooService.class.getName(), instance2.getInstanceName());
        fooP = (String) fooRef.getProperty("foo");
        barP = (Integer) fooRef.getProperty("bar");
        bazP = (String) fooRef.getProperty("baz");

        assertEquals("Check foo equality", fooP, "oof");
        assertEquals("Check bar equality", barP, new Integer(0));
        assertEquals("Check baz equality", bazP, "zab");

        // Check field value
        FooService fs = (FooService) osgiHelper.getServiceObject(fooRef);
        Properties p = fs.fooProps();
        fooP = (String) p.get("foo");
        barP = (Integer) p.get("bar");

        assertEquals("Check foo field equality", fooP, "oof");
        assertEquals("Check bar field equality", barP, new Integer(0));

        Integer updated = (Integer) fs.fooProps().get("updated");

        assertEquals("Check updated", 1, updated.intValue());
    }


    @Test
    public void testDynamicString() {
        ServiceReference fooRef = ipojoHelper.getServiceReferenceByName(FooService.class.getName(), instance.getInstanceName());
        assertNotNull("Check FS availability", fooRef);

        String fooP = (String) fooRef.getProperty("foo");
        Integer barP = (Integer) fooRef.getProperty("bar");
        String bazP = (String) fooRef.getProperty("baz");

        assertEquals("Check foo equality", fooP, "foo");
        assertEquals("Check bar equality", barP, new Integer(2));
        assertEquals("Check baz equality", bazP, "baz");

        ServiceReference msRef = ipojoHelper.getServiceReferenceByName(ManagedServiceFactory.class.getName(), instance.getFactory().getName());
        assertNotNull("Check ManagedServiceFactory availability", msRef);

        // Configuration of baz
        Properties conf = new Properties();
        conf.put("baz", "zab");
        conf.put("foo", "oof");
        conf.put("bar", "0");
        ManagedServiceFactory ms = (ManagedServiceFactory) osgiHelper.getServiceObject(msRef);
        try {
            ms.updated(instance.getInstanceName(), conf);
        } catch (ConfigurationException e) {
            fail("Configuration Exception : " + e);
        }

        // Recheck props
        fooRef = ipojoHelper.getServiceReferenceByName(FooService.class.getName(), instance.getInstanceName());
        fooP = (String) fooRef.getProperty("foo");
        barP = (Integer) fooRef.getProperty("bar");
        bazP = (String) fooRef.getProperty("baz");

        assertEquals("Check foo equality", fooP, "oof");
        assertEquals("Check bar equality", barP, new Integer(0));
        assertEquals("Check baz equality", bazP, "zab");

        // Check field value
        FooService fs = (FooService) osgiHelper.getServiceObject(fooRef);
        Properties p = fs.fooProps();
        fooP = (String) p.get("foo");
        barP = (Integer) p.get("bar");

        assertEquals("Check foo field equality", fooP, "oof");
        assertEquals("Check bar field equality", barP, new Integer(0));

        Integer updated = (Integer) fs.fooProps().get("updated");

        assertEquals("Check updated", 1, updated.intValue());
    }

}
