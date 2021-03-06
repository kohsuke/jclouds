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
package org.jclouds.aws.ec2.xml;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.testng.Assert.assertEquals;

import java.io.InputStream;
import java.net.UnknownHostException;
import java.util.Set;

import org.jclouds.aws.ec2.domain.Attachment;
import org.jclouds.aws.ec2.domain.AvailabilityZone;
import org.jclouds.aws.ec2.domain.InstanceState;
import org.jclouds.aws.ec2.domain.InstanceType;
import org.jclouds.aws.ec2.domain.Reservation;
import org.jclouds.aws.ec2.domain.RootDeviceType;
import org.jclouds.aws.ec2.domain.RunningInstance;
import org.jclouds.aws.ec2.domain.RunningInstance.EbsBlockDevice;
import org.jclouds.date.DateService;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

/**
 * Tests behavior of {@code DescribeInstancesResponseHandler}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "ec2.DescribeInstancesResponseHandlerTest")
public class DescribeInstancesResponseHandlerTest extends BaseEC2HandlerTest {

   private DateService dateService;

   @BeforeTest
   @Override
   protected void setUpInjector() {
      super.setUpInjector();
      dateService = injector.getInstance(DateService.class);
      assert dateService != null;
   }

   public void testWhenRunning() throws UnknownHostException {

      InputStream is = getClass().getResourceAsStream("/ec2/describe_instances_running.xml");
      Set<Reservation<? extends RunningInstance>> contents = Sets.newLinkedHashSet();

      contents.add(new Reservation<RunningInstance>(defaultRegion, ImmutableSet.of("adriancole.ec2ingress"),
               ImmutableSet.of(new RunningInstance(defaultRegion, ImmutableSet.of("adriancole.ec2ingress"), "0",
                        "ec2-174-129-81-68.compute-1.amazonaws.com", "ami-1fd73376", "i-0799056f",
                        InstanceState.RUNNING, InstanceType.M1_SMALL, "174.129.81.68", "aki-a71cf9ce",
                        "adriancole.ec21", dateService.iso8601DateParse("2009-11-09T03:00:34.000Z"), false,
                        AvailabilityZone.US_EAST_1C, null, "paravirtual", null, "ip-10-243-42-70.ec2.internal",
                        "10.243.42.70", ImmutableSet.<String> of(), "ari-a51cf9cc", null, null, null,
                        RootDeviceType.INSTANCE_STORE, null, ImmutableMap.<String, EbsBlockDevice> of())),
               "993194456877", null, "r-a3c508cb"));

      Set<Reservation<? extends RunningInstance>> result = getReservations(is);

      assertEquals(result, contents);
   }

   public void testApplyInputStream() {

      InputStream is = getClass().getResourceAsStream("/ec2/describe_instances.xml");
      Set<Reservation<? extends RunningInstance>> contents = Sets.newLinkedHashSet();

      contents.add(new Reservation<RunningInstance>(defaultRegion, ImmutableSet.of("default"), ImmutableSet.of(
               new RunningInstance(defaultRegion, ImmutableSet.of("default"), "23",
                        "ec2-72-44-33-4.compute-1.amazonaws.com", "ami-6ea54007", "i-28a64341", InstanceState.RUNNING,
                        InstanceType.M1_LARGE, (String) null, "aki-ba3adfd3", "example-key-name", dateService
                                 .iso8601DateParse("2007-08-07T11:54:42.000Z"), false, AvailabilityZone.US_EAST_1B,
                        null, "paravirtual", null, "10-251-50-132.ec2.internal", null, ImmutableSet.of("774F4FF8"),
                        "ari-badbad00", null, null, null, RootDeviceType.INSTANCE_STORE, null, ImmutableMap
                                 .<String, EbsBlockDevice> of()), new RunningInstance(defaultRegion, ImmutableSet
                        .of("default"), "23", "ec2-72-44-33-6.compute-1.amazonaws.com", "ami-6ea54007", "i-28a64435",
                        InstanceState.RUNNING, InstanceType.M1_LARGE, (String) null, "aki-ba3adfd3",
                        "example-key-name", dateService.iso8601DateParse("2007-08-07T11:54:42.000Z"), false,
                        AvailabilityZone.US_EAST_1B, null, "paravirtual", null, "10-251-50-134.ec2.internal", null,
                        ImmutableSet.of("774F4FF8"), "ari-badbad00", null, null, null, RootDeviceType.INSTANCE_STORE,
                        null, ImmutableMap.<String, EbsBlockDevice> of())), "UYY3TLBUXIEON5NQVUUX6OMPWBZIQNFM", null,
               "r-44a5402d"));

      Set<Reservation<? extends RunningInstance>> result = getReservations(is);

      assertEquals(result, contents);
   }

   @Test(enabled = false)
   // TODO not sure why equals fails
   public void testApplyInputStreamEuc() {

      InputStream is = getClass().getResourceAsStream("/ec2/describe_instances_euc.xml");
      Set<Reservation<? extends RunningInstance>> contents = Sets.newLinkedHashSet();

      contents.add(new Reservation<RunningInstance>(defaultRegion, ImmutableSet.of("default"), ImmutableSet
               .of(new RunningInstance(defaultRegion, ImmutableSet.of("jclouds#euc"), "1", null, "emi-9ACB1363",
                        "i-3FFA0762", InstanceState.SHUTTING_DOWN, InstanceType.M1_LARGE, null, "eki-6CBD12F2",
                        "jclouds#euc-17", dateService.iso8601DateParse("2010-06-16T03:06:19.000Z"), false, "open",
                        null, "paravirtual", null, "10.7.0.179", null, ImmutableSet.<String> of(), "eri-A97113E4",
                        null, null, null, RootDeviceType.INSTANCE_STORE, null, ImmutableMap
                                 .<String, EbsBlockDevice> of())), "jclouds", null, "r-4D2A08AD"));

      Set<Reservation<? extends RunningInstance>> result = getReservations(is);

      assertEquals(result, contents);
   }

   public void testEBS() throws UnknownHostException {

      InputStream is = getClass().getResourceAsStream("/ec2/describe_instances_ebs.xml");
      Set<Reservation<? extends RunningInstance>> contents = Sets.newLinkedHashSet();

      contents.add(new Reservation<RunningInstance>(defaultRegion, ImmutableSet.of("adriancole.ec2ebsingress"),
               ImmutableSet.of(new RunningInstance(defaultRegion, ImmutableSet.of("adriancole.ec2ebsingress"), "0",
                        "ec2-75-101-203-146.compute-1.amazonaws.com", "ami-849875ed", "i-e564438d",
                        InstanceState.RUNNING, InstanceType.M1_SMALL, "75.101.203.146", "aki-a71cf9ce",
                        "adriancole.ec2ebs1", dateService.iso8601DateParse("2009-12-30T04:06:23.000Z"), false,
                        AvailabilityZone.US_EAST_1B, "placement", "hvm", null,
                        "domU-12-31-39-09-CE-53.compute-1.internal", "10.210.209.157", ImmutableSet.<String> of(),
                        "ari-a51cf9cc", null, null, null, RootDeviceType.EBS, "/dev/sda1", ImmutableMap
                                 .<String, EbsBlockDevice> of("/dev/sda1", new EbsBlockDevice("vol-dc6ca8b5",
                                          Attachment.Status.ATTACHED, dateService
                                                   .iso8601DateParse("2009-12-30T04:06:29.000Z"), true)))),
               "993194456877", null, "r-596dd731"));

      Set<Reservation<? extends RunningInstance>> result = getReservations(is);

      assertEquals(result, contents);
   }

   private Set<Reservation<? extends RunningInstance>> getReservations(InputStream is) {
      DescribeInstancesResponseHandler handler = injector.getInstance(DescribeInstancesResponseHandler.class);
      addDefaultRegionToHandler(handler);
      Set<Reservation<? extends RunningInstance>> result = factory.create(handler).parse(is);
      return result;
   }

   private void addDefaultRegionToHandler(ParseSax.HandlerWithResult<?> handler) {
      GeneratedHttpRequest<?> request = createMock(GeneratedHttpRequest.class);
      expect(request.getArgs()).andReturn(new Object[] { null }).atLeastOnce();
      replay(request);
      handler.setContext(request);
   }
}
