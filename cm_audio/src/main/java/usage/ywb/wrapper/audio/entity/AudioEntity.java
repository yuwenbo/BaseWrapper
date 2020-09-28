// Copyright (c) 1998-2015 Core Solutions Limited. All rights reserved.
// ============================================================================
// CURRENT VERSION CNT.5.0.1
// ============================================================================
// CHANGE LOG
// CNT.5.0.1 : 2015-XX-XX, frank.yu, creation
// ============================================================================
package usage.ywb.wrapper.audio.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author frank.yu
 *
 * DATE:2015.05.25
 */
public class AudioEntity implements Parcelable {

    public String id;
    public String name;
    public String data;
    public String album;
    public String artist;
    public long size;
    public long time;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.name);
        dest.writeString(this.data);
        dest.writeString(this.album);
        dest.writeString(this.artist);
        dest.writeLong(this.size);
        dest.writeLong(this.time);
    }

    public AudioEntity() {
    }

    protected AudioEntity(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
        this.data = in.readString();
        this.album = in.readString();
        this.artist = in.readString();
        this.size = in.readLong();
        this.time = in.readLong();
    }

    public static final Parcelable.Creator<AudioEntity> CREATOR = new Parcelable.Creator<AudioEntity>() {
        @Override
        public AudioEntity createFromParcel(Parcel source) {
            return new AudioEntity(source);
        }

        @Override
        public AudioEntity[] newArray(int size) {
            return new AudioEntity[size];
        }
    };
}
