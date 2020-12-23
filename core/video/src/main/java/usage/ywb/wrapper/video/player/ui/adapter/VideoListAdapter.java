package usage.ywb.wrapper.video.player.ui.adapter;

import java.util.ArrayList;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;

import usage.ywb.wrapper.video.BR;
import usage.ywb.wrapper.video.R;
import usage.ywb.wrapper.video.player.model.VideoEntity;
import usage.ywb.wrapper.video.player.ui.view.ViewHolder;

/**
 *
 */
public class VideoListAdapter extends RecyclerView.Adapter<ViewHolder> {

    private Context context;
    private LayoutInflater inflater;

    private ArrayList<VideoEntity> arrayList;

    private View.OnClickListener onClickListener;

    private int scrollState;

    public VideoListAdapter(Context context, ArrayList<VideoEntity> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
        inflater = LayoutInflater.from(context);
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public void setScrollState(int scrollState) {
        this.scrollState = scrollState;
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final VideoEntity video = arrayList.get(position);
        holder.itemView.setTag(position);
        holder.getBinding().setVariable(BR.entity, video);
        holder.getBinding().setVariable(BR.position, position);
        holder.getBinding().setVariable(BR.activity, context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int arg1) {
        final View itemView = inflater.inflate(R.layout.video_item_video, parent, false);
        final LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        itemView.setLayoutParams(layoutParams);
        return new ViewHolder(itemView);
    }

}
