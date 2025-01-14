package io.wangk.peekaboo.webadmin.upms.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.wangk.peekaboo.common.core.base.dao.BaseDaoMapper;
import io.wangk.peekaboo.common.core.base.service.BaseService;
import io.wangk.peekaboo.common.sequence.wrapper.IdGeneratorWrapper;
import io.wangk.peekaboo.webadmin.upms.dao.SysUserAuthMapper;
import io.wangk.peekaboo.webadmin.upms.model.SysUser;
import io.wangk.peekaboo.webadmin.upms.model.SysUserAuth;
import io.wangk.peekaboo.webadmin.upms.service.SysUserAuthService;
import io.wangk.peekaboo.webadmin.upms.service.SysUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 用户第三方授权数据数据操作服务类。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Slf4j
@Service("sysUserAuthService")
public class SysUserAuthServiceImpl extends BaseService<SysUserAuth, Long> implements SysUserAuthService {

    @Autowired
    private IdGeneratorWrapper idGenerator;
    @Autowired
    private SysUserAuthMapper sysUserAuthMapper;
    @Autowired
    private SysUserService sysUserService;

    /**
     * 返回当前Service的主表Mapper对象。
     *
     * @return 主表Mapper对象。
     */
    @Override
    protected BaseDaoMapper<SysUserAuth> mapper() {
        return sysUserAuthMapper;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public SysUserAuth saveNew(SysUserAuth sysUserAuth) {
        this.updateSysUserWithSysUserAuth(sysUserAuth);
        sysUserAuthMapper.insert(this.buildDefaultValue(sysUserAuth));
        return sysUserAuth;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean update(SysUserAuth sysUserAuth) {
        this.updateSysUserWithSysUserAuth(sysUserAuth);
        sysUserAuth.setUpdateTime(new Date());
        return sysUserAuthMapper.updateById(sysUserAuth) == 1;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean remove(Long id) {
        return sysUserAuthMapper.deleteById(id) == 1;
    }

    @Override
    public void removeByUserId(Long userId) {
        sysUserAuthMapper.delete(new LambdaQueryWrapper<SysUserAuth>().eq(SysUserAuth::getUserId, userId));
    }

    @Override
    public SysUserAuth getByUserId(String source, Long userId) {
        LambdaQueryWrapper<SysUserAuth> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUserAuth::getSource, source);
        queryWrapper.eq(SysUserAuth::getUserId, userId);
        return sysUserAuthMapper.selectOne(queryWrapper);
    }

    private SysUserAuth buildDefaultValue(SysUserAuth sysUserAuth) {
        if (sysUserAuth.getId() == null) {
            sysUserAuth.setId(idGenerator.nextLongId());
        }
        sysUserAuth.setCreateTime(new Date());
        sysUserAuth.setUpdateTime(sysUserAuth.getCreateTime());
        return sysUserAuth;
    }

    private void updateSysUserWithSysUserAuth(SysUserAuth sysUserAuth) {
        SysUser sysUser = sysUserService.getById(sysUserAuth.getUserId());
        List<SysUserAuth> sysUserAuthList;
        if (StrUtil.isNotEmpty(sysUser.getUserAuthInfo())) {
            sysUserAuthList = JSON.parseArray(sysUser.getUserAuthInfo(), SysUserAuth.class);
        } else {
            sysUserAuthList = new LinkedList<>();
        }
        SysUserAuth clonedOne = BeanUtil.copyProperties(
                sysUserAuth, SysUserAuth.class, "id", "userId", "updateTime", "createTime");
        sysUserAuthList.stream()
                .filter(au -> au.getSource().equals(sysUserAuth.getSource()))
                .findFirst().ifPresent(sysUserAuthList::remove);
        sysUserAuthList.add(clonedOne);
        sysUser.setUserAuthInfo(JSON.toJSONString(sysUserAuthList));
        sysUserService.updateById(sysUser);
    }
}
