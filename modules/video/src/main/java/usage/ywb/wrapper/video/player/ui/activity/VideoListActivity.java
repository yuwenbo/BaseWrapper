package usage.ywb.wrapper.video.player.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import usage.ywb.wrapper.video.R;
import usage.ywb.wrapper.video.player.model.VideoData;
import usage.ywb.wrapper.video.player.model.VideoEntity;
import usage.ywb.wrapper.video.player.ui.adapter.RecyclerAdapter;
import usage.ywb.wrapper.video.utils.Constants;

/**
 * @author Kingdee.ywb
 * @version [ V.2.5.6  2019/6/26 ]
 */
public class VideoListActivity extends AppCompatActivity implements RecyclerAdapter.OnItemClickListener {

    /**
     * 列表控件
     */
    private RecyclerView recyclerView;
    /**
     * 列表适配器
     */
    private RecyclerAdapter adapter;
    /**
     * 数据列表
     */
    private ArrayList<VideoEntity> arrayList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                && PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull final RecyclerView recyclerView, final int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (adapter != null) {
                    adapter.setScrollState(newState);
                }
            }
        });
        arrayList = VideoData.getLocalVideosList(this);
        adapter = new RecyclerAdapter(this, arrayList);
        adapter.setOnItemClickListener(this);
        recyclerView.setAdapter(adapter);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean ok = true;
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                ok = false;
                break;
            }
        }
        if (ok) {
            arrayList = VideoData.getLocalVideosList(this);
            adapter.notifyDataSetChanged();
        } else {
            Toast.makeText(this, "无法获取权限", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onItemClick(View convertView, int position) {
        final Intent intent = new Intent();
//        intent.setClass(this, MediaActivity.class);
        intent.setClass(this, VideoActivity.class);
        intent.putExtra(Constants.VIDEO_INDEX, position);
        startActivity(intent);
    }


    @Override
    protected void onDestroy() {
        recyclerView.clearOnScrollListeners();
        super.onDestroy();
    }

}
