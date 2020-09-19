package org.liangxiong.message.interceptor;

import org.apache.kafka.clients.producer.ProducerInterceptor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.liangxiong.message.entity.Company;

import java.util.Map;

/**
 * @author liangxiong
 * @version 1.0.0
 * @date 2020-08-01 14:20
 * @description 生产者拦截器自定义实现
 **/
public class ProducerInterceptorPrefix implements ProducerInterceptor<String, Company> {

    private volatile int success = 0;

    private volatile int failure = 0;

    @Override
    public ProducerRecord<String, Company> onSend(ProducerRecord<String, Company> record) {
        // 增加前缀
        Company company = record.value();
        String updateValue = "prefix1-" + company.getName();
        company.setName(updateValue);
        return new ProducerRecord<String, Company>(record.topic(), record.partition(), record.timestamp(), record.key(), company, record.headers());
    }

    /**
     * 方法调用优先于callable方法
     *
     * @param metadata
     * @param exception
     */
    @Override
    public void onAcknowledgement(RecordMetadata metadata, Exception exception) {
        if (exception != null) {
            failure++;
        } else {
            success++;
        }
    }

    @Override
    public void close() {
        System.out.println("消息发送成功率: " + (double) success / (success + failure));
    }

    @Override
    public void configure(Map<String, ?> configs) {

    }
}
