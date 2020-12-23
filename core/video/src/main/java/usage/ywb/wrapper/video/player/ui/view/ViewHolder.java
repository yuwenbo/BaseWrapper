package usage.ywb.wrapper.video.player.ui.view;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
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

    private final ViewDataBinding binding;

    /**
     * @param itemView
     */
    public ViewHolder(View itemView) {
        super(itemView);
        binding = DataBindingUtil.bind(itemView);
    }

    public ViewDataBinding getBinding() {
        return binding;
    }

}
