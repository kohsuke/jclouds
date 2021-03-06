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
package org.jclouds.rest;

import org.jclouds.http.HttpRequest;

/**
 * Thrown when there is an authorization error.
 * 
 * @author Adrian Cole
 */
public class AuthorizationException extends RuntimeException {

   /** The serialVersionUID */
   private static final long serialVersionUID = -2272965726680821281L;

   public AuthorizationException() {
      super();
   }

   public AuthorizationException(String arg0, Throwable arg1) {
      super(arg0, arg1);
   }

   public AuthorizationException(HttpRequest resource, String error) {
      super(String.format("%s -> %s", resource.getRequestLine(), error));
   }

   public AuthorizationException(HttpRequest resource, String error, Throwable arg1) {
      super(String.format("%s -> %s", resource.getRequestLine(), error), arg1);
   }

   public AuthorizationException(Throwable arg0) {
      super(arg0);
   }
}
