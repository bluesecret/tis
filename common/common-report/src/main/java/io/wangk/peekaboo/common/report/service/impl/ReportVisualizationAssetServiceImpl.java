package io.wangk.peekaboo.common.report.service.impl;

import cn.hutool.core.util.BooleanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import io.wangk.peekaboo.common.core.base.dao.BaseDaoMapper;
import io.wangk.peekaboo.common.core.base.service.BaseService;
import io.wangk.peekaboo.common.core.object.TokenData;
import io.wangk.peekaboo.common.report.dao.ReportVisualizationAssetMapper;
import io.wangk.peekaboo.common.report.model.ReportVisualizationAsset;
import io.wangk.peekaboo.common.report.service.ReportVisualizationAssetService;
import io.wangk.peekaboo.common.sequence.wrapper.IdGeneratorWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 报表可视化数据操作服务类。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Slf4j
@Service("reportVisualizationAssetService")
public class ReportVisualizationAssetServiceImpl
        extends BaseService<ReportVisualizationAsset, Long> implements ReportVisualizationAssetService {

    @Autowired
    private ReportVisualizationAssetMapper reportVisualizationAssetMapper;
    @Autowired
    private IdGeneratorWrapper idGenerator;

    @Override
    protected BaseDaoMapper<ReportVisualizationAsset> mapper() {
        return reportVisualizationAssetMapper;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ReportVisualizationAsset saveNew(ReportVisualizationAsset asset) {
        reportVisualizationAssetMapper.insert(this.buildDefaultValue(asset));
        return asset;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean update(ReportVisualizationAsset asset, ReportVisualizationAsset originalAsset) {
        asset.setCreateUserId(originalAsset.getCreateUserId());
        asset.setUpdateUserId(TokenData.takeFromRequest().getUserId());
        asset.setCreateTime(originalAsset.getCreateTime());
        asset.setUpdateTime(new Date());
        // 这里重点提示，在执行主表数据更新之前，如果有哪些字段不支持修改操作，请用原有数据对象字段替换当前数据字段。
        UpdateWrapper<ReportVisualizationAsset> uw = this.createUpdateQueryForNullValue(asset, asset.getAssetId());
        return reportVisualizationAssetMapper.update(asset, uw) == 1;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean remove(Long assetId) {
        return reportVisualizationAssetMapper.deleteById(assetId) == 1;
    }

    @Override
    public List<ReportVisualizationAsset> getReportVisualizationAssetList(ReportVisualizationAsset filter) {
        LambdaQueryWrapper<ReportVisualizationAsset> qw = new LambdaQueryWrapper<>();
        qw.select(ReportVisualizationAsset::getAssetId,
                ReportVisualizationAsset::getVisualId,
                ReportVisualizationAsset::getAssetName,
                ReportVisualizationAsset::getThumbnailImg,
                ReportVisualizationAsset::getUpdateUserId,
                ReportVisualizationAsset::getUpdateTime,
                ReportVisualizationAsset::getCreateUserId,
                ReportVisualizationAsset::getCreateTime);
        qw.orderByDesc(ReportVisualizationAsset::getAssetId);
        if (filter == null || filter.getVisualId() == null) {
            qw.isNull(ReportVisualizationAsset::getVisualId);
        } else {
            qw.eq(ReportVisualizationAsset::getVisualId, filter.getVisualId());
        }
        TokenData tokenData = TokenData.takeFromRequest();
        if (BooleanUtil.isFalse(tokenData.getIsAdmin())) {
            qw.eq(ReportVisualizationAsset::getCreateUserId, tokenData.getUserId());
        }
        return reportVisualizationAssetMapper.selectList(qw);
    }

    private ReportVisualizationAsset buildDefaultValue(ReportVisualizationAsset asset) {
        if (asset.getAssetId() == null) {
            asset.setAssetId(idGenerator.nextLongId());
        }
        TokenData tokenData = TokenData.takeFromRequest();
        asset.setCreateUserId(tokenData.getUserId());
        asset.setUpdateUserId(tokenData.getUserId());
        Date now = new Date();
        asset.setCreateTime(now);
        asset.setUpdateTime(now);
        return asset;
    }
}
