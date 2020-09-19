package org.liangxiong.message.kafka;

import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.liangxiong.message.constants.KafkaConstant;
import org.liangxiong.message.deserializer.CompanyDeserializer;
import org.liangxiong.message.entity.Company;

import java.time.Duration;
import java.util.*;

/**
 * @author liangxiong
 * @version 1.0.0
 * @date 2020-08-16 09:48
 * @description 第一种多线程实现方式
 **/
public class FirstMultiThreadConsumer {

    private static Properties initConfig() {
        Properties properties = new Properties();
        // broker列表
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, KafkaConstant.BOOTSTRAP_SERVER);
        // key反序列化
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        // value反序列化
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, CompanyDeserializer.class.getName());
        // 消费组id
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, KafkaConstant.GROUP_ID);
        // 开启自动提交消费位移
        properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        return properties;
    }

    /**
     * 一个线程对应一个KafkaConsumer实例
     */
    static class KafkaConsumerThread extends Thread {

        private KafkaConsumer<String, Company> consumer;

        KafkaConsumerThread(String topic, Properties properties) {
            // 构造实例
            this.consumer = new KafkaConsumer(properties);
            // 订阅主题
            this.consumer.subscribe(Arrays.asList(topic));
        }

        @Override
        public void run() {
            while (true) {
                // 拉取消息
                ConsumerRecords<String, Company> records = this.consumer.poll(Duration.ofSeconds(3));
                if (records.count() == 0) {
                    continue;
                }
                Set<TopicPartition> partitions = records.partitions();
                for (TopicPartition partition : partitions) {
                    // 获取特定分区消息
                    List<ConsumerRecord<String, Company>> recordList = records.records(partition);
                    long lastConsumerOffset = recordList.get(recordList.size() - 1).offset();
                    System.out.println("lastConsumerOffset: " + lastConsumerOffset + ", current thread: " + Thread.currentThread().getName());
                    recordList.stream().forEach(System.out::println);
                    this.consumer.commitAsync(Collections.singletonMap(partition, new OffsetAndMetadata(lastConsumerOffset + 1)), new OffsetCommitCallback() {
                        @Override
                        public void onComplete(Map<TopicPartition, OffsetAndMetadata> offsets, Exception exception) {
                            if (exception != null) {
                                exception.printStackTrace();
                            } else {
                                System.out.println("async commit: " + offsets);
                            }
                        }
                    });
                }
            }
        }
    }

    /**
     * 一个线程对应一个KafkaConsumer实例,可以称之为消费线程,所有消费线程隶属于同一个消费组
     * 这种实现方式的并发度取决于分区个数
     *
     * @param args
     */
    public static void main(String[] args) {
        // 线程数量最好小于或等于分区个数
        int threadNumber = 4;
        for (int i = 0; i < threadNumber; i++) {
            new KafkaConsumerThread(KafkaConstant.TOPIC, initConfig()).start();
        }
    }
}
