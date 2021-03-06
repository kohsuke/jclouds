package org.jclouds.rest;

import java.io.InputStream;
import java.net.URI;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;

import org.jclouds.http.functions.ParseETagHeader;
import org.jclouds.http.options.HttpRequestOptions;
import org.jclouds.io.Payload;
import org.jclouds.rest.annotations.EndpointParam;
import org.jclouds.rest.annotations.ExceptionParser;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.functions.ReturnFalseOnNotFoundOr404;
import org.jclouds.rest.functions.ReturnNullOnNotFoundOr404;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * Simple rest client
 * 
 * @author Adrian Cole
 */
public interface HttpAsyncClient {
   /**
    * @see HttpClient#post
    */
   @PUT
   @ResponseParser(ParseETagHeader.class)
   ListenableFuture<String> put(@EndpointParam URI location, Payload payload);

   @PUT
   @ResponseParser(ParseETagHeader.class)
   ListenableFuture<String> put(@EndpointParam URI location, Payload payload,
            HttpRequestOptions options);

   /**
    * @see HttpClient#post
    */
   @POST
   @ResponseParser(ParseETagHeader.class)
   ListenableFuture<String> post(@EndpointParam URI location, Payload payload);

   @POST
   @ResponseParser(ParseETagHeader.class)
   ListenableFuture<String> post(@EndpointParam URI location, Payload payload,
            HttpRequestOptions options);

   /**
    * @see HttpClient#exists
    */
   @HEAD
   @ExceptionParser(ReturnFalseOnNotFoundOr404.class)
   ListenableFuture<Boolean> exists(@EndpointParam URI location);

   /**
    * @see HttpClient#get
    */
   @GET
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<InputStream> get(@EndpointParam URI location);

   @GET
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<InputStream> get(@EndpointParam URI location, HttpRequestOptions options);

   /**
    * @see HttpClient#delete
    */
   @DELETE
   @ExceptionParser(ReturnFalseOnNotFoundOr404.class)
   ListenableFuture<Boolean> delete(@EndpointParam URI location);

}
