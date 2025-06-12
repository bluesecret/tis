package io.wangk.peekaboo.webadmin.app.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.wangk.peekaboo.common.core.base.dao.BaseDaoMapper;
import io.wangk.peekaboo.common.core.base.service.BaseService;
import io.wangk.peekaboo.common.core.object.TokenData;
import io.wangk.peekaboo.common.dict.model.GlobalDict;
import io.wangk.peekaboo.common.sequence.wrapper.IdGeneratorWrapper;
import io.wangk.peekaboo.webadmin.app.dao.TisDeviceMapper;
import io.wangk.peekaboo.webadmin.app.dao.TisUserDeviceMapper;
import io.wangk.peekaboo.webadmin.app.model.TisDeviceInfo;
import io.wangk.peekaboo.webadmin.app.model.TisPatInfo;
import io.wangk.peekaboo.webadmin.app.model.TisUserDevice;
import io.wangk.peekaboo.webadmin.app.service.TisDeviceInfoService;
import io.wangk.peekaboo.webadmin.upms.model.SysUser;
import io.wangk.peekaboo.webadmin.upms.model.SysUserPost;
import io.wangk.peekaboo.webadmin.upms.model.SysUserRole;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 设备数据操作服务类。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Slf4j
@Service("tisDeviceInfoService")
public class TisDeviceInfoServiceImpl extends BaseService<TisDeviceInfo, Long> implements TisDeviceInfoService  {

    @Autowired
    private IdGeneratorWrapper idGenerator;
    @Autowired
    private TisDeviceInfoService tisDeviceInfoService;
    @Autowired
    private TisDeviceMapper tisDeviceMapper;
    @Autowired
    private TisUserDeviceMapper tisUserDeviceMapper;

    /**
     * 返回当前Service的主表Mapper对象。
     *
     * @return 主表Mapper对象。
     */
    @Override
    protected BaseDaoMapper<TisDeviceInfo> mapper() {
        return tisDeviceMapper;
    }

    @Override
    public TisDeviceInfo saveNew(TisDeviceInfo tisDeviceInfo) {
         tisDeviceMapper.insert(buildDefaultValue(tisDeviceInfo));
        return tisDeviceInfo;
    }

    @Override
    public boolean existSerNo(String serNo) {
        LambdaQueryWrapper<TisDeviceInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TisDeviceInfo::getSerNo, serNo);
        return tisDeviceMapper.selectCount(queryWrapper) > 0;
    }

    @Override
    public boolean update(TisDeviceInfo tisDeviceInfo, TisDeviceInfo originalTisDeviceInfo) {
        tisDeviceInfo.setCreatedUserId(originalTisDeviceInfo.getCreatedUserId());
        tisDeviceInfo.setCreateTime(originalTisDeviceInfo.getCreateTime());
        tisDeviceInfo.setUpdateUserId(TokenData.takeFromRequest().getUserId());
        tisDeviceInfo.setUpdateTime(new Date());
        if (tisDeviceMapper.updateById(tisDeviceInfo) != 1) {
            return false;
        }
        return true;
    }

    @Override
    public List<TisUserDevice> getTisUserDeviceListByUserId(Long userId) {
        TisUserDevice filter = new TisUserDevice();
        filter.setUserId(userId);
        return tisUserDeviceMapper.selectList(new QueryWrapper<>(filter));
    }

    @Override
    public void addUserDeviceList(SysUser user, List<TisUserDevice> userDeviceList) {
        // 先删除原有的User-Post关联关系，再重新插入新的关联关系
        TisUserDevice deletedUserDevice = new TisUserDevice();
        deletedUserDevice.setUserId(user.getUserId());
        tisUserDeviceMapper.delete(new QueryWrapper<>(deletedUserDevice));
        for (TisUserDevice userDevice : userDeviceList) {
            tisUserDeviceMapper.insert(userDevice);
        }
    }

    public List<TisDeviceInfo> getTisPatInfoList(TisDeviceInfo filter, String orderBy) {
        return null;
    }

    private TisDeviceInfo buildDefaultValue(TisDeviceInfo tisDeviceInfo) {
        if (tisDeviceInfo.getId() == null) {
            tisDeviceInfo.setId(idGenerator.nextLongId());
        }
        TokenData tokenData = TokenData.takeFromRequest();
        tisDeviceInfo.setUpdateUserId(tokenData.getUserId());
        Date now = new Date();
        tisDeviceInfo.setCreateTime(now);
        tisDeviceInfo.setUpdateTime(now);
        return tisDeviceInfo;
    }
}
