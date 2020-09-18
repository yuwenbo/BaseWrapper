package usage.ywb.wrapper.modules.home.ui;

import android.os.Environment;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * @author yuwenbo
 * @version [ V.2.8.8  2020/8/10 ]
 */
public class MyClassLoader extends ClassLoader {

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        byte[] bytes = getByteArray(name);
        return defineClass(name, bytes, 0, bytes.length);
    }


    private byte[] getByteArray(String name) {
        String path = Environment.getExternalStorageDirectory().getPath() + "/Download/CarGeer/" + name + ".class";
        FileInputStream fileInputStream = null;
        ByteArrayOutputStream outputStream = null;
        try {
            fileInputStream = new FileInputStream(path);
            outputStream = new ByteArrayOutputStream();
            byte[] buff = new byte[1024];
            while (-1 != fileInputStream.read(buff)) {
                outputStream.write(buff);
            }
            return outputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
        return null;
    }

}
