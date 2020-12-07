package usage.ywb.wrapper.mvp.common.hook;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import dalvik.system.DexClassLoader;
import usage.ywb.wrapper.mvp.BaseApplication;

/**
 * @author yuwenbo
 * @version [ V.2.9.6  2020/12/2 ]
 */
public class PluginClassLoaderHelper {

    private Map<String, DexClassLoader> classLoaderList;

    private static PluginClassLoaderHelper helper;

    private PluginClassLoaderHelper() {
        this.classLoaderList = new HashMap<>();
    }

    public static PluginClassLoaderHelper getHelper() {
        if (helper == null) {
            synchronized (new byte[0]) {
                if (helper == null) {
                    helper = new PluginClassLoaderHelper();
                }
            }
        }
        return helper;
    }

    public DexClassLoader getClassLoader(String plugin) {
        DexClassLoader classLoader = classLoaderList.get(plugin);
        if (classLoader == null) {
            File file = new File(BaseApplication.getApplication().getCacheDir(), plugin);
            DexClassLoader dexClassLoader = new DexClassLoader(file.getPath(), file.getAbsolutePath(), null, ClassLoader.getSystemClassLoader());
            classLoaderList.put(plugin, dexClassLoader);
            return dexClassLoader;
        } else {
            return classLoader;
        }
    }
}
