package usage.ywb.demo.gamemerge10;

/**
 * 移动块的位置类
 *
 * @author yuwenbo
 * @version [ v1.0.0, 2017/3/31 ]
 */
public class MoveBlock {

    /**
     * 初始位置
     */
    private int x, y;

    /**
     * 移动位置
     */
    private int toX, toY;

    /**
     * @param x
     * @param y
     * @param toX
     * @param toY
     * @author yuwenbo
     * @date [ 2017/3/31 10:43 ]
     */
    public MoveBlock(int x, int y, int toX, int toY) {
        this.x = x;
        this.y = y;
        this.toX = toX;
        this.toY = toY;
    }


    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getToX() {
        return toX;
    }

    public int getToY() {
        return toY;
    }
}
