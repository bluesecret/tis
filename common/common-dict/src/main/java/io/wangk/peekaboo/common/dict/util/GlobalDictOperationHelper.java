package io.wangk.peekaboo.common.dict.util;

import cn.hutool.core.util.StrUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.page.PageMethod;
import io.wangk.peekaboo.common.core.constant.ApplicationConstant;
import io.wangk.peekaboo.common.core.object.ResponseResult;
import io.wangk.peekaboo.common.core.object.MyPageData;
import io.wangk.peekaboo.common.core.object.MyPageParam;
import io.wangk.peekaboo.common.core.util.MyModelUtil;
import io.wangk.peekaboo.common.core.util.MyPageUtil;
import io.wangk.peekaboo.common.dict.dto.GlobalDictDto;
import io.wangk.peekaboo.common.dict.model.GlobalDict;
import io.wangk.peekaboo.common.dict.model.GlobalDictItem;
import io.wangk.peekaboo.common.dict.service.GlobalDictService;
import io.wangk.peekaboo.common.dict.vo.GlobalDictVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 全局编码字典操作的通用帮助对象。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Slf4j
@Component
public class GlobalDictOperationHelper {

    @Autowired
    private GlobalDictService globalDictService;

    /**
     * 获取全部编码字典列表。
     *
     * @param globalDictDtoFilter 过滤对象。
     * @param pageParam           分页参数。
     * @return 字典的数据列表。
     */
    public ResponseResult<MyPageData<GlobalDictVo>> listAllGlobalDict(
            GlobalDictDto globalDictDtoFilter, MyPageParam pageParam) {
        if (pageParam != null) {
            PageMethod.startPage(pageParam.getPageNum(), pageParam.getPageSize());
        }
        GlobalDict filter = MyModelUtil.copyTo(globalDictDtoFilter, GlobalDict.class);
        List<GlobalDict> dictList = globalDictService.getGlobalDictList(filter, null);
        List<GlobalDictVo> dictVoList = MyModelUtil.copyCollectionTo(dictList, GlobalDictVo.class);
        long totalCount = 0L;
        if (dictList instanceof Page) {
            totalCount = ((Page<GlobalDict>) dictList).getTotal();
        }
        return ResponseResult.success(MyPageUtil.makeResponseData(dictVoList, totalCount));
    }

    public List<Map<String, Object>> toDictDataList(List<? extends GlobalDictItem> resultList, String itemIdType) {
        return resultList.stream().map(item -> {
            Map<String, Object> dataMap = new HashMap<>(4);
            Object itemId = item.getItemId();
            if (StrUtil.equals(itemIdType, "Long")) {
                itemId = Long.valueOf(item.getItemId());
            } else if (StrUtil.equals(itemIdType, "Integer")) {
                itemId = Integer.valueOf(item.getItemId());
            }
            dataMap.put(ApplicationConstant.DICT_ID, itemId);
            dataMap.put(ApplicationConstant.DICT_NAME, item.getItemName());
            dataMap.put("showOrder", item.getShowOrder());
            dataMap.put("status", item.getStatus());
            return dataMap;
        }).collect(Collectors.toList());
    }

    public List<Map<String, Object>> toDictDataList2(List<? extends GlobalDictItem> resultList) {
        return resultList.stream().map(item -> {
            Map<String, Object> dataMap = new HashMap<>(5);
            dataMap.put(ApplicationConstant.DICT_ID, item.getId());
            dataMap.put("itemId", item.getItemId());
            dataMap.put(ApplicationConstant.DICT_NAME, item.getItemName());
            dataMap.put("showOrder", item.getShowOrder());
            dataMap.put("status", item.getStatus());
            return dataMap;
        }).collect(Collectors.toList());
    }
}
