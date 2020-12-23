package usage.ywb.wrapper.video.player.model;

import android.text.format.DateFormat;

import java.io.Serializable;
import java.text.DecimalFormat;


/**
 * @author yuwenbo
 */
public class VideoEntity implements Serializable {

    private String name;
    private String uri;
    private long size;
    private long time;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getSizeText() {
        return String.format("%s MB", new DecimalFormat("#.##").format((float) getSize() / 1024 / 1024));
    }

    public String getTimeText() {
        return DateFormat.format("mm:ss", getTime()).toString();
    }


}
