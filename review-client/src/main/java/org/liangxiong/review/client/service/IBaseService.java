package org.liangxiong.review.client.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.liangxiong.review.client.entity.BaseEntity;

/**
 * @author liangxiong
 * @version 1.0.0
 * @date 2021-03-27 08:03
 * @description
 **/
public interface IBaseService<T extends BaseEntity> extends IService<T> {

    Integer deleteByIdWithFill(T t);

    Integer batchDeleteWithFill(T t);
}
