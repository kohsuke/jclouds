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

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.logging.Logger;
import org.jclouds.vcloud.domain.Catalog;
import org.jclouds.vcloud.domain.CatalogItem;
import org.jclouds.vcloud.domain.Organization;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;

/**
 * @author Adrian Cole
 */
@Singleton
public class AllCatalogItemsInOrganization implements Function<Organization, Iterable<? extends CatalogItem>> {

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   public Logger logger = Logger.NULL;

   private final AllCatalogsInOrganization allCatalogsInOrganization;

   private final AllCatalogItemsInCatalog allCatalogItemsInCatalog;

   @Inject
   AllCatalogItemsInOrganization(AllCatalogsInOrganization allCatalogsInOrganization,
            AllCatalogItemsInCatalog allCatalogItemsInCatalog) {
      this.allCatalogsInOrganization = allCatalogsInOrganization;
      this.allCatalogItemsInCatalog = allCatalogItemsInCatalog;
   }

   @Override
   public Iterable<? extends CatalogItem> apply(Organization from) {
      return Iterables.concat(Iterables.transform(allCatalogsInOrganization.apply(from),
               new Function<Catalog, Iterable<? extends CatalogItem>>() {
                  @Override
                  public Iterable<? extends CatalogItem> apply(Catalog from) {
                     return allCatalogItemsInCatalog.apply(from);
                  }

               }));
   }
}