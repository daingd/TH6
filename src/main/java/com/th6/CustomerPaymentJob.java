package com.th6;

import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Collector;
import scala.Tuple2;

import java.io.IOException;

public class CustomerPaymentJob {

    private final KafkaSource<String> source;

    public CustomerPaymentJob(KafkaSource<String> source) {
        this.source = source;
    }
    public static void main(String[] args) throws Exception {
        CustomerPaymentJob job =
                new CustomerPaymentJob(generateSource(OrderPaymentJob.BROKERS,OrderPaymentJob.TOPIC_NAME));
        job.execute();

    }

    private void execute() throws Exception {
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.fromSource(source, WatermarkStrategy.forMonotonousTimestamps(), "Kafka Source")
                .flatMap(new Tokenizer())
                .keyBy(new CustomerPaymentKeySelector())
                .print();
        env.execute("Provide customerPayment");
    }

    private static KafkaSource<String> generateSource(String brokers, String topicName) {
        return KafkaSource.<String>builder()
                .setBootstrapServers(brokers)
                .setTopics(topicName)
                .setStartingOffsets(OffsetsInitializer.earliest())
                .setValueOnlyDeserializer(new SimpleStringSchema())
                .build();
    }

    private class Tokenizer implements FlatMapFunction<String, OrderPayment> {

        @Override
        public void flatMap(String value, Collector<OrderPayment> out) throws Exception {
            ObjectMapper mapper = new ObjectMapper();
            try {

                OrderPayment[] serviceModels = mapper.readValue(value, OrderPayment[].class);
                for (OrderPayment item: serviceModels) {
                    out.collect(item);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class CustomerPaymentKeySelector implements KeySelector<OrderPayment,Tuple2<Integer,Integer>> {
        @Override
        public Tuple2<Integer, Integer> getKey(OrderPayment value) throws Exception {
            return new Tuple2<>(value.getCustomerId(),value.getTransactionType());
        }
    }
}
