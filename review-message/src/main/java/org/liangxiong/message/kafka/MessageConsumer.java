package org.liangxiong.message.kafka;

import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.liangxiong.message.constants.KafkaConstant;
import org.liangxiong.message.deserializer.CompanyDeserializer;
import org.liangxiong.message.entity.Company;
import org.liangxiong.message.interceptor.ConsumerInterceptorTtl;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author liangxiong
 * @version 1.0.0
 * @date 2020-07-31 15:53
 * @description
 **/
public class MessageConsumer {

    private static Properties initConfig() {
        Properties properties = new Properties();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, KafkaConstant.BOOTSTRAP_SERVER);
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, CompanyDeserializer.class.getName());
        // 消费组id
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, KafkaConstant.GROUP_ID);
        properties.put(ConsumerConfig.CLIENT_ID_CONFIG, "consumer.client.id.demo");
        // 是否自动提交消费位移
        properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        // 自动位移重置,针对新的消费组或消费者
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        // 消费者拦截器
        properties.put(ConsumerConfig.INTERCEPTOR_CLASSES_CONFIG, ConsumerInterceptorTtl.class.getName());
        return properties;
    }

    /**
     * 设定从尾部开始消费
     *
     * @param consumer     消费者
     * @param partitionSet 分区
     */
    private static Map<TopicPartition, Long> endConsume(KafkaConsumer consumer, Set<TopicPartition> partitionSet) {
        return consumer.endOffsets(partitionSet);
    }

    /**
     * 设定从头部开始消费
     *
     * @param consumer     消费者
     * @param partitionSet 分区
     */
    private static Map<TopicPartition, Long> startConsume(KafkaConsumer consumer, Set<TopicPartition> partitionSet) {
        return consumer.beginningOffsets(partitionSet);
    }

    public static void main(String[] args) {
        AtomicBoolean isRunning = new AtomicBoolean(true);
        KafkaConsumer<String, Company> consumer = new KafkaConsumer<>(initConfig());
        // 获取指定主题的所有分区元数据(根据创建topic时的定义获取)
        List<PartitionInfo> partitionInfos = consumer.partitionsFor(KafkaConstant.TOPIC);
        List<TopicPartition> partitions = new ArrayList<>(4);
        partitionInfos.stream().forEach(e -> {
            partitions.add(new TopicPartition(e.topic(), e.partition()));
        });
        // 订阅特定分区消息
        Map<TopicPartition, OffsetAndMetadata> metadataMap = new HashMap<>(8);
        consumer.subscribe(Arrays.asList(KafkaConstant.TOPIC), new ConsumerRebalanceListener() {
            /**
             * 再均衡动作之前,消费者停止消费之后
             * @param partitions 分区信息
             */
            @Override
            public void onPartitionsRevoked(Collection<TopicPartition> partitions) {
                // 同步提交消费位移
                consumer.commitSync(metadataMap);
                // 清除缓冲区位移信息
                metadataMap.clear();
                // 或者把消费位移写入数据库
            }

            @Override
            public void onPartitionsAssigned(Collection<TopicPartition> partitions) {
                // 再均衡动作之后,消费消息之前;可以从数据库读取消费位移信息
            }
        });
        // 消费特定位置的消息
        Set<TopicPartition> partitionSet = new HashSet<>(8);
        while (partitionSet.size() == 0) {
            consumer.poll(Duration.ofSeconds(3));
            partitionSet = consumer.assignment();
        }
        // 从特定位置开始
        // 方式1:特定位置消费
        Map<TopicPartition, Long> partitionLongMap = endConsume(consumer, partitionSet);
        for (TopicPartition topicPartition : partitionSet) {
            consumer.seek(topicPartition, partitionLongMap.get(topicPartition));
        }
        // 方式2:头部/尾部消费
        //consumer.seekToEnd(partitionSet);
        /// 方式3:根据时间条件设置消费位置
        /*Map<TopicPartition, Long> timestampsToSearch = new HashMap<>(8);
        for (TopicPartition topicPartition : partitionSet) {
            timestampsToSearch.put(topicPartition, System.currentTimeMillis() - 1 * 3600 * 1000);
        }
        Map<TopicPartition, OffsetAndTimestamp> timestampMap = consumer.offsetsForTimes(timestampsToSearch);
        for (TopicPartition topicPartition : partitionSet) {
            OffsetAndTimestamp timestamp = timestampMap.get(topicPartition);
            if (timestamp != null) {
                consumer.seek(topicPartition, timestamp.offset());
            }
        }*/
        while (isRunning.get()) {
            ConsumerRecords<String, Company> records = consumer.poll(Duration.ofSeconds(1));
            if (records.isEmpty()) {
                continue;
            }
            for (TopicPartition topicPartition : records.partitions()) {
                List<ConsumerRecord<String, Company>> recordList = records.records(topicPartition);
                // 最后一条消息的偏移量
                long consumedOffset = recordList.get(recordList.size() - 1).offset();
                System.out.println("consumed offset: " + consumedOffset);
                for (ConsumerRecord record : recordList) {
                    // 消费位移写入局部变量
                    metadataMap.put(topicPartition, new OffsetAndMetadata(record.offset() + 1));
                    System.out.println("topic: " + record.topic() + ",partition: " + record.partition() + ",offset: " + record.offset());
                }
                // 按照分区的维度提交消费位移
                consumer.commitAsync(Collections.singletonMap(topicPartition, new OffsetAndMetadata(consumedOffset + 1)), (Map<TopicPartition, OffsetAndMetadata> offsets, Exception exception) -> {
                            if (exception != null) {
                                exception.printStackTrace();
                            } else {
                                System.out.println("callback: " + offsets);
                            }
                        }
                );
                // Get the offset of the <i>next record</i> that will be fetched
                System.out.println("position: " + consumer.position(topicPartition));
                // Get the last committed offsets for the given partitions
                System.out.println("committed: " + consumer.committed(Collections.singleton(topicPartition)));
            }
        }
    }
}
