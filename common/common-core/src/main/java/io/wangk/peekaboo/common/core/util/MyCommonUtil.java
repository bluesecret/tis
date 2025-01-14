package io.wangk.peekaboo.common.core.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.text.StrFormatter;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import io.wangk.peekaboo.common.core.constant.AppDeviceType;
import io.wangk.peekaboo.common.core.constant.ApplicationConstant;
import io.wangk.peekaboo.common.core.exception.MyRuntimeException;
import io.wangk.peekaboo.common.core.object.Tuple2;
import io.wangk.peekaboo.common.core.validator.AddGroup;
import io.wangk.peekaboo.common.core.validator.UpdateGroup;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.groups.Default;
import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 脚手架中常用的基本工具方法集合，一般而言工程内部使用的方法。
 *
 * @author wangk
 * @date 2025-01-14
 */
public class MyCommonUtil {

    private static final Validator VALIDATOR;
    private static final String REGEX_VAR = "\\$\\{(.+?)\\}";

    static {
        VALIDATOR = Validation.buildDefaultValidatorFactory().getValidator();
    }

    /**
     * 创建uuid。
     *
     * @return 返回uuid。
     */
    public static String generateUuid() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 对用户密码进行加盐后加密。
     *
     * @param password     明文密码。
     * @param passwordSalt 盐值。
     * @return 加密后的密码。
     */
    public static String encrptedPassword(String password, String passwordSalt) {
        return DigestUtil.md5Hex(password + passwordSalt);
    }

