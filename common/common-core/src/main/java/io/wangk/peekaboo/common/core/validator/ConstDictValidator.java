package io.wangk.peekaboo.common.core.validator;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 数据字段自定义验证，用于验证Model中关联的常量字典值的合法性。
 *
 * @author wangk
 * @date 2025-01-14
 */
public class ConstDictValidator implements ConstraintValidator<ConstDictRef, Object> {

    private ConstDictRef constDictRef;

    @Override
    public void initialize(ConstDictRef constDictRef) {
        this.constDictRef = constDictRef;
    }

    @Override
    public boolean isValid(Object s, ConstraintValidatorContext constraintValidatorContext) {
        if (ObjectUtil.isEmpty(s)) {
            return true;
        }
        Method method =
                ReflectUtil.getMethodByName(constDictRef.constDictClass(), "isValid");
        if (!constDictRef.multiSelect()) {
            return ReflectUtil.invokeStatic(method, s);
        }
        List<String> stringKeys = StrUtil.split(s.toString(), StrUtil.COMMA);
        List<? extends Serializable> keys;
        if (constDictRef.keyClass().equals(Integer.class)) {
            keys = stringKeys.stream().map(Integer::valueOf).collect(Collectors.toList());
        } else if (constDictRef.keyClass().equals(Long.class)) {
            keys = stringKeys.stream().map(Long::valueOf).collect(Collectors.toList());
        } else {
            keys = stringKeys;
        }
        for (Object key : keys) {
            if (!(boolean) ReflectUtil.invokeStatic(method, key)) {
                return false;
            }
        }
        return true;
    }
}
