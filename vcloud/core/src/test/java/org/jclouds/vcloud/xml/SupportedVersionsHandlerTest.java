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
package org.jclouds.vcloud.xml;

import static org.testng.Assert.assertEquals;

import java.io.InputStream;
import java.net.URI;
import java.util.SortedMap;

import org.jclouds.http.functions.ParseSax;
import org.jclouds.http.functions.ParseSax.Factory;
import org.jclouds.http.functions.config.SaxParserModule;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSortedMap;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Tests behavior of {@code SupportedVersionsHandler}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "vcloud.SupportedVersionsHandlerTest")
public class SupportedVersionsHandlerTest {

   public void testApplyInputStream() {
      InputStream is = getClass().getResourceAsStream("/versions.xml");

      Injector injector = Guice.createInjector(new SaxParserModule());
      Factory factory = injector.getInstance(ParseSax.Factory.class);

      SortedMap<String, URI> result = factory.create(
               injector.getInstance(SupportedVersionsHandler.class)).parse(is);
      assertEquals(result, ImmutableSortedMap.of("0.8", URI
               .create("https://services.vcloudexpress.terremark.com/api/v0.8/login")));
   }
}
