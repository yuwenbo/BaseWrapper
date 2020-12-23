package usage.ywb.wrapper.video.player.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import usage.ywb.wrapper.mvvm.ui.BaseWrapperActivity;
import usage.ywb.wrapper.mvvm.utils.PermissionUtils;
import usage.ywb.wrapper.video.R;
import usage.ywb.wrapper.video.databinding.VideoActivityListBinding;
import usage.ywb.wrapper.video.player.model.VideoData;
import usage.ywb.wrapper.video.player.model.VideoEntity;
import usage.ywb.wrapper.video.player.ui.adapter.VideoListAdapter;
import usage.ywb.wrapper.video.utils.Constants;

/**
 * @author Kingdee.ywb
 * @version [ V.2.5.6  2019/6/26 ]
 */
public class VideoListActivity extends BaseWrapperActivity<VideoActivityListBinding> {


    private static final String[] PERMISSION = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    /**
     * 列表适配器
     */
    private VideoListAdapter adapter;
    /**
     * 数据列表
     */
    private ArrayList<VideoEntity> arrayList;


    @Override
    protected int getLayoutResourceId() {
        return R.layout.video_activity_list;
    }

    @Override
    protected void initContentView() {
        super.initContentView();
        PermissionUtils.requestPermissions(this, 0, PERMISSION);
        getBinding().recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull final RecyclerView recyclerView, final int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (adapter != null) {
                    adapter.setScrollState(newState);
                }
            }
        });
    }

    @Override
    public void onPermissionsGranted(int requestCode, String[] perms) {
        super.onPermissionsGranted(requestCode, perms);
        arrayList = VideoData.getLocalVideosList(this);
        adapter = new VideoListAdapter(this, arrayList);
        getBinding().recyclerView.setAdapter(adapter);
    }

    public void onClick(View v, int position) {
        final Intent intent = new Intent();
//        intent.setClass(this, MediaActivity.class);
        intent.setClass(this, VideoActivity.class);
        intent.putExtra(Constants.VIDEO_INDEX, position);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        getBinding().recyclerView.clearOnScrollListeners();
        super.onDestroy();
    }


}
