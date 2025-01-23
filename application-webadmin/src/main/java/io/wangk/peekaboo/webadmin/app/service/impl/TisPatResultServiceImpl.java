package io.wangk.peekaboo.webadmin.app.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.*;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import io.wangk.peekaboo.webadmin.app.service.*;
import io.wangk.peekaboo.webadmin.app.dao.*;
import io.wangk.peekaboo.webadmin.app.model.*;
import io.wangk.peekaboo.common.core.base.dao.BaseDaoMapper;
import io.wangk.peekaboo.common.core.object.MyRelationParam;
import io.wangk.peekaboo.common.core.object.CallResult;
import io.wangk.peekaboo.common.core.base.service.BaseService;
import io.wangk.peekaboo.common.sequence.wrapper.IdGeneratorWrapper;
import com.github.pagehelper.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 患者检测结果数据操作服务类。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Slf4j
@Service("tisPatResultService")
public class TisPatResultServiceImpl extends BaseService<TisPatResult, Long> implements TisPatResultService {

    @Autowired
    private IdGeneratorWrapper idGenerator;
    @Autowired
    private TisPatResultMapper tisPatResultMapper;
    @Autowired
    private TisPatInfoService tisPatInfoService;

    /**
     * 返回当前Service的主表Mapper对象。
     *
     * @return 主表Mapper对象。
     */
    @Override
    protected BaseDaoMapper<TisPatResult> mapper() {
        return tisPatResultMapper;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public TisPatResult saveNew(TisPatResult tisPatResult) {
        tisPatResultMapper.insert(this.buildDefaultValue(tisPatResult));
        return tisPatResult;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveNewBatch(List<TisPatResult> tisPatResultList) {
        if (CollUtil.isNotEmpty(tisPatResultList)) {
            tisPatResultList.forEach(this::buildDefaultValue);
            tisPatResultMapper.insertList(tisPatResultList);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveNewBatch(List<TisPatResult> tisPatResultList, int batchSize) {
        if (CollUtil.isEmpty(tisPatResultList)) {
            return;
        }
        if (batchSize <= 0) {
            batchSize = 10000;
        }
        int start = 0;
        do {
            int end = Math.min(tisPatResultList.size(), start + batchSize);
            List<TisPatResult> subList = tisPatResultList.subList(start, end);
            // 如果数据量过大，同时当前表中存在createTime或updateTime等字段，可以考虑在外部创建一次 new Date()，
            // 然后传入buildDefaultValue，这样可以减少对象的创建次数，降低GC，提升效率。橙单之所以没有这样生成，是因为
            // 有些业务场景下需要按照这两个日期字段排序，因此我们只是在这里给出优化建议。
            subList.forEach(this::buildDefaultValue);
            tisPatResultMapper.insertList(subList);
            if (end == tisPatResultList.size()) {
                break;
            }
            start += batchSize;
        } while (true);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean update(TisPatResult tisPatResult, TisPatResult originalTisPatResult) {
        // 这里重点提示，在执行主表数据更新之前，如果有哪些字段不支持修改操作，请用原有数据对象字段替换当前数据字段。
        UpdateWrapper<TisPatResult> uw = this.createUpdateQueryForNullValue(tisPatResult, tisPatResult.getId());
        return tisPatResultMapper.update(tisPatResult, uw) == 1;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean remove(Long id) {
        return tisPatResultMapper.deleteById(id) == 1;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int removeByPatId(Long patId) {
        TisPatResult deletedObject = new TisPatResult();
        deletedObject.setPatId(patId);
        return tisPatResultMapper.delete(new QueryWrapper<>(deletedObject));
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateBatchByPatId(Long patId, List<TisPatResult> dataList) {
        this.updateBatchOneToManyRelation("patId", patId,
                null, null, dataList, this::saveNewBatch);
    }

    @Override
    public List<TisPatResult> getTisPatResultList(TisPatResult filter, String orderBy) {
        return tisPatResultMapper.getTisPatResultList(filter, orderBy);
    }

    @Override
    public List<TisPatResult> getTisPatResultListWithRelation(TisPatResult filter, String orderBy) {
        List<TisPatResult> resultList = tisPatResultMapper.getTisPatResultList(filter, orderBy);
        // 在缺省生成的代码中，如果查询结果resultList不是Page对象，说明没有分页，那么就很可能是数据导出接口调用了当前方法。
        // 为了避免一次性的大量数据关联，规避因此而造成的系统运行性能冲击，这里手动进行了分批次读取，开发者可按需修改该值。
        int batchSize = resultList instanceof Page ? 0 : 1000;
        this.buildRelationForDataList(resultList, MyRelationParam.normal(), batchSize);
        return resultList;
    }

    @Override
    public CallResult verifyImportList(List<TisPatResult> dataList, Set<String> ignoreFieldSet) {
        CallResult callResult;
        return CallResult.ok();
    }

    @Override
    public CallResult verifyRelatedData(TisPatResult tisPatResult, TisPatResult originalTisPatResult) {
        String errorMessageFormat = "数据验证失败，关联的%s并不存在，请刷新后重试！";
        //这里是一对多的验证
        if (this.needToVerify(tisPatResult, originalTisPatResult, TisPatResult::getPatId)
                && !tisPatInfoService.existId(tisPatResult.getPatId())) {
            return CallResult.error(String.format(errorMessageFormat, "患者ID"));
        }
        return CallResult.ok();
    }

    private TisPatResult buildDefaultValue(TisPatResult tisPatResult) {
        if (tisPatResult.getId() == null) {
            tisPatResult.setId(idGenerator.nextLongId());
        }
        return tisPatResult;
    }
}
