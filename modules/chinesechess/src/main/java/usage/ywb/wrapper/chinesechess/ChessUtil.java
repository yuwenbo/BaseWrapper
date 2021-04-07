package usage.ywb.wrapper.chinesechess;


import java.util.ArrayList;
import java.util.List;

import usage.ywb.wrapper.chinesechess.ChessPieces;

/**
 * @author yuwenbo
 * @version [ v1.0.0, 2016/4/27 ]
 */
public class ChessUtil {


    /**
     * 将棋子布局转换成业务对象集合
     *
     * @return
     */
    public static List<ChessPieces> getChessList(Integer[][] types) {
        List<ChessPieces> piecesList = new ArrayList<ChessPieces>();
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 9; j++) {
                int value = types[i][j];
                if (value != 0) {
                    ChessPieces pieces = new ChessPieces();
                    pieces.setPoint(j, i);
                    switch (value) {
                        case Rule.RED_MARSHAL:
                            pieces.setName("帥");
                            pieces.setPiecesNo(Rule.RED_MARSHAL);
                            pieces.setCamp(ChessPieces.CAMP_RED);
                            break;
                        case Rule.RED_GUARD:
                            pieces.setName("士");
                            pieces.setPiecesNo(Rule.RED_GUARD);
                            pieces.setCamp(ChessPieces.CAMP_RED);
                            break;
                        case Rule.RED_PREMIER:
                            pieces.setPiecesNo(Rule.RED_PREMIER);
                            pieces.setName("相");
                            pieces.setCamp(ChessPieces.CAMP_RED);
                            break;
                        case Rule.RED_HORSE:
                            pieces.setPiecesNo(Rule.RED_HORSE);
                            pieces.setName("馬");
                            pieces.setCamp(ChessPieces.CAMP_RED);
                            break;
                        case Rule.RED_CHARIOT:
                            pieces.setName("車");
                            pieces.setPiecesNo(Rule.RED_CHARIOT);
                            pieces.setCamp(ChessPieces.CAMP_RED);
                            break;
                        case Rule.RED_CANNON:
                            pieces.setName("炮");
                            pieces.setPiecesNo(Rule.RED_CANNON);
                            pieces.setCamp(ChessPieces.CAMP_RED);
                            break;
                        case Rule.RED_SOLDIER:
                            pieces.setName("兵");
                            pieces.setPiecesNo(Rule.RED_SOLDIER);
                            pieces.setCamp(ChessPieces.CAMP_RED);
                            break;
                        case Rule.BLUE_SOLDIER:
                            pieces.setName("卒");
                            pieces.setPiecesNo(Rule.BLUE_SOLDIER);
                            pieces.setCamp(ChessPieces.CAMP_BLUE);
                            break;
                        case Rule.BLUE_CANNON:
                            pieces.setName("炮");
                            pieces.setPiecesNo(Rule.BLUE_CANNON);
                            pieces.setCamp(ChessPieces.CAMP_BLUE);
                            break;
                        case Rule.BLUE_CHARIOT:
                            pieces.setPiecesNo(Rule.BLUE_CHARIOT);
                            pieces.setName("車");
                            pieces.setCamp(ChessPieces.CAMP_BLUE);
                            break;
                        case Rule.BLUE_HORSE:
                            pieces.setPiecesNo(Rule.BLUE_HORSE);
                            pieces.setName("馬");
                            pieces.setCamp(ChessPieces.CAMP_BLUE);
                            break;
                        case Rule.BLUE_PREMIER:
                            pieces.setPiecesNo(Rule.BLUE_PREMIER);
                            pieces.setName("象");
                            pieces.setCamp(ChessPieces.CAMP_BLUE);
                            break;
                        case Rule.BLUE_GUARD:
                            pieces.setName("士");
                            pieces.setPiecesNo(Rule.BLUE_GUARD);
                            pieces.setCamp(ChessPieces.CAMP_BLUE);
                            break;
                        case Rule.BLUE_MARSHAL:
                            pieces.setName("将");
                            pieces.setPiecesNo(Rule.BLUE_MARSHAL);
                            pieces.setCamp(ChessPieces.CAMP_BLUE);
                            break;
                        default:
                            pieces = null;
                            break;
                    }
                    if (pieces != null) {
                        piecesList.add(pieces);
                    }
                }
            }
        }
        return piecesList;
    }


    /**
     * 初始化开局棋子
     * <p/>
     * 0没有棋子
     * <p/>
     * 红方：
     * 1帥，2士，3相，4馬，5車，6炮，7兵
     * <p/>
     * 蓝方：
     * 17将，18士，19象，20馬，21車，22炮，23卒
     *
     * @return 开局棋盘布局
     */
    public static Integer[][] initPiecesTypes() {
        Integer[][] chess = new Integer[][]{
                {Rule.BLUE_CHARIOT, Rule.BLUE_HORSE, Rule.BLUE_PREMIER, Rule.BLUE_GUARD, Rule.BLUE_MARSHAL,
                        Rule.BLUE_GUARD, Rule.BLUE_PREMIER, Rule.BLUE_HORSE, Rule.BLUE_CHARIOT},
                {0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, Rule.BLUE_CANNON, 0, 0, 0, 0, 0, Rule.BLUE_CANNON, 0},
                {Rule.BLUE_SOLDIER, 0, Rule.BLUE_SOLDIER, 0, Rule.BLUE_SOLDIER, 0, Rule.BLUE_SOLDIER, 0, Rule.BLUE_SOLDIER},
                {0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0},
                {Rule.RED_SOLDIER, 0, Rule.RED_SOLDIER, 0, Rule.RED_SOLDIER, 0, Rule.RED_SOLDIER, 0, Rule.RED_SOLDIER},
                {0, Rule.RED_CANNON, 0, 0, 0, 0, 0, Rule.RED_CANNON, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0},
                {Rule.RED_CHARIOT, Rule.RED_HORSE, Rule.RED_PREMIER, Rule.RED_GUARD, Rule.RED_MARSHAL,
                        Rule.RED_GUARD, Rule.RED_PREMIER, Rule.RED_HORSE, Rule.RED_CHARIOT}
        };
        return chess;
    }

    /**
     * 复制一个二维数组
     *
     * @param piecesArray
     * @return
     */
    public static Integer[][] copy(Integer[][] piecesArray) {
        Integer[][] pieces = new Integer[piecesArray.length][piecesArray[0].length];
        for (int i = 0; i < piecesArray.length; i++) {
            for (int j = 0; j < piecesArray[0].length; j++) {
                pieces[i][j] = piecesArray[i][j];
            }
        }
        return pieces;
    }

}
