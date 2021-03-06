package com.th6.job;

import com.th6.util.DataGenerator;
import com.th6.util.SimpleStringSchema;
import org.apache.flink.connector.kafka.sink.KafkaRecordSerializationSchema;
import org.apache.flink.connector.kafka.sink.KafkaSink;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.source.SourceFunction;

public class OrderPaymentJob {

    public static final String BROKERS = "10.1.12.183:9092";
    public static final String TOPIC_NAME = "dai_orderPayment_practice";
    private final SourceFunction<String> source;

    public OrderPaymentJob(SourceFunction<String> source) {
        this.source = source;
    }

    public static void main(String[] args) throws Exception {
        OrderPaymentJob job = new OrderPaymentJob(new OrderGenerator());
        job.execute();
    }

    public void execute() throws Exception {

        // set up streaming execution environment
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        // set up the pipeline
        env.addSource(source).sinkTo(createSink(BROKERS,TOPIC_NAME));

        // run the pipeline and return the result
        env.execute("Provide OderPayment");
    }


    public static KafkaSink<String> createSink(String brokers,String topicName){
        return KafkaSink.<String>builder()
                .setBootstrapServers(brokers)
                .setRecordSerializer(KafkaRecordSerializationSchema.builder()
                        .setTopic(topicName)
                        .setValueSerializationSchema(new SimpleStringSchema())
                        .build()
                )
                .build();


    }

    public static class OrderGenerator implements SourceFunction<String> {

        private final DataGenerator generator = new DataGenerator();

        @Override
        public void run(SourceContext<String> ctx) throws Exception {
            while (generator.isRunning()){
                ctx.collect(generator.next().toString());
                Thread.sleep(1000);
            }
        }

        @Override
        public void cancel() {
            generator.stop();

        }
    }
}
