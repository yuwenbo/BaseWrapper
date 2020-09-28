package usage.ywb.wrapper.video.player.model;


import java.io.Serializable;

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
}
