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
package org.jclouds.azure.storage.blob.domain;

import java.net.URI;
import java.util.Date;
import java.util.Map;

/**
 * 
 * @author Adrian Cole
 * 
 */
public interface BlobProperties extends Comparable<BlobProperties> {

   Map<String, String> getMetadata();

   /**
    *  
    */
   BlobType getType();

   LeaseStatus getLeaseStatus();

   URI getUrl();

   String getName();

   Date getLastModified();

   String getETag();

   Long getContentLength();

   /**
    * This value present in system metadata requests on blobs which were created specifying the
    * Content-MD5 header.
    */
   byte[] getContentMD5();

   /**
    * 
    * A standard MIME type describing the format of the contents. If none is provided, the default
    * is binary/octet-stream.
    * 
    * @see <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.17"/>
    */
   String getContentType();

   /**
    * Specifies what content encodings have been applied to the object and thus what decoding
    * mechanisms must be applied in order to obtain the media-type referenced by the Content-Type
    * header field.
    * 
    * @see <a href= "http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html?sec14.11" />
    */
   public String getContentEncoding();

   public String getContentLanguage();
}
