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
package org.jclouds.aws.ec2.compute.strategy;

import static com.google.common.collect.Iterables.concat;
import static org.jclouds.concurrent.FutureIterables.transformParallel;

import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.Constants;
import org.jclouds.aws.ec2.EC2AsyncClient;
import org.jclouds.aws.ec2.options.DescribeImagesOptions;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.logging.Logger;

import com.google.common.base.Function;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class DescribeImagesParallel implements
         Function<Iterable<Entry<String, DescribeImagesOptions>>, Iterable<? extends org.jclouds.aws.ec2.domain.Image>> {
   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   protected final EC2AsyncClient sync;
   final ExecutorService executor;

   @Inject
   public DescribeImagesParallel(EC2AsyncClient sync, @Named(Constants.PROPERTY_USER_THREADS) ExecutorService executor) {
      super();
      this.sync = sync;
      this.executor = executor;
   }

   @Override
   public Iterable<? extends org.jclouds.aws.ec2.domain.Image> apply(
            Iterable<Entry<String, DescribeImagesOptions>> queries) {
      return concat(transformParallel(
               queries,
               new Function<Entry<String, DescribeImagesOptions>, Future<Set<? extends org.jclouds.aws.ec2.domain.Image>>>() {

                  @Override
                  public Future<Set<? extends org.jclouds.aws.ec2.domain.Image>> apply(
                           Entry<String, DescribeImagesOptions> from) {
                     return sync.getAMIServices().describeImagesInRegion(from.getKey(), from.getValue());
                  }

               }, executor, null, logger, "amis"));
   }

}