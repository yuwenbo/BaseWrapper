// IAudioInterface.aidl
package usage.ywb.wrapper.audio;

// Declare any non-default types here with import statements

interface IAudioInterface {

    void prepare();

    void play();

    boolean isPlaying();

    void seek(int progress) ;

    void stop();

    void release();

}