package usage.ywb.wrapper.chinesechess;

/**
 * 棋子
 * @author yuwenbo
 * @version [ v1.0.0, 2016/4/27 ]
 */
public class ChessPieces {

    /**
     * 棋子所属阵营--红方
     */
    public static final int CAMP_RED = 0;
    /**
     * 棋子所属阵营--蓝方
     */
    public static final int CAMP_BLUE = 1;

    /**
     * 棋子的名称
     */
    private String name;

    /**
     * 棋子的x矩阵坐标
     */
    private int x;

    /**
     * 棋子Y矩阵坐标
     */
    private int y;

    /**
     * 棋子所属的阵营
     */
    private int camp;

    /**
     * 棋子编号
     */
    public int piecesNo;


    public void setPoint(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public String getName() {
        return name;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getCamp() {
        return camp;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCamp(int camp) {
        this.camp = camp;
    }

    public int getPiecesNo() {
        return piecesNo;
    }

    public void setPiecesNo(int piecesNo) {
        this.piecesNo = piecesNo;
    }

}
