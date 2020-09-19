package org.liangxiong.message.serializer;

import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;
import org.apache.kafka.common.serialization.Serializer;
import org.liangxiong.message.entity.Company;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

/**
 * @author liangxiong
 * @version 1.0.0
 * @date 2020-07-31 21:31
 * @description 自定义实现针对Company实体的序列化
 **/
public class CompanySerializer implements Serializer<Company> {

    @Override
    public byte[] serialize(String topic, Company data) {
        if (data == null) {
            return null;
        }
        Schema schema = RuntimeSchema.getSchema(data.getClass());
        LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
        byte[] protoStuff;
        try {
            protoStuff = ProtostuffIOUtil.toByteArray(data, schema, buffer);
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        } finally {
            buffer.clear();
        }
        return protoStuff;
    }

    private byte[] old(Company data) {
        byte[] name, address, rank;
        try {
            if (data.getName() != null) {
                name = data.getName().getBytes("UTF-8");
            } else {
                name = new byte[0];
            }
            if (data.getAddress() != null) {
                address = data.getAddress().getBytes("UTF-8");
            } else {
                address = new byte[0];
            }
            if (data.getRank() != null) {
                rank = new byte[]{data.getRank().byteValue()};
            } else {
                rank = new byte[0];
            }
            ByteBuffer buffer = ByteBuffer.allocate(4 + 4 + 4 + name.length + address.length + rank.length);
            buffer.putInt(name.length);
            buffer.put(name);
            buffer.putInt(address.length);
            buffer.put(address);
            buffer.putInt(rank.length);
            buffer.put(rank);
            return buffer.array();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return new byte[0];
    }
}
