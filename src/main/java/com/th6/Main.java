package com.th6;

import org.apache.flink.connector.kafka.sink.KafkaRecordSerializationSchema;
import org.apache.flink.connector.kafka.sink.KafkaSink;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class Main {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.addSource(new OrderPaymentJob.OrderGenerator()).sinkTo(KafkaSink.<String>builder()
                .setBootstrapServers("localhost:9092")
                .setRecordSerializer(KafkaRecordSerializationSchema.builder()
                        .setTopic("OrderPayment_Thin181197")
                        .setValueSerializationSchema(new SimpleStringSchema())
                        .build()
                )
                .build());
        env.execute("Provide OderPayment");


    }
}
