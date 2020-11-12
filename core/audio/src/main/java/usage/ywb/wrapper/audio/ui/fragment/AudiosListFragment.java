package usage.ywb.wrapper.audio.ui.fragment;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import usage.ywb.wrapper.audio.R;
import usage.ywb.wrapper.audio.ui.activity.MainActivity;
import usage.ywb.wrapper.audio.entity.AudioEntity;
import usage.ywb.wrapper.audio.utils.AudioData;
import usage.ywb.wrapper.audio.ui.adapter.AudioListAdapter;

import java.util.List;

/**
 * @author yuwenbo
 * @version [ v1.0.0, 2016/6/24 ]
 */
public class AudiosListFragment extends Fragment implements AudioListAdapter.OnItemClickListener {

    /**
     * 列表控件
     */
    private RecyclerView recyclerView;
    private AudioListAdapter adapter;

    private List<AudioEntity> audiosList;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        audiosList = AudioData.getAudiosList();
        adapter = new AudioListAdapter(audiosList, getActivity());
        adapter.setOnItemClickListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.audio_fragment_list, null);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView = (RecyclerView) view.findViewById(R.id.audio_list);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        return view;
    }


    /**
     * 设置当前播放位置
     *
     * @param position
     */
    public void setCurrPosition(int position) {
        if (audiosList != null) {
            if (position > -1 && position < audiosList.size()) {
                adapter.setPosition(position);
                recyclerView.smoothScrollToPosition(position);
                adapter.notifyDataSetChanged();
//                smoothScrollToPosition(position, recyclerView.getWidth() / 2, recyclerView.getHeight() / 2);
            }
        }
    }

    /**
     * 将指定的item移动到RecycleView的指定位置
     *
     * @param position 指定的item位置
     * @param x        移动的目标位置X坐标
     * @param y        移动的目标位置Y坐标
     */
    public void smoothScrollToPosition(int position, int x, int y) {
        RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForLayoutPosition(position);
        if (viewHolder != null) {
            View view = viewHolder.itemView;
            int offsetY = y - (view.getTop() - recyclerView.getScrollY()) - view.getHeight() / 2;
            int offsetX = x - (view.getLeft() - recyclerView.getScrollX()) - view.getWidth() / 2;
            recyclerView.smoothScrollBy(-offsetX, -offsetY);
        } else {
            recyclerView.smoothScrollToPosition(position);
        }
    }

    @Override
    public void onItemClick(View convertView, int position) {
        ((MainActivity) getActivity()).setPosition(position);
        ((MainActivity) getActivity()).start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
