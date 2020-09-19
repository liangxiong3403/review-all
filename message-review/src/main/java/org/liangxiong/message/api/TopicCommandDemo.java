package org.liangxiong.message.api;

/**
 * @author liangxiong
 * @version 1.0.0
 * @date 2020-08-22 14:58
 * @description
 **/
public class TopicCommandDemo {

    private static void createTopic() {
        String[] options = new String[]{
                // broker地址
                "--bootstrap-server", "192.168.199.2:9092,192.168.199.3:9092,192.168.199.4:9092",
                // 创建topic命令
                "--create",
                // 分区数量
                "--partitions", "1",
                // 副本因子
                "--replication-factor", "1",
                // 主题名称
                "--topic", "topic-create-api"};
        kafka.admin.TopicCommand.main(options);
    }

    private static void describeTopic() {
        String[] options = new String[]{
                // broker地址
                "--bootstrap-server", "192.168.199.2:9092,192.168.199.3:9092,192.168.199.4:9092",
                // 查看topic命令
                "--describe",
                // 主题名称
                "--topic", "topic-create-api"};
        kafka.admin.TopicCommand.main(options);
    }

    public static void main(String[] args) {
        describeTopic();
    }
}
