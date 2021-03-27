package org.liangxiong.review.client.converter;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.CellData;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.property.ExcelContentProperty;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author liangxiong
 * @version 1.0.0
 * @date 2021-03-26 22:10
 * @description 时间格式化,用于EasyExcel导出LocalDateTime类型数据
 **/
public class LocalDateTimeConverter implements Converter<LocalDateTime> {

    private static final String TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

    @Override
    public Class<LocalDateTime> supportJavaTypeKey() {
        return LocalDateTime.class;
    }

    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return CellDataTypeEnum.STRING;
    }

    @Override
    public LocalDateTime convertToJavaData(CellData cellData, ExcelContentProperty contentProperty,
                                       GlobalConfiguration globalConfiguration) {
        return LocalDateTime.parse(cellData.getStringValue(), DateTimeFormatter.ofPattern(TIME_PATTERN));
    }

    @Override
    public CellData<String> convertToExcelData(LocalDateTime value, ExcelContentProperty contentProperty,
                                               GlobalConfiguration globalConfiguration) {
        return new CellData<>(value.format(DateTimeFormatter.ofPattern(TIME_PATTERN)));
    }
}