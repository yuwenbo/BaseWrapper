package usage.ywb.wrapper.modules.home.ui;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import dalvik.system.DexClassLoader;
import usage.ywb.wrapper.R;
import usage.ywb.wrapper.base.activity.BaseWrapperActivity;

/**
 * @author yuwenbo
 * @version [ V.2.8.8  2020/8/7 ]
 */
public class MainActivity extends BaseWrapperActivity {

    Handler handler;


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

        findViewById(R.id.load).setOnClickListener(v -> {
            File file = copyToCache("CircleLayout");
            loadFile(file);
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Log.i("MainActivity", "" + Thread.currentThread().getId());
            requestPermissions(new String[]{"android.permission.READ_EXTERNAL_STORAGE"}, 1);
        }



    }


    private File copyToCache(String name) {
        String path = Environment.getExternalStorageDirectory().getPath() +
                File.separator + "Download" + File.separator + "CarGeer" + File.separator + name + ".apk";
        File cache = getCacheDir();
        if (!cache.exists() && cache.mkdirs()) {
            Log.i("MainActivity", "创建新文件夹");
        }
        String d = cache.getPath() + "/" + name + ".apk";
        File copy = new File(d);
        FileInputStream inputStream = null;
        FileOutputStream outputStream = null;
        try {
            if (!copy.exists()) {
                if (copy.createNewFile()) {
                    Log.i("MainActivity", "创建新文件");
                }
                inputStream = new FileInputStream(path);
                outputStream = new FileOutputStream(copy);
                byte[] buff = new byte[1024];
                while (-1 != inputStream.read(buff)) {
                    outputStream.write(buff);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return copy;
    }


    private void loadFile(final File file) {
        new Thread(() -> {
            DexClassLoader classLoader = new DexClassLoader(file.getPath(), file.getAbsolutePath(), null, getClassLoader());
            try {
                Class clazz = classLoader.loadClass("usage.ywb.wrapper.circlelayout.ui.MainActivity");
                Message message = handler.obtainMessage(1, clazz);
                handler.sendMessage(message);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }).start();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }
}
