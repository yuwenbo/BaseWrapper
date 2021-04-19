package usage.ywb.wrapper.container;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.ScrollView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import usage.ywb.wrapper.widgets.R;


/**
 * 可以约束最大高度的布局
 *
 * @author Kingdee.ywb
 * @version [ V.2.6.1  2019/7/17 ]
 */
public class ConstraintScrollView extends ScrollView {

    private static final int[] ATTRS = new int[]{android.R.attr.maxHeight};

    private float mMaxHeight;

    public ConstraintScrollView(@NonNull Context context) {
        this(context, null);
    }

    public ConstraintScrollView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ConstraintScrollView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray array = context.obtainStyledAttributes(attrs, ATTRS);
        int count = array.getIndexCount();
        for (int i = 0; i < count; i++) {
            int type = array.getIndex(i);
            if (type == android.R.attr.maxHeight) {
                //获得布局中限制的最大高度
                mMaxHeight = array.getDimension(type, -1);
            }
        }
        array.recycle();
    }

    /**
     * 设置View高度跟随子内容变化（Item高度固定）
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mMaxHeight > 0) {
            //获取lv本身高度
            int specSize = MeasureSpec.getSize(heightMeasureSpec);
            //限制高度小于lv高度,设置为限制高度
            if (mMaxHeight <= specSize) {
                heightMeasureSpec = MeasureSpec.makeMeasureSpec(Float.valueOf(mMaxHeight).intValue(), MeasureSpec.AT_MOST);
            }
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        } else {
            int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
            super.onMeasure(widthMeasureSpec, expandSpec);
        }
    }

}
