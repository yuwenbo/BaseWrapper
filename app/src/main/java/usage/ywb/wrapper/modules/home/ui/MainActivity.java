package usage.ywb.wrapper.modules.home.ui;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Files;

import butterknife.OnClick;
import dalvik.system.DexClassLoader;
import usage.ywb.wrapper.R;
import usage.ywb.wrapper.mvp.common.activity.BaseWrapperActivity;
import usage.ywb.wrapper.mvp.common.hook.FileDexUtils;
import usage.ywb.wrapper.mvp.utils.PermissionUtils;

/**
 * @author yuwenbo
 * @version [ V.2.8.8  2020/8/7 ]
 */
public class MainActivity extends BaseWrapperActivity {

    Handler handler;

    private static final String[] PERMISSIONS = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        handler = new Handler(getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                Class clazz = (Class) msg.obj;
                startActivity(new Intent(MainActivity.this, clazz));
            }
        };
    }

    @OnClick(R.id.load)
    protected void load(){
        PermissionUtils.requestPermissions(MainActivity.this, 1, PERMISSIONS);
    }

    @OnClick(R.id.repair)
    protected void repair(){
        File file = copyToCache("audio.apk");
        FileDexUtils.loadFixedDex(this, file);
    }


    private File copyToCache(String name) {
        String path = Environment.getExternalStorageDirectory().getPath() +
                File.separator + "Download" + File.separator + name;
        File source = new File(path);
        if (!source.exists() || !source.isFile()) {
            return null;
        }
        File dir = getCacheDir();
        if (!dir.exists() && dir.mkdirs()) {
            Log.i("MainActivity", "创建新文件夹");
        }
        File plugin = new File(dir.getPath() + File.separator + name);
        FileChannel input = null;
        FileChannel output = null;
        try {
            if (!plugin.exists() || plugin.createNewFile()) {
                Log.i("MainActivity", "创建新文件");
            }
            if (plugin.length() != source.length()) {
                input = new FileInputStream(source).getChannel();
                output = new FileOutputStream(plugin).getChannel();
                output.transferFrom(input, 0, input.size());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return plugin;
    }


    private void loadFile(final File file) {
        new Thread(() -> {
            // 根据apk路径加载apk代码到DexClassLoader中
            DexClassLoader classLoader = new DexClassLoader(file.getPath(), file.getAbsolutePath(), null, ClassLoader.getSystemClassLoader());
            try {
                Class clazz = classLoader.loadClass("usage.ywb.wrapper.audio.ui.activity.MainActivity");
                Message message = handler.obtainMessage(1, clazz);
                handler.sendMessage(message);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }).start();
    }

    @Override
    public void onPermissionsGranted(int requestCode, String[] perms) {
        super.onPermissionsGranted(requestCode, perms);
        File file = copyToCache("audio.apk");
        loadFile(file);
    }

}
