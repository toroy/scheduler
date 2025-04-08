package com.clubfactory.platform.scheduler.engine.utils;

import com.clubfactory.platform.scheduler.core.exception.PluginLoadException;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

/**
 * 插件加载工具
 * @author xiejiajun
 */
public class PluginLoader extends URLClassLoader {


    public PluginLoader(String ...paths) {
        this(PluginLoader.class.getClassLoader(), paths);
    }

    public PluginLoader(ClassLoader parent, String ...paths) {
        super(getURLs(paths), parent);
    }

    /**
     * 往classLoader中新增URL
     * @param pluginPath
     */
    public void addURL(String pluginPath) {
        URL[] pluginUrls = PluginLoader.getURLs(pluginPath);
        if (pluginUrls.length == 0) {
            return;
        }
        for (URL url : pluginUrls) {
            this.addURL(url);
        }
    }

    /**
     * 根据插件路径生成URL列表
     * @param paths
     * @return
     */
    private static URL[] getURLs(String ...paths) {
        Validate.isTrue(null != paths && 0 != paths.length,
                "jar包路径不能为空.");

        List<String> dirs = new ArrayList<>();
        for (String path : paths) {
            dirs.add(path);
            PluginLoader.collectDirs(path, dirs);
        }

        List<URL> urls = new ArrayList<>();
        for (String path : dirs) {
            urls.addAll(doGetURLs(path));
        }

        return urls.toArray(new URL[0]);
    }

    /**
     * 遍历文件夹
     * @param path
     * @param collector
     */
    private static void collectDirs(String path, List<String> collector) {
        if (null == path || StringUtils.isBlank(path)) {
            return;
        }

        File current = new File(path);
        if (!current.exists() || !current.isDirectory()) {
            return;
        }
        File[] children = current.listFiles();
        if (children == null) {
            return;
        }

        for (File child : children) {
            if (!child.isDirectory()) {
                continue;
            }

            collector.add(child.getAbsolutePath());
            collectDirs(child.getAbsolutePath(), collector);
        }
    }

    /**
     * 将Jar路径转化成URL
     * @param path
     * @return
     */
    private static List<URL> doGetURLs(final String path) {
        Validate.isTrue(!StringUtils.isBlank(path), "jar包路径不能为空.");

        File jarPath = new File(path);

        Validate.isTrue(jarPath.exists() && jarPath.isDirectory(),
                "jar包路径必须存在且为目录.");

        // 获取所有jar文件
        File[] allJars = new File(path).listFiles(
                filePath -> filePath.getName().endsWith(".jar"));

        if (allJars == null || allJars.length == 0) {
            return new ArrayList<>(0);
        }
        List<URL> jarUrls = new ArrayList<>(allJars.length);

        for (File allJar : allJars) {
            try {
                jarUrls.add(allJar.toURI().toURL());
            } catch (Exception e) {
                throw new PluginLoadException("加载插件出错", e);
            }
        }
        return jarUrls;
    }
}
