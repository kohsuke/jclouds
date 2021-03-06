/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */
package org.jclouds.aws.ec2.services;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;

import org.jclouds.aws.ec2.EC2AsyncClient;
import org.jclouds.aws.ec2.EC2Client;
import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.jclouds.rest.RestContext;
import org.jclouds.rest.RestContextFactory;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;

/**
 * Tests behavior of {@code WindowsClient}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", sequential = true, testName = "ec2.WindowsClientLiveTest")
public class WindowsClientLiveTest {

   private WindowsClient client;
   private static final String DEFAULT_INSTANCE = "i-TODO";
   private static final String DEFAULT_BUCKET = "TODO";

   private RestContext<EC2Client, EC2AsyncClient> context;

   @BeforeGroups(groups = { "live" })
   public void setupClient() throws IOException {
      String identity = checkNotNull(System.getProperty("jclouds.test.identity"), "jclouds.test.identity");
      String credential = checkNotNull(System.getProperty("jclouds.test.credential"), "jclouds.test.credential");

      context = new RestContextFactory().createContext("ec2", identity, credential, ImmutableSet
            .<Module> of(new Log4JLoggingModule()));
      client = context.getApi().getWindowsServices();
   }

   @Test(enabled = false)
   // TODO get instance
   public void testBundleInstanceInRegion() {
      client
            .bundleInstanceInRegion(
                  null,
                  DEFAULT_INSTANCE,
                  "prefix",
                  DEFAULT_BUCKET,
                  "{\"expiration\": \"2008-08-30T08:49:09Z\",\"conditions\": [{\"bucket\": \"my-bucket\"},[\"starts-with\", \"$key\", \"my-new-image\"]]}");
   }

   @Test(enabled = false)
   // TODO get instance
   public void testCancelBundleTaskInRegion() {

   }

   @Test(enabled = false)
   // TODO get instance
   public void testDescribeBundleTasksInRegion() {

   }
}
