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
package org.jclouds.rackspace.cloudservers.functions;

import static org.testng.Assert.assertEquals;

import java.io.InputStream;
import java.net.UnknownHostException;

import org.jclouds.date.DateService;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.functions.UnwrapOnlyJsonValue;
import org.jclouds.io.Payloads;
import org.jclouds.json.config.GsonModule;
import org.jclouds.rackspace.cloudservers.domain.Image;
import org.jclouds.rackspace.cloudservers.domain.ImageStatus;
import org.jclouds.rackspace.config.RackspaceParserModule;
import org.testng.annotations.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;

/**
 * Tests behavior of {@code ParseImageFromJsonResponse}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "cloudservers.ParseImageFromJsonResponseTest")
public class ParseImageFromJsonResponseTest {
   Injector i = Guice.createInjector(new RackspaceParserModule(), new GsonModule());

   DateService dateService = i.getInstance(DateService.class);

   public void testApplyInputStreamDetails() throws UnknownHostException {
      InputStream is = getClass().getResourceAsStream("/cloudservers/test_get_image_details.json");

      UnwrapOnlyJsonValue<Image> parser = i.getInstance(Key.get(new TypeLiteral<UnwrapOnlyJsonValue<Image>>() {
      }));
      Image response = parser.apply(new HttpResponse(200, "ok", Payloads.newInputStreamPayload(is)));

      assertEquals(response.getId(), 2);
      assertEquals(response.getName(), "CentOS 5.2");
      assertEquals(response.getCreated(), dateService.iso8601SecondsDateParse("2010-08-10T12:00:00Z"));
      assertEquals(response.getProgress(), new Integer(80));
      assertEquals(response.getServerId(), new Integer(12));
      assertEquals(response.getStatus(), ImageStatus.SAVING);
      assertEquals(response.getUpdated(), dateService.iso8601SecondsDateParse(("2010-10-10T12:00:00Z")));
   }
}
