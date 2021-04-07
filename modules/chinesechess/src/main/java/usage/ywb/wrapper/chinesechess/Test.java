package usage.ywb.wrapper.chinesechess;

import java.util.ArrayList;


/**
 * @author yuwenbo
 * @version [ v1.0.0, 2016/12/26 ]
 */

public class Test {

    public ArrayList<Integer> range(int startIndex, int endIndex) {
        int[] array = { 1, 2, 3, 4, 5, 6, 7, 8 };
        ArrayList<Integer> result = new ArrayList<Integer>();

        if (startIndex < 0 || startIndex >= array.length) {
            return null;
        }
        if (endIndex < 0 || endIndex >= array.length) {
            return null;
        }
        // int index = startIndex < endIndex ? endIndex : startIndex;

        int sum = 0;
        for (int i = 0; i < array.length; i++) {
            sum = sum + array[i];
            if (i >= startIndex) {
                result.add(sum);
            }
            if (i >= endIndex) {
                break;
            }
        }

        return result;
    }


}
