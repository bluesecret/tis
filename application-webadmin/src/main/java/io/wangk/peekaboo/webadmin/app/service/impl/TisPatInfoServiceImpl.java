package io.wangk.peekaboo.webadmin.app.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import io.wangk.peekaboo.webadmin.app.service.*;
import io.wangk.peekaboo.webadmin.app.dao.*;
import io.wangk.peekaboo.webadmin.app.model.*;
import io.wangk.peekaboo.common.core.base.dao.BaseDaoMapper;
import io.wangk.peekaboo.common.core.object.TokenData;
import io.wangk.peekaboo.common.core.object.MyRelationParam;
import io.wangk.peekaboo.common.core.base.service.BaseService;
import io.wangk.peekaboo.common.sequence.wrapper.IdGeneratorWrapper;
import com.github.pagehelper.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 患者数据操作服务类。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Slf4j
@Service("tisPatInfoService")
public class TisPatInfoServiceImpl extends BaseService<TisPatInfo, Long> implements TisPatInfoService {

    @Autowired
    private IdGeneratorWrapper idGenerator;
    @Autowired
    private TisPatInfoMapper tisPatInfoMapper;
    @Autowired
    private TisPatResultService tisPatResultService;

    /**
     * 返回当前Service的主表Mapper对象。
     *
     * @return 主表Mapper对象。
     */
    @Override
    protected BaseDaoMapper<TisPatInfo> mapper() {
        return tisPatInfoMapper;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public TisPatInfo saveNew(TisPatInfo tisPatInfo) {
        tisPatInfoMapper.insert(this.buildDefaultValue(tisPatInfo));
        return tisPatInfo;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveNewBatch(List<TisPatInfo> tisPatInfoList) {
        if (CollUtil.isNotEmpty(tisPatInfoList)) {
            tisPatInfoList.forEach(this::buildDefaultValue);
            tisPatInfoMapper.insertList(tisPatInfoList);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public TisPatInfo saveNewWithRelation(TisPatInfo tisPatInfo, JSONObject relationData) {
        this.saveNew(tisPatInfo);
        this.saveOrUpdateRelationData(tisPatInfo, relationData);
        return tisPatInfo;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean update(TisPatInfo tisPatInfo, TisPatInfo originalTisPatInfo) {
        tisPatInfo.setUpdateUserId(TokenData.takeFromRequest().getUserId());
        tisPatInfo.setCreateTime(originalTisPatInfo.getCreateTime());
        tisPatInfo.setUpdateTime(new Date());
        // 这里重点提示，在执行主表数据更新之前，如果有哪些字段不支持修改操作，请用原有数据对象字段替换当前数据字段。
        UpdateWrapper<TisPatInfo> uw = this.createUpdateQueryForNullValue(tisPatInfo, tisPatInfo.getId());
        return tisPatInfoMapper.update(tisPatInfo, uw) == 1;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateWithRelation(
            TisPatInfo tisPatInfo, TisPatInfo originalTisPatInfo, JSONObject relationData) {
        // tisPatInfo 为空的时候，无需修改主表数据。
        if (tisPatInfo != null && !this.update(tisPatInfo, originalTisPatInfo)) {
            return false;
        }
        this.saveOrUpdateRelationData(originalTisPatInfo, relationData);
        return true;
    }

    private void saveOrUpdateRelationData(TisPatInfo tisPatInfo, JSONObject relationData) {
        List<TisPatResult> tisPatResultList =
                relationData.getObject("tisPatResultList", new TypeReference<List<TisPatResult>>() {});
        // 对于一对多更新，分为以下三步：
        // 1. 在关联从表中，删除掉与主表字段关联，但是又没有出现在本地更新中的数据。我们将这些数据视为需要删除的数据。
        // 2. 在本次更新数据列表中，如果从表的对象没有主键Id，我们视为新数据，可以批量插入。
        // 3. 在本次更新数据列表中，如果从表的对象存在主键Id，我们视为已有数据，逐条更新。
        if (tisPatResultList != null) {
            tisPatResultService.updateBatchByPatId(tisPatInfo.getId(), tisPatResultList);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean remove(Long id) {
        if (tisPatInfoMapper.deleteById(id) == 0) {
            return false;
        }
        tisPatResultService.removeByPatId(id);
        return true;
    }

    @Override
    public List<TisPatInfo> getTisPatInfoList(TisPatInfo filter, String orderBy) {
        return tisPatInfoMapper.getTisPatInfoList(filter, orderBy);
    }

    @Override
    public List<TisPatInfo> getTisPatInfoListWithRelation(TisPatInfo filter, TisPatResult tisPatResultFilter, String orderBy) {
        List<TisPatInfo> resultList =
                tisPatInfoMapper.getTisPatInfoListEx(filter, tisPatResultFilter, orderBy);
        // 在缺省生成的代码中，如果查询结果resultList不是Page对象，说明没有分页，那么就很可能是数据导出接口调用了当前方法。
        // 为了避免一次性的大量数据关联，规避因此而造成的系统运行性能冲击，这里手动进行了分批次读取，开发者可按需修改该值。
        int batchSize = resultList instanceof Page ? 0 : 1000;
        this.buildRelationForDataList(resultList, MyRelationParam.normal(), batchSize);
        return resultList;
    }

    private TisPatInfo buildDefaultValue(TisPatInfo tisPatInfo) {
        if (tisPatInfo.getId() == null) {
            tisPatInfo.setId(idGenerator.nextLongId());
        }
        TokenData tokenData = TokenData.takeFromRequest();
        tisPatInfo.setUpdateUserId(tokenData.getUserId());
        Date now = new Date();
        tisPatInfo.setCreateTime(now);
        tisPatInfo.setUpdateTime(now);
        return tisPatInfo;
    }
}
