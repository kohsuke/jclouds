/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.gogrid.compute.functions;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeState;
import org.jclouds.compute.domain.internal.NodeMetadataImpl;
import org.jclouds.domain.Credentials;
import org.jclouds.domain.Location;
import org.jclouds.gogrid.GoGridClient;
import org.jclouds.gogrid.domain.Server;
import org.jclouds.logging.Logger;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

/**
 * @author Oleksiy Yarmula
 */
@Singleton
public class ServerToNodeMetadata implements Function<Server, NodeMetadata> {
   public static final Pattern ALL_BEFORE_HYPHEN_HEX = Pattern.compile("([^-]+)-[0-9a-f]+");

   @Resource
   protected Logger logger = Logger.NULL;
   private final Map<String, NodeState> serverStateToNodeState;
   private final GoGridClient client;
   private final Set<? extends Image> images;
   private final Map<String, ? extends Location> locations;

   private static class FindImageForServer implements Predicate<Image> {
      private final Server instance;

      @Inject
      private FindImageForServer(Server instance) {
         this.instance = instance;
      }

      @Override
      public boolean apply(Image input) {
         return input.getProviderId().equals(instance.getImage().getId() + "")
                  && (input.getLocation() == null || input.getLocation().getId().equals(
                           instance.getDatacenter().getId() + ""));
      }
   }

   @Inject
   ServerToNodeMetadata(Map<String, NodeState> serverStateToNodeState, GoGridClient client,
            Set<? extends Image> images, Map<String, ? extends Location> locations) {
      this.serverStateToNodeState = checkNotNull(serverStateToNodeState, "serverStateToNodeState");
      this.client = checkNotNull(client, "client");
      this.images = checkNotNull(images, "images");
      this.locations = checkNotNull(locations, "locations");
   }

   @Override
   public NodeMetadata apply(Server from) {
      Matcher matcher = ALL_BEFORE_HYPHEN_HEX.matcher(from.getName());
      final String tag = matcher.find() ? matcher.group(1) : null;
      Set<String> ipSet = ImmutableSet.of(from.getIp().getIp());
      NodeState state = serverStateToNodeState.get(from.getState().getName());
      Credentials creds = client.getServerServices().getServerCredentialsList().get(from.getName());
      Image image = null;
      try {
         image = Iterables.find(images, new FindImageForServer(from));
      } catch (NoSuchElementException e) {
         logger.warn("could not find a matching image for server %s", from);
      }
      return new NodeMetadataImpl(from.getId() + "", from.getName(), from.getId() + "", locations
               .get(from.getDatacenter().getId() + ""), null, ImmutableMap.<String, String> of(),
               tag, image, state, ipSet, ImmutableList.<String> of(), ImmutableMap
                        .<String, String> of(), creds);
   }
}