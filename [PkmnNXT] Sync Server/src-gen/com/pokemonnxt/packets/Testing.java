// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: com/pokemonnxt/packets/test.proto

package com.pokemonnxt.packets;

public final class Testing {
  private Testing() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
  }
  public interface testOrBuilder
      extends com.google.protobuf.MessageOrBuilder {
    
    // required int32 test = 1;
    boolean hasTest();
    int getTest();
  }
  public static final class test extends
      com.google.protobuf.GeneratedMessage
      implements testOrBuilder {
    // Use test.newBuilder() to construct.
    private test(Builder builder) {
      super(builder);
    }
    private test(boolean noInit) {}
    
    private static final test defaultInstance;
    public static test getDefaultInstance() {
      return defaultInstance;
    }
    
    public test getDefaultInstanceForType() {
      return defaultInstance;
    }
    
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return com.pokemonnxt.packets.Testing.internal_static_packets_test_descriptor;
    }
    
    protected com.google.protobuf.GeneratedMessage.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return com.pokemonnxt.packets.Testing.internal_static_packets_test_fieldAccessorTable;
    }
    
    private int bitField0_;
    // required int32 test = 1;
    public static final int TEST_FIELD_NUMBER = 1;
    private int test_;
    public boolean hasTest() {
      return ((bitField0_ & 0x00000001) == 0x00000001);
    }
    public int getTest() {
      return test_;
    }
    
    private void initFields() {
      test_ = 0;
    }
    private byte memoizedIsInitialized = -1;
    public final boolean isInitialized() {
      byte isInitialized = memoizedIsInitialized;
      if (isInitialized != -1) return isInitialized == 1;
      
      if (!hasTest()) {
        memoizedIsInitialized = 0;
        return false;
      }
      memoizedIsInitialized = 1;
      return true;
    }
    
    public void writeTo(com.google.protobuf.CodedOutputStream output)
                        throws java.io.IOException {
      getSerializedSize();
      if (((bitField0_ & 0x00000001) == 0x00000001)) {
        output.writeInt32(1, test_);
      }
      getUnknownFields().writeTo(output);
    }
    
    private int memoizedSerializedSize = -1;
    public int getSerializedSize() {
      int size = memoizedSerializedSize;
      if (size != -1) return size;
    
      size = 0;
      if (((bitField0_ & 0x00000001) == 0x00000001)) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt32Size(1, test_);
      }
      size += getUnknownFields().getSerializedSize();
      memoizedSerializedSize = size;
      return size;
    }
    
    private static final long serialVersionUID = 0L;
    @java.lang.Override
    protected java.lang.Object writeReplace()
        throws java.io.ObjectStreamException {
      return super.writeReplace();
    }
    
    public static com.pokemonnxt.packets.Testing.test parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return newBuilder().mergeFrom(data).buildParsed();
    }
    public static com.pokemonnxt.packets.Testing.test parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return newBuilder().mergeFrom(data, extensionRegistry)
               .buildParsed();
    }
    public static com.pokemonnxt.packets.Testing.test parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return newBuilder().mergeFrom(data).buildParsed();
    }
    public static com.pokemonnxt.packets.Testing.test parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return newBuilder().mergeFrom(data, extensionRegistry)
               .buildParsed();
    }
    public static com.pokemonnxt.packets.Testing.test parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return newBuilder().mergeFrom(input).buildParsed();
    }
    public static com.pokemonnxt.packets.Testing.test parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return newBuilder().mergeFrom(input, extensionRegistry)
               .buildParsed();
    }
    public static com.pokemonnxt.packets.Testing.test parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      Builder builder = newBuilder();
      if (builder.mergeDelimitedFrom(input)) {
        return builder.buildParsed();
      } else {
        return null;
      }
    }
    public static com.pokemonnxt.packets.Testing.test parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      Builder builder = newBuilder();
      if (builder.mergeDelimitedFrom(input, extensionRegistry)) {
        return builder.buildParsed();
      } else {
        return null;
      }
    }
    public static com.pokemonnxt.packets.Testing.test parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return newBuilder().mergeFrom(input).buildParsed();
    }
    public static com.pokemonnxt.packets.Testing.test parseFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return newBuilder().mergeFrom(input, extensionRegistry)
               .buildParsed();
    }
    
    public static Builder newBuilder() { return Builder.create(); }
    public Builder newBuilderForType() { return newBuilder(); }
    public static Builder newBuilder(com.pokemonnxt.packets.Testing.test prototype) {
      return newBuilder().mergeFrom(prototype);
    }
    public Builder toBuilder() { return newBuilder(this); }
    
    @java.lang.Override
    protected Builder newBuilderForType(
        com.google.protobuf.GeneratedMessage.BuilderParent parent) {
      Builder builder = new Builder(parent);
      return builder;
    }
    public static final class Builder extends
        com.google.protobuf.GeneratedMessage.Builder<Builder>
       implements com.pokemonnxt.packets.Testing.testOrBuilder {
      public static final com.google.protobuf.Descriptors.Descriptor
          getDescriptor() {
        return com.pokemonnxt.packets.Testing.internal_static_packets_test_descriptor;
      }
      
      protected com.google.protobuf.GeneratedMessage.FieldAccessorTable
          internalGetFieldAccessorTable() {
        return com.pokemonnxt.packets.Testing.internal_static_packets_test_fieldAccessorTable;
      }
      
      // Construct using com.pokemonnxt.packets.Testing.test.newBuilder()
      private Builder() {
        maybeForceBuilderInitialization();
      }
      
      private Builder(BuilderParent parent) {
        super(parent);
        maybeForceBuilderInitialization();
      }
      private void maybeForceBuilderInitialization() {
        if (com.google.protobuf.GeneratedMessage.alwaysUseFieldBuilders) {
        }
      }
      private static Builder create() {
        return new Builder();
      }
      
      public Builder clear() {
        super.clear();
        test_ = 0;
        bitField0_ = (bitField0_ & ~0x00000001);
        return this;
      }
      
      public Builder clone() {
        return create().mergeFrom(buildPartial());
      }
      
      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return com.pokemonnxt.packets.Testing.test.getDescriptor();
      }
      
      public com.pokemonnxt.packets.Testing.test getDefaultInstanceForType() {
        return com.pokemonnxt.packets.Testing.test.getDefaultInstance();
      }
      
      public com.pokemonnxt.packets.Testing.test build() {
        com.pokemonnxt.packets.Testing.test result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return result;
      }
      
      private com.pokemonnxt.packets.Testing.test buildParsed()
          throws com.google.protobuf.InvalidProtocolBufferException {
        com.pokemonnxt.packets.Testing.test result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(
            result).asInvalidProtocolBufferException();
        }
        return result;
      }
      
      public com.pokemonnxt.packets.Testing.test buildPartial() {
        com.pokemonnxt.packets.Testing.test result = new com.pokemonnxt.packets.Testing.test(this);
        int from_bitField0_ = bitField0_;
        int to_bitField0_ = 0;
        if (((from_bitField0_ & 0x00000001) == 0x00000001)) {
          to_bitField0_ |= 0x00000001;
        }
        result.test_ = test_;
        result.bitField0_ = to_bitField0_;
        onBuilt();
        return result;
      }
      
      public Builder mergeFrom(com.google.protobuf.Message other) {
        if (other instanceof com.pokemonnxt.packets.Testing.test) {
          return mergeFrom((com.pokemonnxt.packets.Testing.test)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }
      
      public Builder mergeFrom(com.pokemonnxt.packets.Testing.test other) {
        if (other == com.pokemonnxt.packets.Testing.test.getDefaultInstance()) return this;
        if (other.hasTest()) {
          setTest(other.getTest());
        }
        this.mergeUnknownFields(other.getUnknownFields());
        return this;
      }
      
      public final boolean isInitialized() {
        if (!hasTest()) {
          
          return false;
        }
        return true;
      }
      
      public Builder mergeFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws java.io.IOException {
        com.google.protobuf.UnknownFieldSet.Builder unknownFields =
          com.google.protobuf.UnknownFieldSet.newBuilder(
            this.getUnknownFields());
        while (true) {
          int tag = input.readTag();
          switch (tag) {
            case 0:
              this.setUnknownFields(unknownFields.build());
              onChanged();
              return this;
            default: {
              if (!parseUnknownField(input, unknownFields,
                                     extensionRegistry, tag)) {
                this.setUnknownFields(unknownFields.build());
                onChanged();
                return this;
              }
              break;
            }
            case 8: {
              bitField0_ |= 0x00000001;
              test_ = input.readInt32();
              break;
            }
          }
        }
      }
      
      private int bitField0_;
      
      // required int32 test = 1;
      private int test_ ;
      public boolean hasTest() {
        return ((bitField0_ & 0x00000001) == 0x00000001);
      }
      public int getTest() {
        return test_;
      }
      public Builder setTest(int value) {
        bitField0_ |= 0x00000001;
        test_ = value;
        onChanged();
        return this;
      }
      public Builder clearTest() {
        bitField0_ = (bitField0_ & ~0x00000001);
        test_ = 0;
        onChanged();
        return this;
      }
      
      // @@protoc_insertion_point(builder_scope:packets.test)
    }
    
    static {
      defaultInstance = new test(true);
      defaultInstance.initFields();
    }
    
    // @@protoc_insertion_point(class_scope:packets.test)
  }
  
  private static com.google.protobuf.Descriptors.Descriptor
    internal_static_packets_test_descriptor;
  private static
    com.google.protobuf.GeneratedMessage.FieldAccessorTable
      internal_static_packets_test_fieldAccessorTable;
  
  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n!com/pokemonnxt/packets/test.proto\022\007pac" +
      "kets\"\024\n\004test\022\014\n\004test\030\001 \002(\005B!\n\026com.pokemo" +
      "nnxt.packetsB\007Testing"
    };
    com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner assigner =
      new com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner() {
        public com.google.protobuf.ExtensionRegistry assignDescriptors(
            com.google.protobuf.Descriptors.FileDescriptor root) {
          descriptor = root;
          internal_static_packets_test_descriptor =
            getDescriptor().getMessageTypes().get(0);
          internal_static_packets_test_fieldAccessorTable = new
            com.google.protobuf.GeneratedMessage.FieldAccessorTable(
              internal_static_packets_test_descriptor,
              new java.lang.String[] { "Test", },
              com.pokemonnxt.packets.Testing.test.class,
              com.pokemonnxt.packets.Testing.test.Builder.class);
          return null;
        }
      };
    com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
        }, assigner);
  }
  
  // @@protoc_insertion_point(outer_class_scope)
}
