
package usage.ywb.wrapper.audio.ui.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import usage.ywb.wrapper.audio.R;
import usage.ywb.wrapper.audio.entity.AudioEntity;

import java.util.List;

/**
 * @author frank.yu DATE:2015.05.25
 */
public class AudioListAdapter extends RecyclerView.Adapter<AudioListAdapter.AudioViewHolder> {

    private OnItemClickListener onItemClickListener;
    private List<AudioEntity> musicList;
    private Context context;
    private int position = -1;

    /**
     * @param position the position to set
     */
    public void setPosition(final int position) {
        this.position = position;
    }

    /**
     * @param musicList
     * @param context
     */
    public AudioListAdapter(List<AudioEntity> musicList, Context context) {
        super();
        this.musicList = musicList;
        this.context = context;
    }

    /**
     * @param onItemClickListener the onItemClickListener to set
     */
    public void setOnItemClickListener(final OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public int getItemCount() {
        return musicList.size();
    }

    @Override
    public void onBindViewHolder(final AudioViewHolder arg0, final int arg1) {
        final AudioEntity music = musicList.get(arg1);
        if (position == arg1) {
            arg0.icon.setImageResource(R.drawable.actionbar_music_normal);
            arg0.itemView.setBackgroundResource(R.drawable.media_current);
        } else {
            arg0.icon.setImageResource(R.drawable.actionbar_music_selected);
            arg0.itemView.setBackgroundColor(0x00000000);
        }
        arg0.name.setText(music.name);
        arg0.artist.setText(music.artist);
        arg0.itemView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(arg0.itemView, arg1);
                }
            }
        });
    }

    @Override
    public AudioViewHolder onCreateViewHolder(final ViewGroup arg0, final int arg1) {
        final View view = View.inflate(context, R.layout.audio_adapter_item, null);
        final LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(layoutParams);
        return new AudioViewHolder(view);
    }


    public static class AudioViewHolder extends RecyclerView.ViewHolder {

        public final ImageView icon;
        public final TextView name;
        public final TextView artist;
        public final ImageButton more;

        /**
         * @param itemView
         */
        public AudioViewHolder(final View itemView) {
            super(itemView);
            icon = (ImageView) itemView.findViewById(R.id.music_icon);
            name = (TextView) itemView.findViewById(R.id.music_name);
            artist = (TextView) itemView.findViewById(R.id.music_artist);
            more = (ImageButton) itemView.findViewById(R.id.music_more);
        }

    }

    public interface OnItemClickListener {

        void onItemClick(View convertView, final int position);
    }

}
