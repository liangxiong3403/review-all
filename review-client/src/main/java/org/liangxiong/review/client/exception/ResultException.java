package org.liangxiong.review.client.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.liangxiong.review.client.constants.ResultStatus;

/**
 * @author liangxiong
 * @version 1.0.0
 * @date 2020-09-08 20:26
 * @description 自定义异常类型, 测试全局异常处理
 **/
@Getter
@Setter
@AllArgsConstructor
public class ResultException extends RuntimeException {

    /**
     * 提示信息
     */
    private String message;

    /**
     * 返回状态码
     */
    private ResultStatus resultStatus;

}
