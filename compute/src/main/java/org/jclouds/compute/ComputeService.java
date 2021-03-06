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
package org.jclouds.compute;

import java.util.Map;
import java.util.Set;

import org.jclouds.compute.domain.ComputeMetadata;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.Size;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.domain.TemplateBuilder;
import org.jclouds.compute.internal.BaseComputeService;
import org.jclouds.compute.options.RunScriptOptions;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.domain.Location;
import org.jclouds.io.Payload;
import org.jclouds.ssh.ExecResponse;

import com.google.common.base.Predicate;
import com.google.inject.ImplementedBy;

/**
 * Provides portable access to launching compute instances.
 * 
 * @author Adrian Cole
 * @author Ivan Meredith
 */
@ImplementedBy(BaseComputeService.class)
public interface ComputeService {

   /**
    * @return a reference to the context that created this ComputeService.
    */
   ComputeServiceContext getContext();

   /**
    * Makes a new template builder for this service
    */
   TemplateBuilder templateBuilder();

   /**
    * Makes a new set of options for running nodes
    */
   TemplateOptions templateOptions();

   /**
    * The list sizes command shows you the options including virtual cpu count,
    * memory, and disks. cpu count is not a portable quantity across clouds, as
    * they are measured differently. However, it is a good indicator of relative
    * speed within a cloud. memory is measured in megabytes and disks in
    * gigabytes.
    * 
    * @return a map of sizes by ID, conceding that in some clouds the "id" is
    *         not used.
    */
   Set<? extends Size> listSizes();

   /**
    * Images define the operating system and metadata related to a node. In some
    * clouds, Images are bound to a specific region, and their identifiers are
    * different across these regions. For this reason, you should consider
    * matching image requirements like operating system family with
    * TemplateBuilder as opposed to choosing an image explicitly. The
    * getImages() command returns a map of images by id.
    */
   Set<? extends Image> listImages();

   /**
    * all nodes available to the current user by id. If possible, the returned
    * set will include {@link NodeMetadata} objects.
    */
   Set<? extends ComputeMetadata> listNodes();

   /**
    * The list locations command returns all the valid locations for nodes. A
    * location has a scope, which is typically region or zone. A region is a
    * general area, like eu-west, where a zone is similar to a datacenter. If a
    * location has a parent, that implies it is within that location. For
    * example a location can be a rack, whose parent is likely to be a zone.
    */
   Set<? extends Location> listAssignableLocations();

   /**
    * 
    * The compute api treats nodes as a group based on a tag you specify. Using
    * this tag, you can choose to operate one or many nodes as a logical unit
    * without regard to the implementation details of the cloud.
    * <p/>
    * 
    * The set that is returned will include credentials you can use to ssh into
    * the nodes. The "key" part of the credentials is either a password or a
    * private key. You have to inspect the value to determine this.
    * 
    * <pre>
    * if (node.getCredentials().key.startsWith("-----BEGIN RSA PRIVATE KEY-----"))
    *    // it is a private key, not a password.
    * </pre>
    * 
    * <p/>
    * Note. if all you want to do is execute a script at bootup, you should
    * consider use of the runscript option.
    * <p/>
    * If resources such as security groups are needed, they will be reused or
    * created for you. Inbound port 22 will always be opened up.
    * 
    * @param tag
    *           - common identifier to group nodes by, cannot contain hyphens
    * @param count
    *           - how many to fire up.
    * @param template
    *           - how to configure the nodes
    * @return all of the nodes the api was able to launch in a running state.
    * 
    * @throws RunNodesException
    *            when there's a problem applying options to nodes. Note that
    *            successful and failed nodes are a part of this exception, so be
    *            sure to inspect this carefully.
    */
   Set<? extends NodeMetadata> runNodesWithTag(String tag, int count, Template template) throws RunNodesException;

   /**
    * Like {@link ComputeService#runNodesWithTag(String,int,Template)}, except
    * that the template is default, equivalent to {@code
    * templateBuilder().any().options(templateOptions)}.
    */
   Set<? extends NodeMetadata> runNodesWithTag(String tag, int count, TemplateOptions templateOptions)
         throws RunNodesException;

   /**
    * Like {@link ComputeService#runNodesWithTag(String,int,TemplateOptions)},
    * except that the options are default, as specified in
    * {@link ComputeService#templateOptions}.
    */
   Set<? extends NodeMetadata> runNodesWithTag(String tag, int count) throws RunNodesException;

   /**
    * destroy the node, given its id. If it is the only node in a tag set, the
    * dependent resources will also be destroyed.
    */
   void destroyNode(String id);

   /**
    * nodes matching the filter are treated as a logical set. Using the delete
    * command, you can save time by removing the nodes in parallel. When the
    * last node in a set is destroyed, any indirect resources it uses, such as
    * keypairs, are also destroyed.
    * 
    * @return list of nodes destroyed
    */
   Set<? extends NodeMetadata> destroyNodesMatching(Predicate<NodeMetadata> filter);

   /**
    * reboot the node, given its id.
    */
   void rebootNode(String id);

   /**
    * nodes matching the filter are treated as a logical set. Using this
    * command, you can save time by rebooting the nodes in parallel.
    */
   void rebootNodesMatching(Predicate<NodeMetadata> filter);

   /**
    * Find a node by its id.
    */
   NodeMetadata getNodeMetadata(String id);

   /**
    * get all nodes including details such as image and ip addresses even if it
    * incurs extra requests to the service.
    * 
    * @param filter
    *           how to select the nodes you are interested in details on.
    */
   Set<? extends NodeMetadata> listNodesDetailsMatching(Predicate<ComputeMetadata> filter);

   /**
    * Runs the script without any additional options
    * 
    * @see #runScriptOnNodesMatching(Predicate, Payload, 
    *      org.jclouds.compute.options.RunScriptOptions)
    * @see org.jclouds.compute.predicates.NodePredicates#runningWithTag(String)
    */
   Map<? extends NodeMetadata, ExecResponse> runScriptOnNodesMatching(Predicate<NodeMetadata> filter, Payload runScript)
         throws RunScriptOnNodesException;

   /**
    * Run the script on all nodes with the specific tag.
    * 
    * @param filter
    *           Predicate-based filter to define on which nodes the script is to
    *           be executed
    * @param runScript
    *           payload containing the script to run
    * @param options
    *           nullable options to how to run the script, whether to override
    *           credentials
    * @return map with node identifiers and corresponding responses
    * @throws RunScriptOnNodesException
    *            if anything goes wrong during script execution
    * 
    * @see org.jclouds.compute.predicates.NodePredicates#runningWithTag(String)
    * @see org.jclouds.io.Payloads
    */
   Map<? extends NodeMetadata, ExecResponse> runScriptOnNodesMatching(Predicate<NodeMetadata> filter,
         Payload runScript, RunScriptOptions options) throws RunScriptOnNodesException;

}
