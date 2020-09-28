package usage.ywb.wrapper.video.player.ui.view;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import usage.ywb.wrapper.video.R;


/**
 * @author frank.yu
 *
 */
public class ViewHolder extends RecyclerView.ViewHolder {

    public ImageView image;
    public MarqueeTextView name;
    public TextView size;
    public TextView time;

    /**
     * @param itemView
     */
    public ViewHolder(View itemView) {
        super(itemView);

        image = (ImageView) itemView.findViewById(R.id.video_icon);
        name = (MarqueeTextView) itemView.findViewById(R.id.video_name);
        size = (TextView) itemView.findViewById(R.id.video_size);
        time = (TextView) itemView.findViewById(R.id.video_time);

    }

}
