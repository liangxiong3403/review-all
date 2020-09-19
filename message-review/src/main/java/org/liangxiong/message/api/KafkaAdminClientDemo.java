package org.liangxiong.message.api;

import org.apache.kafka.clients.admin.*;
import org.apache.kafka.common.config.ConfigResource;
import org.liangxiong.message.constants.KafkaConstant;

import java.util.Collections;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ExecutionException;

/**
 * @author liangxiong
 * @version 1.0.0
 * @date 2020-08-23 16:16
 * @description 使用KafkaAdminClient
 **/
public class KafkaAdminClientDemo {

    private static Properties initConfig() {
        Properties properties = new Properties();
        properties.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, KafkaConstant.BOOTSTRAP_SERVER);
        properties.put(AdminClientConfig.REQUEST_TIMEOUT_MS_CONFIG, 30000);
        return properties;
    }

    /**
     * 创建主题
     *
     * @param client    客户端
     * @param topicName 主题名称
     */
    private static void createTopic(AdminClient client, String topicName) {
        // 创建主题
        NewTopic topic = new NewTopic(topicName, 4, (short) 1);
        topic.configs(Collections.singletonMap("cleanup.policy", "compact"));
        CreateTopicsResult createTopicsResult = client.createTopics(Collections.singleton(topic));
        try {
            // 同步调用
            createTopicsResult.all().get();
            // 便利结果
            createTopicsResult.values().forEach((key, value) -> System.out.println("key: " + key + ",value: " + value));
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } finally {
            if (client != null) {
                client.close();
            }
        }
    }

    /**
     * 删除主题
     *
     * @param client    客户端
     * @param topicName 主题名称
     */
    private static void deleteTopic(AdminClient client, String topicName) {
        DeleteTopicsResult deleteTopicsResult = client.deleteTopics(Collections.singleton(topicName));
        try {
            deleteTopicsResult.all().get();
            deleteTopicsResult.values().forEach((key, value) -> System.out.println("key: " + key + ",value: " + value));
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } finally {
            if (client != null) {
                client.close();
            }
        }
    }

    /**
     * 查看主题详细信息
     *
     * @param client    客户端
     * @param topicName 主题名称
     */
    private static void describeTopics(AdminClient client, String topicName) {
        DescribeTopicsResult describeTopicsResult = client.describeTopics(Collections.singleton(topicName));
        try {
            describeTopicsResult.all().get();
            describeTopicsResult.values().forEach((key, value) -> System.out.println("key: " + key + ",value: " + value));
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } finally {
            if (client != null) {
                client.close();
            }
        }
    }

    /**
     * 查看主题配置信息
     *
     * @param client    客户端
     * @param topicName 主题名称
     */
    private static void describeTopicConfigs(AdminClient client, String topicName) {
        DescribeConfigsResult describeConfigsResult = client.describeConfigs(Collections.singleton(new ConfigResource(ConfigResource.Type.TOPIC, topicName)));
        try {
            describeConfigsResult.all().get();
            describeConfigsResult.values().forEach((key, value) -> System.out.println("key: " + key + ",value: " + value));
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } finally {
            if (client != null) {
                client.close();
            }
        }
    }

    /**
     * 修改主题配置信息
     *
     * @param client    客户端
     * @param topicName 主题名称
     */
    private static void alterTopicConfigs(AdminClient client, String topicName) {
        AlterConfigOp configOp = new AlterConfigOp(new ConfigEntry("max.message.bytes", "20000"), AlterConfigOp.OpType.SET);
        Set<AlterConfigOp> opSet = Collections.singleton(configOp);
        AlterConfigsResult alterConfigsResult = client.incrementalAlterConfigs(Collections.singletonMap(new ConfigResource(ConfigResource.Type.TOPIC, topicName), opSet));
        try {
            alterConfigsResult.all().get();
            alterConfigsResult.values().forEach((key, value) -> System.out.println("key: " + key + ",value: " + value));
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } finally {
            if (client != null) {
                client.close();
            }
        }
    }

    /**
     * 增加主题分区数量
     *
     * @param client    客户端
     * @param topicName 主题名称
     */
    private static void increaseTopicPartition(AdminClient client, String topicName) {
        CreatePartitionsResult createPartitionsResult = client.createPartitions(Collections.singletonMap(topicName, NewPartitions.increaseTo(5)));
        try {
            createPartitionsResult.all().get();
            createPartitionsResult.values().forEach((key, value) -> System.out.println("key: " + key + ",value: " + value));
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } finally {
            if (client != null) {
                client.close();
            }
        }
    }

    public static void main(String[] args) {
        // 构造KafkaAdminClient客户端
        AdminClient client = AdminClient.create(initConfig());
        String topicName = "topic-admin";
        describeTopics(client, topicName);
    }
}
