package org.liangxiong.message.interceptor;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.kafka.clients.consumer.ConsumerInterceptor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.liangxiong.message.constants.KafkaConstant;
import org.liangxiong.message.entity.Company;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author liangxiong
 * @version 1.0.0
 * @date 2020-08-09 21:57
 * @description 自定义消费组拦截器实现过期消息处理
 **/
public class ConsumerInterceptorTtl implements ConsumerInterceptor<String, Company> {

    /**
     * 在消费组poll方法返回结果前被调用
     * This is called just before the records are returned by poll
     *
     * @param records 消息结果集
     * @return
     */
    @Override
    public ConsumerRecords<String, Company> onConsume(ConsumerRecords<String, Company> records) {
        // 当前系统时间
        long now = System.currentTimeMillis();
        Map<TopicPartition, List<ConsumerRecord<String, Company>>> newRecords = new HashMap<>(16);
        for (TopicPartition partition : records.partitions()) {
            // 获取每个分区的消息
            List<ConsumerRecord<String, Company>> recordList = records.records(partition);
            // 过滤没有过期地消息
            List<ConsumerRecord<String, Company>> newTopicRecords = recordList.stream().filter(e -> (now - e.timestamp()) < KafkaConstant.EXPIRE_INTERVAL).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(newTopicRecords)) {
                newRecords.put(partition, newTopicRecords);
            }
        }
        return new ConsumerRecords<>(newRecords);
    }

    /**
     * 在消费组提交消费位移后被调用
     *
     * @param offsets 消费位移
     */
    @Override
    public void onCommit(Map<TopicPartition, OffsetAndMetadata> offsets) {
        offsets.forEach((partition, offset) -> {
            System.out.println("consumer interceptor receive: " + partition + " : " + offset.offset());
        });
    }

    @Override
    public void close() {

    }

    @Override
    public void configure(Map<String, ?> configs) {

    }
}
