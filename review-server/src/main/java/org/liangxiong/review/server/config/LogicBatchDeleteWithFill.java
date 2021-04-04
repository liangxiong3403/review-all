package org.liangxiong.review.server.config;

import com.baomidou.mybatisplus.core.enums.SqlMethod;
import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.metadata.TableFieldInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.sql.SqlScriptUtils;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;

import java.util.List;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

/**
 * @author liangxiong
 * @version 1.0.0
 * @date 2021-03-20 10:13
 * @description 批量逻辑删除的时候填充字段:填充修改时间和修改人等填充字段
 **/
public class LogicBatchDeleteWithFill extends AbstractMethod {

    /**
     * mapper 对应的方法名
     */
    private static final String MAPPER_METHOD = "batchDeleteWithFill";

    private static final String PARAM_IDS = "ids";

    @Override
    public MappedStatement injectMappedStatement(Class<?> mapperClass, Class<?> modelClass, TableInfo tableInfo) {
        String sql;
        SqlMethod sqlMethod = SqlMethod.LOGIC_DELETE_BATCH_BY_IDS;
        if (tableInfo.isWithLogicDelete()) {
            List<TableFieldInfo> fieldInfos = tableInfo.getFieldList().stream()
                    .filter(TableFieldInfo::isWithUpdateFill)
                    .collect(toList());
            if (CollectionUtils.isNotEmpty(fieldInfos)) {
                String sqlSet = "SET " + fieldInfos.stream().map(i -> i.getSqlSet(EMPTY)).collect(joining(EMPTY))
                        + tableInfo.getLogicDeleteSql(false, false);
                sql = String.format(sqlMethod.getSql(), tableInfo.getTableName(), sqlSet,
                        tableInfo.getKeyColumn(),
                        SqlScriptUtils.convertForeach("#{item}", PARAM_IDS, null, "item", COMMA),
                        tableInfo.getLogicDeleteSql(true, true));
            } else {
                sql = String.format(sqlMethod.getSql(), tableInfo.getTableName(), sqlLogicSet(tableInfo),
                        tableInfo.getKeyColumn(),
                        SqlScriptUtils.convertForeach("#{item}", PARAM_IDS, null, "item", COMMA),
                        tableInfo.getLogicDeleteSql(true, true));
            }
        } else {
            sqlMethod = SqlMethod.DELETE_BATCH_BY_IDS;
            sql = String.format(sqlMethod.getSql(), tableInfo.getTableName(), tableInfo.getKeyColumn(),
                    SqlScriptUtils.convertForeach("#{item}", PARAM_IDS, null, "item", COMMA));
        }
        /*
         * 参数类型必须使用modelClass,不然修改填充字段无法执行
         */
        SqlSource sqlSource = languageDriver.createSqlSource(configuration, sql, modelClass);
        return addUpdateMappedStatement(mapperClass, modelClass, getMethod(sqlMethod), sqlSource);
    }

    @Override
    public String getMethod(SqlMethod sqlMethod) {
        // 自定义 mapper 方法名
        return MAPPER_METHOD;
    }
}
