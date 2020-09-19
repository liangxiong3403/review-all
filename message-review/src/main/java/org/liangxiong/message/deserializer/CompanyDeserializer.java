package org.liangxiong.message.deserializer;

import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;
import org.liangxiong.message.entity.Company;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

/**
 * @author liangxiong
 * @version 1.0.0
 * @date 2020-08-01 20:58
 * @description 自定义反序列化
 **/
public class CompanyDeserializer implements Deserializer<Company> {

    @Override
    public Company deserialize(String topic, byte[] data) {
        if (data == null) {
            return null;
        }
        Schema schema = RuntimeSchema.getSchema(Company.class);
        Company target = new Company();
        ProtostuffIOUtil.mergeFrom(data, target, schema);
        return target;
    }

    private Company old(byte[] data) {
        if (data.length < 8) {
            throw new SerializationException("size of data received by CompanyDeserializer is shorter than expected!");
        }
        ByteBuffer buffer = ByteBuffer.wrap(data);
        int nameLen, addressLen, rankLen;
        String name, address;
        nameLen = buffer.getInt();
        byte[] nameBytes = new byte[nameLen];
        buffer.get(nameBytes);
        addressLen = buffer.getInt();
        byte[] addressBytes = new byte[addressLen];
        buffer.get(addressBytes);
        rankLen = buffer.getInt();
        byte[] rankBytes = new byte[rankLen];
        buffer.get(rankBytes);
        try {
            name = new String(nameBytes, "UTF-8");
            address = new String(addressBytes, "UTF-8");
            return new Company(name, address, (int) rankBytes[0]);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }
}
