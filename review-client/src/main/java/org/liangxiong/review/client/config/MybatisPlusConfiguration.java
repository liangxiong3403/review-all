package org.liangxiong.review.client.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.autoconfigure.ConfigurationCustomizer;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.injector.DefaultSqlInjector;
import com.baomidou.mybatisplus.extension.injector.methods.LogicDeleteByIdWithFill;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import com.baomidou.mybatisplus.extension.plugins.inner.BlockAttackInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.TenantLineInnerInterceptor;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author liangxiong
 * @version 1.0.0
 * @date 2020-12-09 21:24
 * @description mybatis-plus配置插入填充:创建时间/修改时间/创建人
 **/
@Slf4j
@Configuration
public class MybatisPlusConfiguration implements MetaObjectHandler {

    /**
     * 插入时填充数据
     *
     * @param metaObject
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        if (log.isInfoEnabled()) {
            log.info("start insert fill ....");
        }
        // 方式1
        //setFieldValByName("createdTime", new Date(), metaObject);
        // 方式2
        this.strictInsertFill(metaObject, "createdTime", LocalDateTime::now, LocalDateTime.class);
        this.strictInsertFill(metaObject, "updatedTime", LocalDateTime::now, LocalDateTime.class);
        this.strictInsertFill(metaObject, "deleted", () -> 0, Integer.class);
        // 方式1
        //setFieldValByName("createdBy", "Administrator", metaObject);
        // 方式2
        this.strictInsertFill(metaObject, "createdBy", () -> "Administrator", String.class);
    }

    /**
     * 修改时填充数据
     *
     * @param metaObject
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        if (log.isInfoEnabled()) {
            log.info("start update fill ....");
        }
        // 方式1
        //setFieldValByName("updatedTime", new Date(), metaObject);
        // 方式2
        this.strictUpdateFill(metaObject, "updatedTime", LocalDateTime::now, LocalDateTime.class);
        this.strictUpdateFill(metaObject, "updatedBy", () -> "Administrator", String.class);
    }

    /**
     * 注解@JsonValue和代码配置二选一
     * 枚举值写入数据库;前端传递中文,数据库写入整型.比如:男->1;女->0;未知->-1
     *
     * @return
     */
    @Bean
    public Jackson2ObjectMapperBuilderCustomizer customizer() {
        return builder -> builder.featuresToEnable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING);
    }

    /**
     * 新的分页插件,一缓和二缓遵循mybatis的规则,需要设置 MybatisConfiguration#useDeprecatedExecutor=false避免缓存出现问题(该属性会在旧插件移除后一同移除)
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        /*
         *如果用了分页插件注意先 add TenantLineInnerInterceptor再add PaginationInnerInterceptor
         *用了分页插件必须设置 MybatisConfiguration#useDeprecatedExecutor=false
         */
        interceptor.addInnerInterceptor(new TenantLineInnerInterceptor(new TenantLineHandler() {
            @Override
            public Expression getTenantId() {
                return new LongValue(1);
            }

            @Override
            public boolean ignoreTable(String tableName) {
                return !"user".equalsIgnoreCase(tableName);
            }
        }));
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        // 防止全部更新和删除
        interceptor.addInnerInterceptor(new BlockAttackInnerInterceptor());
        return interceptor;
    }

    @Bean
    public ConfigurationCustomizer configurationCustomizer() {
        return configuration -> configuration.setUseDeprecatedExecutor(false);
    }

    @Bean
    public MySqlInjector sqlInjector() {
        return new MySqlInjector();
    }

    /**
     * 逻辑删除时更新修改时间
     */
    public static class MySqlInjector extends DefaultSqlInjector {

        @Override
        public List<AbstractMethod> getMethodList(Class<?> mapperClass) {
            List<AbstractMethod> methodList = super.getMethodList(mapperClass);
            methodList.add(new LogicDeleteByIdWithFill());
            return methodList;
        }
    }
}
