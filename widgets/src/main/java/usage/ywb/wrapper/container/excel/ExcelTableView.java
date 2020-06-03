package usage.ywb.wrapper.container.excel;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import usage.ywb.personal.widgets.R;
import usage.ywb.wrapper.container.recycleview.RecycleViewDivider;


/**
 * Excel表格
 *
 * @author ywb
 * @version [ V.1.0.0  2020/3/9 ]
 */
public class ExcelTableView extends FrameLayout {

    private TextView titleView;
    /**
     * 竖直方向的左侧列表
     */
    private RecyclerView leftRecycleView;
    /**
     * 水平方向的顶部列表
     */
    private RecyclerView topRecycleView;
    /**
     * 右侧朱表格内容的列表容器（由于竖直方向单列的宽度要与顶部单个cell的宽度一致，所以外部容器采取竖直方向排列）
     */
    private MajorRecycleView rightRecycleView;

    private BaseExcelTableAdapter adapter;

    /**
     * 左侧列表的宽度
     */
    private int leftWidth;
    /**
     * 顶部列表的高度
     */
    private int topHeight;
    /**
     * 单个cell的高度，同时对左侧列表和右侧列表生效
     */
    private int itemHeight;
    /**
     * 竖直方向上已划动的距离
     */
    private int scrollY;


    public ExcelTableView(@NonNull Context context) {
        this(context, null);
    }

    public ExcelTableView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ExcelTableView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.ExcelTableView);
        leftWidth = getResources().getDimensionPixelSize(R.dimen.excel_left_default_width);
        topHeight = getResources().getDimensionPixelSize(R.dimen.excel_cell_default_height);
        itemHeight = getResources().getDimensionPixelSize(R.dimen.excel_cell_default_height);
        for (int i = 0, len = array.getIndexCount(); i < len; i++) {
            int attr = array.getIndex(i);
            if (attr == R.styleable.ExcelTableView_leftWidth) {
                leftWidth = array.getDimensionPixelSize(attr, leftWidth);
            } else if (attr == R.styleable.ExcelTableView_topHeight) {
                topHeight = array.getDimensionPixelSize(attr, topHeight);
            } else if (attr == R.styleable.ExcelTableView_itemHeight) {
                itemHeight = array.getDimensionPixelSize(attr, itemHeight);
            }
        }
        addView(createTitleView());
        createSplitLineView();
        addView(createTopView());
//        addView(createLeftView());
//        addView(createRightView());

        MajorScrollView scrollView = new MajorScrollView(context);
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        params.topMargin = topHeight;
        scrollView.setLayoutParams(params);
        addView(scrollView);

        FrameLayout layout = new FrameLayout(context);
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        layout.setLayoutParams(layoutParams);
        scrollView.addView(layout);

        layout.addView(createLeftView());
        layout.addView(createRightView());

        array.recycle();
    }


    /**
     * 水平方向的滚动监听，实现顶部列表与主表格水平方向联动
     */
    RecyclerView.OnScrollListener horizontalScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            if (recyclerView == rightRecycleView) {
                topRecycleView.removeOnScrollListener(this);
                topRecycleView.scrollBy(dx, dy);
                topRecycleView.addOnScrollListener(this);
            } else if (recyclerView == topRecycleView) {
                rightRecycleView.removeOnScrollListener(this);
                rightRecycleView.scrollBy(dx, dy);
                rightRecycleView.addOnScrollListener(this);
            }
        }
    };


    /**
     * 竖直方向的滚动监听，实现左侧列表与主表格竖直方向联动
     */
    RecyclerView.OnScrollListener verticalScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            scrollY += dy;
            for (int i = 0; i < rightRecycleView.getChildCount(); i++) {
                if (rightRecycleView.getChildAt(i) instanceof RecyclerView) {
                    RecyclerView child = (RecyclerView) rightRecycleView.getChildAt(i);
                    if (recyclerView == child) {
                        continue;
                    }
                    fastScrollVertical(child, scrollY);
                }
            }
            if (recyclerView != leftRecycleView) {
                fastScrollVertical(leftRecycleView, scrollY);
            }
            if (adapter != null) {
                adapter.setScrollY(scrollY);
            }
        }
    };

    public static void fastScrollVertical(RecyclerView recyclerView, int scrollY) {
        LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
        manager.scrollToPositionWithOffset(0, -scrollY);
    }

    public void setTitleText(String text) {
        titleView.setText(text);
    }

    public void setAdapter(BaseExcelTableAdapter adapter) {
        this.adapter = adapter;
        adapter.setItemHeight(itemHeight);
        leftRecycleView.setAdapter(adapter.getLeftAdapter());
        topRecycleView.setAdapter(adapter.getTopAdapter());
        rightRecycleView.setAdapter(adapter.getRightAdapter());
//        adapter.getRightAdapter().setOnScrollListener(verticalScrollListener);
    }

    void createSplitLineView() {
        View lineH = new View(getContext());
        LayoutParams paramsH = new LayoutParams(LayoutParams.MATCH_PARENT, 2);
        paramsH.topMargin = topHeight;
        lineH.setLayoutParams(paramsH);
        lineH.setBackgroundColor(0xdfdfdf);
        addView(lineH);
        View lineV = new View(getContext());
        LayoutParams paramsV = new LayoutParams(2, LayoutParams.MATCH_PARENT);
        paramsV.leftMargin = leftWidth;
        lineV.setLayoutParams(paramsV);
        lineV.setBackgroundColor(0xdfdfdf);
        addView(lineV);
    }

    View createTitleView() {
        titleView = new TextView(getContext());
        if (leftWidth != 0 && topHeight != 0) {
            LayoutParams params = new LayoutParams(leftWidth, topHeight);
            titleView.setLayoutParams(params);
        }
        titleView.setGravity(Gravity.CENTER);
        titleView.setTextSize(13);
        return titleView;
    }

    View createTopView() {
        topRecycleView = new RecyclerView(getContext());
        if (topHeight != 0 && leftWidth != 0) {
            LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, topHeight);
            params.leftMargin = leftWidth;
            topRecycleView.setLayoutParams(params);
            topRecycleView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        }
        topRecycleView.addOnScrollListener(horizontalScrollListener);
        return topRecycleView;
    }

    View createLeftView() {
        leftRecycleView = new RecyclerView(getContext());
        if (leftWidth != 0 && topHeight != 0) {
            LayoutParams params = new LayoutParams(leftWidth, LayoutParams.MATCH_PARENT);
//            params.topMargin = topHeight;
            leftRecycleView.setLayoutParams(params);
            leftRecycleView.setLayoutManager(new LinearLayoutManager(getContext()));
        }
        leftRecycleView.setNestedScrollingEnabled(false);
        leftRecycleView.addItemDecoration(new RecycleViewDivider(getContext(), RecyclerView.HORIZONTAL));
//        leftRecycleView.addOnScrollListener(verticalScrollListener);
        return leftRecycleView;
    }

    View createRightView() {
        rightRecycleView = new MajorRecycleView(getContext());
        if (topHeight != 0) {
            LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            params.leftMargin = leftWidth;
//            params.topMargin = topHeight;
            rightRecycleView.setLayoutParams(params);
            rightRecycleView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        }
        rightRecycleView.addOnScrollListener(horizontalScrollListener);
        return rightRecycleView;
    }


}
