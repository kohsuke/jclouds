/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.chef.strategy.internal;

import static org.testng.Assert.assertEquals;

import java.util.Map;
import java.util.Set;

import org.jclouds.chef.ChefClient;
import org.jclouds.chef.domain.Node;
import org.jclouds.domain.JsonBall;
import org.jclouds.ohai.config.BaseOhaiModule;
import org.jclouds.ohai.config.ConfiguresOhai;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Injector;
import com.google.inject.Module;

/**
 * Tests behavior of {@code CreateNodeAndPopulateAutomaticAttributesImpl}
 * strategies
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", testName = "chef.CreateNodeAndPopulateAutomaticAttributesImplLiveTest")
public class CreateNodeAndPopulateAutomaticAttributesImplLiveTest extends BaseChefStrategyLiveTest {

   private CreateNodeAndPopulateAutomaticAttributesImpl strategy;
   private ChefClient chef;

   @ConfiguresOhai
   static class TestOhaiModule extends BaseOhaiModule {

      @Override
      protected Iterable<Supplier<Map<String, JsonBall>>> suppliers(Injector injector) {
         Supplier<Map<String, JsonBall>> supplier = Suppliers.<Map<String, JsonBall>> ofInstance(ImmutableMap
               .<String, JsonBall> of("foo", new JsonBall("bar")));
         return ImmutableList.<Supplier<Map<String, JsonBall>>> of(supplier);
      }
   }

   protected void addTestModulesTo(Set<Module> modules) {
      modules.add(new TestOhaiModule());
   }

   @BeforeTest(groups = "live", dependsOnMethods = "setupClient")
   void setupStrategy() {
      this.strategy = injector.getInstance(CreateNodeAndPopulateAutomaticAttributesImpl.class);
      this.chef = injector.getInstance(ChefClient.class);
   }

   @Test
   public void testExecute() {
      Set<String> runList = ImmutableSet.of("role[" + prefix + "]");
      try {
         strategy.execute(prefix, runList);
         Node node = chef.getNode(prefix);
         assertEquals(node.getName(), prefix);
         assertEquals(node.getRunList(), runList);
         assertEquals(node.getAutomatic().get("foo").toString(), "\"bar\"");
      } finally {
         injector.getInstance(ChefClient.class).deleteNode(prefix);
      }
   }
}
