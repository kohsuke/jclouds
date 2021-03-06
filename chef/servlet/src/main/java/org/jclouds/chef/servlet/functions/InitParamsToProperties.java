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
package org.jclouds.chef.servlet.functions;

import java.util.Enumeration;
import java.util.Properties;

import javax.inject.Singleton;
import javax.servlet.ServletContextEvent;

import com.google.common.base.Function;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class InitParamsToProperties implements Function<ServletContextEvent, Properties> {

   public static final InitParamsToProperties INSTANCE = new InitParamsToProperties();

   public Properties apply(ServletContextEvent servletContextEvent) {
      Properties overrides = new Properties();
      Enumeration<?> e = servletContextEvent.getServletContext().getInitParameterNames();
      while (e.hasMoreElements()) {
         String propertyName = e.nextElement().toString();
         overrides.setProperty(propertyName, servletContextEvent.getServletContext().getInitParameter(propertyName));
      }
      return overrides;
   }
}