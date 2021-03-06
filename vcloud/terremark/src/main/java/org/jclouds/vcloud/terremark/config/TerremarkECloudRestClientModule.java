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
package org.jclouds.vcloud.terremark.config;

import javax.inject.Singleton;

import org.jclouds.http.RequiresHttp;
import org.jclouds.rest.ConfiguresRestClient;
import org.jclouds.vcloud.VCloudAsyncClient;
import org.jclouds.vcloud.VCloudClient;
import org.jclouds.vcloud.terremark.TerremarkVCloudAsyncClient;
import org.jclouds.vcloud.terremark.TerremarkVCloudClient;
import org.jclouds.vcloud.terremark.TerremarkECloudAsyncClient;
import org.jclouds.vcloud.terremark.TerremarkECloudClient;

import com.google.inject.Provides;

/**
 * Configures the VCloud authentication service connection, including logging and http transport.
 * 
 * @author Adrian Cole
 */
@RequiresHttp
@ConfiguresRestClient
public class TerremarkECloudRestClientModule extends
         TerremarkRestClientModule<TerremarkECloudClient, TerremarkECloudAsyncClient> {

   public TerremarkECloudRestClientModule() {
      super(TerremarkECloudClient.class, TerremarkECloudAsyncClient.class);
   }

   @Provides
   @Singleton
   protected VCloudAsyncClient provideVCloudAsyncClient(TerremarkECloudAsyncClient in) {
      return in;
   }

   @Provides
   @Singleton
   protected VCloudClient provideVCloudClient(TerremarkECloudClient in) {
      return in;
   }

   @Provides
   @Singleton
   protected TerremarkVCloudAsyncClient provideTerremarkAsyncClient(TerremarkECloudAsyncClient in) {
      return in;
   }

   @Provides
   @Singleton
   protected TerremarkVCloudClient provideTerremarkClient(TerremarkECloudClient in) {
      return in;
   }

}
