/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.ohai.config;

import java.util.Properties;

import javax.inject.Named;

import com.google.inject.Provides;

/**
 * Wires the components needed to parse ohai data from a JVM
 * 
 * @author Adrian Cole
 */
public abstract class BaseOhaiJVMModule extends BaseOhaiModule {

   @Named("systemProperties")
   @Provides
   protected Properties systemProperties() {
      return System.getProperties();
   }

}