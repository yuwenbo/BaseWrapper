package usage.ywb.wrapper.chinesechess;

import android.util.Log;

import androidx.annotation.NonNull;

import usage.ywb.wrapper.chinesechess.ChessPieces;
import usage.ywb.wrapper.chinesechess.ChessPiecesView;

/**
 * 游戏规则
 *
 * @author yuwenbo
 * @version [ v1.0.0, 2016/4/29 ]
 */
public class Rule {

    /** 帥 */
    public static final int RED_MARSHAL = 1;
    /** 士 */
    public static final int RED_GUARD = 2;
    /** 相 */
    public static final int RED_PREMIER = 3;
    /** 馬 */
    public static final int RED_HORSE = 4;
    /** 車 */
    public static final int RED_CHARIOT = 5;
    /** 炮 */
    public static final int RED_CANNON = 6;
    /** 兵 */
    public static final int RED_SOLDIER = 7;


    /** 帥 */
    public static final int BLUE_MARSHAL = 17;
    /** 士 */
    public static final int BLUE_GUARD = 18;
    /** 相 */
    public static final int BLUE_PREMIER = 19;
    /** 馬 */
    public static final int BLUE_HORSE = 20;
    /** 車 */
    public static final int BLUE_CHARIOT = 21;
    /** 炮 */
    public static final int BLUE_CANNON = 22;
    /** 兵 */
    public static final int BLUE_SOLDIER = 23;



    /**
     * 老帅的移动规则
     *
     * @param array  棋子对应棋盘布局的二维数组
     * @param startX 棋子移动的起始X坐标
     * @param startY 棋子移动的其实Y坐标
     * @param endX  棋子移动的目标X坐标
     * @param endY  棋子移动的目标Y坐标
     * @return 如果目标位置是符合规则的移动则返回true，否则返回false
     */
    public static boolean marshal(Integer[][] array, int startX, int startY, int endX, int endY) {
        if (Math.abs(endX - startX) + Math.abs(endY - startY) == 1) {
            //每次只能移动一步
            return true;
        }
        return false;
    }

    /**
     * 士的移动规则
     *
     * @param array  棋子对应棋盘布局的二维数组
     * @param startX 棋子移动的起始X坐标
     * @param startY 棋子移动的其实Y坐标
     * @param endX  棋子移动的目标X坐标
     * @param endY  棋子移动的目标Y坐标
     * @return 如果目标位置是符合规则的移动则返回true，否则返回false
     */
    public static boolean guard(Integer[][] array, int startX, int startY, int endX, int endY) {
        if (Math.abs(endX - startX) == 1 && Math.abs(endY - startY) == 1) {
            // 每次只能走一步斜线
            return true;
        }
        return false;
    }

    /**
     * 相的移动规则
     *
     * @param array  棋子对应棋盘布局的二维数组
     * @param startX 棋子移动的起始X坐标
     * @param startY 棋子移动的其实Y坐标
     * @param endX  棋子移动的目标X坐标
     * @param endY  棋子移动的目标Y坐标
     * @return 如果目标位置是符合规则的移动则返回true，否则返回false
     */
    public static boolean premier(@NonNull Integer[][] array, int startX, int startY, int endX, int endY) {
        if (Math.abs(endX - startX) == 2 && Math.abs(endY - startY) == 2) {
            // 只能走田字格
            if (array[(startY + endY) / 2][(startX + endX) / 2] == 0) {
                // 相眼里没有子
                return true;
            }
        }
        return false;
    }

    /**
     * 馬的移动规则
     *
     * @param array  棋子对应棋盘布局的二维数组
     * @param startX 棋子移动的起始X坐标
     * @param startY 棋子移动的其实Y坐标
     * @param endX  棋子移动的目标X坐标
     * @param endY  棋子移动的目标Y坐标
     * @return 如果目标位置是符合规则的移动则返回true，否则返回false
     */
    public static boolean horse(@NonNull Integer[][] array, int startX, int startY, int endX, int endY) {
        if (Math.abs(endX - startX) == 2 && Math.abs(endY - startY) == 1) {
            // 只能走日字
            if (array[startY][(endX + startX) / 2] == 0) {
                // 不会被蹩马腿
                return true;
            }
        } else if (Math.abs(endX - startX) == 1 && Math.abs(endY - startY) == 2) {
            // 只能走日字
            if (array[(endY + startY) / 2][startX] == 0) {
                // 不会被蹩马腿
                return true;
            }
        }
        return false;
    }

