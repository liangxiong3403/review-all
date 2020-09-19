package org.liangxiong.review.client.vo;

import lombok.Getter;
import lombok.ToString;
import org.liangxiong.review.client.constants.ResultStatus;

/**
 * @author liangxiong
 * @version 1.0.0
 * @date 2020-09-08 19:55
 * @description 响应实体类
 **/
@Getter
@ToString
public class Result<T> {

    /**
     * 状态码
     */
    private Integer code;

    /**
     * 提示信息
     */
    private String message;

    /**
     * 返回参数
     */
    private T data;

    public Result(ResultStatus resultStatus, T data) {
        this.code = resultStatus.getCode();
        this.message = resultStatus.getMessage();
        this.data = data;
    }

    public static Result<Void> success() {
        return new Result<Void>(ResultStatus.SUCCESS, null);
    }

    public static <T> Result<T> success(T data) {
        return new Result<T>(ResultStatus.SUCCESS, data);
    }

    public static <T> Result<T> success(ResultStatus resultStatus, T data) {
        if (resultStatus == null) {
            success(data);
        }
        return new Result<T>(resultStatus, data);
    }

    public static Result<Void> failure() {
        return new Result<Void>(ResultStatus.INTERNAL_SERVER_ERROR, null);
    }

    public static <T> Result<T> failure(ResultStatus resultStatus) {
        return failure(ResultStatus.INTERNAL_SERVER_ERROR, null);
    }

    public static <T> Result<T> failure(ResultStatus resultStatus, T data) {
        if (resultStatus == null) {
            return new Result<T>(ResultStatus.INTERNAL_SERVER_ERROR, data);
        }
        return new Result<T>(resultStatus, data);
    }
}
