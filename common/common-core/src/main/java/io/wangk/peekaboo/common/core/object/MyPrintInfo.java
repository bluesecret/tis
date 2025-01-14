package io.wangk.peekaboo.common.core.object;

import com.alibaba.fastjson.JSONArray;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 打印信息对象。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Data
@NoArgsConstructor
public class MyPrintInfo {

    /**
     * 打印模板Id。
     */
    private Long printId;
    /**
     * 打印参数列表。对应于common-report模块的ReportPrintParam对象。
     */
    private List<JSONArray> printParams;

    public MyPrintInfo(Long printId, List<JSONArray> printParams) {
        this.printId = printId;
        this.printParams = printParams;
    }
}
