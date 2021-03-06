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
package org.jclouds.aws.handlers;

import static org.jclouds.http.HttpUtils.releasePayload;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.aws.AWSResponseException;
import org.jclouds.aws.domain.AWSError;
import org.jclouds.aws.util.AWSUtils;
import org.jclouds.blobstore.ContainerNotFoundException;
import org.jclouds.blobstore.KeyNotFoundException;
import org.jclouds.http.HttpCommand;
import org.jclouds.http.HttpErrorHandler;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.HttpResponseException;
import org.jclouds.logging.Logger;
import org.jclouds.rest.AuthorizationException;
import org.jclouds.rest.ResourceNotFoundException;

import com.google.common.annotations.VisibleForTesting;

/**
 * This will parse and set an appropriate exception on the command object.
 * 
 * @see AWSError
 * @author Adrian Cole
 * 
 */
@Singleton
public class ParseAWSErrorFromXmlContent implements HttpErrorHandler {
   @Resource
   protected Logger logger = Logger.NULL;

   @VisibleForTesting
   final AWSUtils utils;

   @Inject
   ParseAWSErrorFromXmlContent(AWSUtils utils) {
      this.utils = utils;
   }

   public void handleError(HttpCommand command, HttpResponse response) {
      HttpRequest request = command.getRequest();
      Exception exception = new HttpResponseException(command, response);
      try {
         AWSError error = utils.parseAWSErrorFromContent(request, response);
         exception = error != null ? new AWSResponseException(command, response, error) : exception;
         String notFoundMessage = error != null ? error.getMessage() : String.format("%s -> %s", request
                  .getRequestLine(), response.getStatusLine());
         switch (response.getStatusCode()) {
            case 400:
               if (error.getCode().endsWith(".NotFound") || error.getCode().endsWith(".Unknown"))
                  exception = new ResourceNotFoundException(notFoundMessage, exception);
               else if (error.getCode().equals("IncorrectState"))
                  exception = new IllegalStateException(error.getMessage(), exception);
               else if (error.getCode().equals("AuthFailure"))
                  exception = new AuthorizationException(command.getRequest(), error != null ? error.getMessage()
                           : response.getStatusLine());
               break;
            case 401:
            case 403:
               exception = new AuthorizationException(command.getRequest(), error != null ? error.getMessage()
                        : response.getStatusLine());
               break;
            case 404:
               if (!command.getRequest().getMethod().equals("DELETE")) {
                  String container = request.getEndpoint().getHost();
                  String key = request.getEndpoint().getPath();
                  if (key == null || key.equals("/"))
                     exception = new ContainerNotFoundException(container, notFoundMessage);
                  else
                     exception = new KeyNotFoundException(container, key, notFoundMessage);
               }
               break;
         }
      } finally {
         releasePayload(response);
         command.setException(exception);
      }
   }

}