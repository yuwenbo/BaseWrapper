package usage.ywb.wrapper.video.player.ui.adapter;

import java.text.DecimalFormat;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;

import usage.ywb.wrapper.video.R;
import usage.ywb.wrapper.video.player.model.VideoEntity;
import usage.ywb.wrapper.video.player.ui.view.ViewHolder;
import usage.ywb.wrapper.video.utils.BitmapCache;
import usage.ywb.wrapper.video.utils.ThumbnailTask;

/**
 *
 */
public class RecyclerAdapter extends Adapter<ViewHolder> implements OnClickListener {

    private LayoutInflater inflater;

    private ArrayList<VideoEntity> arrayList;
    private OnItemClickListener onItemClickListener;

    private int scrollState;

    public void setOnItemClickListener(final OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public RecyclerAdapter(Context context, ArrayList<VideoEntity> arrayList) {
        this.arrayList = arrayList;
        inflater = LayoutInflater.from(context);
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
        if (scrollState == RecyclerView.SCROLL_STATE_IDLE) {
            final String path = Uri.parse(video.getUri()).getPath();
            final Bitmap bitmap = BitmapCache.getInstance().get(path);
            if (bitmap == null) {
                new ThumbnailTask(path, holder.image);
            }
            holder.image.setImageBitmap(bitmap);
        }
        holder.name.setText(video.getName());
        holder.size.setText(String.format("%s MB", new DecimalFormat("#.##").format((float) video.getSize() / 1024 / 1024)));
        holder.time.setText(DateFormat.format("mm:ss", video.getTime()));
        holder.itemView.setOnClickListener(this);
        holder.itemView.setTag(position);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int arg1) {
        final View itemView = inflater.inflate(R.layout.video_item_video, parent, false);
        final LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        itemView.setLayoutParams(layoutParams);
        return new ViewHolder(itemView);
    }

    @Override
    public void onClick(View v) {
        if (onItemClickListener != null) {
            onItemClickListener.onItemClick(v, (int) v.getTag());
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View convertView, int position);
    }

}
