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
     * <code>int32 srcSid = 1;</code>
     */
    int getSrcSid();

    /**
     * <code>int32 dstSid = 2;</code>
     */
    int getDstSid();

    /**
     * <code>int32 msgId = 3;</code>
     */
    int getMsgId();

    /**
     * <code>int64 sequence = 4;</code>
     */
    long getSequence();

    /**
     * <code>int32 errCode = 5;</code>
     */
    int getErrCode();

    /**
     * <code>string errInfo = 6;</code>
     */
    java.lang.String getErrInfo();
    /**
     * <code>string errInfo = 6;</code>
     */
    com.google.protobuf.ByteString
        getErrInfoBytes();
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
      srcSid_ = 0;
      dstSid_ = 0;
      msgId_ = 0;
      sequence_ = 0L;
      errCode_ = 0;
      errInfo_ = "";
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

              srcSid_ = input.readInt32();
              break;
            }
            case 16: {

              dstSid_ = input.readInt32();
              break;
            }
            case 24: {

              msgId_ = input.readInt32();
              break;
            }
            case 32: {

              sequence_ = input.readInt64();
              break;
            }
            case 40: {

              errCode_ = input.readInt32();
              break;
            }
            case 50: {
              java.lang.String s = input.readStringRequireUtf8();

              errInfo_ = s;
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

    public static final int SRCSID_FIELD_NUMBER = 1;
    private int srcSid_;
    /**
     * <code>int32 srcSid = 1;</code>
     */
    public int getSrcSid() {
      return srcSid_;
    }

    public static final int DSTSID_FIELD_NUMBER = 2;
    private int dstSid_;
    /**
     * <code>int32 dstSid = 2;</code>
     */
    public int getDstSid() {
      return dstSid_;
    }

    public static final int MSGID_FIELD_NUMBER = 3;
    private int msgId_;
    /**
     * <code>int32 msgId = 3;</code>
     */
    public int getMsgId() {
      return msgId_;
    }

    public static final int SEQUENCE_FIELD_NUMBER = 4;
    private long sequence_;
    /**
     * <code>int64 sequence = 4;</code>
     */
    public long getSequence() {
      return sequence_;
    }

    public static final int ERRCODE_FIELD_NUMBER = 5;
    private int errCode_;
    /**
     * <code>int32 errCode = 5;</code>
     */
    public int getErrCode() {
      return errCode_;
    }

    public static final int ERRINFO_FIELD_NUMBER = 6;
    private volatile java.lang.Object errInfo_;
    /**
     * <code>string errInfo = 6;</code>
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
     * <code>string errInfo = 6;</code>
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
      if (srcSid_ != 0) {
        output.writeInt32(1, srcSid_);
      }
      if (dstSid_ != 0) {
        output.writeInt32(2, dstSid_);
      }
      if (msgId_ != 0) {
        output.writeInt32(3, msgId_);
      }
      if (sequence_ != 0L) {
        output.writeInt64(4, sequence_);
      }
      if (errCode_ != 0) {
        output.writeInt32(5, errCode_);
      }
      if (!getErrInfoBytes().isEmpty()) {
        com.google.protobuf.GeneratedMessageV3.writeString(output, 6, errInfo_);
      }
    }

    public int getSerializedSize() {
      int size = memoizedSize;
      if (size != -1) return size;

      size = 0;
      if (srcSid_ != 0) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt32Size(1, srcSid_);
      }
      if (dstSid_ != 0) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt32Size(2, dstSid_);
      }
      if (msgId_ != 0) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt32Size(3, msgId_);
      }
      if (sequence_ != 0L) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt64Size(4, sequence_);
      }
      if (errCode_ != 0) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt32Size(5, errCode_);
      }
      if (!getErrInfoBytes().isEmpty()) {
        size += com.google.protobuf.GeneratedMessageV3.computeStringSize(6, errInfo_);
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
      result = result && (getSrcSid()
          == other.getSrcSid());
      result = result && (getDstSid()
          == other.getDstSid());
      result = result && (getMsgId()
          == other.getMsgId());
      result = result && (getSequence()
          == other.getSequence());
      result = result && (getErrCode()
          == other.getErrCode());
      result = result && getErrInfo()
          .equals(other.getErrInfo());
      return result;
    }

    @java.lang.Override
    public int hashCode() {
      if (memoizedHashCode != 0) {
        return memoizedHashCode;
      }
      int hash = 41;
      hash = (19 * hash) + getDescriptor().hashCode();
      hash = (37 * hash) + SRCSID_FIELD_NUMBER;
      hash = (53 * hash) + getSrcSid();
      hash = (37 * hash) + DSTSID_FIELD_NUMBER;
      hash = (53 * hash) + getDstSid();
      hash = (37 * hash) + MSGID_FIELD_NUMBER;
      hash = (53 * hash) + getMsgId();
      hash = (37 * hash) + SEQUENCE_FIELD_NUMBER;
      hash = (53 * hash) + com.google.protobuf.Internal.hashLong(
          getSequence());
      hash = (37 * hash) + ERRCODE_FIELD_NUMBER;
      hash = (53 * hash) + getErrCode();
      hash = (37 * hash) + ERRINFO_FIELD_NUMBER;
      hash = (53 * hash) + getErrInfo().hashCode();
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
        srcSid_ = 0;

        dstSid_ = 0;

        msgId_ = 0;

        sequence_ = 0L;

        errCode_ = 0;

        errInfo_ = "";

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
        result.srcSid_ = srcSid_;
        result.dstSid_ = dstSid_;
        result.msgId_ = msgId_;
        result.sequence_ = sequence_;
        result.errCode_ = errCode_;
        result.errInfo_ = errInfo_;
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
        if (other.getSrcSid() != 0) {
          setSrcSid(other.getSrcSid());
        }
        if (other.getDstSid() != 0) {
          setDstSid(other.getDstSid());
        }
        if (other.getMsgId() != 0) {
          setMsgId(other.getMsgId());
        }
        if (other.getSequence() != 0L) {
          setSequence(other.getSequence());
        }
        if (other.getErrCode() != 0) {
          setErrCode(other.getErrCode());
        }
        if (!other.getErrInfo().isEmpty()) {
          errInfo_ = other.errInfo_;
          onChanged();
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

      private int srcSid_ ;
      /**
       * <code>int32 srcSid = 1;</code>
       */
      public int getSrcSid() {
        return srcSid_;
      }
      /**
       * <code>int32 srcSid = 1;</code>
       */
      public Builder setSrcSid(int value) {
        
        srcSid_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>int32 srcSid = 1;</code>
       */
      public Builder clearSrcSid() {
        
        srcSid_ = 0;
        onChanged();
        return this;
      }

      private int dstSid_ ;
      /**
       * <code>int32 dstSid = 2;</code>
       */
      public int getDstSid() {
        return dstSid_;
      }
      /**
       * <code>int32 dstSid = 2;</code>
       */
      public Builder setDstSid(int value) {
        
        dstSid_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>int32 dstSid = 2;</code>
       */
      public Builder clearDstSid() {
        
        dstSid_ = 0;
        onChanged();
        return this;
      }

      private int msgId_ ;
      /**
       * <code>int32 msgId = 3;</code>
       */
      public int getMsgId() {
        return msgId_;
      }
      /**
       * <code>int32 msgId = 3;</code>
       */
      public Builder setMsgId(int value) {
        
        msgId_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>int32 msgId = 3;</code>
       */
      public Builder clearMsgId() {
        
        msgId_ = 0;
        onChanged();
        return this;
      }

      private long sequence_ ;
      /**
       * <code>int64 sequence = 4;</code>
       */
      public long getSequence() {
        return sequence_;
      }
      /**
       * <code>int64 sequence = 4;</code>
       */
      public Builder setSequence(long value) {
        
        sequence_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>int64 sequence = 4;</code>
       */
      public Builder clearSequence() {
        
        sequence_ = 0L;
        onChanged();
        return this;
      }

      private int errCode_ ;
      /**
       * <code>int32 errCode = 5;</code>
       */
      public int getErrCode() {
        return errCode_;
      }
      /**
       * <code>int32 errCode = 5;</code>
       */
      public Builder setErrCode(int value) {
        
        errCode_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>int32 errCode = 5;</code>
       */
      public Builder clearErrCode() {
        
        errCode_ = 0;
        onChanged();
        return this;
      }

      private java.lang.Object errInfo_ = "";
      /**
       * <code>string errInfo = 6;</code>
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
       * <code>string errInfo = 6;</code>
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
       * <code>string errInfo = 6;</code>
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
       * <code>string errInfo = 6;</code>
       */
      public Builder clearErrInfo() {
        
        errInfo_ = getDefaultInstance().getErrInfo();
        onChanged();
        return this;
      }
      /**
       * <code>string errInfo = 6;</code>
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
      "\n\010mq.proto\"l\n\007MsgHead\022\016\n\006srcSid\030\001 \001(\005\022\016\n" +
      "\006dstSid\030\002 \001(\005\022\r\n\005msgId\030\003 \001(\005\022\020\n\010sequence" +
      "\030\004 \001(\003\022\017\n\007errCode\030\005 \001(\005\022\017\n\007errInfo\030\006 \001(\t" +
      "B \n\025com.dd.server.mqB\007MqProtob\006prot" +
      "o3"
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
        new java.lang.String[] { "SrcSid", "DstSid", "MsgId", "Sequence", "ErrCode", "ErrInfo", });
  }

  // @@protoc_insertion_point(outer_class_scope)
}