    /**
     * 車的移动规则
     *
     * @param array  棋子对应棋盘布局的二维数组
     * @param startX 棋子移动的起始X坐标
     * @param startY 棋子移动的其实Y坐标
     * @param endX  棋子移动的目标X坐标
     * @param endY  棋子移动的目标Y坐标
     * @return 如果目标位置是符合规则的移动则返回true，否则返回false
     */
    public static boolean chariot(@NonNull Integer[][] array, int startX, int startY, int endX, int endY) {
        // 在車的直行路线上不得有其他棋子(目标位置可有可无，没有则是移动，有则是吃子)
        if (endX == startX && endY != startY) {
            // 如果是在竖直方向上直行
            int moveY = startY;
            while (Math.abs(moveY - endY) > 1) {
                if (moveY > endY + 1) {
                    moveY--;
                } else if(moveY < endY-1){
                    moveY++;
                } else {
                    break;
                }
                if (array[moveY][endX] != 0) {
                    return false;
                }
            }
        } else if (endY == startY && endX != startX) {
            // 如果是在横向方向上直行
            int moveX = startX;
            while (Math.abs(moveX - endX) > 1) {
                if (moveX > endX + 1) {
                    moveX--;
                } else if(moveX < endX -1){
                    moveX++;
                } else {
                    break;
                }
                if (array[endY][moveX] != 0) {
                    return false;
                }
            }
        } else {
            return false;
        }
        return true;
    }

    /**
     * 炮的移动规则
     * 普通移动时跟車一样路线上不得有其他棋子且移动目标也不得有其他棋子
     * 当吃子的时候移动目标需要有子，且起始位置与移动目标之间有且仅有一子
     *
     * @param array  棋子对应棋盘布局的二维数组
     * @param startX 棋子移动的起始X坐标
     * @param startY 棋子移动的其实Y坐标
     * @param endX  棋子移动的目标X坐标
     * @param endY  棋子移动的目标Y坐标
     * @return 如果目标位置是符合规则的移动则返回true，否则返回false
     */
    public static boolean cannon(Integer[][] array, int startX, int startY, int endX, int endY) {
        int count = 0;// 炮与目标位置之间的棋子数量（不包含起始位置和终点位置）
        // 炮只能直行
        if (endX == startX && endY != startY) {  // 如果是在竖直方向上直行
            int moveY = startY;
            while (Math.abs(moveY - endY) > 1) {
                if (moveY > endY + 1) {
                    moveY--;
                } else if(moveY < endY-1){
                    moveY++;
                } else {
                    break;
                }
                if (array[moveY][endX] != 0) {
                    count++;
                }
            }
        } else if (endY == startY && endX != startX) {   // 如果是在水平方向上直行
            int moveX = startX;
            while (Math.abs(moveX - endX) > 1) {
                if (moveX > endX + 1) {
                    moveX--;
                } else if(moveX < endX -1){
                    moveX++;
                } else {
                    break;
                }
                if (array[endY][moveX] != 0) {
                    count++;
                }
            }
        }else{
            // 不是直行则不符合规则
            return false;
        }
        if(count == 0){// 路径上没有其他棋子认为是普通移动
            if (array[endY][endX] != 0) {
                // 普通移动的目标位置不得有棋子
                return false;
            }else{
                return true;
            }
        }else if(count == 1){// 移动路径上有且仅有一个棋子认为是吃子
            if (array[endY][endX] != 0) {
                // 吃子时目标必须有子
                return true;
            }else{
                return false;
            }
        }else{ // 移动路劲上有多个棋子不符合规则
            return false;
        }
    }

    /**
     * 兵的移动规则
     *
     * @param array  棋子对应棋盘布局的二维数组
     * @param startX 棋子移动的起始X坐标
     * @param startY 棋子移动的其实Y坐标
     * @param endX  棋子移动的目标X坐标
     * @param endY  棋子移动的目标Y坐标
     * @return 如果目标位置是符合规则的移动则返回true，否则返回false
     */
    public boolean soldier(Integer[][] array, int startX, int startY, int endX, int endY) {

        return false;
    }

