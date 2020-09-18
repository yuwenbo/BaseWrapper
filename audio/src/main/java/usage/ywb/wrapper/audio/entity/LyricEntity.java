package usage.ywb.wrapper.audio.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author yuwenbo
 * @version [ v1.0.0, 2016/6/27 ]
 */
public class LyricEntity implements Serializable {


    private final List<String> texts = new ArrayList<String>();
    private final List<Integer> times = new ArrayList<Integer>();


}
