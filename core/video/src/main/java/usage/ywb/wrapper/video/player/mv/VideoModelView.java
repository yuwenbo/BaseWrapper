package usage.ywb.wrapper.video.player.mv;

import android.widget.ImageView;

import androidx.databinding.BindingAdapter;
import androidx.lifecycle.ViewModel;

import com.bumptech.glide.Glide;

import usage.ywb.wrapper.video.utils.ThumbnailTask;
import usage.ywb.wrapper.video.utils.ThumbnailTask2;

/**
 * @author yuwenbo
 * @version [ V.2.9.6  2020/12/17 ]
 */
public class VideoModelView extends ViewModel {


    @BindingAdapter(value = {"url"})
    public static void setImage(ImageView view, String url){
//        new ThumbnailTask(view).execute(url);
//        new ThumbnailTask2().load(url).setImageView(view);
        Glide.with(view).asBitmap().load(url).into(view);
    }

}
