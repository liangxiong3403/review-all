package org.liangxiong.message.kafka;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.liangxiong.message.constants.KafkaConstant;
import org.liangxiong.message.entity.Company;
import org.liangxiong.message.interceptor.ProducerInterceptorPrefix;
import org.liangxiong.message.interceptor.ProducerInterceptorPrefixPlus;
import org.liangxiong.message.serializer.CompanySerializer;

import java.time.Duration;
import java.util.Properties;

/**
 * @author liangxiong
 * @version 1.0.0
 * @date 2020-07-31 15:41
 * @description 消息生产者
 **/
public class MessageProducer {

    private static Properties initConfig() {
        Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, KafkaConstant.BOOTSTRAP_SERVER);
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, CompanySerializer.class.getName());
        properties.put(ProducerConfig.CLIENT_ID_CONFIG, "producer.client.id.demo");
        properties.put(ProducerConfig.INTERCEPTOR_CLASSES_CONFIG, ProducerInterceptorPrefix.class.getName() + "," + ProducerInterceptorPrefixPlus.class.getName());
        properties.put(ProducerConfig.ACKS_CONFIG, "1");
        return properties;
    }

    public static void main(String[] args) {
        // 构造生产者实例
        KafkaProducer<String, Company> producer = new KafkaProducer(initConfig());
        // 构造消息实例
        for (int i = 0; i < 10; i++) {
            producer.send(new ProducerRecord<>(KafkaConstant.TOPIC, new Company("kafka", "china", i)));
        }
        // 超时关闭的异常:org.apache.kafka.common.KafkaException: Producer is closed forcefully.
        producer.close(Duration.ofMillis(500));
    }
}
