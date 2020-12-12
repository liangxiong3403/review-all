package org.liangxiong.message.constants;

/**
 * @author liangxiong
 * @version 1.0.0
 * @date 2020-07-31 15:44
 * @description kafka
 **/
public class KafkaConstant {

    /**
     * bootstrap server
     */
    public static final String BOOTSTRAP_SERVER = "192.168.199.2:9092,192.168.199.3:9092,192.168.199.4:9092";
    /**
     * topic name
     */
    public static final String TOPIC = "topic-demo";
    /**
     * group id
     */
    public static final String GROUP_ID = "group-demo";
    /**
     * 过期时间间隔
     */
    public static final long EXPIRE_INTERVAL = 10 * 1000;
}
