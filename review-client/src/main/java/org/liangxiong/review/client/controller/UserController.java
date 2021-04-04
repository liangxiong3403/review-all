package org.liangxiong.review.client.controller;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import cn.afterturn.easypoi.pdf.entity.PdfExportParams;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Workbook;
import org.liangxiong.common.enhance.ChinesePdfExportUtil;
import org.liangxiong.common.handler.CustomerWriteHandler;
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
@Slf4j
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
        EasyExcel.write(outputStream, User.class).sheet("模板")
                .registerWriteHandler(new CustomerWriteHandler()).doWrite(data);
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

    /**
     * notice:PDF内容宽度或者单元格高度设置不当会导致内容缺失
     *
     * @param response
     */
    @GetMapping("/exportPdfByEasyPoi")
    public void exportPdfByEasyPoi(HttpServletResponse response) {
        response.setContentType("application/pdf");
        response.setCharacterEncoding("utf-8");
        try (ServletOutputStream outputStream = response.getOutputStream()) {
            String fileName = URLEncoder.encode("用户数据", "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".pdf");
            List<User> data = list();
            PdfExportParams pdfExportParams = new PdfExportParams();
            ChinesePdfExportUtil.exportPdf(pdfExportParams, User.class, data, outputStream);
        } catch (IOException e) {
            log.error("export pdf error: {}", e.getMessage());
        }
    }

    @PostMapping("/format")
    public String format(@RequestBody UserDTO dto) {
        String pattern = "yyyy-MM-dd HH:mm:ss";
        return String.join("-",
                DateUtil.beginOfDay(dto.getStartTime()).toString(pattern),
                DateUtil.endOfDay(dto.getEndTime()).toString(pattern));
    }
}
