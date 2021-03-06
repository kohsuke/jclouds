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
package org.jclouds.blobstore.functions;

import static com.google.common.base.Preconditions.checkArgument;
import static org.jclouds.Constants.PROPERTY_API_VERSION;
import static org.jclouds.blobstore.reference.BlobStoreConstants.PROPERTY_USER_METADATA_PREFIX;
import static org.jclouds.blobstore.util.BlobStoreUtils.getKeyFor;
import static org.jclouds.http.HttpUtils.attemptToParseSizeAndRangeFromHeaders;

import java.util.Map.Entry;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.ws.rs.core.HttpHeaders;

import org.jclouds.blobstore.domain.MutableBlobMetadata;
import org.jclouds.date.DateService;
import org.jclouds.http.HttpException;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.rest.InvocationContext;
import org.jclouds.rest.internal.GeneratedHttpRequest;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;

/**
 * @author Adrian Cole
 */
public class ParseSystemAndUserMetadataFromHeaders implements Function<HttpResponse, MutableBlobMetadata>,
      InvocationContext {
   private final String metadataPrefix;
   private final DateService dateParser;
   private final Provider<MutableBlobMetadata> metadataFactory;
   private final String apiVersion;

   private GeneratedHttpRequest<?> request;

   @Inject
   public ParseSystemAndUserMetadataFromHeaders(Provider<MutableBlobMetadata> metadataFactory, DateService dateParser,
         @Named(PROPERTY_USER_METADATA_PREFIX) String metadataPrefix, @Named(PROPERTY_API_VERSION) String apiVersion) {
      this.metadataFactory = metadataFactory;
      this.dateParser = dateParser;
      this.metadataPrefix = metadataPrefix;
      this.apiVersion = apiVersion;
   }

   public MutableBlobMetadata apply(HttpResponse from) {
      String objectKey = getKeyFor(request, from);
      MutableBlobMetadata to = metadataFactory.get();
      to.setName(objectKey);
      setContentTypeOrThrowException(from, to);
      addETagTo(from, to);
      addContentMD5To(from, to);
      parseLastModifiedOrThrowException(from, to);
      setContentLength(from, to);
      addUserMetadataTo(from, to);
      return to;
   }

   @VisibleForTesting
   void addUserMetadataTo(HttpResponse from, MutableBlobMetadata metadata) {
      for (Entry<String, String> header : from.getHeaders().entries()) {
         if (header.getKey() != null && header.getKey().startsWith(metadataPrefix))
            metadata.getUserMetadata().put((header.getKey().substring(metadataPrefix.length())).toLowerCase(),
                  header.getValue());
      }
   }

   @VisibleForTesting
   void setContentLength(HttpResponse from, MutableBlobMetadata metadata) throws HttpException {
      metadata.setSize(attemptToParseSizeAndRangeFromHeaders(from));
   }

   @VisibleForTesting
   void parseLastModifiedOrThrowException(HttpResponse from, MutableBlobMetadata metadata) throws HttpException {
      String lastModified = from.getFirstHeaderOrNull(HttpHeaders.LAST_MODIFIED);
      if (lastModified == null)
         throw new HttpException(HttpHeaders.LAST_MODIFIED + " header not present in response: " + from.getStatusLine());
      // Eucalyptus 1.6 returns iso8601 dates
      if (apiVersion.indexOf("Walrus-1.6") != -1) {
         metadata.setLastModified(dateParser.iso8601DateParse(lastModified.replace("+0000", "Z")));
      } else {
         metadata.setLastModified(dateParser.rfc822DateParse(lastModified));
      }

      if (metadata.getLastModified() == null)
         throw new HttpException("could not parse: " + HttpHeaders.LAST_MODIFIED + ": " + lastModified);
   }

   @VisibleForTesting
   protected void addETagTo(HttpResponse from, MutableBlobMetadata metadata) {
      String eTag = from.getFirstHeaderOrNull(HttpHeaders.ETAG);
      if (metadata.getETag() == null && eTag != null) {
         metadata.setETag(eTag);
      }
   }

   @VisibleForTesting
   protected void addContentMD5To(HttpResponse from, MutableBlobMetadata metadata) {
      if (from.getPayload() != null)
         metadata.setContentMD5(from.getPayload().getContentMD5());
   }

   @VisibleForTesting
   void setContentTypeOrThrowException(HttpResponse from, MutableBlobMetadata metadata) throws HttpException {
      if (from.getPayload() != null)
         metadata.setContentType(from.getPayload().getContentType());
      if (from.getStatusCode() != 204 && (metadata.getContentType() == null
            || "application/unknown".equals(metadata.getContentType())))
         throw new HttpException(HttpHeaders.CONTENT_TYPE + " not found in headers");
   }

   public ParseSystemAndUserMetadataFromHeaders setContext(HttpRequest request) {
      checkArgument(request instanceof GeneratedHttpRequest<?>, "note this handler requires a GeneratedHttpRequest");
      this.request = (GeneratedHttpRequest<?>) request;
      return this;
   }
}