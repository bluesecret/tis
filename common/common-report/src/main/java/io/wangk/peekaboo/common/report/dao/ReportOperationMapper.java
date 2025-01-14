package io.wangk.peekaboo.common.report.dao;

import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

/**
 * 统计表单运行时数据操作访问接口。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Mapper
public interface ReportOperationMapper {

    /**
     * 直接执行纯动态SQL。
     * @param sql 在外部拼接好的动态SQL。
     * @return 查询结果集。
     */
    @Select("${sql}")
    List<Map<String, Object>> execQuery(@Param("sql") String sql);
}
