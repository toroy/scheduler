// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: scheduler.proto

package com.clubfactory.platform.scheduler.logger.grpc;

public interface LogParameterOrBuilder extends
        // @@protoc_insertion_point(interface_extends:scheduler.LogParameter)
        com.google.protobuf.MessageOrBuilder {

  /**
   * <pre>
   **
   * path
   * </pre>
   *
   * <code>string path = 1;</code>
   */
  java.lang.String getPath();
  /**
   * <pre>
   **
   * path
   * </pre>
   *
   * <code>string path = 1;</code>
   */
  com.google.protobuf.ByteString
  getPathBytes();

  /**
   * <pre>
   **
   * skip line num
   * </pre>
   *
   * <code>int32 skipLineNum = 2;</code>
   */
  int getSkipLineNum();

  /**
   * <pre>
   **
   * display limt num
   * </pre>
   *
   * <code>int32 limit = 3;</code>
   */
  int getLimit();
}
