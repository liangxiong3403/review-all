package org.liangxiong.review.server.config;

import com.alibaba.fastjson.support.spring.GenericFastJsonRedisSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * @author liangxiong
 * @date 2019-12-04 20:05
 * @description Web相关配置
 **/
@Configuration
public class WebConfiguration implements WebMvcConfigurer {

    /**
     * 跨域请求配置
     *
     * @return
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // 那些地址需要跨域处理
        registry.addMapping("/**")
            // 那些origin需要跨域处理(域:协议+主机IP+端口号)
            .allowedOrigins("*")
            // 允许那些方法进行跨域访问
            .allowedMethods("PUT", "DELETE", "GET", "POST", "OPTIONS", "HEAD")
            // 允许哪些请求头进行跨域访问
            .allowedHeaders("*")
            // 是否支持用户凭证
            .allowCredentials(false)
            // 客户端缓存前一个响应时间
            .maxAge(3600);
    }

    /**
     * 控制redis序列化策略
     *
     * @param redisConnectionFactory
     * @return
     */
    @Bean
    public RedisTemplate redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate redisTemplate = new RedisTemplate();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        GenericFastJsonRedisSerializer fastJsonRedisSerializer = new GenericFastJsonRedisSerializer();
        // 设置默认的Serialize,包含 keySerializer & valueSerializer
        redisTemplate.setDefaultSerializer(fastJsonRedisSerializer);
        return redisTemplate;
    }

    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.stream().filter(c -> c instanceof MappingJackson2HttpMessageConverter).forEach(cvt -> {
            MappingJackson2HttpMessageConverter converter = (MappingJackson2HttpMessageConverter) cvt;
            ObjectMapper objectMapper = converter.getObjectMapper();
            SimpleModule simpleModule = new SimpleModule();
            // handle type of Long error for table primary key
            simpleModule.addSerializer(Long.class, ToStringSerializer.instance);
            objectMapper.registerModule(simpleModule);
        });
    }
}
