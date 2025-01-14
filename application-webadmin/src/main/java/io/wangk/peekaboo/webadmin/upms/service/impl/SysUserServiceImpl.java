package io.wangk.peekaboo.webadmin.upms.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.*;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.github.pagehelper.page.PageMethod;
import io.wangk.peekaboo.webadmin.upms.service.*;
import io.wangk.peekaboo.webadmin.upms.dao.*;
import io.wangk.peekaboo.webadmin.upms.model.*;
import io.wangk.peekaboo.webadmin.upms.model.constant.SysUserStatus;
import io.wangk.peekaboo.common.ext.util.BizWidgetDatasourceExtHelper;
import io.wangk.peekaboo.common.ext.base.BizWidgetDatasource;
import io.wangk.peekaboo.common.ext.constant.BizWidgetDatasourceType;
import io.wangk.peekaboo.common.core.base.dao.BaseDaoMapper;
import io.wangk.peekaboo.common.core.constant.UserFilterGroup;
import io.wangk.peekaboo.common.core.constant.GlobalDeletedFlag;
import io.wangk.peekaboo.common.core.object.*;
import io.wangk.peekaboo.common.core.base.service.BaseService;
import io.wangk.peekaboo.common.core.util.MyModelUtil;
import io.wangk.peekaboo.common.core.util.MyPageUtil;
import io.wangk.peekaboo.common.sequence.wrapper.IdGeneratorWrapper;
import com.github.pagehelper.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 用户管理数据操作服务类。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Slf4j
@Service("sysUserService")
public class SysUserServiceImpl extends BaseService<SysUser, Long> implements SysUserService, BizWidgetDatasource {

    @Autowired
    private IdGeneratorWrapper idGenerator;
    @Autowired
    private SysUserMapper sysUserMapper;
    @Autowired
    private SysUserAuthService sysUserAuthService;
    @Autowired
    private SysUserRoleMapper sysUserRoleMapper;
    @Autowired
    private SysUserPostMapper sysUserPostMapper;
    @Autowired
    private SysDataPermUserMapper sysDataPermUserMapper;
    @Autowired
    private SysDeptService sysDeptService;
    @Autowired
    private SysRoleService sysRoleService;
    @Autowired
    private SysDataPermService sysDataPermService;
    @Autowired
    private SysPostService sysPostService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private BizWidgetDatasourceExtHelper bizWidgetDatasourceExtHelper;

    /**
     * 返回当前Service的主表Mapper对象。
     *
     * @return 主表Mapper对象。
     */
    @Override
    protected BaseDaoMapper<SysUser> mapper() {
        return sysUserMapper;
    }

    @PostConstruct
    private void registerBizWidgetDatasource() {
        bizWidgetDatasourceExtHelper.registerDatasource(BizWidgetDatasourceType.UPMS_USER_TYPE, this);
    }

    @Override
    public MyPageData<Map<String, Object>> getDataList(
            String type, Map<String, Object> filter, MyOrderParam orderParam, MyPageParam pageParam) {
        if (pageParam != null) {
            PageMethod.startPage(pageParam.getPageNum(), pageParam.getPageSize(), pageParam.getCount());
        }
        List<SysUser> userList = null;
        String orderBy = MyOrderParam.buildOrderBy(orderParam, SysUser.class, false);
        SysUser userFilter = BeanUtil.toBean(filter, SysUser.class);
        if (filter != null) {
            Object group = filter.get("USER_FILTER_GROUP");
            if (group != null) {
                JSONObject filterGroupJson = JSON.parseObject(group.toString());
                String groupType = filterGroupJson.getString("type");
                String values = filterGroupJson.getString("values");
                if (UserFilterGroup.USER.equals(groupType)) {
                    List<String> loginNames = StrUtil.splitTrim(values, ",");
                    userList = sysUserMapper.getSysUserListByLoginNames(loginNames, userFilter, orderBy);
                } else {
                    Set<Long> groupIds = StrUtil.splitTrim(values, ",")
                            .stream().map(Long::valueOf).collect(Collectors.toSet());
                    userList = this.getUserListByGroupIds(groupType, groupIds, userFilter, orderBy);
                }
            }
        }
        if (userList == null) {
            userList = this.getSysUserList(userFilter, orderBy);
        }
        this.buildRelationForDataList(userList, MyRelationParam.dictOnly());
        return MyPageUtil.makeResponseData(userList, BeanUtil::beanToMap);
    }

