package io.wangk.peekaboo.common.online.dao;

import io.wangk.peekaboo.common.online.dto.OnlineFilterDto;
import io.wangk.peekaboo.common.online.object.ColumnData;
import io.wangk.peekaboo.common.online.object.JoinTableInfo;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

/**
 * 在线表单运行时数据操作访问接口。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Mapper
public interface OnlineOperationMapper {

    /**
     * 插入新数据。
     *
     * @param tableName       数据表名。
     * @param columnNames     字段名列表。
     * @param columnValueList 字段值列表。
     */
    @Insert("<script>"
            + "INSERT INTO ${tableName} (${columnNames}) VALUES "
            + "    <foreach collection=\"columnValueList\" item=\"columnValue\" separator=\",\" open=\"(\" close=\")\">"
            + "        #{columnValue} "
            + "    </foreach>"
            + "</script>")
    void insert(
            @Param("tableName") String tableName,
            @Param("columnNames") String columnNames,
            @Param("columnValueList") List<Object> columnValueList);

    /**
     * 更新表数据。
     *
     * @param tableName        数据表名。
     * @param updateColumnList 更新字段列表。
     * @param filterList       SQL过滤条件列表。
     * @param dataPermFilter   数据权限过滤字符串。
     * @return 更新行数。
     */
    @Update("<script>"
            + "UPDATE ${tableName} SET "
            + "    <foreach collection=\"updateColumnList\" item=\"columnData\" separator=\",\" >"
            + "        <if test=\"columnData.columnValue != null\">"
            + "            ${columnData.column.columnName} = #{columnData.columnValue} "
            + "        </if>"
            + "        <if test=\"columnData.columnValue == null\">"
            + "            ${columnData.column.columnName} = NULL "
            + "        </if>"
            + "    </foreach>"
            + "<where>"
            + "    <if test=\"filterList != null\">"
            + "        <foreach collection=\"filterList\" item=\"filter\">"
            + "            <if test=\"filter.filterType == 1\">"
            + "                AND ${filter.tableName}.${filter.columnName} = #{filter.columnValue} "
            + "            </if>"
            + "            <if test=\"filter.filterType == 4\">"
            + "                AND ${filter.tableName}.${filter.columnName} IN "
            + "                <foreach collection=\"filter.columnValueList\" item=\"columnValue\" separator=\",\" open=\"(\" close=\")\">"
            + "                    #{columnValue} "
            + "                </foreach>"
            + "            </if>"
            + "            <if test=\"filter.filterType == 6\">"
            + "                AND ${filter.tableName}.${filter.columnName} NOT IN "
            + "                <foreach collection=\"filter.columnValueList\" item=\"columnValue\" separator=\",\" open=\"(\" close=\")\">"
            + "                    #{columnValue} "
            + "                </foreach>"
            + "            </if>"
            + "        </foreach>"
            + "    </if>"
            + "    <if test=\"dataPermFilter != null and dataPermFilter != ''\">"
            + "        AND ${dataPermFilter} "
            + "    </if>"
            + "</where>"
            + "</script>")
    int update(
            @Param("tableName") String tableName,
            @Param("updateColumnList") List<ColumnData> updateColumnList,
            @Param("filterList") List<OnlineFilterDto> filterList,
            @Param("dataPermFilter") String dataPermFilter);

    /**
     * 删除指定数据。
     *
     * @param tableName      表名。
     * @param filterList     SQL过滤条件列表。
     * @param dataPermFilter 数据权限过滤字符串。
     * @return 删除行数。
     */
    @Delete("<script>"
            + "DELETE FROM ${tableName} "
            + "<where>"
            + "    <if test=\"filterList != null\">"
            + "        <foreach collection=\"filterList\" item=\"filter\">"
            + "            <if test=\"filter.filterType == 1\">"
            + "                AND ${filter.tableName}.${filter.columnName} = #{filter.columnValue} "
            + "            </if>"
            + "            <if test=\"filter.filterType == 4\">"
            + "                AND ${filter.tableName}.${filter.columnName} IN "
            + "                <foreach collection=\"filter.columnValueList\" item=\"columnValue\" separator=\",\" open=\"(\" close=\")\">"
            + "                    #{columnValue} "
            + "                </foreach>"
            + "            </if>"
            + "            <if test=\"filter.filterType == 6\">"
            + "                AND ${filter.tableName}.${filter.columnName} NOT IN "
            + "                <foreach collection=\"filter.columnValueList\" item=\"columnValue\" separator=\",\" open=\"(\" close=\")\">"
            + "                    #{columnValue} "
            + "                </foreach>"
            + "            </if>"
            + "        </foreach>"
            + "    </if>"
            + "    <if test=\"dataPermFilter != null and dataPermFilter != ''\">"
            + "        AND ${dataPermFilter}"
            + "    </if>"
            + "</where>"
            + "</script>")
    int delete(
            @Param("tableName") String tableName,
            @Param("filterList") List<OnlineFilterDto> filterList,
            @Param("dataPermFilter") String dataPermFilter);

    /**
     * 执行动态查询，并返回查询结果集。
     *
     * @param masterTableName 主表名称。
     * @param joinInfoList    关联表信息列表。
     * @param selectFields    返回字段列表，逗号分隔。
     * @param filterList      SQL过滤条件列表。
     * @param customFilterSql 自定义过滤SQL。
     * @param orderBy         排序字符串。
     * @return 查询结果集。
     */
    @Select("<script>"
            + "SELECT ${selectFields} FROM ${masterTableName} "
            + "<if test=\"joinInfoList != null\">"
            + "    <foreach collection=\"joinInfoList\" item=\"joinInfo\">"
            + "        <if test=\"joinInfo.leftJoin\">"
            + "            LEFT JOIN ${joinInfo.joinTableName} ON ${joinInfo.joinCondition}"
            + "        </if>"
            + "        <if test=\"!joinInfo.leftJoin\">"
            + "            INNER JOIN ${joinInfo.joinTableName} ON ${joinInfo.joinCondition}"
            + "        </if>"
            + "    </foreach>"
            + "</if>"
            + "<where>"
            + "    <if test=\"filterList != null\">"
            + "        <foreach collection=\"filterList\" item=\"filter\">"
            + "            <if test=\"filter.filterType == 1\">"
            + "                AND ${filter.tableName}.${filter.columnName} = #{filter.columnValue} "
            + "            </if>"
            + "            <if test=\"filter.filterType == 9\">"
            + "                AND ${filter.tableName}.${filter.columnName} &lt;&gt; #{filter.columnValue} "
            + "            </if>"
            + "            <if test=\"filter.filterType == 2\">"
            + "                <if test=\"filter.columnValueStart != null and filter.columnValueStart != ''\">"
            + "                    AND ${filter.tableName}.${filter.columnName} &gt;= #{filter.columnValueStart} "
            + "                </if>"
            + "                <if test=\"filter.columnValueEnd != null and filter.columnValueEnd != ''\">"
            + "                    AND ${filter.tableName}.${filter.columnName} &lt;= #{filter.columnValueEnd} "
            + "                </if>"
            + "            </if>"
            + "            <if test=\"filter.filterType == 3\">"
            + "                AND ${filter.tableName}.${filter.columnName} LIKE #{filter.columnValue} "
            + "            </if>"
            + "            <if test=\"filter.filterType == 4\">"
            + "                AND ${filter.tableName}.${filter.columnName} IN "
            + "                <foreach collection=\"filter.columnValueList\" item=\"columnValue\" separator=\",\" open=\"(\" close=\")\">"
            + "                    #{columnValue} "
            + "                </foreach>"
            + "            </if>"
            + "            <if test=\"filter.filterType == 5\">"
            + "                AND "
            + "                <foreach collection=\"filter.columnValueList\" item=\"columnValue\" separator=\" OR \" open=\"(\" close=\")\">"
            + "                    ${filter.tableName}.${filter.columnName} LIKE #{columnValue} "
            + "                </foreach>"
            + "            </if>"
            + "        </foreach>"
            + "    </if>"
            + "    <if test=\"customFilterSql != null and customFilterSql != ''\">"
            + "        AND ${customFilterSql{r'}'} "
            + "    </if>"
            + "</where>"
            + "<if test=\"orderBy != null and orderBy != ''\">"
            + "    ORDER BY ${orderBy}"
            + "</if>"
            + "</script>")
    List<Map<String, Object>> getList(
            @Param("masterTableName") String masterTableName,
            @Param("joinInfoList") List<JoinTableInfo> joinInfoList,
            @Param("selectFields") String selectFields,
            @Param("filterList") List<OnlineFilterDto> filterList,
            @Param("customFilterSql") String customFilterSql,
            @Param("orderBy") String orderBy);

    /**
     * 以字典键值对的方式返回数据。
     *
     * @param tableName      表名称。
     * @param selectFields   返回字段列表，逗号分隔。
     * @param filterList     SQL过滤条件列表。
     * @param dataPermFilter 数据权限过滤字符串。
     * @return 查询结果集。
     */
    @Select("<script>"
            + "SELECT ${selectFields} FROM ${tableName} "
            + "<where>"
            + "    <if test=\"filterList != null\">"
            + "        <foreach collection=\"filterList\" item=\"filter\">"
            + "            <if test=\"filter.filterType == 1\">"
            + "                AND ${filter.columnName} = #{filter.columnValue} "
            + "            </if>"
            + "            <if test=\"filter.filterType == 4\">"
            + "                AND ${filter.columnName} IN "
            + "                <foreach collection=\"filter.columnValueList\" item=\"columnValue\" separator=\",\" open=\"(\" close=\")\">"
            + "                    #{columnValue} "
            + "                </foreach>"
            + "            </if>"
            + "        </foreach>"
            + "    </if>"
            + "    <if test=\"dataPermFilter != null and dataPermFilter != ''\">"
            + "        AND ${dataPermFilter} "
            + "    </if>"
            + "</where>"
            + "</script>")
    List<Map<String, Object>> getDictList(
            @Param("tableName") String tableName,
            @Param("selectFields") String selectFields,
            @Param("filterList") List<OnlineFilterDto> filterList,
            @Param("dataPermFilter") String dataPermFilter);

    /**
     * 根据指定的表名、显示字段列表、过滤条件字符串和分组字段，返回聚合计算后的查询结果。
     *
     * @param selectTable    表名称。
     * @param selectFields   返回字段列表，逗号分隔。
     * @param whereClause    SQL常量形式的条件从句。
     * @param groupBy        分组字段列表，逗号分隔。
     * @return 对象可选字段Map列表。
     */
    @Select("<script>"
            + "SELECT ${selectFields} FROM ${selectTable}"
            + "<where>"
            + "    <if test=\"whereClause != null and whereClause != ''\">"
            + "        AND ${whereClause}"
            + "    </if>"
            + "</where>"
            + "<if test=\"groupBy != null and groupBy != ''\">"
            + "    GROUP BY ${groupBy}"
            + "</if>"
            + "</script>")
    List<Map<String, Object>> getGroupedListByCondition(
            @Param("selectTable") String selectTable,
            @Param("selectFields") String selectFields,
            @Param("whereClause") String whereClause,
            @Param("groupBy") String groupBy);
}
