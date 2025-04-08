package com.clubfactory.platform.scheduler.engine.utils;

import com.clubfactory.platform.scheduler.core.exception.PluginLoadException;
import com.clubfactory.platform.scheduler.engine.supplier.PluginSupplier;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.apache.commons.lang.Validate;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.Map;
import java.util.Set;

/**
 * @author xiejiajun
 */
public class PluginLoadUtil {

    private static final Map<String, PluginLoader> pluginLoaderMap = Maps.newHashMap();
    private static final Set<String> loadedPlugins = Sets.newConcurrentHashSet();


    /**
     * 获取插件实例
     * @param pluginDir
     * @param pluginName
     * @param supplier
     * @param <R>
     * @return
     */
    public static <R> R newInstance(String pluginDir, String pluginName, PluginSupplier<R> supplier) {
        ClassLoader pluginLoader;
        if (StringUtils.isBlank(pluginDir)) {
            pluginLoader = PluginLoadUtil.class.getClassLoader();
        } else {
            pluginLoader = getPluginLoader(pluginDir, pluginName);
        }
        ClassLoader oldClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(pluginLoader);
            return supplier.get(pluginLoader);
        } catch (Exception e) {
            throw new PluginLoadException("插件加载出错: ", e);
        } finally {
            Thread.currentThread().setContextClassLoader(oldClassLoader);
        }
    }


    /**
     * 根据插件目录和插件名称获取插件加载器
     * @param pluginDir
     * @param pluginName
     * @return
     */
    private static PluginLoader getPluginLoader(String pluginDir, String pluginName) {
        Validate.isTrue(StringUtils.isNotBlank(pluginDir) && StringUtils.isNotBlank(pluginName),
                "jar包路径不能为空.");
        pluginDir = pluginDir.trim();
        pluginName = pluginName.trim().toLowerCase();

        return getOrCreateLoader(pluginDir, pluginName);

    }

    /**
     * 初始化插件加载器
     * @param pluginDir
     * @return
     */
    private static synchronized PluginLoader getOrCreateLoader(String pluginDir, String pluginName) {
        PluginLoader pluginLoader = pluginLoaderMap.computeIfAbsent(pluginDir, key -> {
            try {
                PluginLoader classLoader = new PluginLoader(pluginDir);
                pluginLoaderMap.put(pluginDir, classLoader);
                return classLoader;
            } catch (Throwable e) {
                throw new PluginLoadException("retrieve ClassLoad happens error");
            }
        });
        if (!loadedPlugins.contains(pluginName)) {
            pluginLoader.addURL(createPath(pluginDir, pluginName));
            loadedPlugins.add(pluginName);
        }
        return pluginLoader;
    }

    /**
     * 构建绝对路径
     * @param parentDir
     * @param currentDirName
     * @return
     */
    private static String createPath(String parentDir, String currentDirName) {
        parentDir = parentDir.trim();
        currentDirName = currentDirName.trim();
        if (parentDir.endsWith(File.separator)) {
            parentDir = StringUtils.substringBeforeLast(parentDir, File.separator);
        }
        return parentDir + File.separator + currentDirName;
    }
}
