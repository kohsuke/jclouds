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
package org.jclouds.atmosonline.saas.blobstore.integration;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.jclouds.blobstore.integration.internal.BaseInputStreamMapIntegrationTest;
import org.testng.annotations.Test;

/**
 * @author Adrian Cole
 */
@Test(groups = "live", testName = "emcsaas.AtmosStorageInputStreamMapIntegrationTest")
public class AtmosStorageInputStreamMapIntegrationLiveTest extends BaseInputStreamMapIntegrationTest {

   @Override
   @Test(enabled = false)
   public void testContainsBytesValue() throws InterruptedException, ExecutionException,
            TimeoutException {
      // TODO not reliable
   }

   @Override
   @Test(enabled = false)
   public void testContainsFileValue() throws InterruptedException, ExecutionException,
            TimeoutException {
      // TODO not reliable
   }

   @Override
   @Test(enabled = false)
   public void testContainsInputStreamValue() throws InterruptedException, ExecutionException,
            TimeoutException {
      // TODO not reliable
   }

   @Override
   @Test(enabled = false)
   public void testContainsStringValue() throws InterruptedException, ExecutionException,
            TimeoutException {
      // TODO not reliable
   }

   @Override
   @Test(enabled = false)
   public void testPut() {
      // TODO not reliable NPE@BaseInputStreamMapIntegrationTest.java:258
   }

   @Override
   @Test(enabled = false)
   public void testPutBytes() {
      // TODO not reliable NPE
   }

   @Override
   @Test(enabled = false)
   public void testPutFile() {
      // TODO not reliable NPE
   }

   @Override
   @Test(enabled = false)
   public void testPutString() {
      // TODO not reliable NPE
   }

}