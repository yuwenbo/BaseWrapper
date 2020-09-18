package usage.ywb.wrapper.audio.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import usage.ywb.wrapper.audio.R;


/**
 * @author frank.yu
 */
public class VisualizerView extends View {

    private float[] points;
    private byte[] waveform;
    private Paint paint;

    private static final int COUNT = 20;

    public VisualizerView(Context context) {
        this(context, null);
    }

    public VisualizerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VisualizerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    private void init() {
        paint = new Paint();
        paint.setColor(getResources().getColor(R.color.line));
        paint.setAlpha(127);
    }

    @Override
    protected void onDraw(final Canvas canvas) {
        super.onDraw(canvas);
        if (waveform == null) {
            return;
        }
        points = new float[waveform.length * 4];
        int width = canvas.getWidth();
        int height = canvas.getHeight();
        for (int i = 0; i < COUNT; i++) {
            if (waveform[i] < 0) {
                waveform[i] = 127;
            }
            points[i * 4 + 0] = width / COUNT * i + width / 45;
            points[i * 4 + 1] = waveform[i] * height / 127 - 2;
            points[i * 4 + 2] = width / COUNT * i + width / 45;
            points[i * 4 + 3] = height;
        }
        paint.setStrokeWidth(width / 30);
        canvas.drawLines(points, paint);
    }

    /**
     * @param waveform
     */
    public void updateVisualizer(final byte[] waveform) {
        this.waveform = waveform;
        postInvalidate();
    }

}
