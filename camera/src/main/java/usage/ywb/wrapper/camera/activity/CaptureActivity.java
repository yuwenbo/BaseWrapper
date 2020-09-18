package usage.ywb.wrapper.camera.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

import usage.ywb.wrapper.camera.utils.CaptureHelper;
import usage.ywb.wrapper.camera.R;
import usage.ywb.wrapper.camera.interfaces.CameraLauncher;
import usage.ywb.wrapper.camera.view.ViewfinderView;

/**
 * @author yuwenbo
 * @version [ V.1.0.0  2018/7/11 ]
 */
public class CaptureActivity extends AppCompatActivity implements CaptureHelper.OnCaptureCropListener, ViewfinderView.OnDrawCompletedListener {

    private static final String TAG = CaptureActivity.class.getSimpleName();

    private ViewfinderView viewfinderView;
    private Button torchButton;

    private CaptureHelper cameraHelper;
    private CameraLauncher cameraLauncher;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture);
        TextureView textureView = findViewById(R.id.capture_preview);
        viewfinderView = findViewById(R.id.view_finder);
        viewfinderView.setOnDrawCompletedListener(this);
        cameraHelper = new CaptureHelper(this, textureView);
        cameraHelper.setOnCaptureCropListener(this);
        cameraLauncher = cameraHelper.getCameraLauncher();
        findViewById(R.id.capture_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraLauncher.capture();
            }
        });

        torchButton = findViewById(R.id.torch_button);
        torchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getTag() == null) {
                    if (cameraHelper.setFlash(true)) {
                        v.setTag(1);
                        torchButton.setText("关灯");
                    }
                } else {
                    if (cameraHelper.setFlash(false)) {
                        v.setTag(null);
                        torchButton.setText("开灯");
                    }
                }
            }
        });

    }

    @Override
    protected void onPause() {
        cameraLauncher.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        cameraLauncher.onResume();
    }

    @Override
    protected void onDestroy() {
        cameraLauncher.release();
        super.onDestroy();
    }

    @Override
    public void onCapture(final Bitmap bitmap) {
        Log.i(TAG, "CaptureActivity#onCapture：" + Thread.currentThread().getName());
        String path = saveBitmap(bitmap);
        Intent intent = new Intent();
        intent.setData(Uri.parse(path));
        setResult(RESULT_OK, intent);
        finish();
    }

    private String saveBitmap(Bitmap bitmap) {
        if (bitmap != null && !bitmap.isRecycled()) {
            File directory = new File(getExternalFilesDir(null), "picture");
            final Bitmap.CompressFormat format = Bitmap.CompressFormat.JPEG;
            if (directory.exists() || directory.mkdirs()) {
                OutputStream stream = null;
                File file = new File(directory, UUID.randomUUID() + ".jpg");
                try {
                    stream = new FileOutputStream(file);
                    bitmap.compress(format, 100, stream);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } finally {
                    if (stream != null) {
                        try {
                            stream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    bitmap.recycle();
                }
                return file.getPath();
            }
        }
        return null;
    }


    @Override
    public void onCompleted(int width, int height, Rect rect) {
        if (cameraHelper != null) {
            cameraHelper.setCropRect(rect, viewfinderView.getCropWidthRate(), viewfinderView.getCropHeightRate());
        }
    }
}
