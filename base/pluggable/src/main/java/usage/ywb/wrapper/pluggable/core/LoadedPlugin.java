package usage.ywb.wrapper.pluggable.core;

import android.content.Context;

import java.io.File;

import dalvik.system.DexClassLoader;

/**
 * @author yuwenbo
 * @version [ V.2.9.6  2020/12/18 ]
 */
public class LoadedPlugin {

    protected Context hostContent;
    protected File pluginFile;

    protected DexClassLoader classLoader;

    protected String mPackageName;


    protected Context baseContext;


    public LoadedPlugin(Context hostContent, File pluginFile) {
        this.hostContent = hostContent;
        this.pluginFile = pluginFile;
        // 根据apk路径加载apk代码到DexClassLoader中
        classLoader = new DexClassLoader(pluginFile.getPath(), pluginFile.getAbsolutePath(), null, ClassLoader.getSystemClassLoader());
    }

    public DexClassLoader getClassLoader() {
        return classLoader;
    }
}