    /**
     * 判断棋子移动到目标位置是否可行
     *
     * @param chessPiecesView 准备移动的棋子控件
     * @param x               移动的目标坐标X
     * @param y               移动的目标坐标Y
     * @return 指定位置可移动返回true，否则false
     */
    public static boolean checkMove(Integer[][] mPiecesArray, ChessPiecesView chessPiecesView, int x, int y) {
        ChessPieces chessPieces = chessPiecesView.getChessPieces();
        Log.i("ChessLayout", "准备移动的棋子:" + chessPieces.getPiecesNo());
        switch (chessPieces.getPiecesNo()) {
            case Rule.RED_MARSHAL:// 我方 帥
                if (x <= 5 && x >= 3 & y <= 9 & y >= 7) {
                    //不得出属于它的田字格
                    if (Rule.marshal(mPiecesArray, chessPieces.getX(), chessPieces.getY(), x, y)) {
                        return true;
                    }
                }
                break;
            case Rule.RED_GUARD:// 我方 士
                if (x <= 5 && x >= 3 & y <= 9 & y >= 7) {
                    //不得出属于它的田字格
                    if (Rule.guard(mPiecesArray, chessPieces.getX(), chessPieces.getY(), x, y)) {
                        return true;
                    }
                }
                break;
            case Rule.RED_PREMIER:// 我方 相
                if (y >= 5 && y <= 9 && x >= 0 && x <= 8) {
                    // 不能过对岸
                    if (Rule.premier(mPiecesArray, chessPieces.getX(), chessPieces.getY(), x, y)) {
                        return true;
                    }
                }
                break;
            case Rule.RED_HORSE:// 我方 馬
            case Rule.BLUE_HORSE:// 敌方 馬
                if (y >= 0 && y <= 9 && x >= 0 && x <= 8) {
                    if (Rule.horse(mPiecesArray, chessPieces.getX(), chessPieces.getY(), x, y)) {
                        return true;
                    }
                }
                break;
            case Rule.RED_CHARIOT:// 我方 車
            case Rule.BLUE_CHARIOT:// 敌方 車
                if (y >= 0 && y <= 9 && x >= 0 && x <= 8) {
                    if (Rule.chariot(mPiecesArray, chessPieces.getX(), chessPieces.getY(), x, y)) {
                        return true;
                    }
                }
                break;
            case Rule.RED_CANNON:// 我方 炮
            case Rule.BLUE_CANNON:// 敌方 炮
                if (y >= 0 && y <= 9 && x >= 0 && x <= 8) {
                    if (Rule.cannon(mPiecesArray, chessPieces.getX(), chessPieces.getY(), x, y)) {
                        return true;
                    }
                }
                break;
            case Rule.RED_SOLDIER:// 我方 兵
                if (y >= 0 && y <= 6 && x >= 0 && x <= 8) {
                    if (y > 4) {// 过河之前
                        // 只能前进不能后退，不能左右，只能走一步
                        if (chessPieces.getY() - y == 1 && x == chessPieces.getX()) {
                            return true;
                        }
                    } else {// 过河后
                        // 只能前进一步或者左右一步
                        if ((x == chessPieces.getX() && chessPieces.getY() - y == 1) ||
                                (Math.abs(x - chessPieces.getX()) == 1 && chessPieces.getY() == y)) {
                            return true;
                        }
                    }
                }
                break;
            case Rule.BLUE_MARSHAL:// 敌方 将
                if (x <= 5 && x >= 3 & y <= 2 & y >= 0) {
                    //不得出属于它的田字格
                    if (Rule.marshal(mPiecesArray, chessPieces.getX(), chessPieces.getY(), x, y)) {
                        return true;
                    }
                }
                break;
            case Rule.BLUE_GUARD:// 敌方 士
                if (x <= 5 && x >= 3 & y <= 2 & y >= 0) {
                    //不得出属于它的田字格
                    if (Rule.guard(mPiecesArray, chessPieces.getX(), chessPieces.getY(), x, y)) {
                        return true;
                    }
                }
                break;
            case Rule.BLUE_PREMIER:// 敌方 象
                if (y >= 0 && y <= 4 && x >= 0 && x <= 8) {
                    // 不能过对岸
                    if (Rule.premier(mPiecesArray, chessPieces.getX(), chessPieces.getY(), x, y)) {
                        return true;
                    }
                }
                break;
            case Rule.BLUE_SOLDIER:// 敌方 卒
                if (y >= 3 && y <= 9 && x >= 0 && x <= 8) {
                    if (y <= 4) {// 过河之前
                        // 只能前进不能后退，不能左右，只能走一步
                        if (chessPieces.getY() - y == -1 && x == chessPieces.getX()) {
                            return true;
                        }
                    } else {// 过河后
                        // 只能前进一步或者左右一步
                        if ((x == chessPieces.getX() && chessPieces.getY() - y == -1) ||
                                (Math.abs(x - chessPieces.getX()) == 1 && chessPieces.getY() == y)) {
                            return true;
                        }
                    }
                }
                break;
            default:
                return false;
        }
        return false;
    }

}
