package io.wangk.peekaboo.common.core.util;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyCodeSource;
import groovy.lang.GroovyObject;
import lombok.extern.slf4j.Slf4j;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Groovy脚本的加载器。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Slf4j
public class GroovyLoader {

    private static final Map<String, GroovyObject> SCRIPT_MAP = new HashMap<>();
    private static final GroovyClassLoader CLASS_LOADER = new GroovyClassLoader();

    public static synchronized GroovyObject loadScript(String script) {
        if (StrUtil.isEmpty(script)) {
            return null;
        }
        String key = DigestUtil.md5Hex(script);
        if (SCRIPT_MAP.containsKey(key)) {
            return SCRIPT_MAP.get(key);
        }
        try {
            Class<?> clazz = CLASS_LOADER.parseClass(script);
            return doLoad(clazz, key);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            CLASS_LOADER.clearCache();
        }
        log.error("load script error, can not load Script");
        return null;
    }

    public static synchronized GroovyObject loadScriptFile(String scriptFile) {
        if (StrUtil.isEmpty(scriptFile)) {
            return null;
        }
        String key = DigestUtil.md5Hex(scriptFile);
        if (SCRIPT_MAP.containsKey(key)) {
            return SCRIPT_MAP.get(key);
        }
        URL url = GroovyLoader.class.getClassLoader().getResource(scriptFile);
        try {
            Class<?> clazz = CLASS_LOADER.parseClass(new GroovyCodeSource(url));
            return doLoad(clazz, key);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            CLASS_LOADER.clearCache();
        }
        log.error("load script file error, can not load Script File");
        return null;
    }

    private static GroovyObject doLoad(Class<?> clazz, String key)
            throws IllegalAccessException, InstantiationException {
        if (clazz != null) {
            GroovyObject groovyObject = (GroovyObject) clazz.newInstance();
            SCRIPT_MAP.put(key, groovyObject);
            return groovyObject;
        }
        return null;
    }

    private GroovyLoader() {
    }
}
