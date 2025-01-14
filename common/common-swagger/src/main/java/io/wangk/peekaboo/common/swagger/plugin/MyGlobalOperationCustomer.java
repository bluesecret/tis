package io.wangk.peekaboo.common.swagger.plugin;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import io.wangk.peekaboo.common.core.annotation.MyRequestBody;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.models.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.customizers.GlobalOperationCustomizer;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;
import java.util.stream.Stream;

/**
 * @author <a href="xiaoymin@foxmail.com">xiaoymin@foxmail.com</a>
 */
@Slf4j
@Component
public class MyGlobalOperationCustomer implements GlobalOperationCustomizer {

    /**
     * 注解包路径名称
     */
    private static final String REF_KEY = "$ref";
    private static final String REF_SCHEMA_PREFIX = "#/components/schemas/";
    private final Map<Class<?>, Set<String>> cacheClassProperties = MapUtil.newHashMap();
    private static final String EXTENSION_ORANGE_FORM_NAME = "x-orangeforms";
    private static final String EXTENSION_ORANGE_FORM_IGNORE_NAME = "x-orangeforms-ignore-parameters";

    @Override
    public Operation customize(Operation operation, HandlerMethod handlerMethod) {
        this.handleSummary(operation, handlerMethod);
        if (handlerMethod.getMethod().getParameterCount() <= 0) {
            return operation;
        }
        Parameter[] parameters = handlerMethod.getMethod().getParameters();
        if (ArrayUtil.isEmpty(parameters)) {
            return operation;
        }
        Map<String, Object> properties = MapUtil.newHashMap();
        Map<String, Object> extensions = MapUtil.newHashMap();
        Set<String> ignoreFieldName = CollUtil.newHashSet();
        List<String> required = new ArrayList<>();
        Map<String, io.swagger.v3.oas.annotations.Parameter> paramMap = getParameterDescription(handlerMethod.getMethod());
        for (Parameter parameter : parameters) {
            Annotation[] annos = parameter.getAnnotations();
            if (ArrayUtil.isEmpty(annos)) {
                continue;
            }
            long count = Stream.of(annos).filter(anno -> anno.annotationType().equals(MyRequestBody.class)).count();
            if (count > 0) {
                this.handleParameterDetail(parameter, properties, paramMap, ignoreFieldName, required);
            }
        }
        if (!properties.isEmpty()) {
            extensions.put("properties", properties);
            extensions.put("type", "object");
            //required字段
            if (!required.isEmpty()) {
                extensions.put("required", required);
            }
            String generateSchemaName = handlerMethod.getMethod().getName() + "DynamicReq";
            Map<String, Object> orangeExtensions = MapUtil.newHashMap();
            orangeExtensions.put(generateSchemaName, extensions);
            //增加扩展属性
            operation.addExtension(EXTENSION_ORANGE_FORM_NAME, orangeExtensions);
            if (!ignoreFieldName.isEmpty()) {
                operation.addExtension(EXTENSION_ORANGE_FORM_IGNORE_NAME, ignoreFieldName);
            }
        }
        return operation;
    }

    private void handleSummary(Operation operation, HandlerMethod handlerMethod) {
        io.swagger.v3.oas.annotations.Operation operationAnno =
                handlerMethod.getMethod().getAnnotation(io.swagger.v3.oas.annotations.Operation.class);
        if (operationAnno == null || StrUtil.isBlank(operationAnno.summary())) {
            operation.setSummary(handlerMethod.getMethod().getName());
        }
    }

    private void handleParameterDetail(
            Parameter parameter,
            Map<String, Object> properties,
            Map<String, io.swagger.v3.oas.annotations.Parameter> paramMap,
            Set<String> ignoreFieldName,
            List<String> required) {
        Class<?> parameterType = parameter.getType();
        String schemaName = parameterType.getSimpleName();
        //添加忽律参数名称
        ignoreFieldName.addAll(getClassFields(parameterType));
        //处理schema注解别名的情况
        Schema schema = parameterType.getAnnotation(Schema.class);
        if (schema != null && StrUtil.isNotBlank(schema.name())) {
            schemaName = schema.name();
        }
        Map<String, Object> value = MapUtil.newHashMap();
        //此处需要判断parameter的基础数据类型
        if (parameterType.isPrimitive() || parameterType.getName().startsWith("java.lang")) {
            //基础数据类型
            ignoreFieldName.add(parameter.getName());
            value.put("type", parameterType.getSimpleName().toLowerCase());
            //判断format
        } else if (Collection.class.isAssignableFrom(parameterType)) {
            //集合类型
            value.put("type", "array");
            //获取泛型
            getGenericType(parameterType, parameter.getParameterizedType())
                    .ifPresent(s -> value.put("items", MapUtil.builder(REF_KEY, REF_SCHEMA_PREFIX + s).build()));
        } else {
            //引用类型
            value.put(REF_KEY, REF_SCHEMA_PREFIX + schemaName);
        }
        //补一个description
        io.swagger.v3.oas.annotations.Parameter paramAnnotation = paramMap.get(parameter.getName());
        if (paramAnnotation != null) {
            //忽略该参数
            ignoreFieldName.add(paramAnnotation.name());
            value.put("description", paramAnnotation.description());
            if (StrUtil.isNotBlank(paramAnnotation.example())) {
                value.put("default", paramAnnotation.example());
            }
            // required参数
            if (paramAnnotation.required()) {
                required.add(parameter.getName());
            }
        }
        properties.put(parameter.getName(), value);
    }

    private Optional<String> getGenericType(Class<?> clazz, Type type) {
        Type genericSuperclass = clazz.getGenericSuperclass();
        if (genericSuperclass instanceof ParameterizedType || type instanceof ParameterizedType) {
            if (type instanceof ParameterizedType) {
                genericSuperclass = type;
            }
            ParameterizedType parameterizedType = (ParameterizedType) genericSuperclass;
            Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
            return Optional.of(((Class) actualTypeArguments[0]).getSimpleName());
        }
        return Optional.empty();
    }

    private Set<String> getClassFields(Class<?> parameterType) {
        if (parameterType == null) {
            return Collections.emptySet();
        }
        if (cacheClassProperties.containsKey(parameterType)) {
            return cacheClassProperties.get(parameterType);
        }
        Set<String> fieldNames = new HashSet<>();
        try {
            Field[] fields = parameterType.getDeclaredFields();
            if (fields.length > 0) {
                for (Field field : fields) {
                    fieldNames.add(field.getName());
                }
                cacheClassProperties.put(parameterType, fieldNames);
                return fieldNames;
            }
        } catch (Exception e) {
            //ignore
        }
        return Collections.emptySet();
    }

    private Map<String, io.swagger.v3.oas.annotations.Parameter> getParameterDescription(Method method) {
        Parameters parameters = method.getAnnotation(Parameters.class);
        Map<String, io.swagger.v3.oas.annotations.Parameter> resultMap = MapUtil.newHashMap();
        if (parameters != null) {
            io.swagger.v3.oas.annotations.Parameter[] parameters1 = parameters.value();
            if (parameters1 != null && parameters1.length > 0) {
                for (io.swagger.v3.oas.annotations.Parameter parameter : parameters1) {
                    resultMap.put(parameter.name(), parameter);
                }
                return resultMap;
            }
        } else {
            io.swagger.v3.oas.annotations.Parameter parameter =
                    method.getAnnotation(io.swagger.v3.oas.annotations.Parameter.class);
            if (parameter != null) {
                resultMap.put(parameter.name(), parameter);
            }
        }
        return resultMap;
    }
}
