package com.clubfactory.platform.scheduler.logger.grpc;

import static io.grpc.MethodDescriptor.generateFullMethodName;
import static io.grpc.stub.ClientCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ClientCalls.asyncClientStreamingCall;
import static io.grpc.stub.ClientCalls.asyncServerStreamingCall;
import static io.grpc.stub.ClientCalls.asyncUnaryCall;
import static io.grpc.stub.ClientCalls.blockingServerStreamingCall;
import static io.grpc.stub.ClientCalls.blockingUnaryCall;
import static io.grpc.stub.ClientCalls.futureUnaryCall;
import static io.grpc.stub.ServerCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ServerCalls.asyncClientStreamingCall;
import static io.grpc.stub.ServerCalls.asyncServerStreamingCall;
import static io.grpc.stub.ServerCalls.asyncUnaryCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedStreamingCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall;

/**
 * <pre>
 **
 *  log view service
 * </pre>
 */
@javax.annotation.Generated(
        value = "by gRPC proto compiler (version 1.9.0)",
        comments = "Source: scheduler.proto")
public final class LogViewServiceGrpc {

  private LogViewServiceGrpc() {}

  public static final String SERVICE_NAME = "scheduler.LogViewService";

  // Static method descriptors that strictly reflect the proto.
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  @java.lang.Deprecated // Use {@link #getRollViewLogMethod()} instead. 
  public static final io.grpc.MethodDescriptor<LogParameter,
          RetStrInfo> METHOD_ROLL_VIEW_LOG = getRollViewLogMethod();

