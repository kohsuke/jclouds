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
package org.jclouds.http.filters;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.Constants.PROPERTY_CREDENTIAL;
import static org.jclouds.Constants.PROPERTY_IDENTITY;

import java.io.UnsupportedEncodingException;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.ws.rs.core.HttpHeaders;

import org.jclouds.crypto.Crypto;
import org.jclouds.crypto.CryptoStreams;
import org.jclouds.http.HttpException;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpRequestFilter;

import com.google.common.collect.ImmutableSet;

/**
 * Uses Basic Authentication to sign the request.
 * 
 * @see <a href= "http://en.wikipedia.org/wiki/Basic_access_authentication" />
 * @author Adrian Cole
 * 
 */
@Singleton
public class BasicAuthentication implements HttpRequestFilter {

   private final Set<String> credentialList;

   @Inject
   BasicAuthentication(@Named(PROPERTY_IDENTITY) String user, @Named(PROPERTY_CREDENTIAL) String password, Crypto crypto)
            throws UnsupportedEncodingException {
      this.credentialList = ImmutableSet.of("Basic "
               + CryptoStreams.base64(String.format("%s:%s", checkNotNull(user, "user"),
                        checkNotNull(password, "password")).getBytes("UTF-8")));
   }

   public void filter(HttpRequest request) throws HttpException {
      request.getHeaders().replaceValues(HttpHeaders.AUTHORIZATION, credentialList);
   }
}