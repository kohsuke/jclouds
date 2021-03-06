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
package org.jclouds.vcloud.functions;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.vcloud.endpoints.internal.VAppRoot;

import com.google.common.base.Function;

/**
 * @author Adrian Cole
 */
@Singleton
public class VAppIdToUri implements Function<Object, String> {
   @Inject
   @VAppRoot
   private String vAppRoot;

   public String apply(Object from) {
      checkArgument(checkNotNull(from, "from") instanceof String,
               "this binder is only valid for Strings!");
      return String.format("%s/%s", vAppRoot, from);
   }

}