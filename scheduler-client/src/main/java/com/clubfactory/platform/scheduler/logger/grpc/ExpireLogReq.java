// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: scheduler.proto

package com.clubfactory.platform.scheduler.logger.grpc;

/**
 * <pre>
 **
 * 过期N天前日志
 * </pre>
 *
 * Protobuf type {@code scheduler.ExpireLogReq}
 */
public  final class ExpireLogReq extends
        com.google.protobuf.GeneratedMessageV3 implements
        // @@protoc_insertion_point(message_implements:scheduler.ExpireLogReq)
        ExpireLogReqOrBuilder {
  private static final long serialVersionUID = 0L;
  // Use ExpireLogReq.newBuilder() to construct.
  private ExpireLogReq(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
    super(builder);
  }
  private ExpireLogReq() {
    dir_ = "";
    nDays_ = 0;
  }

  @java.lang.Override
  public final com.google.protobuf.UnknownFieldSet
  getUnknownFields() {
    return this.unknownFields;
  }
  private ExpireLogReq(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
    this();
    if (extensionRegistry == null) {
      throw new java.lang.NullPointerException();
    }
    int mutable_bitField0_ = 0;
    com.google.protobuf.UnknownFieldSet.Builder unknownFields =
            com.google.protobuf.UnknownFieldSet.newBuilder();
    try {
      boolean done = false;
      while (!done) {
        int tag = input.readTag();
        switch (tag) {
          case 0:
            done = true;
            break;
          default: {
            if (!parseUnknownFieldProto3(
                    input, unknownFields, extensionRegistry, tag)) {
              done = true;
            }
            break;
          }
          case 10: {
            java.lang.String s = input.readStringRequireUtf8();

            dir_ = s;
            break;
          }
          case 16: {

            nDays_ = input.readInt32();
            break;
          }
        }
      }
    } catch (com.google.protobuf.InvalidProtocolBufferException e) {
      throw e.setUnfinishedMessage(this);
    } catch (java.io.IOException e) {
      throw new com.google.protobuf.InvalidProtocolBufferException(
              e).setUnfinishedMessage(this);
    } finally {
      this.unknownFields = unknownFields.build();
      makeExtensionsImmutable();
    }
  }
  public static final com.google.protobuf.Descriptors.Descriptor
  getDescriptor() {
    return SchdulerProto.internal_static_scheduler_ExpireLogReq_descriptor;
  }

  protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
  internalGetFieldAccessorTable() {
    return SchdulerProto.internal_static_scheduler_ExpireLogReq_fieldAccessorTable
            .ensureFieldAccessorsInitialized(
                    ExpireLogReq.class, ExpireLogReq.Builder.class);
  }

  public static final int DIR_FIELD_NUMBER = 1;
  private volatile java.lang.Object dir_;
  /**
   * <pre>
   **
   * dirPath
   * </pre>
   *
   * <code>string dir = 1;</code>
   */
  public java.lang.String getDir() {
    java.lang.Object ref = dir_;
    if (ref instanceof java.lang.String) {
      return (java.lang.String) ref;
    } else {
      com.google.protobuf.ByteString bs =
              (com.google.protobuf.ByteString) ref;
      java.lang.String s = bs.toStringUtf8();
      dir_ = s;
      return s;
    }
  }
  /**
   * <pre>
   **
   * dirPath
   * </pre>
   *
   * <code>string dir = 1;</code>
   */
  public com.google.protobuf.ByteString
  getDirBytes() {
    java.lang.Object ref = dir_;
    if (ref instanceof java.lang.String) {
      com.google.protobuf.ByteString b =
              com.google.protobuf.ByteString.copyFromUtf8(
                      (java.lang.String) ref);
      dir_ = b;
      return b;
    } else {
      return (com.google.protobuf.ByteString) ref;
    }
  }

  public static final int NDAYS_FIELD_NUMBER = 2;
  private int nDays_;
  /**
   * <pre>
   **
   * n days : n
   * </pre>
   *
   * <code>int32 nDays = 2;</code>
   */
  public int getNDays() {
    return nDays_;
  }

  private byte memoizedIsInitialized = -1;
  public final boolean isInitialized() {
    byte isInitialized = memoizedIsInitialized;
    if (isInitialized == 1) return true;
    if (isInitialized == 0) return false;

    memoizedIsInitialized = 1;
    return true;
  }

  public void writeTo(com.google.protobuf.CodedOutputStream output)
          throws java.io.IOException {
    if (!getDirBytes().isEmpty()) {
      com.google.protobuf.GeneratedMessageV3.writeString(output, 1, dir_);
    }
    if (nDays_ != 0) {
      output.writeInt32(2, nDays_);
    }
    unknownFields.writeTo(output);
  }

  public int getSerializedSize() {
    int size = memoizedSize;
    if (size != -1) return size;

    size = 0;
    if (!getDirBytes().isEmpty()) {
      size += com.google.protobuf.GeneratedMessageV3.computeStringSize(1, dir_);
    }
    if (nDays_ != 0) {
      size += com.google.protobuf.CodedOutputStream
              .computeInt32Size(2, nDays_);
    }
    size += unknownFields.getSerializedSize();
    memoizedSize = size;
    return size;
  }

  @java.lang.Override
  public boolean equals(final java.lang.Object obj) {
    if (obj == this) {
      return true;
    }
    if (!(obj instanceof ExpireLogReq)) {
      return super.equals(obj);
    }
    ExpireLogReq other = (ExpireLogReq) obj;

    boolean result = true;
    result = result && getDir()
            .equals(other.getDir());
    result = result && (getNDays()
            == other.getNDays());
    result = result && unknownFields.equals(other.unknownFields);
    return result;
  }

  @java.lang.Override
  public int hashCode() {
    if (memoizedHashCode != 0) {
      return memoizedHashCode;
    }
    int hash = 41;
    hash = (19 * hash) + getDescriptor().hashCode();
    hash = (37 * hash) + DIR_FIELD_NUMBER;
    hash = (53 * hash) + getDir().hashCode();
    hash = (37 * hash) + NDAYS_FIELD_NUMBER;
    hash = (53 * hash) + getNDays();
    hash = (29 * hash) + unknownFields.hashCode();
    memoizedHashCode = hash;
    return hash;
  }

  public static ExpireLogReq parseFrom(
          java.nio.ByteBuffer data)
          throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static ExpireLogReq parseFrom(
          java.nio.ByteBuffer data,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static ExpireLogReq parseFrom(
          com.google.protobuf.ByteString data)
          throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static ExpireLogReq parseFrom(
          com.google.protobuf.ByteString data,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static ExpireLogReq parseFrom(byte[] data)
          throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static ExpireLogReq parseFrom(
          byte[] data,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static ExpireLogReq parseFrom(java.io.InputStream input)
          throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
            .parseWithIOException(PARSER, input);
  }
  public static ExpireLogReq parseFrom(
          java.io.InputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
            .parseWithIOException(PARSER, input, extensionRegistry);
  }
  public static ExpireLogReq parseDelimitedFrom(java.io.InputStream input)
          throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
            .parseDelimitedWithIOException(PARSER, input);
  }
  public static ExpireLogReq parseDelimitedFrom(
          java.io.InputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
            .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
  }
  public static ExpireLogReq parseFrom(
          com.google.protobuf.CodedInputStream input)
          throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
            .parseWithIOException(PARSER, input);
  }
  public static ExpireLogReq parseFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
            .parseWithIOException(PARSER, input, extensionRegistry);
  }

  public Builder newBuilderForType() { return newBuilder(); }
  public static Builder newBuilder() {
    return DEFAULT_INSTANCE.toBuilder();
  }
  public static Builder newBuilder(ExpireLogReq prototype) {
    return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
  }
  public Builder toBuilder() {
    return this == DEFAULT_INSTANCE
            ? new Builder() : new Builder().mergeFrom(this);
  }

  @java.lang.Override
  protected Builder newBuilderForType(
          com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
    Builder builder = new Builder(parent);
    return builder;
  }
  /**
   * <pre>
   **
   * 过期N天前日志
   * </pre>
   *
   * Protobuf type {@code scheduler.ExpireLogReq}
   */
  public static final class Builder extends
          com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
          // @@protoc_insertion_point(builder_implements:scheduler.ExpireLogReq)
          ExpireLogReqOrBuilder {
    public static final com.google.protobuf.Descriptors.Descriptor
    getDescriptor() {
      return SchdulerProto.internal_static_scheduler_ExpireLogReq_descriptor;
    }

    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
    internalGetFieldAccessorTable() {
      return SchdulerProto.internal_static_scheduler_ExpireLogReq_fieldAccessorTable
              .ensureFieldAccessorsInitialized(
                      ExpireLogReq.class, ExpireLogReq.Builder.class);
    }

    // Construct using ExpireLogReq.newBuilder()
    private Builder() {
      maybeForceBuilderInitialization();
    }

    private Builder(
            com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
      super(parent);
      maybeForceBuilderInitialization();
    }
    private void maybeForceBuilderInitialization() {
      if (com.google.protobuf.GeneratedMessageV3
              .alwaysUseFieldBuilders) {
      }
    }
    public Builder clear() {
      super.clear();
      dir_ = "";

      nDays_ = 0;

      return this;
    }

    public com.google.protobuf.Descriptors.Descriptor
    getDescriptorForType() {
      return SchdulerProto.internal_static_scheduler_ExpireLogReq_descriptor;
    }

    public ExpireLogReq getDefaultInstanceForType() {
      return ExpireLogReq.getDefaultInstance();
    }

    public ExpireLogReq build() {
      ExpireLogReq result = buildPartial();
      if (!result.isInitialized()) {
        throw newUninitializedMessageException(result);
      }
      return result;
    }

    public ExpireLogReq buildPartial() {
      ExpireLogReq result = new ExpireLogReq(this);
      result.dir_ = dir_;
      result.nDays_ = nDays_;
      onBuilt();
      return result;
    }

    public Builder clone() {
      return (Builder) super.clone();
    }
    public Builder setField(
            com.google.protobuf.Descriptors.FieldDescriptor field,
            java.lang.Object value) {
      return (Builder) super.setField(field, value);
    }
    public Builder clearField(
            com.google.protobuf.Descriptors.FieldDescriptor field) {
      return (Builder) super.clearField(field);
    }
    public Builder clearOneof(
            com.google.protobuf.Descriptors.OneofDescriptor oneof) {
      return (Builder) super.clearOneof(oneof);
    }
    public Builder setRepeatedField(
            com.google.protobuf.Descriptors.FieldDescriptor field,
            int index, java.lang.Object value) {
      return (Builder) super.setRepeatedField(field, index, value);
    }
    public Builder addRepeatedField(
            com.google.protobuf.Descriptors.FieldDescriptor field,
            java.lang.Object value) {
      return (Builder) super.addRepeatedField(field, value);
    }
    public Builder mergeFrom(com.google.protobuf.Message other) {
      if (other instanceof ExpireLogReq) {
        return mergeFrom((ExpireLogReq)other);
      } else {
        super.mergeFrom(other);
        return this;
      }
    }

    public Builder mergeFrom(ExpireLogReq other) {
      if (other == ExpireLogReq.getDefaultInstance()) return this;
      if (!other.getDir().isEmpty()) {
        dir_ = other.dir_;
        onChanged();
      }
      if (other.getNDays() != 0) {
        setNDays(other.getNDays());
      }
      this.mergeUnknownFields(other.unknownFields);
      onChanged();
      return this;
    }

    public final boolean isInitialized() {
      return true;
    }

    public Builder mergeFrom(
            com.google.protobuf.CodedInputStream input,
            com.google.protobuf.ExtensionRegistryLite extensionRegistry)
            throws java.io.IOException {
      ExpireLogReq parsedMessage = null;
      try {
        parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        parsedMessage = (ExpireLogReq) e.getUnfinishedMessage();
        throw e.unwrapIOException();
      } finally {
        if (parsedMessage != null) {
          mergeFrom(parsedMessage);
        }
      }
      return this;
    }

    private java.lang.Object dir_ = "";
    /**
     * <pre>
     **
     * dirPath
     * </pre>
     *
     * <code>string dir = 1;</code>
     */
    public java.lang.String getDir() {
      java.lang.Object ref = dir_;
      if (!(ref instanceof java.lang.String)) {
        com.google.protobuf.ByteString bs =
                (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        dir_ = s;
        return s;
      } else {
        return (java.lang.String) ref;
      }
    }
    /**
     * <pre>
     **
     * dirPath
     * </pre>
     *
     * <code>string dir = 1;</code>
     */
    public com.google.protobuf.ByteString
    getDirBytes() {
      java.lang.Object ref = dir_;
      if (ref instanceof String) {
        com.google.protobuf.ByteString b =
                com.google.protobuf.ByteString.copyFromUtf8(
                        (java.lang.String) ref);
        dir_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }
    /**
     * <pre>
     **
     * dirPath
     * </pre>
     *
     * <code>string dir = 1;</code>
     */
    public Builder setDir(
            java.lang.String value) {
      if (value == null) {
        throw new NullPointerException();
      }

      dir_ = value;
      onChanged();
      return this;
    }
    /**
     * <pre>
     **
     * dirPath
     * </pre>
     *
     * <code>string dir = 1;</code>
     */
    public Builder clearDir() {

      dir_ = getDefaultInstance().getDir();
      onChanged();
      return this;
    }
    /**
     * <pre>
     **
     * dirPath
     * </pre>
     *
     * <code>string dir = 1;</code>
     */
    public Builder setDirBytes(
            com.google.protobuf.ByteString value) {
      if (value == null) {
        throw new NullPointerException();
      }
      checkByteStringIsUtf8(value);

      dir_ = value;
      onChanged();
      return this;
    }

    private int nDays_ ;
    /**
     * <pre>
     **
     * n days : n
     * </pre>
     *
     * <code>int32 nDays = 2;</code>
     */
    public int getNDays() {
      return nDays_;
    }
    /**
     * <pre>
     **
     * n days : n
     * </pre>
     *
     * <code>int32 nDays = 2;</code>
     */
    public Builder setNDays(int value) {

      nDays_ = value;
      onChanged();
      return this;
    }
    /**
     * <pre>
     **
     * n days : n
     * </pre>
     *
     * <code>int32 nDays = 2;</code>
     */
    public Builder clearNDays() {

      nDays_ = 0;
      onChanged();
      return this;
    }
    public final Builder setUnknownFields(
            final com.google.protobuf.UnknownFieldSet unknownFields) {
      return super.setUnknownFieldsProto3(unknownFields);
    }

    public final Builder mergeUnknownFields(
            final com.google.protobuf.UnknownFieldSet unknownFields) {
      return super.mergeUnknownFields(unknownFields);
    }


    // @@protoc_insertion_point(builder_scope:scheduler.ExpireLogReq)
  }

  // @@protoc_insertion_point(class_scope:scheduler.ExpireLogReq)
  private static final ExpireLogReq DEFAULT_INSTANCE;
  static {
    DEFAULT_INSTANCE = new ExpireLogReq();
  }

  public static ExpireLogReq getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  private static final com.google.protobuf.Parser<ExpireLogReq>
          PARSER = new com.google.protobuf.AbstractParser<ExpireLogReq>() {
    public ExpireLogReq parsePartialFrom(
            com.google.protobuf.CodedInputStream input,
            com.google.protobuf.ExtensionRegistryLite extensionRegistry)
            throws com.google.protobuf.InvalidProtocolBufferException {
      return new ExpireLogReq(input, extensionRegistry);
    }
  };

  public static com.google.protobuf.Parser<ExpireLogReq> parser() {
    return PARSER;
  }

  @java.lang.Override
  public com.google.protobuf.Parser<ExpireLogReq> getParserForType() {
    return PARSER;
  }

  public ExpireLogReq getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }

}

