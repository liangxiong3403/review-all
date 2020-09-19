package org.liangxiong.review.client.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.springframework.http.HttpStatus;

/**
 * @author liangxiong
 * @version 1.0.0
 * @date 2020-09-08 19:50
 * @description 响应状态码枚举类
 **/
@AllArgsConstructor
@Getter
@ToString
public enum ResultStatus {

    /**
     * 请求处理成功
     */
    SUCCESS(HttpStatus.OK, 200, "OK"),
    /**
     * 客户端请求错误
     */
    BAD_REQUEST(HttpStatus.BAD_REQUEST, 400, "Bad Request"),
    /**
     * 服务器内部错误
     */
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 500, "Internal Server Error");

    /**
     * http状态码
     */
    private HttpStatus httpStatus;

    /**
     * 业务状态码
     */
    private Integer code;

    /**
     * 提示信息
     */
    private String message;

}
