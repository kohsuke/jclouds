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
package org.jclouds.vcloud.compute.functions;

import static org.jclouds.compute.util.ComputeServiceUtils.parseArchitectureOrNull;
import static org.jclouds.compute.util.ComputeServiceUtils.parseOsFamilyOrNull;

import javax.inject.Inject;

import org.jclouds.compute.domain.Architecture;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.domain.internal.ImageImpl;
import org.jclouds.compute.strategy.PopulateDefaultLoginCredentialsForImageStrategy;
import org.jclouds.domain.Location;
import org.jclouds.vcloud.domain.NamedResource;
import org.jclouds.vcloud.domain.VAppTemplate;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;

/**
 * @author Adrian Cole
 */
public class ImageForVAppTemplate implements Function<VAppTemplate, Image> {
   private final FindLocationForResource findLocationForResource;
   private final PopulateDefaultLoginCredentialsForImageStrategy credentialsProvider;
   private NamedResource parent;

   @Inject
   protected ImageForVAppTemplate(FindLocationForResource findLocationForResource,
            PopulateDefaultLoginCredentialsForImageStrategy credentialsProvider) {
      this.findLocationForResource = findLocationForResource;
      this.credentialsProvider = credentialsProvider;
   }

   public ImageForVAppTemplate withParent(NamedResource parent) {
      this.parent = parent;
      return this;
   }

   @Override
   public Image apply(VAppTemplate from) {
      OsFamily myOs = parseOsFamilyOrNull(from.getName());
      Architecture arch = parseArchitectureOrNull(from.getName());
      Location location = findLocationForResource.apply(parent);
      String name = getName(from.getName());
      String desc = from.getDescription() != null ? from.getDescription() : from.getName();
      return new ImageImpl(from.getId(), name, parent.getId() + "/" + from.getId(), location, from.getLocation(),
               ImmutableMap.<String, String> of(), desc, "", myOs, name, arch, credentialsProvider.execute(from));
   }

   protected String getName(String name) {
      return name;
   }
}