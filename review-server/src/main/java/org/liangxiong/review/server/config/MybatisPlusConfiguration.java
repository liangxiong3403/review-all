package org.liangxiong.review.server.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.context.annotation.Configuration;

import java.util.Date;

/**
 * @author liangxiong
 * @version 1.0.0
 * @date 2020-12-09 21:24
 * @description mybatis-plus配置插入填充:创建时间/修改时间/创建人
 **/
@Configuration
public class MybatisPlusConfiguration implements MetaObjectHandler {

    /**
     * 插入时填充数据
     *
     * @param metaObject
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        Object createTime = getFieldValByName("createTime", metaObject);
        Object createBy = getFieldValByName("createBy", metaObject);
        if (null == createTime) {
            setFieldValByName("createTime", new Date(), metaObject);
        }
        if (null == createBy) {
            setFieldValByName("createBy", "Administrator", metaObject);
        }
    }

    /**
     * 修改时填充数据
     *
     * @param metaObject
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        Object updateTime = getFieldValByName("updateTime", metaObject);
        if (null == updateTime) {
            setFieldValByName("updateTime", new Date(), metaObject);
        }
    }
}
