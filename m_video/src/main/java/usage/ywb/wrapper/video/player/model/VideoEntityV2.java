package usage.ywb.wrapper.video.player.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author yuwenbo
 * @version [ v1.0.0, 2016/12/8 ]
 */

public class VideoEntityV2 implements Parcelable {

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

    protected VideoEntityV2(Parcel in) {
        name = in.readString();
        uri = in.readString();
        size = in.readLong();
        time = in.readLong();
    }

    public static final Creator<VideoEntityV2> CREATOR = new Creator<VideoEntityV2>() {
        @Override
        public VideoEntityV2 createFromParcel(Parcel in) {
            return new VideoEntityV2(in);
        }

        @Override
        public VideoEntityV2[] newArray(int size) {
            return new VideoEntityV2[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(uri);
        parcel.writeLong(size);
        parcel.writeLong(time);
    }

}
