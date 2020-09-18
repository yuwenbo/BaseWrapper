// Copyright (c) 1998-2015 Core Solutions Limited. All rights reserved.
// ============================================================================
// CURRENT VERSION CNT.5.0.1
// ============================================================================
// CHANGE LOG
// CNT.5.0.1 : 2015-XX-XX, frank.yu, creation
// ============================================================================
package usage.ywb.wrapper.audio.entity;

import java.io.Serializable;

/**
 * @author frank.yu
 *
 * DATE:2015.05.25
 */
public class AudioEntity implements Serializable{

    private String id;
    private String name;
    private String data;
    private String album;
    private String artist;
    private long size;
    private long time;

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id
     *            the id to set
     */
    public void setId(final String id) {
        this.id = id;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     *            the name to set
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * @return the data
     */
    public String getData() {
        return data;
    }

    /**
     * @param data
     *            the data to set
     */
    public void setData(final String data) {
        this.data = data;
    }

    /**
     * @return the album
     */
    public String getAlbum() {
        return album;
    }

    /**
     * @param album
     *            the album to set
     */
    public void setAlbum(final String album) {
        this.album = album;
    }

    /**
     * @return the artist
     */
    public String getArtist() {
        return artist;
    }

    /**
     * @param artist
     *            the artist to set
     */
    public void setArtist(final String artist) {
        this.artist = artist;
    }

    /**
     * @return the size
     */
    public long getSize() {
        return size;
    }

    /**
     * @param size
     *            the size to set
     */
    public void setSize(final long size) {
        this.size = size;
    }

    /**
     * @return the time
     */
    public long getTime() {
        return time;
    }

    /**
     * @param time
     *            the time to set
     */
    public void setTime(final long time) {
        this.time = time;
    }

}
