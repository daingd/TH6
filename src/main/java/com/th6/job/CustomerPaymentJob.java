package com.th6.job;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.HazelcastJsonValue;
import com.hazelcast.map.IMap;
import com.th6.pojo.CustomerPayment;
import com.th6.pojo.OrderPayment;
import com.th6.util.HazelCastHelper;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.runtime.state.FunctionInitializationContext;
import org.apache.flink.runtime.state.FunctionSnapshotContext;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.flink.streaming.api.checkpoint.CheckpointedFunction;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.util.Collector;
import scala.Tuple2;

import java.io.IOException;

public class CustomerPaymentJob {

    private static final String DESTINATION_TOPIC_NAME = "dai_customerPayment_practice";
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
        SingleOutputStreamOperator<CustomerPayment> kafkaSource =
                env.fromSource(source, WatermarkStrategy.forMonotonousTimestamps(), "Kafka Source")
                .flatMap(new Tokenizer())
                .keyBy(new CustomerPaymentKeySelector())
                .process(new ProcessTotalSum());
        kafkaSource.print();
        kafkaSource.map(CustomerPayment::toString)
                .sinkTo(OrderPaymentJob.createSink(OrderPaymentJob.BROKERS, DESTINATION_TOPIC_NAME));

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

    private static class Tokenizer implements FlatMapFunction<String, OrderPayment> {

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

    private static class CustomerPaymentKeySelector implements KeySelector<OrderPayment,Tuple2<Integer,Integer>> {
        @Override
        public Tuple2<Integer, Integer> getKey(OrderPayment value)  {
            return new Tuple2<>(value.getCustomerId(),value.getPaymentType());
        }
    }

    private static class ProcessTotalSum
            extends KeyedProcessFunction<Tuple2<Integer,Integer>, OrderPayment, CustomerPayment> implements CheckpointedFunction {

        private transient ValueState<SumTotalWithTimestamp> state;
        private HazelcastInstance instance;
        private transient IMap<Tuple2<Integer,Integer>, HazelcastJsonValue> customerPaymentIMap;
        ObjectMapper mapper = null;

        @Override
        public void initializeState(FunctionInitializationContext context){
            instance = HazelCastHelper.getInstance();
            customerPaymentIMap = instance.getMap("customerPayment");
            mapper = new ObjectMapper();
        }

        @Override
        public void open(Configuration parameters) {
            state = getRuntimeContext().getState(
                    new ValueStateDescriptor<>("myState", SumTotalWithTimestamp.class));

        }

        @Override
        public void onTimer(
                long timestamp,
                KeyedProcessFunction<Tuple2<Integer, Integer>, OrderPayment, CustomerPayment>.OnTimerContext ctx,
                Collector<CustomerPayment> out) throws Exception {
            SumTotalWithTimestamp result = state.value();
            OrderPayment orderPayment = result.orderPayment;
            if (timestamp == result.lastModified + 10000) {
                CustomerPayment customerPayment = new CustomerPayment(orderPayment.getCustomerId(), orderPayment.getBrandID(),
                        orderPayment.getPaymentType(), orderPayment.getTransactionTypeId(), result.count, result.currentSum);

                customerPaymentIMap.put(ctx.getCurrentKey(),new HazelcastJsonValue(mapper.writeValueAsString(customerPayment)));
                out.collect(customerPayment);
            }
        }

        @Override
        public void processElement(OrderPayment value, KeyedProcessFunction<Tuple2<Integer, Integer>, OrderPayment, CustomerPayment>.Context ctx, Collector<CustomerPayment> out) throws Exception {
            SumTotalWithTimestamp current = state.value();
            if (current == null) {
                current = new SumTotalWithTimestamp();
            }
            if (customerPaymentIMap.containsKey(ctx.getCurrentKey())){
                HazelcastJsonValue hazelcastJsonValue = customerPaymentIMap.get(ctx.getCurrentKey());
                CustomerPayment customerPayment = mapper.readValue(hazelcastJsonValue.toString(), CustomerPayment.class);
                current.currentSum = customerPayment.getTotalAmount();
                current.count = customerPayment.getUseTime();
            }
            current.currentSum += value.getTotalAmount();
            current.orderPayment = value;
            current.count+=1;
            current.lastModified = ctx.timestamp();
            state.update(current);
            ctx.timerService().registerProcessingTimeTimer(current.lastModified + 10000);

        }

        @Override
        public void snapshotState(FunctionSnapshotContext context) throws UnsupportedOperationException {}

        private static class SumTotalWithTimestamp {
            protected float currentSum;
            protected long  lastModified;
            protected OrderPayment orderPayment;
            protected int count;

            public SumTotalWithTimestamp() {
                currentSum = 0;
                count=0;
            }
        }
    }


}
