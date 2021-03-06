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
package org.jclouds.blobstore.integration.internal;

import java.io.IOException;

import org.jclouds.blobstore.BlobStoreContext;
import org.testng.ITestContext;

import com.google.inject.Module;

public abstract class BaseTestInitializer {

   public BlobStoreContext init(Module configurationModule, ITestContext testContext)
            throws Exception {
      String endpoint = System.getProperty("jclouds.test.endpoint");
      String app = System.getProperty("jclouds.test.app");
      String identity = System.getProperty("jclouds.test.identity");
      String credential = System.getProperty("jclouds.test.credential");
      if (endpoint != null)
         testContext.setAttribute("jclouds.test.endpoint", endpoint);
      if (app != null)
         testContext.setAttribute("jclouds.test.app", app);
      if (identity != null)
         testContext.setAttribute("jclouds.test.identity", identity);
      if (credential != null)
         testContext.setAttribute("jclouds.test.credential", credential);
      if (identity != null) {
         return createLiveContext(configurationModule, endpoint, app, identity, credential);
      } else {
         return createStubContext();
      }
   }

   protected abstract BlobStoreContext createStubContext() throws IOException;

   protected abstract BlobStoreContext createLiveContext(Module configurationModule, String url,
            String app, String identity, String key) throws IOException;
}