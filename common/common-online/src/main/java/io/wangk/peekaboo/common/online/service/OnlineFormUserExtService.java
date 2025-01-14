package io.wangk.peekaboo.common.online.service;

import io.wangk.peekaboo.common.core.base.service.IBaseService;
import io.wangk.peekaboo.common.online.model.OnlineFormUserExt;

/**
 * 在线表单用户扩展数据操作服务接口。
 *
 * @author wangk
 * @date 2025-01-14
 */
public interface OnlineFormUserExtService extends IBaseService<OnlineFormUserExt, Long> {

    /**
     * 保存新增或者更新对象。
     *
     * @param data 数据对象。
     * @return 返回对象。
     */
    OnlineFormUserExt saveNewOrUpdate(OnlineFormUserExt data);

    /**
     * 获取指定在线表单的关联用户扩展数据。
     *
     * @param formId 在线表单Id。
     * @return 返回关联用户扩展数据。
     */
    OnlineFormUserExt getByFormId(Long formId);
}
