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
package org.jclouds.aws.ec2;

import java.util.concurrent.TimeUnit;

import org.jclouds.aws.ec2.services.AMIClient;
import org.jclouds.aws.ec2.services.AvailabilityZoneAndRegionClient;
import org.jclouds.aws.ec2.services.ElasticBlockStoreClient;
import org.jclouds.aws.ec2.services.ElasticIPAddressClient;
import org.jclouds.aws.ec2.services.InstanceClient;
import org.jclouds.aws.ec2.services.KeyPairClient;
import org.jclouds.aws.ec2.services.MonitoringClient;
import org.jclouds.aws.ec2.services.PlacementGroupClient;
import org.jclouds.aws.ec2.services.SecurityGroupClient;
import org.jclouds.aws.ec2.services.WindowsClient;
import org.jclouds.concurrent.Timeout;
import org.jclouds.rest.annotations.Delegate;

/**
 * Provides synchronous access to EC2 services.
 * 
 * @author Adrian Cole
 */
@Timeout(duration = 180, timeUnit = TimeUnit.SECONDS)
public interface EC2Client {
   /**
    * Provides synchronous access to AMI services.
    */
   @Delegate
   AMIClient getAMIServices();

   /**
    * Provides synchronous access to Elastic IP Address services.
    */
   @Delegate
   ElasticIPAddressClient getElasticIPAddressServices();

   /**
    * Provides synchronous access to Instance services.
    */
   @Delegate
   InstanceClient getInstanceServices();

   /**
    * Provides synchronous access to KeyPair services.
    */
   @Delegate
   KeyPairClient getKeyPairServices();

   /**
    * Provides synchronous access to SecurityGroup services.
    */
   @Delegate
   SecurityGroupClient getSecurityGroupServices();

   /**
    * Provides synchronous access to PlacementGroup services.
    */
   @Delegate
   PlacementGroupClient getPlacementGroupServices();

   /**
    * Provides synchronous access to Monitoring services.
    */
   @Delegate
   MonitoringClient getMonitoringServices();

   /**
    * Provides asynchronous access to Windows services.
    */
   @Delegate
   WindowsClient getWindowsServices();

   /**
    * Provides synchronous access to Availability Zones and Regions services.
    */
   @Delegate
   AvailabilityZoneAndRegionClient getAvailabilityZoneAndRegionServices();

   /**
    * Provides synchronous access to Elastic Block Store services.
    */
   @Delegate
   ElasticBlockStoreClient getElasticBlockStoreServices();

}
