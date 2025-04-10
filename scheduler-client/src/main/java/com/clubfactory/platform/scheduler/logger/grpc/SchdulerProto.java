// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: scheduler.proto

package com.clubfactory.platform.scheduler.logger.grpc;

public final class SchdulerProto {
  private SchdulerProto() {}
  public static void registerAllExtensions(
          com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
          com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
            (com.google.protobuf.ExtensionRegistryLite) registry);
  }
  static final com.google.protobuf.Descriptors.Descriptor
          internal_static_scheduler_RetStrInfo_descriptor;
  static final
  com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
          internal_static_scheduler_RetStrInfo_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
          internal_static_scheduler_RetByteInfo_descriptor;
  static final
  com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
          internal_static_scheduler_RetByteInfo_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
          internal_static_scheduler_LogParameter_descriptor;
  static final
  com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
          internal_static_scheduler_LogParameter_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
          internal_static_scheduler_PathParameter_descriptor;
  static final
  com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
          internal_static_scheduler_PathParameter_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
          internal_static_scheduler_LastNRowsReq_descriptor;
  static final
  com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
          internal_static_scheduler_LastNRowsReq_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
          internal_static_scheduler_ExpireLogReq_descriptor;
  static final
  com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
          internal_static_scheduler_ExpireLogReq_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
          internal_static_scheduler_DBConnReq_descriptor;
  static final
  com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
          internal_static_scheduler_DBConnReq_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
  getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
          descriptor;
  static {
    java.lang.String[] descriptorData = {
            "\n\017scheduler.proto\022\tscheduler\"\031\n\nRetStrIn" +
                    "fo\022\013\n\003msg\030\001 \001(\t\"\033\n\013RetByteInfo\022\014\n\004data\030\001" +
                    " \001(\014\"@\n\014LogParameter\022\014\n\004path\030\001 \001(\t\022\023\n\013sk" +
                    "ipLineNum\030\002 \001(\005\022\r\n\005limit\030\003 \001(\005\"\035\n\rPathPa" +
                    "rameter\022\014\n\004path\030\001 \001(\t\"*\n\014LastNRowsReq\022\014\n" +
                    "\004path\030\001 \001(\t\022\014\n\004rows\030\002 \001(\005\"*\n\014ExpireLogRe" +
                    "q\022\013\n\003dir\030\001 \001(\t\022\r\n\005nDays\030\002 \001(\005\"g\n\tDBConnR" +
                    "eq\022\017\n\007jdbcUrl\030\001 \001(\t\022\020\n\010jdbcUser\030\002 \001(\t\022\024\n" +
                    "\014jdbcPassword\030\003 \001(\t\022\020\n\010jdbcType\030\004 \001(\t\022\017\n" +
                    "\007timeout\030\005 \001(\0052\235\003\n\016LogViewService\022?\n\013rol" +
                    "lViewLog\022\027.scheduler.LogParameter\032\025.sche" +
                    "duler.RetStrInfo\"\000\022<\n\007viewLog\022\030.schedule" +
                    "r.PathParameter\032\025.scheduler.RetStrInfo\"\000" +
                    "\022A\n\013getLogBytes\022\030.scheduler.PathParamete" +
                    "r\032\026.scheduler.RetByteInfo\"\000\022E\n\021getLogOnL" +
                    "astNRows\022\027.scheduler.LastNRowsReq\032\025.sche" +
                    "duler.RetStrInfo\"\000\022E\n\021expireLogNdaysAgo\022" +
                    "\027.scheduler.ExpireLogReq\032\025.scheduler.Ret" +
                    "StrInfo\"\000\022;\n\ntestDBConn\022\024.scheduler.DBCo" +
                    "nnReq\032\025.scheduler.RetStrInfo\"\000BA\n.com.cl" +
                    "ubfactory.platform.scheduler.logger.grpc" +
                    "B\rSchdulerProtoP\001b\006proto3"
    };
    com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner assigner =
            new com.google.protobuf.Descriptors.FileDescriptor.    InternalDescriptorAssigner() {
              public com.google.protobuf.ExtensionRegistry assignDescriptors(
                      com.google.protobuf.Descriptors.FileDescriptor root) {
                descriptor = root;
                return null;
              }
            };
    com.google.protobuf.Descriptors.FileDescriptor
            .internalBuildGeneratedFileFrom(descriptorData,
                    new com.google.protobuf.Descriptors.FileDescriptor[] {
                    }, assigner);
    internal_static_scheduler_RetStrInfo_descriptor =
            getDescriptor().getMessageTypes().get(0);
    internal_static_scheduler_RetStrInfo_fieldAccessorTable = new
            com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
            internal_static_scheduler_RetStrInfo_descriptor,
            new java.lang.String[] { "Msg", });
    internal_static_scheduler_RetByteInfo_descriptor =
            getDescriptor().getMessageTypes().get(1);
    internal_static_scheduler_RetByteInfo_fieldAccessorTable = new
            com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
            internal_static_scheduler_RetByteInfo_descriptor,
            new java.lang.String[] { "Data", });
    internal_static_scheduler_LogParameter_descriptor =
            getDescriptor().getMessageTypes().get(2);
    internal_static_scheduler_LogParameter_fieldAccessorTable = new
            com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
            internal_static_scheduler_LogParameter_descriptor,
            new java.lang.String[] { "Path", "SkipLineNum", "Limit", });
    internal_static_scheduler_PathParameter_descriptor =
            getDescriptor().getMessageTypes().get(3);
    internal_static_scheduler_PathParameter_fieldAccessorTable = new
            com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
            internal_static_scheduler_PathParameter_descriptor,
            new java.lang.String[] { "Path", });
    internal_static_scheduler_LastNRowsReq_descriptor =
            getDescriptor().getMessageTypes().get(4);
    internal_static_scheduler_LastNRowsReq_fieldAccessorTable = new
            com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
            internal_static_scheduler_LastNRowsReq_descriptor,
            new java.lang.String[] { "Path", "Rows", });
    internal_static_scheduler_ExpireLogReq_descriptor =
            getDescriptor().getMessageTypes().get(5);
    internal_static_scheduler_ExpireLogReq_fieldAccessorTable = new
            com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
            internal_static_scheduler_ExpireLogReq_descriptor,
            new java.lang.String[] { "Dir", "NDays", });
    internal_static_scheduler_DBConnReq_descriptor =
            getDescriptor().getMessageTypes().get(6);
    internal_static_scheduler_DBConnReq_fieldAccessorTable = new
            com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
            internal_static_scheduler_DBConnReq_descriptor,
            new java.lang.String[] { "JdbcUrl", "JdbcUser", "JdbcPassword", "JdbcType", "Timeout", });
  }

  // @@protoc_insertion_point(outer_class_scope)
}
