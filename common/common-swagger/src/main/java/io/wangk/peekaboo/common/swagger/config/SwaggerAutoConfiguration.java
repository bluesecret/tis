package io.wangk.peekaboo.common.swagger.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * 自动加载bean的配置对象。
 *
 * @author wangk
 * @date 2025-01-14
 */
@EnableConfigurationProperties(SwaggerProperties.class)
@ConditionalOnProperty(prefix = "common-swagger", name = "enabled")
public class SwaggerAutoConfiguration {

    @Bean
    public GroupedOpenApi upmsApi(SwaggerProperties p) {
        String[] paths = {"/admin/upms/**"};
        String[] packagedToMatch = {p.getServiceBasePackage() + ".upms.controller"};
        return GroupedOpenApi.builder().group("用户权限分组接口")
                .pathsToMatch(paths)
                .packagesToScan(packagedToMatch).build();
    }

    @Bean
    public GroupedOpenApi bizApi(SwaggerProperties p) {
        String[] paths = {"/admin/app/**"};
        String[] packagedToMatch = {p.getServiceBasePackage() + ".app.controller"};
        return GroupedOpenApi.builder().group("业务应用分组接口")
                .pathsToMatch(paths)
                .packagesToScan(packagedToMatch).build();
    }

    @Bean
    public GroupedOpenApi workflowApi(SwaggerProperties p) {
        String[] paths = {"/admin/flow/**"};
        String[] packagedToMatch = {p.getBasePackage() + ".common.flow.controller"};
        return GroupedOpenApi.builder().group("工作流通用操作接口")
                .pathsToMatch(paths)
                .packagesToScan(packagedToMatch).build();
    }

    @Bean
    public GroupedOpenApi onlineApi(SwaggerProperties p) {
        String[] paths = {"/admin/online/**"};
        String[] packagedToMatch = {p.getBasePackage() + ".common.online.controller"};
        return GroupedOpenApi.builder().group("在线表单操作接口")
                .pathsToMatch(paths)
                .packagesToScan(packagedToMatch).build();
    }

    @Bean
    public GroupedOpenApi reportApi(SwaggerProperties p) {
        String[] paths = {"/admin/report/**"};
        String[] packagedToMatch = {p.getBasePackage() + ".common.report.controller"};
        return GroupedOpenApi.builder().group("报表打印操作接口")
                .pathsToMatch(paths)
                .packagesToScan(packagedToMatch).build();
    }

    @Bean
    public OpenAPI customOpenApi(SwaggerProperties p) {
        Info info = new Info().title(p.getTitle()).version(p.getVersion()).description(p.getDescription());
        return new OpenAPI().info(info);
    }
}
