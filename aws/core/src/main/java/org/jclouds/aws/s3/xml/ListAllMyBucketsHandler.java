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
package org.jclouds.aws.s3.xml;

import java.util.Date;
import java.util.Set;

import javax.inject.Inject;

import org.jclouds.aws.s3.domain.BucketMetadata;
import org.jclouds.aws.s3.domain.CanonicalUser;
import org.jclouds.date.DateService;
import org.jclouds.http.functions.ParseSax;

import com.google.common.collect.Sets;

/**
 * Parses the following XML document:
 * <p/>
 * SetAllMyBucketsResult xmlns="http://doc.s3.amazonaws.com/2006-03-01"
 * 
 * @see <a
 *      href="http://docs.amazonwebservices.com/AmazonS3/2006-03-01/index.html?RESTServiceGET.html"
 *      />
 * @author Adrian Cole
 */
public class ListAllMyBucketsHandler extends ParseSax.HandlerWithResult<Set<BucketMetadata>> {

   private Set<BucketMetadata> buckets = Sets.newLinkedHashSet();
   private CanonicalUser currentOwner;
   private StringBuilder currentText = new StringBuilder();

   private final DateService dateParser;
   private String currentName;
   private Date currentCreationDate;

   @Inject
   public ListAllMyBucketsHandler(DateService dateParser) {
      this.dateParser = dateParser;
   }

   public Set<BucketMetadata> getResult() {
      return buckets;
   }

   public void endElement(String uri, String name, String qName) {
      if (qName.equals("ID")) { // owner stuff
         currentOwner = new CanonicalUser(currentText.toString().trim());
      } else if (qName.equals("DisplayName")) {
         currentOwner.setDisplayName(currentText.toString().trim());
      } else if (qName.equals("Bucket")) {
         buckets.add(new BucketMetadata(currentName, currentCreationDate, currentOwner));
      } else if (qName.equals("Name")) {
         currentName = currentText.toString().trim();
      } else if (qName.equals("CreationDate")) {
         currentCreationDate = dateParser.iso8601DateParse(currentText.toString().trim());
      }
      currentText = new StringBuilder();
   }

   public void characters(char ch[], int start, int length) {
      currentText.append(ch, start, length);
   }
}
