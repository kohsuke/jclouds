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
package org.jclouds.aws.s3;

import static org.jclouds.Constants.PROPERTY_API_VERSION;
import static org.jclouds.Constants.PROPERTY_ENDPOINT;
import static org.jclouds.aws.reference.AWSConstants.PROPERTY_DEFAULT_REGIONS;
import static org.jclouds.aws.reference.AWSConstants.PROPERTY_REGIONS;
import static org.jclouds.aws.s3.reference.S3Constants.PROPERTY_S3_SERVICE_PATH;
import static org.jclouds.aws.s3.reference.S3Constants.PROPERTY_S3_VIRTUAL_HOST_BUCKETS;

import java.util.Properties;

/**
 * Builds properties used in Walrus Clients
 * 
 * @author Adrian Cole
 */
public class WalrusPropertiesBuilder extends S3PropertiesBuilder {
   @Override
   protected Properties addEndpoints(Properties properties) {
      properties.setProperty(PROPERTY_REGIONS, "Walrus");
      properties.setProperty(PROPERTY_DEFAULT_REGIONS, "Walrus");
      properties.setProperty(PROPERTY_API_VERSION, "Walrus-1.6");
      properties.setProperty(PROPERTY_ENDPOINT, "http://ecc.eucalyptus.com:8773/services/Walrus");
      properties.setProperty(PROPERTY_ENDPOINT + ".Walrus",
               "http://ecc.eucalyptus.com:8773/services/Walrus");
      properties.setProperty(PROPERTY_S3_SERVICE_PATH, "/services/Walrus");
      properties.setProperty(PROPERTY_S3_VIRTUAL_HOST_BUCKETS, "false");
      return properties;
   }

   public WalrusPropertiesBuilder(Properties properties) {
      super(properties);
   }

}
