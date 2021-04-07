package usage.ywb.wrapper.chinesechess;

import java.util.ArrayList;
import java.util.List;

/**
 * 象棋管理
 *
 * @author yuwenbo
 * @version [ v1.0.0, 2016/12/26 ]
 */
public class ChessManager {

    /**
     * 最大存储棋局步数
     */
    private static int LENGTH = 10;

    /**
     * 存储的最近的行走过的几步棋局，采用先进先出的原则
     */
    private static List<Integer[][]> stepsList = new ArrayList<Integer[][]>();

    public static List<Integer[][]> getStepsList() {
        return stepsList;
    }

    /**
     * 添加一步
     *
     * @param step
     */
    public static void addStep(Integer[][] step) {
        if (stepsList.size() >= LENGTH) {
            stepsList.remove(0);
        }
        stepsList.add(step);
    }

    /**
     * 移除一步
     *
     * @return
     */
    public static Integer[][] fetchStep() {
        if (!stepsList.isEmpty()) {
            return stepsList.remove(stepsList.size() - 1);
        }
        return null;
    }

}
