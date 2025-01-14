package io.wangk.peekaboo.common.core.config;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import io.wangk.peekaboo.common.core.interceptor.MyRequestArgumentResolver;
import io.wangk.peekaboo.common.core.util.ContextUtil;
import io.wangk.peekaboo.common.core.util.MyDateUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import jakarta.servlet.http.HttpServletRequest;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * 所有的项目拦截器、参数解析器、消息对象转换器都在这里集中配置。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Configuration
public class CommonWebMvcConfig implements WebMvcConfigurer {

    @Bean
    public MethodValidationPostProcessor methodValidationPostProcessor() {
        return new MethodValidationPostProcessor();
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        // 添加MyRequestBody参数解析器
        argumentResolvers.add(new MyRequestArgumentResolver());
    }

    private HttpMessageConverter<String> responseBodyConverter() {
        return new StringHttpMessageConverter(StandardCharsets.UTF_8);
    }

    @Bean
    public FastJsonHttpMessageConverter fastJsonHttpMessageConverter() {
        FastJsonHttpMessageConverter fastConverter = new MyFastJsonHttpMessageConverter();
        List<MediaType> supportedMediaTypes = new ArrayList<>();
        supportedMediaTypes.add(MediaType.APPLICATION_JSON);
        supportedMediaTypes.add(MediaType.APPLICATION_FORM_URLENCODED);
        fastConverter.setSupportedMediaTypes(supportedMediaTypes);
        FastJsonConfig fastJsonConfig = new FastJsonConfig();
        fastJsonConfig.setSerializerFeatures(
                SerializerFeature.PrettyFormat,
                SerializerFeature.DisableCircularReferenceDetect,
                SerializerFeature.IgnoreNonFieldGetter);
        fastJsonConfig.setDateFormat(MyDateUtil.COMMON_SHORT_DATETIME_FORMAT);
        fastConverter.setFastJsonConfig(fastJsonConfig);
        return fastConverter;
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(responseBodyConverter());
        converters.add(new ByteArrayHttpMessageConverter());
        converters.add(fastJsonHttpMessageConverter());
    }

    public static class MyFastJsonHttpMessageConverter extends FastJsonHttpMessageConverter {

        @Override
        public boolean canWrite(Type type, Class<?> clazz, MediaType mediaType) {
            HttpServletRequest request = ContextUtil.getHttpRequest();
            if (request == null) {
                return super.canWrite(type, clazz, mediaType);
            }
            if (request.getRequestURI().contains("/v3/api-docs")) {
                return false;
            }
            return super.canWrite(type, clazz, mediaType);
        }
    }
}
