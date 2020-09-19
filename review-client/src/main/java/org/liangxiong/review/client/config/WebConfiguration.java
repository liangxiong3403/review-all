package org.liangxiong.review.client.config;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.alibaba.fastjson.util.IOUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
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
     * JSON序列化配置
     *
     * @param converters 消息转换器
     */
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        FastJsonHttpMessageConverter fastConverter = new FastJsonHttpMessageConverter();
        FastJsonConfig config = new FastJsonConfig();
        // SerializerFeature.DisableCircularReferenceDetect禁用循环引用检测
        config.setSerializerFeatures(SerializerFeature.PrettyFormat, SerializerFeature.WriteMapNullValue,
                SerializerFeature.WriteNullStringAsEmpty, SerializerFeature.WriteNullBooleanAsFalse, SerializerFeature.WriteNullListAsEmpty, SerializerFeature.DisableCircularReferenceDetect);
        fastConverter.setFastJsonConfig(config);
        fastConverter.setDefaultCharset(IOUtils.UTF8);
        // 添加到第一个位置
        converters.add(0, fastConverter);
    }

}
