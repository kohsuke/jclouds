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
package org.jclouds.logging.internal;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.annotation.Resource;

import org.jclouds.io.Payload;
import org.jclouds.io.PayloadEnclosing;
import org.jclouds.io.Payloads;
import org.jclouds.logging.Logger;

import com.google.common.io.ByteStreams;
import com.google.common.io.Closeables;
import com.google.common.io.FileBackedOutputStream;

/**
 * Logs data to the wire LOG, similar to {@code org.apache.HttpWire.impl.conn.Wire}
 * 
 * @author Adrian Cole
 */
public abstract class Wire {

   @Resource
   protected Logger logger = Logger.NULL;

   protected abstract Logger getWireLog();

   private void wire(String header, InputStream instream) {
      StringBuilder buffer = new StringBuilder();
      int ch;
      try {
         while ((ch = instream.read()) != -1) {
            if (ch == 13) {
               buffer.append("[\\r]");
            } else if (ch == 10) {
               buffer.append("[\\n]\"");
               buffer.insert(0, "\"");
               buffer.insert(0, header);
               getWireLog().debug(buffer.toString());
               buffer.setLength(0);
            } else if ((ch < 32) || (ch > 127)) {
               buffer.append("[0x");
               buffer.append(Integer.toHexString(ch));
               buffer.append("]");
            } else {
               buffer.append((char) ch);
            }
         }
         if (buffer.length() > 0) {
            buffer.append('\"');
            buffer.insert(0, '\"');
            buffer.insert(0, header);
            getWireLog().debug(buffer.toString());
         }
      } catch (IOException e) {
         logger.error(e, "Error tapping line");
      }
   }

   public boolean enabled() {
      return getWireLog().isDebugEnabled();
   }

   public InputStream copy(final String header, InputStream instream) {
      int limit = 256 * 1024;
      FileBackedOutputStream out = null;
      try {
         out = new FileBackedOutputStream(limit);
         long bytesRead = ByteStreams.copy(instream, out);
         if (bytesRead >= limit)
            logger.warn("over limit %d/%d: wrote temp file", bytesRead, limit);
         wire(header, out.getSupplier().getInput());
         return out.getSupplier().getInput();
      } catch (IOException e) {
         throw new RuntimeException("Error tapping line", e);
      } finally {
         Closeables.closeQuietly(out);
         Closeables.closeQuietly(instream);
      }
   }

   public InputStream input(InputStream instream) {
      return copy("<< ", checkNotNull(instream, "input"));
   }

   public void input(PayloadEnclosing request) {
      Payload oldContent = request.getPayload();
      Payload wiredPayload = Payloads.newPayload(input(oldContent.getInput()));
      copyPayloadMetadata(oldContent, wiredPayload);
      request.setPayload(wiredPayload);
   }

   public void output(PayloadEnclosing request) {
      Payload oldContent = request.getPayload();
      Payload wiredPayload = Payloads.newPayload(output(oldContent.getRawContent()));
      copyPayloadMetadata(oldContent, wiredPayload);
      request.setPayload(wiredPayload);
   }

   private void copyPayloadMetadata(Payload oldContent, Payload wiredPayload) {
      if (oldContent.getContentLength() != null)
         wiredPayload.setContentLength(oldContent.getContentLength());
      wiredPayload.setContentType(oldContent.getContentType());
      wiredPayload.setContentMD5(oldContent.getContentMD5());
   }

   @SuppressWarnings("unchecked")
   public <T> T output(T data) {
      checkNotNull(data, "data");
      if (data instanceof InputStream) {
         return (T) copy(">> ", (InputStream) data);
      } else if (data instanceof byte[]) {
         output((byte[]) data);
         return data;
      } else if (data instanceof String) {
         output((String) data);
         return data;
      } else if (data instanceof File) {
         output(((File) data));
         return data;
      } else {
         throw new UnsupportedOperationException("Content not supported " + data.getClass());
      }
   }

   private void output(final File out) {
      checkNotNull(out, "output");
      InputStream in = null;
      try {
         in = new FileInputStream(out);
         wire(">> ", in);
      } catch (FileNotFoundException e) {
         logger.error(e, "Error tapping file: %s", out);
      } finally {
         Closeables.closeQuietly(in);
      }
   }

   private void output(byte[] b) {
      wire(">> ", new ByteArrayInputStream(checkNotNull(b, "output")));
   }

   private void output(final String s) {
      output(checkNotNull(s, "output").getBytes());
   }

}