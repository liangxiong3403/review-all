package org.liangxiong.message.kafka;

import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.liangxiong.message.constants.KafkaConstant;
import org.liangxiong.message.deserializer.CompanyDeserializer;
import org.liangxiong.message.entity.Company;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author liangxiong
 * @version 1.0.0
 * @date 2020-08-16 10:51
 * @description 第三种多线程消费方式
 **/
public class ThirdMultiThreadConsumer {

    private static Properties initConfig() {
        Properties properties = new Properties();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, KafkaConstant.BOOTSTRAP_SERVER);
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, CompanyDeserializer.class.getName());
        properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, KafkaConstant.GROUP_ID);
        return properties;
    }

    static class KafkaConsumerThread extends Thread {

        private KafkaConsumer<String, Company> consumer;

        private ExecutorService executorService;

        private static Map<TopicPartition, OffsetAndMetadata> offsets = new HashMap<>(8);

        KafkaConsumerThread(Properties properties, String topic, int threadNumber) {
            this.consumer = new KafkaConsumer<>(properties);
            this.consumer.subscribe(Arrays.asList(topic));
            this.executorService = new ThreadPoolExecutor(threadNumber, threadNumber, 0L, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(1000), new ThreadPoolExecutor.CallerRunsPolicy());
        }

        @Override
        public void run() {
            try {
                while (true) {
                    ConsumerRecords<String, Company> records = this.consumer.poll(Duration.ofSeconds(3));
                    if (records.count() == 0) {
                        continue;
                    }
                    // 交给线程池处理,线程池负责具体消费消息
                    this.executorService.submit(new RecordHandler(records));
                    // 同步提交消费位移
                    synchronized (offsets) {
                        if (!offsets.isEmpty()) {
                            this.consumer.commitSync(offsets);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (consumer != null) {
                    consumer.close();
                }
            }
        }
    }

    static class RecordHandler extends Thread {

        private final ConsumerRecords<String, Company> records;

        public RecordHandler(ConsumerRecords<String, Company> records) {
            this.records = records;
        }

        @Override
        public void run() {
            Set<TopicPartition> partitions = records.partitions();
            for (TopicPartition partition : partitions) {
                List<ConsumerRecord<String, Company>> recordList = records.records(partition);
                long consumedOffset = recordList.get(recordList.size() - 1).offset();
                System.out.println("consumedOffset: " + consumedOffset);
                recordList.stream().forEach(System.out::println);
                synchronized (KafkaConsumerThread.offsets) {
                    if (!KafkaConsumerThread.offsets.containsKey(partition)) {
                        KafkaConsumerThread.offsets.put(partition, new OffsetAndMetadata(consumedOffset + 1));
                    } else {
                        long position = KafkaConsumerThread.offsets.get(partition).offset();
                        if (position < consumedOffset + 1) {
                            // 缓冲区的位移小于当前消费位移
                            KafkaConsumerThread.offsets.put(partition, new OffsetAndMetadata(consumedOffset + 1));
                        }
                    }
                }
            }
            System.out.println("offsets: " + KafkaConsumerThread.offsets);
        }
    }

    public static void main(String[] args) {
        KafkaConsumerThread consumerThread = new KafkaConsumerThread(initConfig(), KafkaConstant.TOPIC, Runtime.getRuntime().availableProcessors());
        consumerThread.start();
    }
}
