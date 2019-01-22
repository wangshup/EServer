// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: mq.proto

package com.dd.server.mq;

public final class MqProto {
  private MqProto() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (com.google.protobuf.ExtensionRegistryLite) registry);
  }
  public interface MsgHeadOrBuilder extends
      // @@protoc_insertion_point(interface_extends:MsgHead)
      com.google.protobuf.MessageOrBuilder {

    /**
     * <code>int64 id = 1;</code>
     */
    long getId();

    /**
     * <code>int32 srcSid = 2;</code>
     */
    int getSrcSid();

    /**
     * <code>int32 dstSid = 3;</code>
     */
    int getDstSid();

    /**
     * <code>int32 cmdId = 4;</code>
     */
    int getCmdId();

    /**
     * <code>int64 uid = 5;</code>
     */
    long getUid();

    /**
     * <code>int32 errCode = 6;</code>
     */
    int getErrCode();

    /**
     * <code>string errInfo = 7;</code>
     */
    java.lang.String getErrInfo();
    /**
     * <code>string errInfo = 7;</code>
     */
    com.google.protobuf.ByteString
        getErrInfoBytes();

    /**
     * <code>int32 errdesc = 8;</code>
     */
    int getErrdesc();
  }
  /**
   * Protobuf type {@code MsgHead}
   */
  public  static final class MsgHead extends
      com.google.protobuf.GeneratedMessageV3 implements
      // @@protoc_insertion_point(message_implements:MsgHead)
      MsgHeadOrBuilder {
    // Use MsgHead.newBuilder() to construct.
    private MsgHead(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
      super(builder);
    }
    private MsgHead() {
      id_ = 0L;
      srcSid_ = 0;
      dstSid_ = 0;
      cmdId_ = 0;
      uid_ = 0L;
      errCode_ = 0;
      errInfo_ = "";
      errdesc_ = 0;
    }

    @java.lang.Override
    public final com.google.protobuf.UnknownFieldSet
    getUnknownFields() {
      return com.google.protobuf.UnknownFieldSet.getDefaultInstance();
    }
    private MsgHead(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      this();
      int mutable_bitField0_ = 0;
      try {
        boolean done = false;
        while (!done) {
          int tag = input.readTag();
          switch (tag) {
            case 0:
              done = true;
              break;
            default: {
              if (!input.skipField(tag)) {
                done = true;
              }
              break;
            }
            case 8: {

              id_ = input.readInt64();
              break;
            }
            case 16: {

              srcSid_ = input.readInt32();
              break;
            }
            case 24: {

              dstSid_ = input.readInt32();
              break;
            }
            case 32: {

              cmdId_ = input.readInt32();
              break;
            }
            case 40: {

              uid_ = input.readInt64();
              break;
            }
            case 48: {

              errCode_ = input.readInt32();
              break;
            }
            case 58: {
              java.lang.String s = input.readStringRequireUtf8();

              errInfo_ = s;
              break;
            }
            case 64: {

              errdesc_ = input.readInt32();
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
        makeExtensionsImmutable();
      }
    }
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return com.dd.server.mq.MqProto.internal_static_MsgHead_descriptor;
    }

    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return com.dd.server.mq.MqProto.internal_static_MsgHead_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              com.dd.server.mq.MqProto.MsgHead.class, com.dd.server.mq.MqProto.MsgHead.Builder.class);
    }

    public static final int ID_FIELD_NUMBER = 1;
    private long id_;
    /**
     * <code>int64 id = 1;</code>
     */
    public long getId() {
      return id_;
    }

    public static final int SRCSID_FIELD_NUMBER = 2;
    private int srcSid_;
    /**
     * <code>int32 srcSid = 2;</code>
     */
    public int getSrcSid() {
      return srcSid_;
    }

    public static final int DSTSID_FIELD_NUMBER = 3;
    private int dstSid_;
    /**
     * <code>int32 dstSid = 3;</code>
     */
    public int getDstSid() {
      return dstSid_;
    }

    public static final int CMDID_FIELD_NUMBER = 4;
    private int cmdId_;
    /**
     * <code>int32 cmdId = 4;</code>
     */
    public int getCmdId() {
      return cmdId_;
    }

    public static final int UID_FIELD_NUMBER = 5;
    private long uid_;
    /**
     * <code>int64 uid = 5;</code>
     */
    public long getUid() {
      return uid_;
    }

    public static final int ERRCODE_FIELD_NUMBER = 6;
    private int errCode_;
    /**
     * <code>int32 errCode = 6;</code>
     */
    public int getErrCode() {
      return errCode_;
    }

    public static final int ERRINFO_FIELD_NUMBER = 7;
    private volatile java.lang.Object errInfo_;
    /**
     * <code>string errInfo = 7;</code>
     */
    public java.lang.String getErrInfo() {
      java.lang.Object ref = errInfo_;
      if (ref instanceof java.lang.String) {
        return (java.lang.String) ref;
      } else {
        com.google.protobuf.ByteString bs = 
            (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        errInfo_ = s;
        return s;
      }
    }
    /**
     * <code>string errInfo = 7;</code>
     */
    public com.google.protobuf.ByteString
        getErrInfoBytes() {
      java.lang.Object ref = errInfo_;
      if (ref instanceof java.lang.String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (java.lang.String) ref);
        errInfo_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }

    public static final int ERRDESC_FIELD_NUMBER = 8;
    private int errdesc_;
    /**
     * <code>int32 errdesc = 8;</code>
     */
    public int getErrdesc() {
      return errdesc_;
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
      if (id_ != 0L) {
        output.writeInt64(1, id_);
      }
      if (srcSid_ != 0) {
        output.writeInt32(2, srcSid_);
      }
      if (dstSid_ != 0) {
        output.writeInt32(3, dstSid_);
      }
      if (cmdId_ != 0) {
        output.writeInt32(4, cmdId_);
      }
      if (uid_ != 0L) {
        output.writeInt64(5, uid_);
      }
      if (errCode_ != 0) {
        output.writeInt32(6, errCode_);
      }
      if (!getErrInfoBytes().isEmpty()) {
        com.google.protobuf.GeneratedMessageV3.writeString(output, 7, errInfo_);
      }
      if (errdesc_ != 0) {
        output.writeInt32(8, errdesc_);
      }
    }

    public int getSerializedSize() {
      int size = memoizedSize;
      if (size != -1) return size;

      size = 0;
      if (id_ != 0L) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt64Size(1, id_);
      }
      if (srcSid_ != 0) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt32Size(2, srcSid_);
      }
      if (dstSid_ != 0) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt32Size(3, dstSid_);
      }
      if (cmdId_ != 0) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt32Size(4, cmdId_);
      }
      if (uid_ != 0L) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt64Size(5, uid_);
      }
      if (errCode_ != 0) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt32Size(6, errCode_);
      }
      if (!getErrInfoBytes().isEmpty()) {
        size += com.google.protobuf.GeneratedMessageV3.computeStringSize(7, errInfo_);
      }
      if (errdesc_ != 0) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt32Size(8, errdesc_);
      }
      memoizedSize = size;
      return size;
    }

    private static final long serialVersionUID = 0L;
    @java.lang.Override
    public boolean equals(final java.lang.Object obj) {
      if (obj == this) {
       return true;
      }
      if (!(obj instanceof com.dd.server.mq.MqProto.MsgHead)) {
        return super.equals(obj);
      }
      com.dd.server.mq.MqProto.MsgHead other = (com.dd.server.mq.MqProto.MsgHead) obj;

      boolean result = true;
      result = result && (getId()
          == other.getId());
      result = result && (getSrcSid()
          == other.getSrcSid());
      result = result && (getDstSid()
          == other.getDstSid());
      result = result && (getCmdId()
          == other.getCmdId());
      result = result && (getUid()
          == other.getUid());
      result = result && (getErrCode()
          == other.getErrCode());
      result = result && getErrInfo()
          .equals(other.getErrInfo());
      result = result && (getErrdesc()
          == other.getErrdesc());
      return result;
    }

    @java.lang.Override
    public int hashCode() {
      if (memoizedHashCode != 0) {
        return memoizedHashCode;
      }
      int hash = 41;
      hash = (19 * hash) + getDescriptor().hashCode();
      hash = (37 * hash) + ID_FIELD_NUMBER;
      hash = (53 * hash) + com.google.protobuf.Internal.hashLong(
          getId());
      hash = (37 * hash) + SRCSID_FIELD_NUMBER;
      hash = (53 * hash) + getSrcSid();
      hash = (37 * hash) + DSTSID_FIELD_NUMBER;
      hash = (53 * hash) + getDstSid();
      hash = (37 * hash) + CMDID_FIELD_NUMBER;
      hash = (53 * hash) + getCmdId();
      hash = (37 * hash) + UID_FIELD_NUMBER;
      hash = (53 * hash) + com.google.protobuf.Internal.hashLong(
          getUid());
      hash = (37 * hash) + ERRCODE_FIELD_NUMBER;
      hash = (53 * hash) + getErrCode();
      hash = (37 * hash) + ERRINFO_FIELD_NUMBER;
      hash = (53 * hash) + getErrInfo().hashCode();
      hash = (37 * hash) + ERRDESC_FIELD_NUMBER;
      hash = (53 * hash) + getErrdesc();
      hash = (29 * hash) + unknownFields.hashCode();
      memoizedHashCode = hash;
      return hash;
    }

    public static com.dd.server.mq.MqProto.MsgHead parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static com.dd.server.mq.MqProto.MsgHead parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static com.dd.server.mq.MqProto.MsgHead parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static com.dd.server.mq.MqProto.MsgHead parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static com.dd.server.mq.MqProto.MsgHead parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static com.dd.server.mq.MqProto.MsgHead parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }
    public static com.dd.server.mq.MqProto.MsgHead parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input);
    }
    public static com.dd.server.mq.MqProto.MsgHead parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }
    public static com.dd.server.mq.MqProto.MsgHead parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static com.dd.server.mq.MqProto.MsgHead parseFrom(
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
    public static Builder newBuilder(com.dd.server.mq.MqProto.MsgHead prototype) {
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
     * Protobuf type {@code MsgHead}
     */
    public static final class Builder extends
        com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
        // @@protoc_insertion_point(builder_implements:MsgHead)
        com.dd.server.mq.MqProto.MsgHeadOrBuilder {
      public static final com.google.protobuf.Descriptors.Descriptor
          getDescriptor() {
        return com.dd.server.mq.MqProto.internal_static_MsgHead_descriptor;
      }

      protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
          internalGetFieldAccessorTable() {
        return com.dd.server.mq.MqProto.internal_static_MsgHead_fieldAccessorTable
            .ensureFieldAccessorsInitialized(
                com.dd.server.mq.MqProto.MsgHead.class, com.dd.server.mq.MqProto.MsgHead.Builder.class);
      }

      // Construct using com.dd.server.mq.MqProto.MsgHead.newBuilder()
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
        id_ = 0L;

        srcSid_ = 0;

        dstSid_ = 0;

        cmdId_ = 0;

        uid_ = 0L;

        errCode_ = 0;

        errInfo_ = "";

        errdesc_ = 0;

        return this;
      }

      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return com.dd.server.mq.MqProto.internal_static_MsgHead_descriptor;
      }

      public com.dd.server.mq.MqProto.MsgHead getDefaultInstanceForType() {
        return com.dd.server.mq.MqProto.MsgHead.getDefaultInstance();
      }

      public com.dd.server.mq.MqProto.MsgHead build() {
        com.dd.server.mq.MqProto.MsgHead result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return result;
      }

      public com.dd.server.mq.MqProto.MsgHead buildPartial() {
        com.dd.server.mq.MqProto.MsgHead result = new com.dd.server.mq.MqProto.MsgHead(this);
        result.id_ = id_;
        result.srcSid_ = srcSid_;
        result.dstSid_ = dstSid_;
        result.cmdId_ = cmdId_;
        result.uid_ = uid_;
        result.errCode_ = errCode_;
        result.errInfo_ = errInfo_;
        result.errdesc_ = errdesc_;
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
        if (other instanceof com.dd.server.mq.MqProto.MsgHead) {
          return mergeFrom((com.dd.server.mq.MqProto.MsgHead)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }

      public Builder mergeFrom(com.dd.server.mq.MqProto.MsgHead other) {
        if (other == com.dd.server.mq.MqProto.MsgHead.getDefaultInstance()) return this;
        if (other.getId() != 0L) {
          setId(other.getId());
        }
        if (other.getSrcSid() != 0) {
          setSrcSid(other.getSrcSid());
        }
        if (other.getDstSid() != 0) {
          setDstSid(other.getDstSid());
        }
        if (other.getCmdId() != 0) {
          setCmdId(other.getCmdId());
        }
        if (other.getUid() != 0L) {
          setUid(other.getUid());
        }
        if (other.getErrCode() != 0) {
          setErrCode(other.getErrCode());
        }
        if (!other.getErrInfo().isEmpty()) {
          errInfo_ = other.errInfo_;
          onChanged();
        }
        if (other.getErrdesc() != 0) {
          setErrdesc(other.getErrdesc());
        }
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
        com.dd.server.mq.MqProto.MsgHead parsedMessage = null;
        try {
          parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          parsedMessage = (com.dd.server.mq.MqProto.MsgHead) e.getUnfinishedMessage();
          throw e.unwrapIOException();
        } finally {
          if (parsedMessage != null) {
            mergeFrom(parsedMessage);
          }
        }
        return this;
      }

      private long id_ ;
      /**
       * <code>int64 id = 1;</code>
       */
      public long getId() {
        return id_;
      }
      /**
       * <code>int64 id = 1;</code>
       */
      public Builder setId(long value) {
        
        id_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>int64 id = 1;</code>
       */
      public Builder clearId() {
        
        id_ = 0L;
        onChanged();
        return this;
      }

      private int srcSid_ ;
      /**
       * <code>int32 srcSid = 2;</code>
       */
      public int getSrcSid() {
        return srcSid_;
      }
      /**
       * <code>int32 srcSid = 2;</code>
       */
      public Builder setSrcSid(int value) {
        
        srcSid_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>int32 srcSid = 2;</code>
       */
      public Builder clearSrcSid() {
        
        srcSid_ = 0;
        onChanged();
        return this;
      }

      private int dstSid_ ;
      /**
       * <code>int32 dstSid = 3;</code>
       */
      public int getDstSid() {
        return dstSid_;
      }
      /**
       * <code>int32 dstSid = 3;</code>
       */
      public Builder setDstSid(int value) {
        
        dstSid_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>int32 dstSid = 3;</code>
       */
      public Builder clearDstSid() {
        
        dstSid_ = 0;
        onChanged();
        return this;
      }

      private int cmdId_ ;
      /**
       * <code>int32 cmdId = 4;</code>
       */
      public int getCmdId() {
        return cmdId_;
      }
      /**
       * <code>int32 cmdId = 4;</code>
       */
      public Builder setCmdId(int value) {
        
        cmdId_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>int32 cmdId = 4;</code>
       */
      public Builder clearCmdId() {
        
        cmdId_ = 0;
        onChanged();
        return this;
      }

      private long uid_ ;
      /**
       * <code>int64 uid = 5;</code>
       */
      public long getUid() {
        return uid_;
      }
      /**
       * <code>int64 uid = 5;</code>
       */
      public Builder setUid(long value) {
        
        uid_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>int64 uid = 5;</code>
       */
      public Builder clearUid() {
        
        uid_ = 0L;
        onChanged();
        return this;
      }

      private int errCode_ ;
      /**
       * <code>int32 errCode = 6;</code>
       */
      public int getErrCode() {
        return errCode_;
      }
      /**
       * <code>int32 errCode = 6;</code>
       */
      public Builder setErrCode(int value) {
        
        errCode_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>int32 errCode = 6;</code>
       */
      public Builder clearErrCode() {
        
        errCode_ = 0;
        onChanged();
        return this;
      }

      private java.lang.Object errInfo_ = "";
      /**
       * <code>string errInfo = 7;</code>
       */
      public java.lang.String getErrInfo() {
        java.lang.Object ref = errInfo_;
        if (!(ref instanceof java.lang.String)) {
          com.google.protobuf.ByteString bs =
              (com.google.protobuf.ByteString) ref;
          java.lang.String s = bs.toStringUtf8();
          errInfo_ = s;
          return s;
        } else {
          return (java.lang.String) ref;
        }
      }
      /**
       * <code>string errInfo = 7;</code>
       */
      public com.google.protobuf.ByteString
          getErrInfoBytes() {
        java.lang.Object ref = errInfo_;
        if (ref instanceof String) {
          com.google.protobuf.ByteString b = 
              com.google.protobuf.ByteString.copyFromUtf8(
                  (java.lang.String) ref);
          errInfo_ = b;
          return b;
        } else {
          return (com.google.protobuf.ByteString) ref;
        }
      }
      /**
       * <code>string errInfo = 7;</code>
       */
      public Builder setErrInfo(
          java.lang.String value) {
        if (value == null) {
    throw new NullPointerException();
  }
  
        errInfo_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>string errInfo = 7;</code>
       */
      public Builder clearErrInfo() {
        
        errInfo_ = getDefaultInstance().getErrInfo();
        onChanged();
        return this;
      }
      /**
       * <code>string errInfo = 7;</code>
       */
      public Builder setErrInfoBytes(
          com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);
        
        errInfo_ = value;
        onChanged();
        return this;
      }

      private int errdesc_ ;
      /**
       * <code>int32 errdesc = 8;</code>
       */
      public int getErrdesc() {
        return errdesc_;
      }
      /**
       * <code>int32 errdesc = 8;</code>
       */
      public Builder setErrdesc(int value) {
        
        errdesc_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>int32 errdesc = 8;</code>
       */
      public Builder clearErrdesc() {
        
        errdesc_ = 0;
        onChanged();
        return this;
      }
      public final Builder setUnknownFields(
          final com.google.protobuf.UnknownFieldSet unknownFields) {
        return this;
      }

      public final Builder mergeUnknownFields(
          final com.google.protobuf.UnknownFieldSet unknownFields) {
        return this;
      }


      // @@protoc_insertion_point(builder_scope:MsgHead)
    }

    // @@protoc_insertion_point(class_scope:MsgHead)
    private static final com.dd.server.mq.MqProto.MsgHead DEFAULT_INSTANCE;
    static {
      DEFAULT_INSTANCE = new com.dd.server.mq.MqProto.MsgHead();
    }

    public static com.dd.server.mq.MqProto.MsgHead getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }

    private static final com.google.protobuf.Parser<MsgHead>
        PARSER = new com.google.protobuf.AbstractParser<MsgHead>() {
      public MsgHead parsePartialFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
          return new MsgHead(input, extensionRegistry);
      }
    };

    public static com.google.protobuf.Parser<MsgHead> parser() {
      return PARSER;
    }

    @java.lang.Override
    public com.google.protobuf.Parser<MsgHead> getParserForType() {
      return PARSER;
    }

    public com.dd.server.mq.MqProto.MsgHead getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
    }

  }

  private static final com.google.protobuf.Descriptors.Descriptor
    internal_static_MsgHead_descriptor;
  private static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_MsgHead_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n\010mq.proto\"\204\001\n\007MsgHead\022\n\n\002id\030\001 \001(\003\022\016\n\006sr" +
      "cSid\030\002 \001(\005\022\016\n\006dstSid\030\003 \001(\005\022\r\n\005cmdId\030\004 \001(" +
      "\005\022\013\n\003uid\030\005 \001(\003\022\017\n\007errCode\030\006 \001(\005\022\017\n\007errIn" +
      "fo\030\007 \001(\t\022\017\n\007errdesc\030\010 \001(\005B\033\n\020com.dd.serv" +
      "er.mqB\007MqProtob\006proto3"
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
    internal_static_MsgHead_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_MsgHead_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_MsgHead_descriptor,
        new java.lang.String[] { "Id", "SrcSid", "DstSid", "CmdId", "Uid", "ErrCode", "ErrInfo", "Errdesc", });
  }

  // @@protoc_insertion_point(outer_class_scope)
}
