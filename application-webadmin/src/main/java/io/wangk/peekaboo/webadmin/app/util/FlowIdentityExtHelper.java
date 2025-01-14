package io.wangk.peekaboo.webadmin.app.util;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import io.wangk.peekaboo.common.flow.util.BaseFlowIdentityExtHelper;
import io.wangk.peekaboo.common.flow.util.FlowCustomExtFactory;
import io.wangk.peekaboo.common.flow.vo.FlowUserInfoVo;
import io.wangk.peekaboo.webadmin.upms.model.SysDept;
import io.wangk.peekaboo.webadmin.upms.model.SysUser;
import io.wangk.peekaboo.webadmin.upms.model.constant.SysUserStatus;
import io.wangk.peekaboo.webadmin.upms.model.SysDeptPost;
import io.wangk.peekaboo.webadmin.upms.service.SysDeptService;
import io.wangk.peekaboo.webadmin.upms.service.SysUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 为流程提供所需的用户身份相关的等扩展信息的帮助类。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Slf4j
@Component
public class FlowIdentityExtHelper implements BaseFlowIdentityExtHelper {

    @Autowired
    private SysDeptService sysDeptService;
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private FlowCustomExtFactory flowCustomExtFactory;

    @PostConstruct
    public void doRegister() {
        flowCustomExtFactory.registerFlowIdentityExtHelper(this);
    }

    @Override
    public Long getLeaderDeptPostId(Long deptId) {
        List<Long> deptPostIdList = sysDeptService.getLeaderDeptPostIdList(deptId);
        return CollUtil.isEmpty(deptPostIdList) ? null : deptPostIdList.get(0);
    }

    @Override
    public Long getUpLeaderDeptPostId(Long deptId) {
        List<Long> deptPostIdList = sysDeptService.getUpLeaderDeptPostIdList(deptId);
        return CollUtil.isEmpty(deptPostIdList) ? null : deptPostIdList.get(0);
    }

    @Override
    public Map<String, String> getDeptPostIdMap(Long deptId, Set<String> postIdSet) {
        Set<Long> postIdSet2 = postIdSet.stream().map(Long::valueOf).collect(Collectors.toSet());
        List<SysDeptPost> deptPostList = sysDeptService.getSysDeptPostList(deptId, postIdSet2);
        if (CollUtil.isEmpty(deptPostList)) {
            return null;
        }
        Map<String, String> resultMap = new HashMap<>(deptPostList.size());
        deptPostList.forEach(sysDeptPost ->
                resultMap.put(sysDeptPost.getPostId().toString(), sysDeptPost.getDeptPostId().toString()));
        return resultMap;
    }

    @Override
    public Map<String, String> getSiblingDeptPostIdMap(Long deptId, Set<String> postIdSet) {
        Set<Long> postIdSet2 = postIdSet.stream().map(Long::valueOf).collect(Collectors.toSet());
        List<SysDeptPost> deptPostList = sysDeptService.getSiblingSysDeptPostList(deptId, postIdSet2);
        if (CollUtil.isEmpty(deptPostList)) {
            return null;
        }
        Map<String, String> resultMap = new HashMap<>(deptPostList.size());
        for (SysDeptPost deptPost : deptPostList) {
            String deptPostId = resultMap.get(deptPost.getPostId().toString());
            if (deptPostId != null) {
                deptPostId = deptPostId + "," + deptPost.getDeptPostId();
            } else {
                deptPostId = deptPost.getDeptPostId().toString();
            }
            resultMap.put(deptPost.getPostId().toString(), deptPostId);
        }
        return resultMap;
    }

    @Override
    public Map<String, String> getUpDeptPostIdMap(Long deptId, Set<String> postIdSet) {
        SysDept sysDept = sysDeptService.getById(deptId);
        if (sysDept == null || sysDept.getParentId() == null) {
            return null;
        }
        return getDeptPostIdMap(sysDept.getParentId(), postIdSet);
    }

    @Override
    public Set<String> getUsernameListByRoleIds(Set<String> roleIdSet) {
        Set<String> usernameSet = new HashSet<>();
        Set<Long> roleIdSet2 = roleIdSet.stream().map(Long::valueOf).collect(Collectors.toSet());
        SysUser filter = new SysUser();
        filter.setUserStatus(SysUserStatus.STATUS_NORMAL);
        for (Long roleId : roleIdSet2) {
            List<SysUser> userList = sysUserService.getSysUserListByRoleId(roleId, filter, null);
            this.extractAndAppendUsernameList(usernameSet, userList);
        }
        return usernameSet;
    }

    @Override
    public List<FlowUserInfoVo> getUserInfoListByRoleIds(Set<String> roleIdSet) {
        List<FlowUserInfoVo> resultList = new LinkedList<>();
        Set<Long> roleIdSet2 = roleIdSet.stream().map(Long::valueOf).collect(Collectors.toSet());
        SysUser filter = new SysUser();
        filter.setUserStatus(SysUserStatus.STATUS_NORMAL);
        for (Long roleId : roleIdSet2) {
            List<SysUser> userList = sysUserService.getSysUserListByRoleId(roleId, filter, null);
            this.copyUserAuthList(userList, resultList);
        }
        return resultList;
    }

    @Override
    public Set<String> getUsernameListByDeptIds(Set<String> deptIdSet) {
        Set<String> usernameSet = new HashSet<>();
        Set<Long> deptIdSet2 = deptIdSet.stream().map(Long::valueOf).collect(Collectors.toSet());
        for (Long deptId : deptIdSet2) {
            SysUser filter = new SysUser();
            filter.setDeptId(deptId);
            filter.setUserStatus(SysUserStatus.STATUS_NORMAL);
            List<SysUser> userList = sysUserService.getSysUserList(filter, null);
            this.extractAndAppendUsernameList(usernameSet, userList);
        }
        return usernameSet;
    }