    private List<SysUser> getUserListByGroupIds(String groupType, Set<Long> groupIds, SysUser filter, String orderBy) {
        if (groupType.equals(UserFilterGroup.DEPT)) {
            return sysUserMapper.getSysUserListByDeptIds(groupIds, filter, orderBy);
        }
        List<Long> userIds = null;
        switch (groupType) {
            case UserFilterGroup.ROLE:
                userIds = sysUserMapper.getUserIdListByRoleIds(groupIds, filter, orderBy);
                break;
            case UserFilterGroup.POST:
                userIds = sysUserMapper.getUserIdListByPostIds(groupIds, filter, orderBy);
                break;
            case UserFilterGroup.DEPT_POST:
                userIds = sysUserMapper.getUserIdListByDeptPostIds(groupIds, filter, orderBy);
                break;
            default:
                break;
        }
        if (CollUtil.isEmpty(userIds)) {
            return Collections.emptyList();
        }
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(SysUser::getUserId, userIds);
        if (StrUtil.isNotBlank(orderBy)) {
            queryWrapper.last(" ORDER BY " + orderBy);
        }
        return sysUserMapper.selectList(queryWrapper);
    }

    @Override
    public List<Map<String, Object>> getDataListWithInList(String type, String fieldName, List<String> fieldValues) {
        List<SysUser> userList;
        if (StrUtil.isBlank(fieldName)) {
            userList = this.getInList(fieldValues.stream().map(Long::valueOf).collect(Collectors.toSet()));
        } else {
            userList = this.getInList(fieldName, MyModelUtil.convertToTypeValues(SysUser.class, fieldName, fieldValues));
        }
        this.buildRelationForDataList(userList, MyRelationParam.dictOnly());
        return MyModelUtil.beanToMapList(userList);
    }

