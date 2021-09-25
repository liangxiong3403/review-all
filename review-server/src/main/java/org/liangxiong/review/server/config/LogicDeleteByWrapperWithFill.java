package org.liangxiong.review.server.config;

import com.baomidou.mybatisplus.core.enums.SqlMethod;
import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.metadata.TableFieldInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;

import java.util.List;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

/**
 * @Author liangxiong
 * @Project
 * @Description 解决{@link com.baomidou.mybatisplus.core.mapper.BaseMapper#delete(com.baomidou.mybatisplus.core.conditions.Wrapper)}没有自动填充的功能
 * @Date 2021-09-25
 */
@SuppressWarnings("serial")
@Slf4j
public class LogicDeleteByWrapperWithFill extends AbstractMethod {

    @Override
    public MappedStatement injectMappedStatement(Class<?> mapperClass, Class<?> modelClass, TableInfo tableInfo) {
        String sql;
        SqlMethod sqlMethod = SqlMethod.LOGIC_DELETE;
        if (tableInfo.isWithLogicDelete()) {
            List<TableFieldInfo> fieldInfos = tableInfo.getFieldList().stream()
                    .filter(TableFieldInfo::isWithUpdateFill)
                    .collect(toList());
            if (CollectionUtils.isNotEmpty(fieldInfos)) {
                String sqlSet = "SET " + fieldInfos.stream().map(i -> i.getSqlSet(EMPTY)).collect(joining(EMPTY))
                        + tableInfo.getLogicDeleteSql(false, false);
                sql = String.format(sqlMethod.getSql(), tableInfo.getTableName(), sqlSet,
                        sqlWhereEntityWrapper(true, tableInfo),
                        sqlComment());
                SqlSource sqlSource = languageDriver.createSqlSource(configuration, sql, modelClass);
                return addUpdateMappedStatement(mapperClass, modelClass, getMethod(sqlMethod), sqlSource);
            }
        }
        log.warn("table: {} has no logic delete field", modelClass.getSimpleName());
        sqlMethod = SqlMethod.DELETE;
        sql = String.format(sqlMethod.getSql(), tableInfo.getTableName(),
                sqlWhereEntityWrapper(true, tableInfo),
                sqlComment());
        SqlSource sqlSource = languageDriver.createSqlSource(configuration, sql, modelClass);
        return this.addDeleteMappedStatement(mapperClass, getMethod(sqlMethod), sqlSource);
    }
}
