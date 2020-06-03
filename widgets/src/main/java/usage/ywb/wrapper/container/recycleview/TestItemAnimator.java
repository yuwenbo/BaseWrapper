package usage.ywb.wrapper.container.recycleview;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import android.util.Log;

/**
 * @author ywb
 * @version [ V.1.0.0  2020/3/9 ]
 */
public class TestItemAnimator extends SimpleItemAnimator {

    private final static String TAG = MyItemAnimator.class.getSimpleName();


    @Override
    public boolean animateRemove(RecyclerView.ViewHolder holder) {
        Log.i(TAG, "animateRemove");
        return true;
    }

    @Override
    public boolean animateAdd(RecyclerView.ViewHolder holder) {
        Log.i(TAG, "animateAdd");
        return true;
    }

    @Override
    public boolean animateMove(RecyclerView.ViewHolder holder, int fromX, int fromY, int toX, int toY) {
        Log.i(TAG, "animateMove");
        return true;
    }

    @Override
    public boolean animateChange(RecyclerView.ViewHolder oldHolder, RecyclerView.ViewHolder newHolder, int fromLeft, int fromTop, int toLeft, int toTop) {
        Log.i(TAG, "animateChange");
        return true;
    }

    @Override
    public void runPendingAnimations() {
        Log.i(TAG, "runPendingAnimations");

    }

    @Override
    public void endAnimation(RecyclerView.ViewHolder item) {
        Log.i(TAG, "endAnimation");

    }

    @Override
    public void endAnimations() {
        Log.i(TAG, "endAnimations");

    }

    @Override
    public boolean isRunning() {
        Log.i(TAG, "isRunning");
        return true;
    }

}
