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
package org.jclouds.azure.storage.filters;

import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.net.URI;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.core.HttpHeaders;

import org.jclouds.encryption.internal.Base64;
import org.jclouds.http.HttpRequest;
import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.jclouds.rest.RestContextFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Injector;
import com.google.inject.Module;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "azurestorage.SharedKeyLiteAuthenticationTest")
public class SharedKeyLiteAuthenticationTest {

   private static final String KEY = Base64.encodeBytes("bar".getBytes());
   private static final String ACCOUNT = "foo";
   private Injector injector;
   private SharedKeyLiteAuthentication filter;

   @DataProvider(parallel = true)
   public Object[][] dataProvider() {
      return new Object[][] {
               { new HttpRequest(
                        HttpMethod.PUT,
                        URI
                                 .create("http://"
                                          + ACCOUNT
                                          + ".blob.core.windows.net/movies/MOV1.avi?comp=block&blockid=BlockId1&timeout=60")) },
               { new HttpRequest(HttpMethod.PUT, URI.create("http://" + ACCOUNT
                        + ".blob.core.windows.net/movies/MOV1.avi?comp=blocklist&timeout=120")) },
               { new HttpRequest(HttpMethod.GET, URI.create("http://" + ACCOUNT
                        + ".blob.core.windows.net/movies/MOV1.avi")) } };
   }

   /**
    * NOTE this test is dependent on how frequently the timestamp updates. At the time of writing,
    * this was once per second. If this timestamp update interval is increased, it could make this
    * test appear to hang for a long time.
    */
   @Test(threadPoolSize = 3, dataProvider = "dataProvider", timeOut = 3000)
   void testIdempotent(HttpRequest request) {
      filter.filter(request);
      String signature = request.getFirstHeaderOrNull(HttpHeaders.AUTHORIZATION);
      String date = request.getFirstHeaderOrNull(HttpHeaders.DATE);
      int iterations = 1;
      while (request.getFirstHeaderOrNull(HttpHeaders.DATE).equals(date)) {
         date = request.getFirstHeaderOrNull(HttpHeaders.DATE);
         filter.filter(request);
         iterations++;
         assertEquals(signature, request.getFirstHeaderOrNull(HttpHeaders.AUTHORIZATION));
      }
      System.out.printf("%s: %d iterations before the timestamp updated %n", Thread.currentThread()
               .getName(), iterations);
   }

   @Test
   void testAclQueryStringRoot() {
      URI host = URI.create("http://" + ACCOUNT + ".blob.core.windows.net/?comp=list");
      HttpRequest request = new HttpRequest(HttpMethod.GET, host);
      StringBuilder builder = new StringBuilder();
      filter.appendUriPath(request, builder);
      assertEquals(builder.toString(), "/?comp=list");
   }

   @Test
   void testAclQueryStringResTypeNotSignificant() {
      URI host = URI.create("http://" + ACCOUNT
               + ".blob.core.windows.net/mycontainer?restype=container");
      HttpRequest request = new HttpRequest(HttpMethod.GET, host);
      StringBuilder builder = new StringBuilder();
      filter.appendUriPath(request, builder);
      assertEquals(builder.toString(), "/mycontainer");
   }

   @Test
   void testAclQueryStringComp() {
      URI host = URI.create("http://" + ACCOUNT + ".blob.core.windows.net/mycontainer?comp=list");
      HttpRequest request = new HttpRequest(HttpMethod.GET, host);
      StringBuilder builder = new StringBuilder();
      filter.appendUriPath(request, builder);
      assertEquals(builder.toString(), "/mycontainer?comp=list");
   }

   @Test
   void testAclQueryStringRelativeWithExtraJunk() {
      URI host = URI
               .create("http://"
                        + ACCOUNT
                        + ".blob.core.windows.net/mycontainer?comp=list&marker=marker&maxresults=1&prefix=prefix");
      HttpRequest request = new HttpRequest(HttpMethod.GET, host);
      StringBuilder builder = new StringBuilder();
      filter.appendUriPath(request, builder);
      assertEquals(builder.toString(), "/mycontainer?comp=list");
   }

   /**
    * before class, as we need to ensure that the filter is threadsafe.
    * @throws IOException 
    * 
    */
   @BeforeClass
   protected void createFilter() throws IOException {
      injector = new RestContextFactory().createContextBuilder("azurequeue", ACCOUNT, KEY,
               ImmutableSet.<Module> of(new Log4JLoggingModule())).buildInjector();
      filter = injector.getInstance(SharedKeyLiteAuthentication.class);
   }

}