package io.wangk.peekaboo.common.mobile.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.github.pagehelper.Page;
import io.wangk.peekaboo.common.core.annotation.MyDataSourceResolver;
import io.wangk.peekaboo.common.core.base.dao.BaseDaoMapper;
import io.wangk.peekaboo.common.core.base.service.BaseService;
import io.wangk.peekaboo.common.core.exception.MyRuntimeException;
import io.wangk.peekaboo.common.core.object.MyRelationParam;
import io.wangk.peekaboo.common.core.object.TokenData;
import io.wangk.peekaboo.common.core.util.MyModelUtil;
import io.wangk.peekaboo.common.mobile.dao.MobileEntryDataPermMapper;
import io.wangk.peekaboo.common.mobile.dao.MobileEntryMapper;
import io.wangk.peekaboo.common.mobile.dao.MobileEntryRoleMapper;
import io.wangk.peekaboo.common.mobile.model.MobileEntry;
import io.wangk.peekaboo.common.mobile.model.MobileEntryDataPerm;
import io.wangk.peekaboo.common.mobile.model.MobileEntryRole;
import io.wangk.peekaboo.common.mobile.service.MobileEntryService;
import io.wangk.peekaboo.common.mobile.util.MobileEntryDataSourceResolver;
import io.wangk.peekaboo.common.sequence.wrapper.IdGeneratorWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@MyDataSourceResolver(resolver = MobileEntryDataSourceResolver.class)
@Service("mobileEntryService")
public class MobileEntryServiceImpl extends BaseService<MobileEntry, Long> implements MobileEntryService {

    @Autowired
    private MobileEntryMapper mobileEntryMapper;
    @Autowired
    private MobileEntryRoleMapper mobileEntryRoleMapper;
    @Autowired
    private MobileEntryDataPermMapper mobileEntryDataPermMapper;
    @Autowired
    private IdGeneratorWrapper idGenerator;

    /**
     * 返回当前Service的主表Mapper对象。
     *
     * @return 主表Mapper对象。
     */
    @Override
    protected BaseDaoMapper<MobileEntry> mapper() {
        return mobileEntryMapper;
    }

