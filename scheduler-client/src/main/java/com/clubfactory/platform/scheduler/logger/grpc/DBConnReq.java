// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: scheduler.proto

package com.clubfactory.platform.scheduler.logger.grpc;

/**
 * <pre>
 **
 * 测试数据源
 * </pre>
 *
 * Protobuf type {@code scheduler.DBConnReq}
 */
public  final class DBConnReq extends
    com.google.protobuf.GeneratedMessageV3 implements
    // @@protoc_insertion_point(message_implements:scheduler.DBConnReq)
        DBConnReqOrBuilder {
private static final long serialVersionUID = 0L;
  // Use DBConnReq.newBuilder() to construct.
  private DBConnReq(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
    super(builder);
  }
  private DBConnReq() {
    jdbcUrl_ = "";
    jdbcUser_ = "";
    jdbcPassword_ = "";
    jdbcType_ = "";
    timeout_ = 0;
  }

  @Override
  public final com.google.protobuf.UnknownFieldSet
  getUnknownFields() {
    return this.unknownFields;
  }
  private DBConnReq(
      com.google.protobuf.CodedInputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    this();
    if (extensionRegistry == null) {
      throw new NullPointerException();
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
            String s = input.readStringRequireUtf8();

            jdbcUrl_ = s;
            break;
          }
          case 18: {
            String s = input.readStringRequireUtf8();

            jdbcUser_ = s;
            break;
          }
          case 26: {
            String s = input.readStringRequireUtf8();

            jdbcPassword_ = s;
            break;
          }
          case 34: {
            String s = input.readStringRequireUtf8();

            jdbcType_ = s;
            break;
          }
          case 40: {

            timeout_ = input.readInt32();
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
    return SchdulerProto.internal_static_scheduler_DBConnReq_descriptor;
  }

  protected FieldAccessorTable
      internalGetFieldAccessorTable() {
    return SchdulerProto.internal_static_scheduler_DBConnReq_fieldAccessorTable
        .ensureFieldAccessorsInitialized(
            DBConnReq.class, DBConnReq.Builder.class);
  }

  public static final int JDBCURL_FIELD_NUMBER = 1;
  private volatile Object jdbcUrl_;
  /**
   * <pre>
   **
   * jdbcUrl
   * </pre>
   *
   * <code>string jdbcUrl = 1;</code>
   */
  public String getJdbcUrl() {
    Object ref = jdbcUrl_;
    if (ref instanceof String) {
      return (String) ref;
    } else {
      com.google.protobuf.ByteString bs =
          (com.google.protobuf.ByteString) ref;
      String s = bs.toStringUtf8();
      jdbcUrl_ = s;
      return s;
    }
  }
  /**
   * <pre>
   **
   * jdbcUrl
   * </pre>
   *
   * <code>string jdbcUrl = 1;</code>
   */
  public com.google.protobuf.ByteString
      getJdbcUrlBytes() {
    Object ref = jdbcUrl_;
    if (ref instanceof String) {
      com.google.protobuf.ByteString b =
          com.google.protobuf.ByteString.copyFromUtf8(
              (String) ref);
      jdbcUrl_ = b;
      return b;
    } else {
      return (com.google.protobuf.ByteString) ref;
    }
  }

  public static final int JDBCUSER_FIELD_NUMBER = 2;
  private volatile Object jdbcUser_;
  /**
   * <pre>
   **
   * jdbcUser
   * </pre>
   *
   * <code>string jdbcUser = 2;</code>
   */
  public String getJdbcUser() {
    Object ref = jdbcUser_;
    if (ref instanceof String) {
      return (String) ref;
    } else {
      com.google.protobuf.ByteString bs =
          (com.google.protobuf.ByteString) ref;
      String s = bs.toStringUtf8();
      jdbcUser_ = s;
      return s;
    }
  }
  /**
   * <pre>
   **
   * jdbcUser
   * </pre>
   *
   * <code>string jdbcUser = 2;</code>
   */
  public com.google.protobuf.ByteString
      getJdbcUserBytes() {
    Object ref = jdbcUser_;
    if (ref instanceof String) {
      com.google.protobuf.ByteString b =
          com.google.protobuf.ByteString.copyFromUtf8(
              (String) ref);
      jdbcUser_ = b;
      return b;
    } else {
      return (com.google.protobuf.ByteString) ref;
    }
  }

  public static final int JDBCPASSWORD_FIELD_NUMBER = 3;
  private volatile Object jdbcPassword_;
  /**
   * <pre>
   **
   * jdbcPassword
   * </pre>
   *
   * <code>string jdbcPassword = 3;</code>
   */
  public String getJdbcPassword() {
    Object ref = jdbcPassword_;
    if (ref instanceof String) {
      return (String) ref;
    } else {
      com.google.protobuf.ByteString bs =
          (com.google.protobuf.ByteString) ref;
      String s = bs.toStringUtf8();
      jdbcPassword_ = s;
      return s;
    }
  }
  /**
   * <pre>
   **
   * jdbcPassword
   * </pre>
   *
   * <code>string jdbcPassword = 3;</code>
   */
  public com.google.protobuf.ByteString
      getJdbcPasswordBytes() {
    Object ref = jdbcPassword_;
    if (ref instanceof String) {
      com.google.protobuf.ByteString b =
          com.google.protobuf.ByteString.copyFromUtf8(
              (String) ref);
      jdbcPassword_ = b;
      return b;
    } else {
      return (com.google.protobuf.ByteString) ref;
    }
  }

  public static final int JDBCTYPE_FIELD_NUMBER = 4;
  private volatile Object jdbcType_;
  /**
   * <pre>
   **
   * jdbcType:MYSQL,PG
   * </pre>
   *
   * <code>string jdbcType = 4;</code>
   */
  public String getJdbcType() {
    Object ref = jdbcType_;
    if (ref instanceof String) {
      return (String) ref;
    } else {
      com.google.protobuf.ByteString bs =
          (com.google.protobuf.ByteString) ref;
      String s = bs.toStringUtf8();
      jdbcType_ = s;
      return s;
    }
  }
  /**
   * <pre>
   **
   * jdbcType:MYSQL,PG
   * </pre>
   *
   * <code>string jdbcType = 4;</code>
   */
  public com.google.protobuf.ByteString
      getJdbcTypeBytes() {
    Object ref = jdbcType_;
    if (ref instanceof String) {
      com.google.protobuf.ByteString b =
          com.google.protobuf.ByteString.copyFromUtf8(
              (String) ref);
      jdbcType_ = b;
      return b;
    } else {
      return (com.google.protobuf.ByteString) ref;
    }
  }

  public static final int TIMEOUT_FIELD_NUMBER = 5;
  private int timeout_;
  /**
   * <pre>
   **
   * 连接超时时间
   * </pre>
   *
   * <code>int32 timeout = 5;</code>
   */
  public int getTimeout() {
    return timeout_;
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
    if (!getJdbcUrlBytes().isEmpty()) {
      com.google.protobuf.GeneratedMessageV3.writeString(output, 1, jdbcUrl_);
    }
    if (!getJdbcUserBytes().isEmpty()) {
      com.google.protobuf.GeneratedMessageV3.writeString(output, 2, jdbcUser_);
    }
    if (!getJdbcPasswordBytes().isEmpty()) {
      com.google.protobuf.GeneratedMessageV3.writeString(output, 3, jdbcPassword_);
    }
    if (!getJdbcTypeBytes().isEmpty()) {
      com.google.protobuf.GeneratedMessageV3.writeString(output, 4, jdbcType_);
    }
    if (timeout_ != 0) {
      output.writeInt32(5, timeout_);
    }
    unknownFields.writeTo(output);
  }

  public int getSerializedSize() {
    int size = memoizedSize;
    if (size != -1) return size;

    size = 0;
    if (!getJdbcUrlBytes().isEmpty()) {
      size += com.google.protobuf.GeneratedMessageV3.computeStringSize(1, jdbcUrl_);
    }
    if (!getJdbcUserBytes().isEmpty()) {
      size += com.google.protobuf.GeneratedMessageV3.computeStringSize(2, jdbcUser_);
    }
    if (!getJdbcPasswordBytes().isEmpty()) {
      size += com.google.protobuf.GeneratedMessageV3.computeStringSize(3, jdbcPassword_);
    }
    if (!getJdbcTypeBytes().isEmpty()) {
      size += com.google.protobuf.GeneratedMessageV3.computeStringSize(4, jdbcType_);
    }
    if (timeout_ != 0) {
      size += com.google.protobuf.CodedOutputStream
        .computeInt32Size(5, timeout_);
    }
    size += unknownFields.getSerializedSize();
    memoizedSize = size;
    return size;
  }

  @Override
  public boolean equals(final Object obj) {
    if (obj == this) {
     return true;
    }
    if (!(obj instanceof DBConnReq)) {
      return super.equals(obj);
    }
    DBConnReq other = (DBConnReq) obj;

    boolean result = true;
    result = result && getJdbcUrl()
        .equals(other.getJdbcUrl());
    result = result && getJdbcUser()
        .equals(other.getJdbcUser());
    result = result && getJdbcPassword()
        .equals(other.getJdbcPassword());
    result = result && getJdbcType()
        .equals(other.getJdbcType());
    result = result && (getTimeout()
        == other.getTimeout());
    result = result && unknownFields.equals(other.unknownFields);
    return result;
  }

  @Override
  public int hashCode() {
    if (memoizedHashCode != 0) {
      return memoizedHashCode;
    }
    int hash = 41;
    hash = (19 * hash) + getDescriptor().hashCode();
    hash = (37 * hash) + JDBCURL_FIELD_NUMBER;
    hash = (53 * hash) + getJdbcUrl().hashCode();
    hash = (37 * hash) + JDBCUSER_FIELD_NUMBER;
    hash = (53 * hash) + getJdbcUser().hashCode();
    hash = (37 * hash) + JDBCPASSWORD_FIELD_NUMBER;
    hash = (53 * hash) + getJdbcPassword().hashCode();
    hash = (37 * hash) + JDBCTYPE_FIELD_NUMBER;
    hash = (53 * hash) + getJdbcType().hashCode();
    hash = (37 * hash) + TIMEOUT_FIELD_NUMBER;
    hash = (53 * hash) + getTimeout();
    hash = (29 * hash) + unknownFields.hashCode();
    memoizedHashCode = hash;
    return hash;
  }

  public static DBConnReq parseFrom(
      java.nio.ByteBuffer data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static DBConnReq parseFrom(
      java.nio.ByteBuffer data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static DBConnReq parseFrom(
      com.google.protobuf.ByteString data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static DBConnReq parseFrom(
      com.google.protobuf.ByteString data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static DBConnReq parseFrom(byte[] data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static DBConnReq parseFrom(
      byte[] data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static DBConnReq parseFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static DBConnReq parseFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }
  public static DBConnReq parseDelimitedFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input);
  }
  public static DBConnReq parseDelimitedFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
  }
  public static DBConnReq parseFrom(
      com.google.protobuf.CodedInputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static DBConnReq parseFrom(
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
  public static Builder newBuilder(DBConnReq prototype) {
    return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
  }
  public Builder toBuilder() {
    return this == DEFAULT_INSTANCE
        ? new Builder() : new Builder().mergeFrom(this);
  }

  @Override
  protected Builder newBuilderForType(
      BuilderParent parent) {
    Builder builder = new Builder(parent);
    return builder;
  }
  /**
   * <pre>
   **
   * 测试数据源
   * </pre>
   *
   * Protobuf type {@code scheduler.DBConnReq}
   */
  public static final class Builder extends
      com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
      // @@protoc_insertion_point(builder_implements:scheduler.DBConnReq)
      DBConnReqOrBuilder {
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return SchdulerProto.internal_static_scheduler_DBConnReq_descriptor;
    }

    protected FieldAccessorTable
        internalGetFieldAccessorTable() {
      return SchdulerProto.internal_static_scheduler_DBConnReq_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              DBConnReq.class, DBConnReq.Builder.class);
    }

    // Construct using DBConnReq.newBuilder()
    private Builder() {
      maybeForceBuilderInitialization();
    }

    private Builder(
        BuilderParent parent) {
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
      jdbcUrl_ = "";

      jdbcUser_ = "";

      jdbcPassword_ = "";

      jdbcType_ = "";

      timeout_ = 0;

      return this;
    }

    public com.google.protobuf.Descriptors.Descriptor
        getDescriptorForType() {
      return SchdulerProto.internal_static_scheduler_DBConnReq_descriptor;
    }

    public DBConnReq getDefaultInstanceForType() {
      return DBConnReq.getDefaultInstance();
    }

    public DBConnReq build() {
      DBConnReq result = buildPartial();
      if (!result.isInitialized()) {
        throw newUninitializedMessageException(result);
      }
      return result;
    }

    public DBConnReq buildPartial() {
      DBConnReq result = new DBConnReq(this);
      result.jdbcUrl_ = jdbcUrl_;
      result.jdbcUser_ = jdbcUser_;
      result.jdbcPassword_ = jdbcPassword_;
      result.jdbcType_ = jdbcType_;
      result.timeout_ = timeout_;
      onBuilt();
      return result;
    }

    public Builder clone() {
      return (Builder) super.clone();
    }
    public Builder setField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        Object value) {
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
        int index, Object value) {
      return (Builder) super.setRepeatedField(field, index, value);
    }
    public Builder addRepeatedField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        Object value) {
      return (Builder) super.addRepeatedField(field, value);
    }
    public Builder mergeFrom(com.google.protobuf.Message other) {
      if (other instanceof DBConnReq) {
        return mergeFrom((DBConnReq)other);
      } else {
        super.mergeFrom(other);
        return this;
      }
    }

    public Builder mergeFrom(DBConnReq other) {
      if (other == DBConnReq.getDefaultInstance()) return this;
      if (!other.getJdbcUrl().isEmpty()) {
        jdbcUrl_ = other.jdbcUrl_;
        onChanged();
      }
      if (!other.getJdbcUser().isEmpty()) {
        jdbcUser_ = other.jdbcUser_;
        onChanged();
      }
      if (!other.getJdbcPassword().isEmpty()) {
        jdbcPassword_ = other.jdbcPassword_;
        onChanged();
      }
      if (!other.getJdbcType().isEmpty()) {
        jdbcType_ = other.jdbcType_;
        onChanged();
      }
      if (other.getTimeout() != 0) {
        setTimeout(other.getTimeout());
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
      DBConnReq parsedMessage = null;
      try {
        parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        parsedMessage = (DBConnReq) e.getUnfinishedMessage();
        throw e.unwrapIOException();
      } finally {
        if (parsedMessage != null) {
          mergeFrom(parsedMessage);
        }
      }
      return this;
    }

    private Object jdbcUrl_ = "";
    /**
     * <pre>
     **
     * jdbcUrl
     * </pre>
     *
     * <code>string jdbcUrl = 1;</code>
     */
    public String getJdbcUrl() {
      Object ref = jdbcUrl_;
      if (!(ref instanceof String)) {
        com.google.protobuf.ByteString bs =
            (com.google.protobuf.ByteString) ref;
        String s = bs.toStringUtf8();
        jdbcUrl_ = s;
        return s;
      } else {
        return (String) ref;
      }
    }
    /**
     * <pre>
     **
     * jdbcUrl
     * </pre>
     *
     * <code>string jdbcUrl = 1;</code>
     */
    public com.google.protobuf.ByteString
        getJdbcUrlBytes() {
      Object ref = jdbcUrl_;
      if (ref instanceof String) {
        com.google.protobuf.ByteString b =
            com.google.protobuf.ByteString.copyFromUtf8(
                (String) ref);
        jdbcUrl_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }
    /**
     * <pre>
     **
     * jdbcUrl
     * </pre>
     *
     * <code>string jdbcUrl = 1;</code>
     */
    public Builder setJdbcUrl(
        String value) {
      if (value == null) {
    throw new NullPointerException();
  }

      jdbcUrl_ = value;
      onChanged();
      return this;
    }
    /**
     * <pre>
     **
     * jdbcUrl
     * </pre>
     *
     * <code>string jdbcUrl = 1;</code>
     */
    public Builder clearJdbcUrl() {

      jdbcUrl_ = getDefaultInstance().getJdbcUrl();
      onChanged();
      return this;
    }
    /**
     * <pre>
     **
     * jdbcUrl
     * </pre>
     *
     * <code>string jdbcUrl = 1;</code>
     */
    public Builder setJdbcUrlBytes(
        com.google.protobuf.ByteString value) {
      if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);

      jdbcUrl_ = value;
      onChanged();
      return this;
    }

    private Object jdbcUser_ = "";
    /**
     * <pre>
     **
     * jdbcUser
     * </pre>
     *
     * <code>string jdbcUser = 2;</code>
     */
    public String getJdbcUser() {
      Object ref = jdbcUser_;
      if (!(ref instanceof String)) {
        com.google.protobuf.ByteString bs =
            (com.google.protobuf.ByteString) ref;
        String s = bs.toStringUtf8();
        jdbcUser_ = s;
        return s;
      } else {
        return (String) ref;
      }
    }
    /**
     * <pre>
     **
     * jdbcUser
     * </pre>
     *
     * <code>string jdbcUser = 2;</code>
     */
    public com.google.protobuf.ByteString
        getJdbcUserBytes() {
      Object ref = jdbcUser_;
      if (ref instanceof String) {
        com.google.protobuf.ByteString b =
            com.google.protobuf.ByteString.copyFromUtf8(
                (String) ref);
        jdbcUser_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }
    /**
     * <pre>
     **
     * jdbcUser
     * </pre>
     *
     * <code>string jdbcUser = 2;</code>
     */
    public Builder setJdbcUser(
        String value) {
      if (value == null) {
    throw new NullPointerException();
  }

      jdbcUser_ = value;
      onChanged();
      return this;
    }
    /**
     * <pre>
     **
     * jdbcUser
     * </pre>
     *
     * <code>string jdbcUser = 2;</code>
     */
    public Builder clearJdbcUser() {

      jdbcUser_ = getDefaultInstance().getJdbcUser();
      onChanged();
      return this;
    }
    /**
     * <pre>
     **
     * jdbcUser
     * </pre>
     *
     * <code>string jdbcUser = 2;</code>
     */
    public Builder setJdbcUserBytes(
        com.google.protobuf.ByteString value) {
      if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);

      jdbcUser_ = value;
      onChanged();
      return this;
    }

    private Object jdbcPassword_ = "";
    /**
     * <pre>
     **
     * jdbcPassword
     * </pre>
     *
     * <code>string jdbcPassword = 3;</code>
     */
    public String getJdbcPassword() {
      Object ref = jdbcPassword_;
      if (!(ref instanceof String)) {
        com.google.protobuf.ByteString bs =
            (com.google.protobuf.ByteString) ref;
        String s = bs.toStringUtf8();
        jdbcPassword_ = s;
        return s;
      } else {
        return (String) ref;
      }
    }
    /**
     * <pre>
     **
     * jdbcPassword
     * </pre>
     *
     * <code>string jdbcPassword = 3;</code>
     */
    public com.google.protobuf.ByteString
        getJdbcPasswordBytes() {
      Object ref = jdbcPassword_;
      if (ref instanceof String) {
        com.google.protobuf.ByteString b =
            com.google.protobuf.ByteString.copyFromUtf8(
                (String) ref);
        jdbcPassword_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }
    /**
     * <pre>
     **
     * jdbcPassword
     * </pre>
     *
     * <code>string jdbcPassword = 3;</code>
     */
    public Builder setJdbcPassword(
        String value) {
      if (value == null) {
    throw new NullPointerException();
  }

      jdbcPassword_ = value;
      onChanged();
      return this;
    }
    /**
     * <pre>
     **
     * jdbcPassword
     * </pre>
     *
     * <code>string jdbcPassword = 3;</code>
     */
    public Builder clearJdbcPassword() {

      jdbcPassword_ = getDefaultInstance().getJdbcPassword();
      onChanged();
      return this;
    }
    /**
     * <pre>
     **
     * jdbcPassword
     * </pre>
     *
     * <code>string jdbcPassword = 3;</code>
     */
    public Builder setJdbcPasswordBytes(
        com.google.protobuf.ByteString value) {
      if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);

      jdbcPassword_ = value;
      onChanged();
      return this;
    }

    private Object jdbcType_ = "";
    /**
     * <pre>
     **
     * jdbcType:MYSQL,PG
     * </pre>
     *
     * <code>string jdbcType = 4;</code>
     */
    public String getJdbcType() {
      Object ref = jdbcType_;
      if (!(ref instanceof String)) {
        com.google.protobuf.ByteString bs =
            (com.google.protobuf.ByteString) ref;
        String s = bs.toStringUtf8();
        jdbcType_ = s;
        return s;
      } else {
        return (String) ref;
      }
    }
    /**
     * <pre>
     **
     * jdbcType:MYSQL,PG
     * </pre>
     *
     * <code>string jdbcType = 4;</code>
     */
    public com.google.protobuf.ByteString
        getJdbcTypeBytes() {
      Object ref = jdbcType_;
      if (ref instanceof String) {
        com.google.protobuf.ByteString b =
            com.google.protobuf.ByteString.copyFromUtf8(
                (String) ref);
        jdbcType_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }
    /**
     * <pre>
     **
     * jdbcType:MYSQL,PG
     * </pre>
     *
     * <code>string jdbcType = 4;</code>
     */
    public Builder setJdbcType(
        String value) {
      if (value == null) {
    throw new NullPointerException();
  }

      jdbcType_ = value;
      onChanged();
      return this;
    }
    /**
     * <pre>
     **
     * jdbcType:MYSQL,PG
     * </pre>
     *
     * <code>string jdbcType = 4;</code>
     */
    public Builder clearJdbcType() {

      jdbcType_ = getDefaultInstance().getJdbcType();
      onChanged();
      return this;
    }
    /**
     * <pre>
     **
     * jdbcType:MYSQL,PG
     * </pre>
     *
     * <code>string jdbcType = 4;</code>
     */
    public Builder setJdbcTypeBytes(
        com.google.protobuf.ByteString value) {
      if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);

      jdbcType_ = value;
      onChanged();
      return this;
    }

    private int timeout_ ;
    /**
     * <pre>
     **
     * 连接超时时间
     * </pre>
     *
     * <code>int32 timeout = 5;</code>
     */
    public int getTimeout() {
      return timeout_;
    }
    /**
     * <pre>
     **
     * 连接超时时间
     * </pre>
     *
     * <code>int32 timeout = 5;</code>
     */
    public Builder setTimeout(int value) {

      timeout_ = value;
      onChanged();
      return this;
    }
    /**
     * <pre>
     **
     * 连接超时时间
     * </pre>
     *
     * <code>int32 timeout = 5;</code>
     */
    public Builder clearTimeout() {

      timeout_ = 0;
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


    // @@protoc_insertion_point(builder_scope:scheduler.DBConnReq)
  }

  // @@protoc_insertion_point(class_scope:scheduler.DBConnReq)
  private static final DBConnReq DEFAULT_INSTANCE;
  static {
    DEFAULT_INSTANCE = new DBConnReq();
  }

  public static DBConnReq getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  private static final com.google.protobuf.Parser<DBConnReq>
      PARSER = new com.google.protobuf.AbstractParser<DBConnReq>() {
    public DBConnReq parsePartialFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return new DBConnReq(input, extensionRegistry);
    }
  };

  public static com.google.protobuf.Parser<DBConnReq> parser() {
    return PARSER;
  }

  @Override
  public com.google.protobuf.Parser<DBConnReq> getParserForType() {
    return PARSER;
  }

  public DBConnReq getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }

}

