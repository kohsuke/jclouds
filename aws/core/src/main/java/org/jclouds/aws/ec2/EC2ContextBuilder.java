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
package org.jclouds.aws.ec2;

import static com.google.common.base.Predicates.instanceOf;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Properties;

import org.jclouds.aws.ec2.compute.config.EC2ComputeServiceContextModule;
import org.jclouds.aws.ec2.compute.config.EC2ResolveImagesModule;
import org.jclouds.aws.ec2.config.EC2RestClientModule;
import org.jclouds.compute.ComputeServiceContextBuilder;
import org.jclouds.concurrent.config.ConfiguresExecutorService;
import org.jclouds.http.config.ConfiguresHttpCommandExecutorService;
import org.jclouds.http.config.JavaUrlHttpCommandExecutorServiceModule;
import org.jclouds.logging.config.LoggingModule;
import org.jclouds.logging.jdk.config.JDKLoggingModule;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import com.google.inject.Injector;
import com.google.inject.Module;

/**
 * Creates {@link EC2ComputeServiceContext} or {@link Injector} instances based
 * on the most commonly requested arguments.
 * <p/>
 * Note that Threadsafe objects will be bound as singletons to the Injector or
 * Context provided.
 * <p/>
 * <p/>
 * If no <code>Module</code>s are specified, the default
 * {@link JDKLoggingModule logging} and
 * {@link JavaUrlHttpCommandExecutorServiceModule http transports} will be
 * installed.
 * 
 * @author Adrian Cole
 * @see EC2ComputeServiceContext
 */
public class EC2ContextBuilder extends ComputeServiceContextBuilder<EC2Client, EC2AsyncClient> {

   public EC2ContextBuilder(Properties props) {
      super(EC2Client.class, EC2AsyncClient.class, props);
   }

   @Override
   public EC2ContextBuilder withModules(Iterable<Module> modules) {
      return (EC2ContextBuilder) super.withModules(modules);
   }

   @Override
   protected void addContextModule(List<Module> modules) {
      modules.add(new EC2ComputeServiceContextModule());
   }

   @Override
   protected void addClientModule(List<Module> modules) {
      modules.add(new EC2RestClientModule());
   }

   @Override
   protected void addImageResolutionModule() {
      modules.add(new EC2ResolveImagesModule());
   }

   @Override
   public Injector buildInjector() {
      try {
         Iterables.find(modules, Predicates.instanceOf(ConfigureELBModule.class));
      } catch (NoSuchElementException e) {
         Iterable<Module> infra = Iterables.filter(modules, new Predicate<Module>() {
            public boolean apply(Module input) {
               return input.getClass().isAnnotationPresent(ConfiguresExecutorService.class)
                     || input.getClass().isAnnotationPresent(ConfiguresHttpCommandExecutorService.class)
                     || instanceOf(LoggingModule.class).apply(input);
            }

         });
         modules.add(new ConfigureELBModule(infra, properties));
      }
      return super.buildInjector();
   }
}