    /**
     * 这个方法一般用于Controller对于入口参数的基本验证。
     * 对于字符串，如果为空字符串，也将视为Blank，同时返回true。
     *
     * @param objs 一组参数。
     * @return 返回是否存在null或空字符串的参数。
     */
    public static boolean existBlankArgument(Object...objs) {
        for (Object obj : objs) {
            if (MyCommonUtil.isBlankOrNull(obj)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 结果和 existBlankArgument 相反。
     *
     * @param objs 一组参数。
     * @return 返回是否存在null或空字符串的参数。
     */
    public static boolean existNotBlankArgument(Object...objs) {
        for (Object obj : objs) {
            if (!MyCommonUtil.isBlankOrNull(obj)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 验证参数是否为空。
     *
     * @param obj 待判断的参数。
     * @return 空或者null返回true，否则false。
     */
    public static boolean isBlankOrNull(Object obj) {
        if (obj instanceof Collection) {
            return CollUtil.isEmpty((Collection<?>) obj);
        }
        return obj == null || (obj instanceof CharSequence && StrUtil.isBlank((CharSequence) obj));
    }

    /**
     * 验证参数是否为非空。
     *
     * @param obj 待判断的参数。
     * @return 空或者null返回false，否则true。
     */
    public static boolean isNotBlankOrNull(Object obj) {
        return !isBlankOrNull(obj);
    }

    /**
     * 判断source是否等于其中任何一个对象值。
     *
     * @param source 源对象。
     * @param others 其他对象。
     * @return 等于其中任何一个返回true，否则false。
     */
    public static boolean equalsAny(Object source, Object...others) {
        for (Object one : others) {
            if (ObjectUtil.equal(source, one)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断模型对象是否通过校验，没有通过返回具体的校验错误信息。
     *
     * @param model  带校验的model。
     * @param groups Validate绑定的校验组。
     * @return 没有错误返回null，否则返回具体的错误信息。
     */
    public static <T> String getModelValidationError(T model, Class<?>...groups) {
        if (model != null) {
            Set<ConstraintViolation<T>> constraintViolations = VALIDATOR.validate(model, groups);
            if (!constraintViolations.isEmpty()) {
                Iterator<ConstraintViolation<T>> it = constraintViolations.iterator();
                ConstraintViolation<T> constraint = it.next();
                return constraint.getMessage();
            }
        }
        return null;
    }

    /**
     * 判断模型对象是否通过校验，没有通过返回具体的校验错误信息。
     *
     * @param model     带校验的model。
     * @param forUpdate 是否为更新。
     * @return 没有错误返回null，否则返回具体的错误信息。
     */
    public static <T> String getModelValidationError(T model, boolean forUpdate) {
        if (model != null) {
            Set<ConstraintViolation<T>> constraintViolations;
            if (forUpdate) {
                constraintViolations = VALIDATOR.validate(model, Default.class, UpdateGroup.class);
            } else {
                constraintViolations = VALIDATOR.validate(model, Default.class, AddGroup.class);
            }
            if (!constraintViolations.isEmpty()) {
                Iterator<ConstraintViolation<T>> it = constraintViolations.iterator();
                ConstraintViolation<T> constraint = it.next();
                return constraint.getMessage();
            }
        }
        return null;
    }

    /**
     * 判断模型对象是否通过校验，没有通过返回具体的校验错误信息。
     *
     * @param modelList 带校验的model列表。
     * @param groups    Validate绑定的校验组。
     * @return 没有错误返回null，否则返回具体的错误信息。
     */
    public static <T> String getModelValidationError(List<T> modelList, Class<?>... groups) {
        if (CollUtil.isNotEmpty(modelList)) {
            for (T model : modelList) {
                String errorMessage = getModelValidationError(model, groups);
                if (StrUtil.isNotBlank(errorMessage)) {
                    return errorMessage;
                }
            }
        }
        return null;
    }

    /**
     * 判断模型对象是否通过校验，没有通过返回具体的校验错误信息。
     *
     * @param modelList 带校验的model列表。
     * @param forUpdate 是否为更新。
     * @return 没有错误返回null，否则返回具体的错误信息。
     */
    public static <T> String getModelValidationError(List<T> modelList, boolean forUpdate) {
        if (CollUtil.isNotEmpty(modelList)) {
            for (T model : modelList) {
                String errorMessage = getModelValidationError(model, forUpdate);
                if (StrUtil.isNotBlank(errorMessage)) {
                    return errorMessage;
                }
            }
        }
        return null;
    }

    /**
     * 拼接参数中的字符串列表，用指定分隔符进行分割，同时每个字符串对象用单引号括起来。
     *
     * @param dataList  字符串集合。
     * @param separator 分隔符。
     * @return 拼接后的字符串。
     */
    public static String joinString(Collection<String> dataList, final char separator) {
        int index = 0;
        StringBuilder sb = new StringBuilder(128);
        for (String data : dataList) {
            sb.append("'").append(data).append("'");
            if (index++ != dataList.size() - 1) {
                sb.append(separator);
            }
        }
        return sb.toString();
    }

    /**
     * 将SQL Like中的通配符替换为字符本身的含义，以便于比较。
     *
     * @param str 待替换的字符串。
     * @return 替换后的字符串。
     */
    public static String replaceSqlWildcard(String str) {
        if (StrUtil.isBlank(str)) {
            return str;
        }
        return StrUtil.replaceChars(StrUtil.replaceChars(str, "_", "\\_"), "%", "\\%");
    }

    /**
     * 获取对象中，非空字段的名字列表。
     *
     * @param object 数据对象。
     * @param clazz  数据对象的class类型。
     * @param <T>    数据对象类型。
     * @return 数据对象中，值不为NULL的字段数组。
     */
    public static <T> String[] getNotNullFieldNames(T object, Class<T> clazz) {
        Field[] fields = ReflectUtil.getFields(clazz);
        List<String> fieldNameList = Arrays.stream(fields)
                .filter(f -> ReflectUtil.getFieldValue(object, f) != null)
                .map(Field::getName).collect(Collectors.toList());
        if (CollUtil.isNotEmpty(fieldNameList)) {
            return fieldNameList.toArray(new String[]{});
        }
        return new String[]{};
    }

    /**
     * 获取请求头中的设备信息。
     *
     * @return 设备类型，具体值可参考AppDeviceType常量类。
     */
    public static int getDeviceType() {
        // 缺省都按照Web登录方式设置，如果前端header中的值为不合法值，这里也不会报错，而是使用Web缺省方式。
        int deviceType = AppDeviceType.WEB;
        String deviceTypeString = ContextUtil.getHttpRequest().getHeader("deviceType");
        if (StrUtil.isNotBlank(deviceTypeString)) {
            Integer type = Integer.valueOf(deviceTypeString);
            if (AppDeviceType.isValid(type)) {
                deviceType = type;
            }
        }
        return deviceType;
    }

    /**
     * 获取请求头中的设备信息。
     *
     * @return 设备类型，具体值可参考AppDeviceType常量类。
     */
    public static String getDeviceTypeWithString() {
        int deviceType = getDeviceType();
        return AppDeviceType.getDeviceTypeName(deviceType);
    }

    /**
     * 获取第三方应用的编码。
     *
     * @return 第三方应用编码。
     */
    public static String getAppCodeFromRequest() {
        HttpServletRequest request = ContextUtil.getHttpRequest();
        String appCode = request.getHeader("AppCode");
        if (StrUtil.isBlank(appCode)) {
            appCode = request.getParameter("AppCode");
        }
        return appCode;
    }

    /**
     * 获取用户身份令牌。
     * 
     * @param tokenKey 令牌的Key。
     * @return 用户身份令牌。
     */
    public static String getTokenFromRequest(String tokenKey) {
        HttpServletRequest request = ContextUtil.getHttpRequest();
        String token = request.getHeader(tokenKey);
        if (StrUtil.isBlank(token)) {
            token = request.getParameter(tokenKey);
        }
        if (StrUtil.isBlank(token)) {
            token = request.getHeader(ApplicationConstant.HTTP_HEADER_INTERNAL_TOKEN);
        }
        return token;
    }

    /**
     * 转换为字典格式的数据列表。
     *
     * @param dataList   源数据列表。
     * @param idGetter   获取字典Id字段值的函数方法。
     * @param nameGetter 获取字典名字段值的函数方法。
     * @param <M>        源数据对象类型。
     * @param <R>        字典Id的类型。
     * @return 字典格式的数据列表。
     */
    public static <M, R> List<Map<String, Object>> toDictDataList(
            Collection<M> dataList, Function<M, R> idGetter, Function<M, Object> nameGetter) {
        if (CollUtil.isEmpty(dataList)) {
            return new LinkedList<>();
        }
        return dataList.stream().map(item -> {
            Map<String, Object> dataMap = new HashMap<>(2);
            dataMap.put(ApplicationConstant.DICT_ID, idGetter.apply(item));
            dataMap.put(ApplicationConstant.DICT_NAME, nameGetter.apply(item));
            return dataMap;
        }).collect(Collectors.toList());
    }

    /**
     * 转换为树形字典格式的数据列表。
     *
     * @param dataList       源数据列表。
     * @param idGetter       获取字典Id字段值的函数方法。
     * @param nameGetter     获取字典名字段值的函数方法。
     * @param parentIdGetter 获取字典Id父字段值的函数方法。
     * @param <M>            源数据对象类型。
     * @param <R>            字典Id的类型。
     * @return 字典格式的数据列表。
     */
    public static <M, R> List<Map<String, Object>> toDictDataList(
            Collection<M> dataList,
            Function<M, R> idGetter,
            Function<M, Object> nameGetter,
            Function<M, R> parentIdGetter) {
        if (CollUtil.isEmpty(dataList)) {
            return new LinkedList<>();
        }
        return dataList.stream().map(item -> {
            Map<String, Object> dataMap = new HashMap<>(2);
            dataMap.put(ApplicationConstant.DICT_ID, idGetter.apply(item));
            dataMap.put(ApplicationConstant.DICT_NAME, nameGetter.apply(item));
            dataMap.put(ApplicationConstant.PARENT_ID, parentIdGetter.apply(item));
            return dataMap;
        }).collect(Collectors.toList());
    }

    /**
     * 转换为字典格式的数据列表，同时支持一个附加字段。
     *
     * @param dataList    源数据列表。
     * @param idGetter    获取字典Id字段值的函数方法。
     * @param nameGetter  获取字典名字段值的函数方法。
     * @param extraName   附加字段名。。
     * @param extraGetter 获取附加字段值的函数方法。
     * @param <M>         源数据对象类型。
     * @param <R>         字典Id的类型。
     * @param <E>         附加字段值的类型。
     * @return 字典格式的数据列表。
     */
    public static <M, R, E> List<Map<String, Object>> toDictDataList(
            Collection<M> dataList,
            Function<M, R> idGetter,
            Function<M, Object> nameGetter,
            String extraName,
            Function<M, E> extraGetter) {
        if (CollUtil.isEmpty(dataList)) {
            return new LinkedList<>();
        }
        return dataList.stream().map(item -> {
            Map<String, Object> dataMap = new HashMap<>(2);
            dataMap.put(ApplicationConstant.DICT_ID, idGetter.apply(item));
            dataMap.put(ApplicationConstant.DICT_NAME, nameGetter.apply(item));
            dataMap.put(extraName, extraGetter.apply(item));
            return dataMap;
        }).collect(Collectors.toList());
    }

    /**
     * 将SQL查询条件中的变量值替换为SQL拼接的字符串值。
     * 
     * @param value 参数值。
     * @return 转换后的参数字符串。
     */
    public static String convertSqlParamValue(Object value) {
        if (value == null) {
            return "null";
        }
        if (value instanceof Number) {
            return String.valueOf(value);
        }
        if (value instanceof Boolean) {
            return String.valueOf(value.equals(Boolean.TRUE) ? 1 : 0);
        }
        StringBuilder builder = new StringBuilder();
        builder.append("'");
        if (value instanceof Date) {
            builder.append(DateUtil.format((Date) value, MyDateUtil.COMMON_SHORT_DATETIME_FORMAT));
        } else if (value instanceof String) {
            builder.append(value);
        }
        builder.append("'");
        return builder.toString();
    }

    /**
     * 获取当前请求的traceId。
     *
     * @return 当前请求的traceId。
     */
    public static String getTraceId() {
        HttpServletRequest request = ContextUtil.getHttpRequest();
        if (request == null) {
            return null;
        }
        return request.getHeader(ApplicationConstant.HTTP_HEADER_TRACE_ID);
    }

    /**
     * 提取所有${}标记的变量名。
     *
     * @param str 待提取变量的字符串。
     * @return 提取后的变量名列表。
     */
    public static List<String> extractVariableNames(String str) {
        List<String> variables = ReUtil.findAll(REGEX_VAR, str, 0);
        if (CollUtil.isNotEmpty(variables)) {
            variables = variables.stream().map(v -> v.substring(2, v.length() - 1)).collect(Collectors.toList());
        }
        return variables;
    }

    /**
     * 提取所有${}标记的变量名，同时用指定的字符串替换变量。
     *
     * @param str         待提取变量的字符串。
     * @param replacement 替换字符串。
     * @return 第一个参数是替换后的字符串，第二个参数是变量名列表。
     */
    public static Tuple2<String, List<String>> findAndReplaceAllVariables(String str, String replacement) {
        List<String> variables = ReUtil.findAll(REGEX_VAR, str, 0);
        if (CollUtil.isNotEmpty(variables)) {
            str = str.replaceAll(REGEX_VAR, replacement);
            variables = variables.stream().map(v -> v.substring(2, v.length() - 1)).collect(Collectors.toList());
        }
        return new Tuple2<>(str, variables);
    }

    /**
     * 提取所有${}标记的变量名，同时用指定的变量值替换，如果变量名中存在“.”, 则按照JSON路径提取值。
     *
     * @param str          待提取变量的字符串。
     * @param variableData key是变量名，value是变量值。
     * @return 替换后的字符串。
     */
    public static String replaceAllWithVariableData(String str, JSONObject variableData) {
        if (StrUtil.isBlank(str)) {
            return str;
        }
        //提取所有变量名，变量名中包含${}。
        List<String> variables = ReUtil.findAll(REGEX_VAR, str, 0);
        if (CollUtil.isNotEmpty(variables)) {
            for (String v : variables) {
                //变量名中包含${}，去除${}。
                String normalizedVariable = v.substring(2, v.length() - 1);
                //获取变量值。
                Object value = verifyAndGetVariable(normalizedVariable, variableData);
                str = StrUtil.replace(str, v, value == null ? StrUtil.EMPTY : value.toString());
            }
        }
        return str;
    }

    private static Object verifyAndGetVariable(String name, JSONObject variableData) {
        String variableName = name;
        //判断是否包含., 如果包含，则只取第一个.之前的字符串作为变量名。
        if (StrUtil.contains(name, StrUtil.DOT)) {
            variableName = StrUtil.subBefore(name, StrUtil.DOT, false);
        }
        //判断变量名是否存在，如果不存在，则抛出异常。
        if (!variableData.containsKey(variableName)) {
            throw new MyRuntimeException(StrFormatter.format("变量值 [{}] 不存在！", name));
        }
        //判断变量名是否包含., 如果包含，则取变量名后面的字符串作为变量路径，并获取变量值。
        if (!StrUtil.contains(name, StrUtil.DOT)) {
            return variableData.get(variableName);
        }
        //获取变量值。
        JSONObject variableObject = variableData.getJSONObject(variableName);
        if (variableObject == null) {
            return null;
        }
        //获取变量路径, 并获取变量值。
        String variablePath = StrUtil.subAfter(name, StrUtil.DOT, false);
        return JSONPath.compile(variablePath).eval(variableObject);
    }

    /**
     * 私有构造函数，明确标识该常量类的作用。
     */
    private MyCommonUtil() {
    }
}
