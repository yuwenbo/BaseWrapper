package usage.ywb.wrapper.modules.home.ui

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.util.Log
import butterknife.OnClick
import usage.ywb.wrapper.R
import usage.ywb.wrapper.mvp.common.activity.BaseWrapperActivity
import usage.ywb.wrapper.pluggable.core.PluginHelper
import usage.ywb.wrapper.utils.PermissionUtils
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.nio.channels.FileChannel

/**
 * @author yuwenbo
 * @version [ V.2.8.8  2020/8/7 ]
 */
class MainActivity : BaseWrapperActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    @OnClick(R.id.load)
    fun load() {
        PermissionUtils.requestPermissions(this@MainActivity, 1, *PERMISSIONS)
    }

    @OnClick(R.id.repair)
    fun repair() {
        startActivity(Intent(this@MainActivity, LoginActivity::class.java))
    }

    private fun copyToCache(name: String): File? {
        val path = Environment.getExternalStorageDirectory().path +
                File.separator + "Download" + File.separator + name
        val source = File(path)
        if (!source.exists() || !source.isFile) {
            return null
        }
        val dir = cacheDir
        if (!dir.exists() && dir.mkdirs()) {
            Log.i("MainActivity", "创建新文件夹")
        }
        val plugin = File(dir, name)
        var input: FileChannel? = null
        var output: FileChannel? = null
        try {
            if (!plugin.exists() || plugin.createNewFile()) {
                Log.i("MainActivity", "创建新文件")
            }
            if (plugin.length() != source.length()) {
                input = FileInputStream(source).channel
                output = FileOutputStream(plugin).channel
                output.transferFrom(input, 0, input.size())
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            if (output != null) {
                try {
                    output.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            if (input != null) {
                try {
                    input.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
        return plugin
    }

    private fun loadFile(file: File?) {
        // 根据apk路径加载apk代码到DexClassLoader中
        val plugin = PluginHelper.getInstance().loadPlugin(File(cacheDir.path, "audio.apk"))
        try {
            val clazz = plugin.classLoader.loadClass("usage.ywb.wrapper.audio.ui.activity.MainActivity")
            val intent = Intent()
            intent.setClassName("usage.ywb.wrapper.audio", clazz.name)
            PluginHelper.getInstance().replaceClassLoader(this, plugin.classLoader)
            //            loadResources(classLoader, file.getAbsolutePath());
            startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: Array<String>) {
        super.onPermissionsGranted(requestCode, perms)
        val file = copyToCache("audio.apk")
        loadFile(file)
    }

    companion object {
        private val PERMISSIONS = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
    }
}