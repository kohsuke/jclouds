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
package org.jclouds.demo.tweetstore.controller;

import static org.easymock.classextension.EasyMock.createMock;
import static org.jclouds.util.Utils.toStringAndClose;
import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.util.Map;
import java.util.SortedSet;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;

import org.jclouds.blobstore.BlobMap;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.BlobStoreContextFactory;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.demo.tweetstore.reference.TweetStoreConstants;
import org.jclouds.twitter.TwitterClient;
import org.jclouds.twitter.domain.Status;
import org.jclouds.twitter.domain.User;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;

/**
 * Tests behavior of {@code StoreTweetsController}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "tweetstore.StoreTweetsControllerTest")
public class StoreTweetsControllerTest {

   TwitterClient createTwitterClient() {
      return createMock(TwitterClient.class);
   }

   Map<String, BlobStoreContext> createBlobStores() throws InterruptedException, ExecutionException {
      Map<String, BlobStoreContext> contexts = ImmutableMap.<String, BlobStoreContext> of("test1",
               new BlobStoreContextFactory().createContext("transient", "dummy", "dummy"), "test2",
               new BlobStoreContextFactory().createContext("transient", "dummy", "dummy"));
      for (BlobStoreContext blobstore : contexts.values()) {
         blobstore.getAsyncBlobStore().createContainerInLocation(null, "favo").get();
      }
      return contexts;
   }

   public void testStoreTweets() throws IOException, InterruptedException, ExecutionException {
      Map<String, BlobStoreContext> stores = createBlobStores();
      StoreTweetsController function = new StoreTweetsController(stores, "favo",
               createTwitterClient());

      SortedSet<Status> allAboutMe = Sets.newTreeSet();
      User frank = new User(1l, "frank");
      Status frankStatus = new Status(1l, frank, "I love beans!");

      User jimmy = new User(2l, "jimmy");
      Status jimmyStatus = new Status(2l, jimmy, "cloud is king");

      allAboutMe.add(frankStatus);
      allAboutMe.add(jimmyStatus);

      function.addMyTweets("test1", allAboutMe);
      function.addMyTweets("test2", allAboutMe);

      for (Entry<String, BlobStoreContext> entry : stores.entrySet()) {
         BlobMap map = entry.getValue().createBlobMap("favo");
         Blob frankBlob = map.get("1");
         assertEquals(frankBlob.getMetadata().getName(), "1");
         assertEquals(frankBlob.getMetadata().getUserMetadata()
                  .get(TweetStoreConstants.SENDER_NAME), "frank");
         assertEquals(frankBlob.getMetadata().getContentType(), "text/plain");
         assertEquals(toStringAndClose(frankBlob.getPayload().getInput()), "I love beans!");

         Blob jimmyBlob = map.get("2");
         assertEquals(jimmyBlob.getMetadata().getName(), "2");
         assertEquals(jimmyBlob.getMetadata().getUserMetadata()
                  .get(TweetStoreConstants.SENDER_NAME), "jimmy");
         assertEquals(jimmyBlob.getMetadata().getContentType(), "text/plain");
         assertEquals(toStringAndClose(jimmyBlob.getPayload().getInput()), "cloud is king");
      }

   }
}
