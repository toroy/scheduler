// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: scheduler.proto

package com.clubfactory.platform.scheduler.logger.grpc;

public interface LastNRowsReqOrBuilder extends
        // @@protoc_insertion_point(interface_extends:scheduler.LastNRowsReq)
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
   * last n rows : n
   * </pre>
   *
   * <code>int32 rows = 2;</code>
   */
  int getRows();
}
