package io.wangk.peekaboo.common.core.util;

import groovy.lang.GroovyObject;
import org.springframework.stereotype.Component;

/**
 * Groovy脚本的执行器。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Component
public class GroovyExecutor {

    public String exec(String script, String method, Object[] args) {
        GroovyObject go = GroovyLoader.loadScript(script);
        return (String) go.invokeMethod(method, args);
    }
}