    @Override
    public List<FlowUserInfoVo> getUserInfoListByDeptIds(Set<String> deptIdSet) {
        List<FlowUserInfoVo> resultList = new LinkedList<>();
        Set<Long> deptIdSet2 = deptIdSet.stream().map(Long::valueOf).collect(Collectors.toSet());
        for (Long deptId : deptIdSet2) {
            SysUser filter = new SysUser();
            filter.setDeptId(deptId);
            filter.setUserStatus(SysUserStatus.STATUS_NORMAL);
            List<SysUser> userList = sysUserService.getListByFilter(filter);
            this.copyUserAuthList(userList, resultList);
        }
        return resultList;
    }

    @Override
    public Set<String> getUsernameListByPostIds(Set<String> postIdSet) {
        Set<String> usernameSet = new HashSet<>();
        Set<Long> postIdSet2 = postIdSet.stream().map(Long::valueOf).collect(Collectors.toSet());
        SysUser filter = new SysUser();
        filter.setUserStatus(SysUserStatus.STATUS_NORMAL);
        for (Long postId : postIdSet2) {
            List<SysUser> userList = sysUserService.getSysUserListByPostId(postId, filter, null);
            this.extractAndAppendUsernameList(usernameSet, userList);
        }
        return usernameSet;
    }

    @Override
    public List<FlowUserInfoVo> getUserInfoListByPostIds(Set<String> postIdSet) {
        List<FlowUserInfoVo> resultList = new LinkedList<>();
        Set<Long> postIdSet2 = postIdSet.stream().map(Long::valueOf).collect(Collectors.toSet());
        SysUser filter = new SysUser();
        filter.setUserStatus(SysUserStatus.STATUS_NORMAL);
        for (Long postId : postIdSet2) {
            List<SysUser> userList = sysUserService.getSysUserListByPostId(postId, filter, null);
            this.copyUserAuthList(userList, resultList);
        }
        return resultList;
    }

    @Override
    public Set<String> getUsernameListByDeptPostIds(Set<String> deptPostIdSet) {
        Set<String> usernameSet = new HashSet<>();
        Set<Long> deptPostIdSet2 = deptPostIdSet.stream().map(Long::valueOf).collect(Collectors.toSet());
        SysUser filter = new SysUser();
        filter.setUserStatus(SysUserStatus.STATUS_NORMAL);
        for (Long deptPostId : deptPostIdSet2) {
            List<SysUser> userList = sysUserService.getSysUserListByDeptPostId(deptPostId, filter, null);
            this.extractAndAppendUsernameList(usernameSet, userList);
        }
        return usernameSet;
    }

    @Override
    public List<FlowUserInfoVo> getUserInfoListByDeptPostIds(Set<String> deptPostIdSet) {
        List<FlowUserInfoVo> resultList = new LinkedList<>();
        if (CollUtil.isEmpty(deptPostIdSet)) {
            return resultList;
        }
        Set<Long> deptPostIdSet2 = deptPostIdSet.stream().map(Long::valueOf).collect(Collectors.toSet());
        SysUser filter = new SysUser();
        filter.setUserStatus(SysUserStatus.STATUS_NORMAL);
        for (Long deptPostId : deptPostIdSet2) {
            List<SysUser> userList = sysUserService.getSysUserListByDeptPostId(deptPostId, filter, null);
            this.copyUserAuthList(userList, resultList);
        }
        return resultList;
    }

    @Override
    public List<FlowUserInfoVo> getUserInfoListByUsernameSet(Set<String> usernameSet) {
        List<FlowUserInfoVo> resultList = new LinkedList<>();
        if (CollUtil.isEmpty(usernameSet)) {
            return resultList;
        }
        List<SysUser> userList = sysUserService.getInList("loginName", usernameSet);
        this.copyUserAuthList(userList, resultList);
        return resultList;
    }

    @Override
    public Boolean supprtDataPerm() {
        return true;
    }

    @Override
    public Map<String, String> mapUserShowNameByLoginName(Set<String> loginNameSet) {
        if (CollUtil.isEmpty(loginNameSet)) {
            return new HashMap<>(1);
        }
        Map<String, String> resultMap = new HashMap<>(loginNameSet.size());
        List<SysUser> userList = sysUserService.getInList("loginName", loginNameSet);
        userList.forEach(user -> resultMap.put(user.getLoginName(), user.getShowName()));
        return resultMap;
    }

    private void extractAndAppendUsernameList(Set<String> resultUsernameList, List<SysUser> userList) {
        List<String> usernameList = userList.stream().map(SysUser::getLoginName).collect(Collectors.toList());
        if (CollUtil.isNotEmpty(usernameList)) {
            resultUsernameList.addAll(usernameList);
        }
    }

    private void copyUserAuthList(List<SysUser> userList, List<FlowUserInfoVo> resultList) {
        if (CollUtil.isEmpty(userList)) {
            return;
        }
        for (SysUser user : userList) {
            FlowUserInfoVo flowUserInfoVo = BeanUtil.copyProperties(user, FlowUserInfoVo.class);
            if (StrUtil.isNotBlank(user.getUserAuthInfo())) {
                List<FlowUserInfoVo.UserAuth> userAuthList =
                        JSON.parseArray(user.getUserAuthInfo(), FlowUserInfoVo.UserAuth.class);
                flowUserInfoVo.setUserAuthList(userAuthList);
            }
            resultList.add(flowUserInfoVo);
        }
    }
}
