package org.jclouds.blobstore.config;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.inject.Singleton;

import org.jclouds.blobstore.AsyncBlobStore;
import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.TransientAsyncBlobStore;
import org.jclouds.blobstore.attr.ConsistencyModel;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.internal.BlobStoreContextImpl;
import org.jclouds.domain.Location;
import org.jclouds.domain.LocationScope;
import org.jclouds.domain.internal.LocationImpl;

import com.google.common.collect.ImmutableSet;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.google.inject.TypeLiteral;

/**
 * Configures the {@link TransientBlobStoreContext}; requires {@link TransientAsyncBlobStore} bound.
 * 
 * @author Adrian Cole
 */
public class TransientBlobStoreContextModule extends AbstractModule {

   // must be singleton for all threads and all objects or tests may fail;
   static final ConcurrentHashMap<String, ConcurrentMap<String, Blob>> map = new ConcurrentHashMap<String, ConcurrentMap<String, Blob>>();
   static final ConcurrentHashMap<String, Location> containerToLocation = new ConcurrentHashMap<String, Location>();

   @Override
   protected void configure() {
      bind(new TypeLiteral<BlobStoreContext>() {
      }).to(new TypeLiteral<BlobStoreContextImpl<TransientBlobStore, AsyncBlobStore>>() {
      }).in(Scopes.SINGLETON);
      bind(new TypeLiteral<ConcurrentMap<String, ConcurrentMap<String, Blob>>>() {
      }).toInstance(map);
      bind(new TypeLiteral<ConcurrentMap<String, Location>>() {
      }).toInstance(containerToLocation);
      install(new BlobStoreObjectModule());
      install(new BlobStoreMapModule());
      bind(ConsistencyModel.class).toInstance(ConsistencyModel.STRICT);
   }

   @Provides
   @Singleton
   Set<Location> provideLocations(Location defaultLocation) {
      return ImmutableSet.of(defaultLocation);
   }

   @Provides
   @Singleton
   BlobStore provide(TransientBlobStore in) {
      return in;
   }

   @Provides
   @Singleton
   Location provideDefaultLocation() {
      return new LocationImpl(LocationScope.PROVIDER, "transient", "transient", null);
   }

}
