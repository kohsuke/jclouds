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

package org.jclouds.compute.callables;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.util.Collections;

import javax.inject.Named;

import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.predicates.ScriptStatusReturnsZero.CommandUsingClient;
import org.jclouds.compute.util.ComputeServiceUtils;
import org.jclouds.compute.util.ComputeServiceUtils.SshCallable;
import org.jclouds.io.Payload;
import org.jclouds.io.Payloads;
import org.jclouds.logging.Logger;
import org.jclouds.scriptbuilder.InitBuilder;
import org.jclouds.scriptbuilder.domain.OsFamily;
import org.jclouds.ssh.ExecResponse;
import org.jclouds.ssh.SshClient;
import org.jclouds.util.Utils;

import com.google.common.base.Predicate;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;

/**
 * 
 * @author Adrian Cole
 */
public class RunScriptOnNode implements SshCallable<ExecResponse> {
   private SshClient ssh;
   protected final Predicate<CommandUsingClient> runScriptNotRunning;
   private final NodeMetadata node;
   private final String scriptName;
   private final Payload script;
   private final boolean runAsRoot;
   private Logger logger = Logger.NULL;

   public RunScriptOnNode(@Named("SCRIPT_COMPLETE") Predicate<CommandUsingClient> runScriptNotRunning,
         NodeMetadata node, String scriptName, Payload script) {
      this(runScriptNotRunning, node, scriptName, script, true);
   }

   public RunScriptOnNode(@Named("SCRIPT_COMPLETE") Predicate<CommandUsingClient> runScriptNotRunning,
         NodeMetadata node, String scriptName, Payload script, boolean runAsRoot) {
      this.runScriptNotRunning = runScriptNotRunning;
      this.node = checkNotNull(node, "node");
      this.scriptName = checkNotNull(scriptName, "scriptName");
      this.script = createRunScript(scriptName, script);
      this.runAsRoot = runAsRoot;
   }

   public static Payload createRunScript(String scriptName, Payload script) {
      String path = "/tmp/" + scriptName;
      InitBuilder initBuilder = new InitBuilder(scriptName, path, path, Collections.<String, String> emptyMap(),
            splitOnNewlines(script));
      return Payloads.newByteArrayPayload(initBuilder.build(OsFamily.UNIX).getBytes());
   }

   static String[] splitOnNewlines(Payload script) {
      try {
         String asString = Utils.toStringAndClose(checkNotNull(script, "script").getInput());
         return Iterables.toArray(Splitter.on("\n").split(asString), String.class);
      } catch (IOException e) {
         throw new RuntimeException(e);
      }
   }

   @Override
   public ExecResponse call() throws Exception {
      ssh.put(scriptName, script);
      ExecResponse returnVal = ssh.exec("chmod 755 " + scriptName);
      returnVal = ssh.exec("./" + scriptName + " init");

      if (runAsRoot)
         returnVal = runScriptAsRoot();
      else
         returnVal = runScriptAsDefaultUser();
      runScriptNotRunning.apply(new CommandUsingClient("./" + scriptName + " status", ssh));
      logger.debug("<< complete(%d)", returnVal.getExitCode());
      if (logger.isDebugEnabled() || returnVal.getExitCode() != 0) {
         logger.debug("<< stdout from %s as %s@%s\n%s", scriptName, node.getCredentials().identity, Iterables.get(node
               .getPublicAddresses(), 0), ssh.exec("./" + scriptName + " tail").getOutput());
         logger.debug("<< stderr from %s as %s@%s\n%s", scriptName, node.getCredentials().identity, Iterables.get(node
               .getPublicAddresses(), 0), ssh.exec("./" + scriptName + " tailerr").getOutput());
      }
      return returnVal;
   }

   @Override
   public void setConnection(SshClient ssh, Logger logger) {
      this.logger = checkNotNull(logger, "logger");
      this.ssh = checkNotNull(ssh, "ssh");
   }

   private ExecResponse runScriptAsRoot() {
      if (node.getCredentials().identity.equals("root")) {
         logger.debug(">> running %s as %s@%s", scriptName, node.getCredentials().identity, Iterables.get(node
               .getPublicAddresses(), 0));
         return ssh.exec("./" + scriptName + " start");
      } else if (ComputeServiceUtils.isKeyAuth(node)) {
         logger.debug(">> running sudo %s as %s@%s", scriptName, node.getCredentials().identity, Iterables.get(node
               .getPublicAddresses(), 0));
         return ssh.exec("sudo ./" + scriptName + " start");
      } else {
         logger.debug(">> running sudo -S %s as %s@%s", scriptName, node.getCredentials().identity, Iterables.get(node
               .getPublicAddresses(), 0));
         return ssh.exec(String.format("echo '%s'|sudo -S ./%s", node.getCredentials().credential, scriptName
               + " start"));
      }
   }

   private ExecResponse runScriptAsDefaultUser() {
      logger.debug(">> running script %s as %s@%s", scriptName, node.getCredentials().identity, Iterables.get(node
            .getPublicAddresses(), 0));
      return ssh.exec(String.format("./%s", scriptName + " start"));
   }

   @Override
   public NodeMetadata getNode() {
      return node;
   }
}