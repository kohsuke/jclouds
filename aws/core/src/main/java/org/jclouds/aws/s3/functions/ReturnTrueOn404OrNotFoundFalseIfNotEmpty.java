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
package org.jclouds.aws.s3.functions;

import static com.google.common.base.Predicates.equalTo;
import static com.google.common.base.Throwables.getCausalChain;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.get;
import static com.google.common.collect.Iterables.size;
import static org.jclouds.http.HttpUtils.returnValueOnCodeOrNull;
import static org.jclouds.util.Utils.propagateOrNull;

import java.util.List;

import javax.inject.Singleton;

import org.jclouds.aws.AWSResponseException;
import org.jclouds.blobstore.ContainerNotFoundException;

import com.google.common.base.Function;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class ReturnTrueOn404OrNotFoundFalseIfNotEmpty implements Function<Exception, Boolean> {

   public Boolean apply(Exception from) {
      List<Throwable> throwables = getCausalChain(from);

      Iterable<AWSResponseException> matchingAWSResponseException = filter(throwables, AWSResponseException.class);
      if (size(matchingAWSResponseException) >= 1 && get(matchingAWSResponseException, 0).getError() != null) {
         if (get(matchingAWSResponseException, 0).getError().getCode().equals("BucketNotEmpty"))
            return false;
      }

      Iterable<ContainerNotFoundException> matchingContainerNotFoundException = filter(throwables,
            ContainerNotFoundException.class);
      if (size(matchingContainerNotFoundException) >= 1) {
         return true;
      }
      if (returnValueOnCodeOrNull(from, true, equalTo(404)) != null)
         return true;

      return Boolean.class.cast(propagateOrNull(from));
   }
}
