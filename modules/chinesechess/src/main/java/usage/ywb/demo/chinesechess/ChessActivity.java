package usage.ywb.demo.chinesechess;

import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import usage.ywb.wrapper.mvp.common.activity.BaseWrapperActivity;
import usage.ywb.wrapper.mvp.widgets.TitleLayout;

/**
 * @author yuwenbo
 * @version [ v1.0.0, 2016/4/13 ]
 */
public class ChessActivity extends BaseWrapperActivity implements ChessBoardView.OnDrawFinishedListener {

    protected FrameLayout mChessMain;

    @BindView(R.id.cbv_chess_board)
    protected ChessBoardView mChessBoard;

    @BindView(R.id.chess_layout)
    protected ChessLayout mChessLayout;

    private Integer[][] piecesTypes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mChessMain = (FrameLayout) getLayoutInflater().inflate(R.layout.activity_chess_main, null);
        setMainBackground();
        setContentView(mChessMain);
        ButterKnife.bind(this);

        mChessBoard.setOnDrawFinished(this);
    }

    @Override
    protected void setTitleView(TitleLayout titleLayout) {
        super.setTitleView(titleLayout);
        titleLayout.setTitleViewText("中国象棋");
    }

    /**
     * 设置主界面背景
     */
    private void setMainBackground() {
        try {
            InputStream stream = getResources().openRawResource(R.raw.bg_chess);
            Drawable drawable = BitmapDrawable.createFromStream(stream, "");
            mChessMain.setBackground(drawable);
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDrawFinished() {
        mChessLayout.setBoardRect(mChessBoard.getBoardRect());
        mChessLayout.setGridWidth(mChessBoard.getGridWidth());

        Log.i("ChessActivity", "--onDraw--onDrawFinished 棋盘矩阵 : " + mChessBoard.getBoardRect());
        Log.i("ChessActivity", "--onDraw--onDrawFinished 棋盘格子: " + mChessBoard.getGridWidth());

        if (piecesTypes == null) {
            piecesTypes = ChessUtil.initPiecesTypes();
            mChessLayout.setPiecesArray(piecesTypes);
        }
        mChessLayout.requestLayout();
    }

    @OnClick({R.id.tv_chess_back, R.id.tv_chess_reset})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_chess_back:
                Integer[][] pieces = ChessManager.fetchStep();
                if (pieces != null) {
                    mChessLayout.setPiecesArray(pieces);
                } else {
                    Toast.makeText(this, "不能再悔了！", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.tv_chess_reset:
                piecesTypes = ChessUtil.initPiecesTypes();
                mChessLayout.setPiecesArray(piecesTypes);
                break;
            default:
                break;
        }
    }


}
