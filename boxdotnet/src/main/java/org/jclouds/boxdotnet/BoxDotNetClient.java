/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
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

import java.util.concurrent.TimeUnit;

import org.jclouds.concurrent.Timeout;

/**
 * Provides synchronous access to BoxDotNet.
 * <p/>
 * 
 * @see BoxDotNetAsyncClient
 * @see <a href="TODO: insert URL of BoxDotNet documentation" />
 * @author Adrian Cole
 */
@Timeout(duration = 4, timeUnit = TimeUnit.SECONDS)
public interface BoxDotNetClient {
   /*
    * Note all these delegate to methods in BoxDotNetAsyncClient with a specified or inherited timeout.
    *   The singatures should match those of BoxDotNetAsyncClient, except the returnvals should not be 
    *   wrapped in a Future 
    */
   
   String list();
   
   /**
    * @return null, if not found
    */
   String get(long id);
   
   void delete(long id);

}
