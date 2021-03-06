/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
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
 * ====================================================================
 */
package org.jclouds.boxdotnet;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.rest.RestContextFactory.contextSpec;
import static org.jclouds.rest.RestContextFactory.createContext;
import static org.testng.Assert.assertNotNull;

import org.jclouds.rest.RestContext;
import org.testng.annotations.AfterGroups;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

/**
 * Tests behavior of {@code BoxDotNetClient}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", testName = "boxdotnet.BoxDotNetClientLiveTest")
public class BoxDotNetClientLiveTest {

   private BoxDotNetClient connection;
   private RestContext<BoxDotNetClient, BoxDotNetAsyncClient> context;

   @BeforeGroups(groups = { "live" })
   public void setupClient() {
      String identity = checkNotNull(System.getProperty("jclouds.test.identity"), "jclouds.test.identity");
      String credential = checkNotNull(System.getProperty("jclouds.test.credential"), "jclouds.test.credential");

      context = createContext(contextSpec("boxdotnet", "https://www.box.net/api/1.0/rest", "1.0",
               identity, credential, BoxDotNetClient.class, BoxDotNetAsyncClient.class));

      connection = context.getApi();
   }

   @AfterGroups(groups = "live")
   void tearDown() {
      if (context != null)
         context.close();
   }

   @Test
   public void testList() throws Exception {
      String response = connection.list();
      assertNotNull(response);
   }

   @Test
   public void testGet() throws Exception {
      String response = connection.get(1l);
      assertNotNull(response);
   }

   /*
    * TODO: add tests for BoxDotNet interface methods
    */
}
