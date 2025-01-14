package io.wangk.peekaboo.common.online.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.wangk.peekaboo.common.core.annotation.MyDataSourceResolver;
import io.wangk.peekaboo.common.core.base.dao.BaseDaoMapper;
import io.wangk.peekaboo.common.core.base.service.BaseService;
import io.wangk.peekaboo.common.core.constant.ApplicationConstant;
import io.wangk.peekaboo.common.core.object.TokenData;
import io.wangk.peekaboo.common.core.util.DefaultDataSourceResolver;
import io.wangk.peekaboo.common.core.util.RedisKeyUtil;
import io.wangk.peekaboo.common.online.dao.OnlineFormUserExtMapper;
import io.wangk.peekaboo.common.online.model.OnlineFormUserExt;
import io.wangk.peekaboo.common.online.service.OnlineFormUserExtService;
import io.wangk.peekaboo.common.sequence.wrapper.IdGeneratorWrapper;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

/**
 * 在线表单用户数据操作服务类。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Slf4j
@MyDataSourceResolver(
        resolver = DefaultDataSourceResolver.class,
        intArg = ApplicationConstant.COMMON_FLOW_AND_ONLINE_DATASOURCE_TYPE)
@Service("onlineFormUserExtService")
public class OnlineFormUserExtServiceImpl extends BaseService<OnlineFormUserExt, Long> implements OnlineFormUserExtService {

    @Autowired
    private IdGeneratorWrapper idGenerator;
    @Autowired
    private OnlineFormUserExtMapper onlineFormUserExtMapper;
    @Autowired
    private RedissonClient redissonClient;

    /**
     * 返回当前Service的主表Mapper对象。
     *
     * @return 主表Mapper对象。
     */
    @Override
    protected BaseDaoMapper<OnlineFormUserExt> mapper() {
        return onlineFormUserExtMapper;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public OnlineFormUserExt saveNewOrUpdate(OnlineFormUserExt data) {
        RBucket<String> bucket = redissonClient.getBucket(RedisKeyUtil.makeUserFormExtKey(data.getOnlineFormId()));
        if (bucket.isExists()) {
            bucket.delete();
        }
        if (data.getId() == null) {
            onlineFormUserExtMapper.insert(this.buildDefaultValue(data));
        } else {
            onlineFormUserExtMapper.updateById(this.buildDefaultValue(data));
        }
        return data;
    }

    @Override
    public OnlineFormUserExt getByFormId(Long formId) {
        RBucket<String> bucket = redissonClient.getBucket(RedisKeyUtil.makeUserFormExtKey(formId));
        if (bucket.isExists()) {
            return JSON.parseObject(bucket.get(), OnlineFormUserExt.class);
        }
        LambdaQueryWrapper<OnlineFormUserExt> qw = new LambdaQueryWrapper<>();
        qw.eq(OnlineFormUserExt::getOnlineFormId, formId);
        qw.eq(OnlineFormUserExt::getUserId, TokenData.takeFromRequest().getUserId());
        OnlineFormUserExt data = onlineFormUserExtMapper.selectOne(qw);
        if (data == null) {
            data = new OnlineFormUserExt();
            data.setOnlineFormId(formId);
            data.setUserId(TokenData.takeFromRequest().getUserId());
        }
        bucket.set(JSON.toJSONString(data), 10, TimeUnit.MINUTES);
        return data;
    }

    private OnlineFormUserExt buildDefaultValue(OnlineFormUserExt data) {
        if (data.getId() == null) {
            data.setId(idGenerator.nextLongId());
        }
        data.setUserId(TokenData.takeFromRequest().getUserId());
        return data;
    }
}