    /**
     * 获取指定登录名的用户对象。
     *
     * @param loginName 指定登录用户名。
     * @return 用户对象。
     */
    @Override
    public SysUser getSysUserByLoginName(String loginName) {
        SysUser filter = new SysUser();
        filter.setLoginName(loginName);
        return sysUserMapper.selectOne(new QueryWrapper<>(filter));
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public SysUser saveNew(SysUser user, Set<Long> roleIdSet, Set<Long> deptPostIdSet, Set<Long> dataPermIdSet) {
        user.setUserId(idGenerator.nextLongId());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setUserStatus(SysUserStatus.STATUS_NORMAL);
        user.setDeletedFlag(GlobalDeletedFlag.NORMAL);
        MyModelUtil.fillCommonsForInsert(user);
        sysUserMapper.insert(user);
        if (CollUtil.isNotEmpty(deptPostIdSet)) {
            for (Long deptPostId : deptPostIdSet) {
                SysDeptPost deptPost = sysDeptService.getSysDeptPost(deptPostId);
                SysUserPost userPost = new SysUserPost();
                userPost.setUserId(user.getUserId());
                userPost.setDeptPostId(deptPostId);
                userPost.setPostId(deptPost.getPostId());
                sysUserPostMapper.insert(userPost);
            }
        }
        if (CollUtil.isNotEmpty(roleIdSet)) {
            for (Long roleId : roleIdSet) {
                SysUserRole userRole = new SysUserRole();
                userRole.setUserId(user.getUserId());
                userRole.setRoleId(roleId);
                sysUserRoleMapper.insert(userRole);
            }
        }
        if (CollUtil.isNotEmpty(dataPermIdSet)) {
            for (Long dataPermId : dataPermIdSet) {
                SysDataPermUser dataPermUser = new SysDataPermUser();
                dataPermUser.setDataPermId(dataPermId);
                dataPermUser.setUserId(user.getUserId());
                sysDataPermUserMapper.insert(dataPermUser);
            }
        }
        return user;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean update(SysUser user, SysUser originalUser, Set<Long> roleIdSet, Set<Long> deptPostIdSet, Set<Long> dataPermIdSet) {
        user.setLoginName(originalUser.getLoginName());
        user.setPassword(originalUser.getPassword());
        MyModelUtil.fillCommonsForUpdate(user, originalUser);
        UpdateWrapper<SysUser> uw = this.createUpdateQueryForNullValue(user, user.getUserId());
        if (sysUserMapper.update(user, uw) != 1) {
            return false;
        }
        // 先删除原有的User-Post关联关系，再重新插入新的关联关系
        SysUserPost deletedUserPost = new SysUserPost();
        deletedUserPost.setUserId(user.getUserId());
        sysUserPostMapper.delete(new QueryWrapper<>(deletedUserPost));
        if (CollUtil.isNotEmpty(deptPostIdSet)) {
            for (Long deptPostId : deptPostIdSet) {
                SysDeptPost deptPost = sysDeptService.getSysDeptPost(deptPostId);
                SysUserPost userPost = new SysUserPost();
                userPost.setUserId(user.getUserId());
                userPost.setDeptPostId(deptPostId);
                userPost.setPostId(deptPost.getPostId());
                sysUserPostMapper.insert(userPost);
            }
        }
        // 先删除原有的User-Role关联关系，再重新插入新的关联关系
        SysUserRole deletedUserRole = new SysUserRole();
        deletedUserRole.setUserId(user.getUserId());
        sysUserRoleMapper.delete(new QueryWrapper<>(deletedUserRole));
        if (CollUtil.isNotEmpty(roleIdSet)) {
            for (Long roleId : roleIdSet) {
                SysUserRole userRole = new SysUserRole();
                userRole.setUserId(user.getUserId());
                userRole.setRoleId(roleId);
                sysUserRoleMapper.insert(userRole);
            }
        }
        // 先删除原有的DataPerm-User关联关系，在重新插入新的关联关系
        SysDataPermUser deletedDataPermUser = new SysDataPermUser();
        deletedDataPermUser.setUserId(user.getUserId());
        sysDataPermUserMapper.delete(new QueryWrapper<>(deletedDataPermUser));
        if (CollUtil.isNotEmpty(dataPermIdSet)) {
            for (Long dataPermId : dataPermIdSet) {
                SysDataPermUser dataPermUser = new SysDataPermUser();
                dataPermUser.setDataPermId(dataPermId);
                dataPermUser.setUserId(user.getUserId());
                sysDataPermUserMapper.insert(dataPermUser);
            }
        }
        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean changePassword(Long userId, String newPass) {
        SysUser updatedUser = new SysUser();
        updatedUser.setUserId(userId);
        updatedUser.setPassword(passwordEncoder.encode(newPass));
        return sysUserMapper.updateById(updatedUser) == 1;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean changeHeadImage(Long userId, String newHeadImage) {
        SysUser updatedUser = new SysUser();
        updatedUser.setUserId(userId);
        updatedUser.setHeadImageUrl(newHeadImage);
        return sysUserMapper.updateById(updatedUser) == 1;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean remove(Long userId) {
        if (sysUserMapper.deleteById(userId) == 0) {
            return false;
        }
        SysUserRole userRole = new SysUserRole();
        userRole.setUserId(userId);
        sysUserRoleMapper.delete(new QueryWrapper<>(userRole));
        SysUserPost userPost = new SysUserPost();
        userPost.setUserId(userId);
        sysUserPostMapper.delete(new QueryWrapper<>(userPost));
        SysDataPermUser dataPermUser = new SysDataPermUser();
        dataPermUser.setUserId(userId);
        sysDataPermUserMapper.delete(new QueryWrapper<>(dataPermUser));
        sysUserAuthService.removeByUserId(userId);
        return true;
    }

    @Override
    public List<SysUser> getSysUserList(SysUser filter, String orderBy) {
        return sysUserMapper.getSysUserList(filter, orderBy);
    }

    @Override
    public List<SysUser> getSysUserListWithRelation(SysUser filter, String orderBy) {
        List<SysUser> resultList = sysUserMapper.getSysUserList(filter, orderBy);
        // 在缺省生成的代码中，如果查询结果resultList不是Page对象，说明没有分页，那么就很可能是数据导出接口调用了当前方法。
        // 为了避免一次性的大量数据关联，规避因此而造成的系统运行性能冲击，这里手动进行了分批次读取，开发者可按需修改该值。
        int batchSize = resultList instanceof Page ? 0 : 1000;
        this.buildRelationForDataList(resultList, MyRelationParam.normal(), batchSize);
        return resultList;
    }

    @Override
    public List<SysUser> getSysUserListByRoleId(Long roleId, SysUser filter, String orderBy) {
        return sysUserMapper.getSysUserListByRoleId(roleId, filter, orderBy);
    }

    @Override
    public List<SysUser> getNotInSysUserListByRoleId(Long roleId, SysUser filter, String orderBy) {
        return sysUserMapper.getNotInSysUserListByRoleId(roleId, filter, orderBy);
    }

    @Override
    public List<SysUser> getSysUserListByDataPermId(Long dataPermId, SysUser filter, String orderBy) {
        return sysUserMapper.getSysUserListByDataPermId(dataPermId, filter, orderBy);
    }

    @Override
    public List<SysUser> getNotInSysUserListByDataPermId(Long dataPermId, SysUser filter, String orderBy) {
        return sysUserMapper.getNotInSysUserListByDataPermId(dataPermId, filter, orderBy);
    }

    @Override
    public List<SysUser> getSysUserListByDeptPostId(Long deptPostId, SysUser filter, String orderBy) {
        return sysUserMapper.getSysUserListByDeptPostId(deptPostId, filter, orderBy);
    }

    @Override
    public List<SysUser> getNotInSysUserListByDeptPostId(Long deptPostId, SysUser filter, String orderBy) {
        return sysUserMapper.getNotInSysUserListByDeptPostId(deptPostId, filter, orderBy);
    }

    @Override
    public List<SysUser> getSysUserListByPostId(Long postId, SysUser filter, String orderBy) {
        return sysUserMapper.getSysUserListByPostId(postId, filter, orderBy);
    }

    @Override
    public SysUser getSysUserByOpenId(String source, String openId) {
        return sysUserMapper.getSysUserByOpenId(source, openId);
    }

    @Override
    public CallResult verifyRelatedData(
            SysUser sysUser, SysUser originalSysUser, String roleIds, String deptPostIds, String dataPermIds) {
        JSONObject jsonObject = new JSONObject();
        if (StrUtil.isBlank(deptPostIds)) {
            return CallResult.error("数据验证失败，用户的部门岗位数据不能为空！");
        }
        Set<Long> deptPostIdSet =
                Arrays.stream(deptPostIds.split(",")).map(Long::valueOf).collect(Collectors.toSet());
        if (!sysPostService.existAllPrimaryKeys(deptPostIdSet, sysUser.getDeptId())) {
            return CallResult.error("数据验证失败，存在不合法的用户岗位，请刷新后重试！");
        }
        jsonObject.put("deptPostIdSet", deptPostIdSet);
        if (StrUtil.isBlank(roleIds)) {
            return CallResult.error("数据验证失败，用户的角色数据不能为空！");
        }
        Set<Long> roleIdSet = Arrays.stream(
                roleIds.split(",")).map(Long::valueOf).collect(Collectors.toSet());
        if (!sysRoleService.existAllPrimaryKeys(roleIdSet)) {
            return CallResult.error("数据验证失败，存在不合法的用户角色，请刷新后重试！");
        }
        jsonObject.put("roleIdSet", roleIdSet);
        if (StrUtil.isBlank(dataPermIds)) {
            return CallResult.error("数据验证失败，用户的数据权限不能为空！");
        }
        Set<Long> dataPermIdSet = Arrays.stream(
                dataPermIds.split(",")).map(Long::valueOf).collect(Collectors.toSet());
        if (!sysDataPermService.existAllPrimaryKeys(dataPermIdSet)) {
            return CallResult.error("数据验证失败，存在不合法的数据权限，请刷新后重试！");
        }
        jsonObject.put("dataPermIdSet", dataPermIdSet);
        //这里是基于字典的验证。
        if (this.needToVerify(sysUser, originalSysUser, SysUser::getDeptId)
                && !sysDeptService.existId(sysUser.getDeptId())) {
            return CallResult.error("数据验证失败，关联的用户部门Id并不存在，请刷新后重试！");
        }
        return CallResult.ok(jsonObject);
    }
}
