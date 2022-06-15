package com.th6.util;

import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.api.common.serialization.SerializationSchema;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.typeutils.TypeExtractor;

public class SimpleStringSchema implements DeserializationSchema<String>, SerializationSchema<String> {
    private static final long serialVersionUID = 1L;

    public String deserialize(byte[] message) {
        return new String(message);
    }

    public boolean isEndOfStream(String nextElement) {
        return false;
    }

    public byte[] serialize(String element) {
        return element.getBytes();
    }

    public TypeInformation<String> getProducedType() {
        return TypeExtractor.getForClass(String.class);
    }

}
