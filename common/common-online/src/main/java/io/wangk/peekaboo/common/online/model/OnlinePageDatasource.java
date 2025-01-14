package io.wangk.peekaboo.common.online.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

/**
 * 在线表单页面和数据源多对多关联实体对象。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Data
@TableName(value = "zz_online_page_datasource")
public class OnlinePageDatasource {

    /**
     * 主键Id。
     */
    @TableId(value = "id")
    private Long id;

    /**
     * 页面主键Id。
     */
    @TableField(value = "page_id")
    private Long pageId;

    /**
     * 数据源主键Id。
     */
    @TableField(value = "datasource_id")
    private Long datasourceId;
}
