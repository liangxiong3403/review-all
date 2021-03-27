package org.liangxiong.review.client.listener;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import lombok.extern.slf4j.Slf4j;
import org.liangxiong.review.client.dto.UserDTO;
import org.liangxiong.review.client.entity.User;
import org.liangxiong.review.client.service.IUserService;

import java.util.ArrayList;
import java.util.List;

/**
 * @author liangxiong
 * @version 1.0.0
 * @date 2021-03-27 07:50
 * @description
 **/
@Slf4j
public class UploadUserListener extends AnalysisEventListener<UserDTO> {

    private static final int BATCH_COUNT = 5;

    private List<User> data = new ArrayList<>();

    private IUserService userService;

    public UploadUserListener(IUserService userService) {
        this.userService = userService;
    }

    @Override
    public void onException(Exception exception, AnalysisContext context) throws Exception {
        log.error("文件上传失败: {}", exception.getMessage());
    }

    @Override
    public void invoke(UserDTO dto, AnalysisContext context) {
        if (log.isInfoEnabled()) {
            log.info("解析到一条数据:{}", dto);
        }
        User user = new User();
        BeanUtil.copyProperties(dto, user);
        data.add(user);
        // 达到BATCH_COUNT了，需要去存储一次数据库，防止数据几万条数据在内存，容易OOM
        if (data.size() >= BATCH_COUNT) {
            saveData();
            // 存储完成清理 list
            data.clear();
        }
    }

    private void saveData() {
        userService.saveBatch(data);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        if (log.isInfoEnabled()) {
            log.info("所有数据解析完成！");
        }
        saveData();
        data.clear();
    }
}
