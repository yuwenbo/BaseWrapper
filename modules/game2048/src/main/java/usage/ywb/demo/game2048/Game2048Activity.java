package usage.ywb.demo.game2048;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import usage.ywb.wrapper.mvp.common.activity.BaseWrapperActivity;
import usage.ywb.wrapper.mvp.widgets.TitleLayout;


/**
 * @author yuwenbo
 */
public class Game2048Activity extends BaseWrapperActivity {

    private LinearLayout currScoreLl;
    private LinearLayout maxScoreLl;
    private GameLayout gameView;
    private TextView replayTv;

    private int currScore = 0;
    private int maxScore = 0;

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private CardView[][] cards;
    private String cardsNum;
    public static final String SHARE_2048 = "MyApp_2048";


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game2048);


        gameView = (GameLayout) findViewById(R.id.game_view);
        currScoreLl = (LinearLayout) findViewById(R.id.game_score);
        maxScoreLl = (LinearLayout) findViewById(R.id.game_max);
        replayTv = (TextView) findViewById(R.id.replay_2048);
        replayTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reset();
            }
        });
        preferences = getSharedPreferences(SHARE_2048, MODE_PRIVATE);
        editor = preferences.edit();

        maxScore = preferences.getInt("max_score", 0);
        currScore = preferences.getInt("curr_score", 0);
        cardsNum = preferences.getString("cards_num", "");
        gameView.setGameListener(new DefaultGameListener() {
            @Override
            public void onPre() {
                setGameData(gameView.getCards());
            }

            @Override
            public void onMerge(int increment) {
                currScore += increment;
                setScore(currScoreLl, currScore);
                if (currScore > maxScore) {
                    maxScore = currScore;
                    setScore(maxScoreLl, maxScore);
                    editor.putInt("max_score", maxScore).commit();
                }
            }
        });
        initScore();
    }

    @Override
    protected void setTitleView(TitleLayout titleLayout) {
        super.setTitleView(titleLayout);
        titleLayout.setTitleViewText("2048");
    }

    private void initScore() {
        String maxStr = String.valueOf(maxScore);
        maxScoreLl.removeAllViews();
        for (int i = 0; i < maxStr.length(); i++) {
            int rid = parseImg(maxStr.charAt(i));
            ImageView imageView = new ImageView(this);
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            imageView.setImageResource(rid);
            maxScoreLl.addView(imageView);
        }

        String currStr = String.valueOf(currScore);
        currScoreLl.removeAllViews();
        for (int i = 0; i < currStr.length(); i++) {
            int rid = parseImg(currStr.charAt(i));
            ImageView imageView = new ImageView(this);
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            imageView.setImageResource(rid);
            currScoreLl.addView(imageView);
        }
    }

    private void setScore(ViewGroup viewGroup, int score) {
        String scoreStr = String.valueOf(score);
        while (scoreStr.length() > viewGroup.getChildCount()) {
            ImageView imageView = new ImageView(this);
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            viewGroup.addView(imageView);
        }
        while (scoreStr.length() < viewGroup.getChildCount()) {
            viewGroup.removeViewAt(0);
        }
        for (int i = 0; i < scoreStr.length(); i++) {
            int rid = parseImg(scoreStr.charAt(i));
            ImageView imageView = (ImageView) viewGroup.getChildAt(i);
            imageView.setImageResource(rid);
        }
    }


    private int parseImg(char c) {
        int number = Integer.valueOf(String.valueOf(c));
        int rid = getResources().getIdentifier("number" + number, "drawable", getPackageName());
        return rid;
    }


    /**
     * 提取并添加游戏进度
     *
     * @param cards
     */
    public void setGameData(final CardView[][] cards) {
        Log.i("Game2048Activity", "游戏开始！");
        this.cards = cards;

//        cardsNum = "[8,4,2,2,16,32,64,128,2048,1024,512,256,4096,8192,16384,32768]";

        if (TextUtils.isEmpty(cardsNum)) {
            gameView.startGame();
            return;
        }
        try {
            final JSONArray json = new JSONArray(cardsNum);
            int index = 0;
            for (int x = 0; x < gameView.getSize(); x++) {
                for (int y = 0; y < gameView.getSize(); y++) {
                    cards[x][y].setNum(json.getInt(index));
                    index++;
                }
            }
        } catch (final JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 用于保存游戏进度
     */
    private void saveGameData() {
        final JSONArray json = new JSONArray();
        for (int x = 0; x < gameView.getSize(); x++) {
            for (int y = 0; y < gameView.getSize(); y++) {
                json.put(cards[x][y].getNum());
            }
        }
        cardsNum = json.toString();
        editor.putString("cards_num", cardsNum).putInt("curr_score", currScore).commit();
    }

    /**
     * 分数重置
     */
    private void reset() {
        currScore = 0;
        setScore(currScoreLl, currScore);
        gameView.setFlag(0);
        gameView.startGame();
    }


    @Override
    protected void onPause() {
        super.onPause();
        saveGameData();
    }


    @Override
    protected void onStop() {
        super.onStop();
        saveGameData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        saveGameData();
    }


}
