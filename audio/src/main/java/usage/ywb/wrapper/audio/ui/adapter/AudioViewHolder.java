package usage.ywb.wrapper.audio.ui.adapter;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import usage.ywb.wrapper.audio.R;


/**
 * @author frank.yu
 *
 * DATE:2015.05.25
 */
public class AudioViewHolder extends RecyclerView.ViewHolder {

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