  private static volatile io.grpc.MethodDescriptor<LogParameter,
          RetStrInfo> getRollViewLogMethod;

  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static io.grpc.MethodDescriptor<LogParameter,
          RetStrInfo> getRollViewLogMethod() {
    io.grpc.MethodDescriptor<LogParameter, RetStrInfo> getRollViewLogMethod;
    if ((getRollViewLogMethod = LogViewServiceGrpc.getRollViewLogMethod) == null) {
      synchronized (LogViewServiceGrpc.class) {
        if ((getRollViewLogMethod = LogViewServiceGrpc.getRollViewLogMethod) == null) {
          LogViewServiceGrpc.getRollViewLogMethod = getRollViewLogMethod =
                  io.grpc.MethodDescriptor.<LogParameter, RetStrInfo>newBuilder()
                          .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
                          .setFullMethodName(generateFullMethodName(
                                  "scheduler.LogViewService", "rollViewLog"))
                          .setSampledToLocalTracing(true)
                          .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                                  LogParameter.getDefaultInstance()))
                          .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                                  RetStrInfo.getDefaultInstance()))
                          .setSchemaDescriptor(new LogViewServiceMethodDescriptorSupplier("rollViewLog"))
                          .build();
        }
      }
    }
    return getRollViewLogMethod;
  }
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  @java.lang.Deprecated // Use {@link #getViewLogMethod()} instead. 
  public static final io.grpc.MethodDescriptor<PathParameter,
          RetStrInfo> METHOD_VIEW_LOG = getViewLogMethod();

  private static volatile io.grpc.MethodDescriptor<PathParameter,
          RetStrInfo> getViewLogMethod;

  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static io.grpc.MethodDescriptor<PathParameter,
          RetStrInfo> getViewLogMethod() {
    io.grpc.MethodDescriptor<PathParameter, RetStrInfo> getViewLogMethod;
    if ((getViewLogMethod = LogViewServiceGrpc.getViewLogMethod) == null) {
      synchronized (LogViewServiceGrpc.class) {
        if ((getViewLogMethod = LogViewServiceGrpc.getViewLogMethod) == null) {
          LogViewServiceGrpc.getViewLogMethod = getViewLogMethod =
                  io.grpc.MethodDescriptor.<PathParameter, RetStrInfo>newBuilder()
                          .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
                          .setFullMethodName(generateFullMethodName(
                                  "scheduler.LogViewService", "viewLog"))
                          .setSampledToLocalTracing(true)
                          .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                                  PathParameter.getDefaultInstance()))
                          .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                                  RetStrInfo.getDefaultInstance()))
                          .setSchemaDescriptor(new LogViewServiceMethodDescriptorSupplier("viewLog"))
                          .build();
        }
      }
    }
    return getViewLogMethod;
  }
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  @java.lang.Deprecated // Use {@link #getGetLogBytesMethod()} instead. 
  public static final io.grpc.MethodDescriptor<PathParameter,
          RetByteInfo> METHOD_GET_LOG_BYTES = getGetLogBytesMethod();

  private static volatile io.grpc.MethodDescriptor<PathParameter,
          RetByteInfo> getGetLogBytesMethod;

  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static io.grpc.MethodDescriptor<PathParameter,
          RetByteInfo> getGetLogBytesMethod() {
    io.grpc.MethodDescriptor<PathParameter, RetByteInfo> getGetLogBytesMethod;
    if ((getGetLogBytesMethod = LogViewServiceGrpc.getGetLogBytesMethod) == null) {
      synchronized (LogViewServiceGrpc.class) {
        if ((getGetLogBytesMethod = LogViewServiceGrpc.getGetLogBytesMethod) == null) {
          LogViewServiceGrpc.getGetLogBytesMethod = getGetLogBytesMethod =
                  io.grpc.MethodDescriptor.<PathParameter, RetByteInfo>newBuilder()
                          .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
                          .setFullMethodName(generateFullMethodName(
                                  "scheduler.LogViewService", "getLogBytes"))
                          .setSampledToLocalTracing(true)
                          .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                                  PathParameter.getDefaultInstance()))
                          .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                                  RetByteInfo.getDefaultInstance()))
                          .setSchemaDescriptor(new LogViewServiceMethodDescriptorSupplier("getLogBytes"))
                          .build();
        }
      }
    }
    return getGetLogBytesMethod;
  }
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  @java.lang.Deprecated // Use {@link #getGetLogOnLastNRowsMethod()} instead. 
  public static final io.grpc.MethodDescriptor<LastNRowsReq,
          RetStrInfo> METHOD_GET_LOG_ON_LAST_NROWS = getGetLogOnLastNRowsMethod();

  private static volatile io.grpc.MethodDescriptor<LastNRowsReq,
          RetStrInfo> getGetLogOnLastNRowsMethod;

  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static io.grpc.MethodDescriptor<LastNRowsReq,
          RetStrInfo> getGetLogOnLastNRowsMethod() {
    io.grpc.MethodDescriptor<LastNRowsReq, RetStrInfo> getGetLogOnLastNRowsMethod;
    if ((getGetLogOnLastNRowsMethod = LogViewServiceGrpc.getGetLogOnLastNRowsMethod) == null) {
      synchronized (LogViewServiceGrpc.class) {
        if ((getGetLogOnLastNRowsMethod = LogViewServiceGrpc.getGetLogOnLastNRowsMethod) == null) {
          LogViewServiceGrpc.getGetLogOnLastNRowsMethod = getGetLogOnLastNRowsMethod =
                  io.grpc.MethodDescriptor.<LastNRowsReq, RetStrInfo>newBuilder()
                          .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
                          .setFullMethodName(generateFullMethodName(
                                  "scheduler.LogViewService", "getLogOnLastNRows"))
                          .setSampledToLocalTracing(true)
                          .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                                  LastNRowsReq.getDefaultInstance()))
                          .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                                  RetStrInfo.getDefaultInstance()))
                          .setSchemaDescriptor(new LogViewServiceMethodDescriptorSupplier("getLogOnLastNRows"))
                          .build();
        }
      }
    }
    return getGetLogOnLastNRowsMethod;
  }
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  @java.lang.Deprecated // Use {@link #getExpireLogNdaysAgoMethod()} instead. 
  public static final io.grpc.MethodDescriptor<ExpireLogReq,
          RetStrInfo> METHOD_EXPIRE_LOG_NDAYS_AGO = getExpireLogNdaysAgoMethod();

  private static volatile io.grpc.MethodDescriptor<ExpireLogReq,
          RetStrInfo> getExpireLogNdaysAgoMethod;

  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static io.grpc.MethodDescriptor<ExpireLogReq,
          RetStrInfo> getExpireLogNdaysAgoMethod() {
    io.grpc.MethodDescriptor<ExpireLogReq, RetStrInfo> getExpireLogNdaysAgoMethod;
    if ((getExpireLogNdaysAgoMethod = LogViewServiceGrpc.getExpireLogNdaysAgoMethod) == null) {
      synchronized (LogViewServiceGrpc.class) {
        if ((getExpireLogNdaysAgoMethod = LogViewServiceGrpc.getExpireLogNdaysAgoMethod) == null) {
          LogViewServiceGrpc.getExpireLogNdaysAgoMethod = getExpireLogNdaysAgoMethod =
                  io.grpc.MethodDescriptor.<ExpireLogReq, RetStrInfo>newBuilder()
                          .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
                          .setFullMethodName(generateFullMethodName(
                                  "scheduler.LogViewService", "expireLogNdaysAgo"))
                          .setSampledToLocalTracing(true)
                          .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                                  ExpireLogReq.getDefaultInstance()))
                          .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                                  RetStrInfo.getDefaultInstance()))
                          .setSchemaDescriptor(new LogViewServiceMethodDescriptorSupplier("expireLogNdaysAgo"))
                          .build();
        }
      }
    }
    return getExpireLogNdaysAgoMethod;
  }
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  @java.lang.Deprecated // Use {@link #getTestDBConnMethod()} instead. 
  public static final io.grpc.MethodDescriptor<DBConnReq,
          RetStrInfo> METHOD_TEST_DBCONN = getTestDBConnMethod();

  private static volatile io.grpc.MethodDescriptor<DBConnReq,
          RetStrInfo> getTestDBConnMethod;

  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static io.grpc.MethodDescriptor<DBConnReq,
          RetStrInfo> getTestDBConnMethod() {
    io.grpc.MethodDescriptor<DBConnReq, RetStrInfo> getTestDBConnMethod;
    if ((getTestDBConnMethod = LogViewServiceGrpc.getTestDBConnMethod) == null) {
      synchronized (LogViewServiceGrpc.class) {
        if ((getTestDBConnMethod = LogViewServiceGrpc.getTestDBConnMethod) == null) {
          LogViewServiceGrpc.getTestDBConnMethod = getTestDBConnMethod =
                  io.grpc.MethodDescriptor.<DBConnReq, RetStrInfo>newBuilder()
                          .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
                          .setFullMethodName(generateFullMethodName(
                                  "scheduler.LogViewService", "testDBConn"))
                          .setSampledToLocalTracing(true)
                          .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                                  DBConnReq.getDefaultInstance()))
                          .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                                  RetStrInfo.getDefaultInstance()))
                          .setSchemaDescriptor(new LogViewServiceMethodDescriptorSupplier("testDBConn"))
                          .build();
        }
      }
    }
    return getTestDBConnMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static LogViewServiceStub newStub(io.grpc.Channel channel) {
    return new LogViewServiceStub(channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static LogViewServiceBlockingStub newBlockingStub(
          io.grpc.Channel channel) {
    return new LogViewServiceBlockingStub(channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static LogViewServiceFutureStub newFutureStub(
          io.grpc.Channel channel) {
    return new LogViewServiceFutureStub(channel);
  }

  /**
   * <pre>
   **
   *  log view service
   * </pre>
   */
  public static abstract class LogViewServiceImplBase implements io.grpc.BindableService {

    /**
     * <pre>
     **
     *  roll view log
     * </pre>
     */
    public void rollViewLog(LogParameter request,
                            io.grpc.stub.StreamObserver<RetStrInfo> responseObserver) {
      asyncUnimplementedUnaryCall(getRollViewLogMethod(), responseObserver);
    }

    /**
     * <pre>
     **
     * view all log
     * </pre>
     */
    public void viewLog(PathParameter request,
                        io.grpc.stub.StreamObserver<RetStrInfo> responseObserver) {
      asyncUnimplementedUnaryCall(getViewLogMethod(), responseObserver);
    }

    /**
     * <pre>
     **
     * get log bytes
     * </pre>
     */
    public void getLogBytes(PathParameter request,
                            io.grpc.stub.StreamObserver<RetByteInfo> responseObserver) {
      asyncUnimplementedUnaryCall(getGetLogBytesMethod(), responseObserver);
    }

    /**
     * <pre>
     **
     * get last N rows
     * </pre>
     */
    public void getLogOnLastNRows(LastNRowsReq request,
                                  io.grpc.stub.StreamObserver<RetStrInfo> responseObserver) {
      asyncUnimplementedUnaryCall(getGetLogOnLastNRowsMethod(), responseObserver);
    }

    /**
     * <pre>
     **
     * 删除N天前日志
     * </pre>
     */
    public void expireLogNdaysAgo(ExpireLogReq request,
                                  io.grpc.stub.StreamObserver<RetStrInfo> responseObserver) {
      asyncUnimplementedUnaryCall(getExpireLogNdaysAgoMethod(), responseObserver);
    }

    /**
     * <pre>
     **
     * 测试数据源连接
     * </pre>
     */
    public void testDBConn(DBConnReq request,
                           io.grpc.stub.StreamObserver<RetStrInfo> responseObserver) {
      asyncUnimplementedUnaryCall(getTestDBConnMethod(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
              .addMethod(
                      getRollViewLogMethod(),
                      asyncUnaryCall(
                              new MethodHandlers<
                                      LogParameter,
                                      RetStrInfo>(
                                      this, METHODID_ROLL_VIEW_LOG)))
              .addMethod(
                      getViewLogMethod(),
                      asyncUnaryCall(
                              new MethodHandlers<
                                      PathParameter,
                                      RetStrInfo>(
                                      this, METHODID_VIEW_LOG)))
              .addMethod(
                      getGetLogBytesMethod(),
                      asyncUnaryCall(
                              new MethodHandlers<
                                      PathParameter,
                                      RetByteInfo>(
                                      this, METHODID_GET_LOG_BYTES)))
              .addMethod(
                      getGetLogOnLastNRowsMethod(),
                      asyncUnaryCall(
                              new MethodHandlers<
                                      LastNRowsReq,
                                      RetStrInfo>(
                                      this, METHODID_GET_LOG_ON_LAST_NROWS)))
              .addMethod(
                      getExpireLogNdaysAgoMethod(),
                      asyncUnaryCall(
                              new MethodHandlers<
                                      ExpireLogReq,
                                      RetStrInfo>(
                                      this, METHODID_EXPIRE_LOG_NDAYS_AGO)))
              .addMethod(
                      getTestDBConnMethod(),
                      asyncUnaryCall(
                              new MethodHandlers<
                                      DBConnReq,
                                      RetStrInfo>(
                                      this, METHODID_TEST_DBCONN)))
              .build();
    }
  }

  /**
   * <pre>
   **
   *  log view service
   * </pre>
   */
  public static final class LogViewServiceStub extends io.grpc.stub.AbstractStub<LogViewServiceStub> {
    private LogViewServiceStub(io.grpc.Channel channel) {
      super(channel);
    }

    private LogViewServiceStub(io.grpc.Channel channel,
                               io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected LogViewServiceStub build(io.grpc.Channel channel,
                                       io.grpc.CallOptions callOptions) {
      return new LogViewServiceStub(channel, callOptions);
    }

    /**
     * <pre>
     **
     *  roll view log
     * </pre>
     */
    public void rollViewLog(LogParameter request,
                            io.grpc.stub.StreamObserver<RetStrInfo> responseObserver) {
      asyncUnaryCall(
              getChannel().newCall(getRollViewLogMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     **
     * view all log
     * </pre>
     */
    public void viewLog(PathParameter request,
                        io.grpc.stub.StreamObserver<RetStrInfo> responseObserver) {
      asyncUnaryCall(
              getChannel().newCall(getViewLogMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     **
     * get log bytes
     * </pre>
     */
    public void getLogBytes(PathParameter request,
                            io.grpc.stub.StreamObserver<RetByteInfo> responseObserver) {
      asyncUnaryCall(
              getChannel().newCall(getGetLogBytesMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     **
     * get last N rows
     * </pre>
     */
    public void getLogOnLastNRows(LastNRowsReq request,
                                  io.grpc.stub.StreamObserver<RetStrInfo> responseObserver) {
      asyncUnaryCall(
              getChannel().newCall(getGetLogOnLastNRowsMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     **
     * 删除N天前日志
     * </pre>
     */
    public void expireLogNdaysAgo(ExpireLogReq request,
                                  io.grpc.stub.StreamObserver<RetStrInfo> responseObserver) {
      asyncUnaryCall(
              getChannel().newCall(getExpireLogNdaysAgoMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     **
     * 测试数据源连接
     * </pre>
     */
    public void testDBConn(DBConnReq request,
                           io.grpc.stub.StreamObserver<RetStrInfo> responseObserver) {
      asyncUnaryCall(
              getChannel().newCall(getTestDBConnMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   * <pre>
   **
   *  log view service
   * </pre>
   */
  public static final class LogViewServiceBlockingStub extends io.grpc.stub.AbstractStub<LogViewServiceBlockingStub> {
    private LogViewServiceBlockingStub(io.grpc.Channel channel) {
      super(channel);
    }

    private LogViewServiceBlockingStub(io.grpc.Channel channel,
                                       io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected LogViewServiceBlockingStub build(io.grpc.Channel channel,
                                               io.grpc.CallOptions callOptions) {
      return new LogViewServiceBlockingStub(channel, callOptions);
    }

    /**
     * <pre>
     **
     *  roll view log
     * </pre>
     */
    public RetStrInfo rollViewLog(LogParameter request) {
      return blockingUnaryCall(
              getChannel(), getRollViewLogMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     **
     * view all log
     * </pre>
     */
    public RetStrInfo viewLog(PathParameter request) {
      return blockingUnaryCall(
              getChannel(), getViewLogMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     **
     * get log bytes
     * </pre>
     */
    public RetByteInfo getLogBytes(PathParameter request) {
      return blockingUnaryCall(
              getChannel(), getGetLogBytesMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     **
     * get last N rows
     * </pre>
     */
    public RetStrInfo getLogOnLastNRows(LastNRowsReq request) {
      return blockingUnaryCall(
              getChannel(), getGetLogOnLastNRowsMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     **
     * 删除N天前日志
     * </pre>
     */
    public RetStrInfo expireLogNdaysAgo(ExpireLogReq request) {
      return blockingUnaryCall(
              getChannel(), getExpireLogNdaysAgoMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     **
     * 测试数据源连接
     * </pre>
     */
    public RetStrInfo testDBConn(DBConnReq request) {
      return blockingUnaryCall(
              getChannel(), getTestDBConnMethod(), getCallOptions(), request);
    }
  }

  /**
   * <pre>
   **
   *  log view service
   * </pre>
   */
  public static final class LogViewServiceFutureStub extends io.grpc.stub.AbstractStub<LogViewServiceFutureStub> {
    private LogViewServiceFutureStub(io.grpc.Channel channel) {
      super(channel);
    }

    private LogViewServiceFutureStub(io.grpc.Channel channel,
                                     io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected LogViewServiceFutureStub build(io.grpc.Channel channel,
                                             io.grpc.CallOptions callOptions) {
      return new LogViewServiceFutureStub(channel, callOptions);
    }

    /**
     * <pre>
     **
     *  roll view log
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<RetStrInfo> rollViewLog(
            LogParameter request) {
      return futureUnaryCall(
              getChannel().newCall(getRollViewLogMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     **
     * view all log
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<RetStrInfo> viewLog(
            PathParameter request) {
      return futureUnaryCall(
              getChannel().newCall(getViewLogMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     **
     * get log bytes
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<RetByteInfo> getLogBytes(
            PathParameter request) {
      return futureUnaryCall(
              getChannel().newCall(getGetLogBytesMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     **
     * get last N rows
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<RetStrInfo> getLogOnLastNRows(
            LastNRowsReq request) {
      return futureUnaryCall(
              getChannel().newCall(getGetLogOnLastNRowsMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     **
     * 删除N天前日志
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<RetStrInfo> expireLogNdaysAgo(
            ExpireLogReq request) {
      return futureUnaryCall(
              getChannel().newCall(getExpireLogNdaysAgoMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     **
     * 测试数据源连接
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<RetStrInfo> testDBConn(
            DBConnReq request) {
      return futureUnaryCall(
              getChannel().newCall(getTestDBConnMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_ROLL_VIEW_LOG = 0;
  private static final int METHODID_VIEW_LOG = 1;
  private static final int METHODID_GET_LOG_BYTES = 2;
  private static final int METHODID_GET_LOG_ON_LAST_NROWS = 3;
  private static final int METHODID_EXPIRE_LOG_NDAYS_AGO = 4;
  private static final int METHODID_TEST_DBCONN = 5;

  private static final class MethodHandlers<Req, Resp> implements
          io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
          io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
          io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
          io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final LogViewServiceImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(LogViewServiceImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_ROLL_VIEW_LOG:
          serviceImpl.rollViewLog((LogParameter) request,
                  (io.grpc.stub.StreamObserver<RetStrInfo>) responseObserver);
          break;
        case METHODID_VIEW_LOG:
          serviceImpl.viewLog((PathParameter) request,
                  (io.grpc.stub.StreamObserver<RetStrInfo>) responseObserver);
          break;
        case METHODID_GET_LOG_BYTES:
          serviceImpl.getLogBytes((PathParameter) request,
                  (io.grpc.stub.StreamObserver<RetByteInfo>) responseObserver);
          break;
        case METHODID_GET_LOG_ON_LAST_NROWS:
          serviceImpl.getLogOnLastNRows((LastNRowsReq) request,
                  (io.grpc.stub.StreamObserver<RetStrInfo>) responseObserver);
          break;
        case METHODID_EXPIRE_LOG_NDAYS_AGO:
          serviceImpl.expireLogNdaysAgo((ExpireLogReq) request,
                  (io.grpc.stub.StreamObserver<RetStrInfo>) responseObserver);
          break;
        case METHODID_TEST_DBCONN:
          serviceImpl.testDBConn((DBConnReq) request,
                  (io.grpc.stub.StreamObserver<RetStrInfo>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
            io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  private static abstract class LogViewServiceBaseDescriptorSupplier
          implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    LogViewServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return SchdulerProto.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("LogViewService");
    }
  }

  private static final class LogViewServiceFileDescriptorSupplier
          extends LogViewServiceBaseDescriptorSupplier {
    LogViewServiceFileDescriptorSupplier() {}
  }

  private static final class LogViewServiceMethodDescriptorSupplier
          extends LogViewServiceBaseDescriptorSupplier
          implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    LogViewServiceMethodDescriptorSupplier(String methodName) {
      this.methodName = methodName;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (LogViewServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
                  .setSchemaDescriptor(new LogViewServiceFileDescriptorSupplier())
                  .addMethod(getRollViewLogMethod())
                  .addMethod(getViewLogMethod())
                  .addMethod(getGetLogBytesMethod())
                  .addMethod(getGetLogOnLastNRowsMethod())
                  .addMethod(getExpireLogNdaysAgoMethod())
                  .addMethod(getTestDBConnMethod())
                  .build();
        }
      }
    }
    return result;
  }
}
