package usage.ywb.wrapper.audio.utils;

/**
 * @author frank.yu
 *         <p/>
 *         DATE:2015.05.28
 */
public class Constants {

    public static final String SHARE_MUSIC = "ywb_audio_player";

    public static final String MUSIC_SERVICE = "usage.ywb.personal.audio.AUDIO_SERVICE";

    /**
     * 列表循环
     */
    public static final int MODE_CYCLE_LIST = 0;
    /**
     * 单曲循环
     */
    public static final int MODE_CYCLE_ONE = 1;
    /**
     * 顺序播放
     */
    public static final int MODE_ORDER_LIST = 2;
    /**
     * 随机播放
     */
    public static final int MODE_RANDOM = 3;

    /**
     * SP保存key，当前播放位置
     */
    public static final String KEY_CURR_POSITION = "curr_position";
    /**
     * SP保存key，当前播放进度
     */
    public static final String KEY_CURR_PROGRESS = "curr_progress";
    /**
     * SP保存key，当前播放模式
     */
    public static final String KEY_CURR_MODE = "curr_mode";


}