    @Override
    public List<MobileEntry> getAllListByOrder(String... orderByProperties) {
        TokenData tokenData = TokenData.takeFromRequest();
        if (tokenData.getTenantId() == null) {
            return super.getAllListByOrder(orderByProperties);
        }
        MobileEntry filter = new MobileEntry();
        filter.setTenantId(tokenData.getTenantId());
        filter.setTenantAvailable(true);
        List<String> columns = new ArrayList<>(orderByProperties.length);
        for (String orderByProperty : orderByProperties) {
            columns.add(this.safeMapToColumnName(orderByProperty));
        }
        QueryWrapper<MobileEntry> qw = new QueryWrapper<>(filter);
        return mapper().selectList(qw.orderByAsc(columns));
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public MobileEntry saveNew(MobileEntry mobileEntry, String roleIdListString) {
        mobileEntryMapper.insert(this.buildDefaultValue(mobileEntry));
        this.insertRelatedData(mobileEntry.getEntryId(), roleIdListString);
        return mobileEntry;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean update(
            MobileEntry mobileEntry,
            MobileEntry originalMobileEntry,
            String roleIdListString) {
        mobileEntryRoleMapper.delete(
                new LambdaQueryWrapper<MobileEntryRole>().eq(MobileEntryRole::getEntryId, mobileEntry.getEntryId()));
        this.insertRelatedData(mobileEntry.getEntryId(), roleIdListString);
        mobileEntry.setTenantId(originalMobileEntry.getTenantId());
        mobileEntry.setCreateUserId(originalMobileEntry.getCreateUserId());
        mobileEntry.setUpdateUserId(TokenData.takeFromRequest().getUserId());
        mobileEntry.setCreateTime(originalMobileEntry.getCreateTime());
        mobileEntry.setUpdateTime(new Date());
        mobileEntry.setEntryType(originalMobileEntry.getEntryType());
        mobileEntry.setTenantAvailable(originalMobileEntry.getTenantAvailable());
        mobileEntry.setTenantCustom(originalMobileEntry.getTenantCustom());
        mobileEntry.setTenantAdminEntryId(originalMobileEntry.getTenantAdminEntryId());
        // 这里重点提示，在执行主表数据更新之前，如果有哪些字段不支持修改操作，请用原有数据对象字段替换当前数据字段。
        UpdateWrapper<MobileEntry> uw = this.createUpdateQueryForNullValue(mobileEntry, mobileEntry.getEntryId());
        return mobileEntryMapper.update(mobileEntry, uw) == 1;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean remove(Long entryId) {
        LambdaQueryWrapper<MobileEntryDataPerm> qw = new LambdaQueryWrapper<>();
        qw.eq(MobileEntryDataPerm::getEntryId, entryId);
        if (mobileEntryDataPermMapper.selectCount(qw) > 0) {
            throw new MyRuntimeException("数据验证失败，当前数据正在被数据权限关联，不能删除！");
        }
        mobileEntryDataPermMapper.delete(qw);
        mobileEntryRoleMapper.delete(new LambdaQueryWrapper<MobileEntryRole>().eq(MobileEntryRole::getEntryId, entryId));
        return mobileEntryMapper.deleteById(entryId) == 1;
    }

    @Override
    public List<MobileEntry> getMobileEntryList(MobileEntry filter, String orderBy) {
        if (filter == null) {
            filter = new MobileEntry();
        }
        filter.setTenantId(TokenData.takeFromRequest().getTenantId());
        return mobileEntryMapper.getMobileEntryList(filter, orderBy);
    }

    @Override
    public List<MobileEntry> getMobileEntryListWithRelation(MobileEntry filter, String orderBy) {
        List<MobileEntry> resultList = this.getMobileEntryList(filter, orderBy);
        // 在缺省生成的代码中，如果查询结果resultList不是Page对象，说明没有分页，那么就很可能是数据导出接口调用了当前方法。
        // 为了避免一次性的大量数据关联，规避因此而造成的系统运行性能冲击，这里手动进行了分批次读取，开发者可按需修改该值。
        int batchSize = resultList instanceof Page ? 0 : 1000;
        this.buildRelationForDataList(resultList, MyRelationParam.normal(), batchSize);
        return resultList;
    }

    @Override
    public List<MobileEntry> getMobileEntryListByRoleIds(String roleIds) {
        List<MobileEntry> commonEntryList =
                mobileEntryMapper.getCommonMobileEntryList(TokenData.takeFromRequest().getTenantId());
        if (StrUtil.isEmpty(roleIds)) {
            return commonEntryList.stream()
                    .sorted(Comparator.comparing(MobileEntry::getShowOrder, Comparator.naturalOrder()))
                    .collect(Collectors.toList());
        }
        Set<Long> roleIdSet = StrUtil.split(roleIds, StrUtil.COMMA).stream().map(Long::valueOf).collect(Collectors.toSet());
        List<MobileEntry> entryList =
                mobileEntryMapper.getMobileEntryListByRoleIds(roleIdSet, TokenData.takeFromRequest().getTenantId());
        CollUtil.addAll(entryList, commonEntryList);
        entryList = entryList.stream()
                .sorted(Comparator.comparing(MobileEntry::getShowOrder, Comparator.naturalOrder()))
                .collect(Collectors.toList());
        LinkedHashMap<Long, MobileEntry> entryMap = new LinkedHashMap<>();
        for (MobileEntry entry : entryList) {
            entryMap.put(entry.getEntryId(), entry);
        }
        return new LinkedList<>(entryMap.values());
    }

    private MobileEntry buildDefaultValue(MobileEntry mobileEntry) {
        if (mobileEntry.getEntryId() == null) {
            mobileEntry.setEntryId(idGenerator.nextLongId());
        }
        if (mobileEntry.getTenantAvailable() == null) {
            mobileEntry.setTenantAvailable(true);
        }
        if (mobileEntry.getTenantCustom() == null) {
            mobileEntry.setTenantCustom(true);
        }
        TokenData tokenData = TokenData.takeFromRequest();
        mobileEntry.setTenantId(tokenData.getTenantId());
        mobileEntry.setCreateUserId(tokenData.getUserId());
        mobileEntry.setUpdateUserId(tokenData.getUserId());
        Date now = new Date();
        mobileEntry.setCreateTime(now);
        mobileEntry.setUpdateTime(now);
        MyModelUtil.setDefaultValue(mobileEntry, "imageData", "");
        return mobileEntry;
    }

    private void insertRelatedData(Long entryId, String roleIdListString) {
        if (StrUtil.isNotBlank(roleIdListString)) {
            Set<Long> roleIdSet =
                    StrUtil.split(roleIdListString, StrUtil.COMMA).stream().map(Long::valueOf).collect(Collectors.toSet());
            roleIdSet.forEach(roleId -> {
                MobileEntryRole o = new MobileEntryRole();
                o.setEntryId(entryId);
                o.setRoleId(roleId);
                mobileEntryRoleMapper.insert(o);
            });
        }
    }
}
