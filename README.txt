Overview:
 
jclouds is an open source framework that helps you get started in the cloud
and reuse your java and clojure development skills. Our api allows you to 
freedom to use portable abstractions or cloud-specific features.
 
our current version is 1.0-beta-6
our dev version is 1.0-SNAPSHOT
 
our compute api supports: ec2, gogrid, rackspace, rimuhosting, vcloud, terremark, 
                          eucalyptus, hosting.com, bluelock, ibmdev, slicehost

  * note * the pom dependency org.jclouds/jclouds-allcompute gives you access to
           to all of these providers

our blobstore api supports: s3, rackspace, azure, atmos online, att synaptic,
                          walrus, googlestorage, transient (in-memory)
 
  * note * the pom dependency org.jclouds/jclouds-allblobstore gives you access to
           to all of these providers

we also have rest clients for: chef, opscodeplatform, pcs2 (mezeo), sdn (nirvanix), twitter


If you want access to all jclouds components, include the maven dependency org.jclouds/jclouds-all


BlobStore Example (Java):
  // init
  context = new BlobStoreContextFactory().createContext(
                  "s3",
                  accesskeyid,
                  secretaccesskey);
  blobStore = context.getBlobStore();
 
  // create container
  blobStore.createContainerInLocation(null, "mycontainer");
  
  // add blob
  blob = blobStore.newBlob("test");
  blob.setPayload("testdata");
  blobStore.putBlob("mycontainer", blob);

BlobStore Example (Clojure):
  (use 'org.jclouds.blobstore)

  (with-blobstore ["azureblob" account encodedkey]
    (create-container "mycontainer")
    (upload-blob "mycontainer" "test" "testdata"))

Compute Example (Java):
  // init
  context = new ComputeServiceContextFactory().createContext(
                  "ec2",
                  accesskeyid,
                  secretaccesskey,
                  ImmutableSet.of(new Log4JLoggingModule(),
                                  new JschSshClientModule()));
  client = context.getComputeService();
 
  // define the requirements of your node
  template = client.templateBuilder().osFamily(UBUNTU).smallest().build();
 
  // these nodes will be accessible via ssh when the call returns
  nodes = client.runNodesWithTag("mycluster", 2, template);

Compute Example (Clojure):
  (use 'org.jclouds.compute)

  ; create a compute service using ssh and log4j extensions
  (def compute 
    (compute-service "terremark" "user" "password" :ssh :log4j))

  ; use the default node template and launch a couple nodes
  ; these will have your ~/.ssh/id_rsa.pub authorized when complete
  (with-compute-service [compute]
    (run-nodes "mycluster" 2))
 
Downloads:
  * distribution zip: http://jclouds.googlecode.com/files/jclouds-1.0-beta-6.zip
  * maven repo: http://jclouds.googlecode.com/svn/repo 
  * snapshot repo: http://jclouds.rimuhosting.com/maven2/snapshots
 
Links:
  * project page: http://code.google.com/p/jclouds/
  * javadocs (1.0-beta-6): http://jclouds.rimuhosting.com/apidocs/
  * javadocs (1.0-SNAPSHOT): http://jclouds.rimuhosting.com/apidocs-SNAPSHOT/
  * community: http://code.google.com/p/jclouds/wiki/AppsThatUseJClouds
  * user group: http://groups.google.com/group/jclouds
  * dev group: http://groups.google.com/group/jclouds-dev
  * twitter: http://twitter.com/jclouds
