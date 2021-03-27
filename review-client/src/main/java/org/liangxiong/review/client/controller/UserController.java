package org.liangxiong.review.client.controller;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.poi.ss.usermodel.Workbook;
import org.liangxiong.review.client.dto.UserDTO;
import org.liangxiong.review.client.entity.User;
import org.liangxiong.review.client.listener.UploadUserListener;
import org.liangxiong.review.client.service.IUserService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/**
 * @author liangxiong
 * @version 1.0.0
 * @date 2021-03-19 21:37
 * @description
 **/
@RequestMapping("/user")
@RestController
public class UserController {

    @Resource
    private IUserService userService;

    @GetMapping("/list")
    public List<User> list() {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.lt("ID", ThreadLocalRandom.current().nextInt(0, 100));
        return userService.list(wrapper);
    }

    @PostMapping("/add")
    public Boolean save(@RequestBody User user) {
        return userService.save(user);
    }

    @GetMapping("/getOne")
    public User getOne(@RequestParam Long id) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("ID", id);
        return userService.getOne(wrapper);
    }

    @DeleteMapping("/delete")
    public Boolean delete(@RequestParam Long id) {
        User user = User.builder().build();
        user.setId(id);
        return userService.deleteByIdWithFill(user) == 1;
    }

    @DeleteMapping("/batchDelete")
    public Boolean batchDelete(@RequestBody List<Long> ids) {
        return userService.batchDeleteWithFill(User.builder().ids(ids).build()) > 0;
    }

    @PutMapping("/update")
    public Boolean delete(@RequestBody User user) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("ID", user.getId());
        return userService.update(user, wrapper);
    }

    @GetMapping("/exportByEasyPoi")
    public void exportByEasyPoi(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("用户数据表格", "UTF-8").replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xls");
        List<User> data = list();
        Workbook workbook = ExcelExportUtil.exportExcel(new ExportParams(), User.class, data);
        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        outputStream.close();
        workbook.close();
    }

    @GetMapping("/exportByEasyExcel")
    public void exportByEasyExcel(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("用户数据表格", "UTF-8").replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xls");
        List<User> data = list();
        ServletOutputStream outputStream = response.getOutputStream();
        EasyExcel.write(outputStream, User.class).sheet("模板").doWrite(data);
    }

    @PostMapping("/uploadByEasyPoi")
    @ResponseBody
    public String uploadByEasyPoi(@RequestParam("file") MultipartFile file) throws Exception {
        ImportParams importParams = new ImportParams();
        importParams.setHeadRows(1);
        List<UserDTO> dtos = ExcelImportUtil.importExcel(file.getInputStream(), UserDTO.class, importParams);
        if (CollectionUtil.isNotEmpty(dtos)) {
            List<User> users = dtos.stream().filter(e -> StrUtil.isNotBlank(e.getName())).map(e -> {
                User user = new User();
                BeanUtil.copyProperties(e, user);
                return user;
            }).collect(Collectors.toList());
            userService.saveBatch(users);
        }
        return "success";
    }

    @PostMapping("/uploadByEasyExcel")
    @ResponseBody
    public String uploadByEasyExcel(@RequestParam("file") MultipartFile file) throws IOException {
        EasyExcel.read(file.getInputStream(), UserDTO.class, new UploadUserListener(userService)).sheet(0).doRead();
        return "success";
    }
}
