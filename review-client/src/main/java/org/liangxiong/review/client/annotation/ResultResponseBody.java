package org.liangxiong.review.client.annotation;

import org.springframework.web.bind.annotation.ResponseBody;

import java.lang.annotation.*;

/**
 * @author liangxiong
 * @version 1.0.0
 * @date 2020-09-08 20:09
 * @description 处理返回值
 **/
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ResponseBody
public @interface ResultResponseBody {
}
